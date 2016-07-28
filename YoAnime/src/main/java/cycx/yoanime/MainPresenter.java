package cycx.yoanime;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import cycx.yoanime.Models.Anime;
import cycx.yoanime.Models.Hummingbird.HummingbirdApi;
import cycx.yoanime.Utils.Events.FavouriteEvent;
import cycx.yoanime.Utils.Events.HbUserEvent;
import cycx.yoanime.Utils.Events.HummingbirdCredentialsUpdatedEvent;
import cycx.yoanime.Utils.Events.HummingbirdSettingsEvent;
import cycx.yoanime.Utils.Events.LastAnimeEvent;
import cycx.yoanime.Utils.Events.OpenAnimeEvent;
import cycx.yoanime.Utils.Events.SearchEvent;
import cycx.yoanime.Utils.Events.SearchSubmittedEvent;
import cycx.yoanime.Utils.Events.SnackbarEvent;
import cycx.yoanime.Utils.GeneralUtils;
import nucleus.presenter.RxPresenter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public class MainPresenter extends RxPresenter<MainActivity> {
    private static final String FAVOURITES_KEY = "favourites_key";

    private MainModel mainModel;

    private boolean needFragment = false;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        EventBus.getDefault().register(this);
        if (savedState != null && mainModel != null) {
            ArrayList<Anime> favourites = savedState.getParcelableArrayList(FAVOURITES_KEY);
            if (favourites != null) {
                mainModel.setFavourites(favourites);
            }
        }
    }

    @Override
    protected void onTakeView(MainActivity view) {
        super.onTakeView(view);

        if (mainModel != null && mainModel.hasSharedPreferences()) {
            mainModel.refreshHbDisplayNameAndUser();
        }

        if (needFragment) {
            view.requestFragment(MainActivity.SEARCH_FRAGMENT, null);
            needFragment = false;
        }

    }

    @Override
    protected void onSave(Bundle state) {
        super.onSave(state);
        if (mainModel.getFavourites() != null) {
            state.putParcelableArrayList(FAVOURITES_KEY, mainModel.getFavourites());
        }
        mainModel.saveFavourites();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainModel.saveFavourites();
        mainModel = null;
    }

    // must be done every time activity onCreate()
    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        if (mainModel != null) {
            mainModel.setSharedPreferences(sharedPreferences);
        } else {
            mainModel = new MainModel(sharedPreferences);
        }
    }

    public MainModel getModel() {
        return mainModel;
    }

    public void launchFromHbLink(String url) {
        Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                return Observable.just(HummingbirdApi.getTitleFromRegularPage(url));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.deliver())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onNext(String title) {
                        EventBus.getDefault().postSticky(new SearchEvent(title));
                    }

                    @Override
                    public void onCompleted() {
                        if (getView() != null) {
                            getView().requestFragment(MainActivity.SEARCH_FRAGMENT, null);
                        } else {
                            needFragment = true;
                        }
                        this.unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        getView().showSnackBar(new SnackbarEvent(GeneralUtils.formatError(e)));
                        this.unsubscribe();
                    }
                });
    }

    public void launchFromMalLink(String url) {
        Observable.just(url)
                .subscribeOn(Schedulers.io())
                .map(a -> a.substring(a.indexOf("http://")).trim())
                .map(a -> {
                    if (a.contains(" ")) {
                        return a.substring(0, a.indexOf(" "));
                    } else {
                        return a;
                    }
                })
                .map(u -> GeneralUtils.getWebPage(u))
                .map(body -> Jsoup.parse(body).select("head > title").first())
                .map(element -> element.text().substring(0, element.text().lastIndexOf("-") - 1))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onNext(String title) {
                        EventBus.getDefault().postSticky(new SearchEvent(title));
                    }

                    @Override
                    public void onCompleted() {
                        if (getView() != null) {
                            getView().requestFragment(MainActivity.SEARCH_FRAGMENT, null);
                        } else {
                            needFragment = true;
                        }
                        this.unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        getView().showSnackBar(new SnackbarEvent(GeneralUtils.formatError(e)));
                        this.unsubscribe();
                    }
                });
    }

    public void refreshFavouritesList() {
        mainModel.refreshFavourites();
    }

    public String getUserAvatar() {
        return mainModel.getHbUser().getAvatar();
    }

    public List<Anime> getFavourites() {
        return mainModel.getFavourites();
    }

    // Must have run setSharedPreferences() before this.
    public void onFreshStart(MainActivity mainActivity) {
        if (mainModel.getLastAnime() != null && MainModel.openToLastAnime) {
            EventBus.getDefault().postSticky(new OpenAnimeEvent(mainModel.getLastAnime()));
            mainActivity.requestFragment(MainActivity.ANIME_FRAGMENT, null);
        } else {
            mainActivity.requestFragment(MainActivity.SEARCH_FRAGMENT, null);
        }
    }


    public void onEvent(FavouriteEvent event) {
        // colors are inconsistent for whatever reason, causing duplicate favourites,
        // so Set is pretty useless ;-;

        // set mainModel if needed and possible
        if (mainModel == null) {
            if (getView() != null) {
                mainModel = new MainModel(getView().getPreferences(Context.MODE_PRIVATE));
            }
        }

        if (mainModel != null) {
            try {
                if (event.addToFavourites) {
                    mainModel.addToFavourites(event.anime);
                } else {
                    mainModel.removeFromFavourites(event.anime);
                }
                mainModel.saveFavourites();
                if (getView() != null) {
                    getView().favouritesChanged(getFavourites());
                }
            } catch (Exception e) {
                postError(e);
            }
        }
    }

    public void onEvent(LastAnimeEvent event) {
        // THIS METHOD IS BEING EXECUTED
        if (mainModel.updateLastAnimeAndFavourite(event.anime) && getView() != null) {
            mainModel.saveFavourites();
            getView().favouritesChanged(getFavourites());
        }
    }

    public void onEvent(SearchSubmittedEvent event) {
        if (getView() != null) {
            if (getView().getSupportFragmentManager().findFragmentByTag(MainActivity.ANIME_FRAGMENT) != null) {
                getView().getSupportFragmentManager().popBackStack();
            }
            if (getView().getSupportFragmentManager().findFragmentByTag(MainActivity.SEARCH_FRAGMENT) == null) {
                getView().requestFragment(MainActivity.SEARCH_FRAGMENT, null);
            }
        }
        EventBus.getDefault().postSticky(new SearchEvent(event.searchTerm));
    }

    public void onEvent(HummingbirdSettingsEvent event) {
        if (getView() != null) {
            getView().requestFragment(MainActivity.HUMMINGBIRD_SETTINGS_FRAGMENT, null);
        }
    }

    public void onEvent(HummingbirdCredentialsUpdatedEvent event) {
        mainModel.loginHummingbird(event.usernameOrEmail, event.password);
    }

    public void onEvent(HbUserEvent event) {
        if (getView() != null) {
            if (mainModel.getHbUser() != null) {
                getView().refreshDrawerUser(mainModel.getHbDisplayName(),
                        mainModel.getHbUser().getAvatar(),
                        mainModel.getHbUser().getCoverImage());
            } else {
                getView().refreshDrawerUser(mainModel.getHbDisplayName(), null, null);
            }
        }
    }

    public void onEvent(SnackbarEvent event) {
        if (getView() != null) {
            getView().showSnackBar(event);
        }
    }

    public void postError(Throwable e) {
        e.printStackTrace();
        EventBus.getDefault().post(new SnackbarEvent(GeneralUtils.formatError(e)));
    }


    public void downloadUpdate (String url) {
        GeneralUtils.lazyDownload(getView(), url);
    }

}