package cycx.yoanime;

import android.content.SharedPreferences;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import de.greenrobot.event.EventBus;
import cycx.yoanime.Models.Anime;
import cycx.yoanime.Models.Hummingbird.HBLibraryEntry;
import cycx.yoanime.Models.Hummingbird.HBUser;
import cycx.yoanime.Models.Hummingbird.HummingbirdApi;
import cycx.yoanime.Utils.Events.HbUserEvent;
import cycx.yoanime.Utils.Events.SnackbarEvent;
import cycx.yoanime.Utils.GeneralUtils;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.OnErrorThrowable;
import rx.schedulers.Schedulers;

public class MainModel {
    private static final String FAVOURITES_PREF = "favourites_preference";
    private static final String LAST_ANIME_PREF = "last_anime_preference";
    public static final String AUTO_UPDATE_PREF = "should_auto_update_preference";
    public static final String OPEN_TO_LAST_ANIME_PREF ="open_to_last_anime_preference";

    public static final String HB_USERNAME_PREF = "hb_username_preference";
    public static final String HB_PASSWORD_PREF = "hb_password_preference";
    public static final String HB_AUTH_TOKEN_PREF = "hb_auth_token_preference";
    public static final String EXTERNAL_DOWNLOAD_PREF = "external_download_preference";

    private SharedPreferences sharedPreferences;
    private HummingbirdApi hummingbirdApi;
    // The key is the anime url.
    private HashMap<String, Anime> favouritesMap;
    private Anime lastAnime;
    public static boolean openToLastAnime = false;
    public static boolean externalDownload = false;

    private String hbAuthToken;
    private HBUser hbUser;

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public boolean hasSharedPreferences () {
        return sharedPreferences != null;
    }

    public MainModel (SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        hummingbirdApi = new HummingbirdApi();
        refreshFavourites();
        refreshLastAnime();
        refreshOpenToLastAnime();
        refreshHbAuthToken();
        refreshHbDisplayNameAndUser();
        refreshExternalDownload();
    }

    public void refreshFavourites () {
        Set<String> favourites = new LinkedHashSet<>(sharedPreferences.getStringSet(FAVOURITES_PREF, new HashSet<>()));

        favouritesMap = new LinkedHashMap<>(favourites.size());
        for (String favourite : favourites) {
            Anime anime = deserializeAnime(favourite);
            if (anime != null) {
                favouritesMap.put(anime.getUrl(), anime);
            }
        }
    }

    public void refreshLastAnime () {
        // need to check for null or else deserialize will throw null pointer exception
        String serializedAnime = sharedPreferences.getString(LAST_ANIME_PREF, null);
        if (serializedAnime != null) {
            lastAnime = deserializeAnime(serializedAnime);
        }
    }

    public void refreshOpenToLastAnime () {
        openToLastAnime = sharedPreferences.getBoolean(OPEN_TO_LAST_ANIME_PREF, false);
    }

    private void refreshExternalDownload() {
                externalDownload = sharedPreferences.getBoolean(EXTERNAL_DOWNLOAD_PREF, false);
            }


    public boolean updateLastAnimeAndFavourite (Anime anime) {
        saveNewLastAnime(anime);
           // THIS METHOD IS BEING EXECUTED
        return updateFavourite(anime);
    }


    /*
    *
    *               FAVOURITES
    *
    */


    public void setFavourites (ArrayList<Anime> favourites) {
        for (Anime favourite : favourites) {
            favouritesMap.put(favourite.getUrl(), favourite);
        }
    }

    public ArrayList<Anime> getFavourites () {
        if (favouritesMap != null) {
            ArrayList<Anime> favourites = new ArrayList<>();
            favourites.addAll(favouritesMap.values());
            return favourites;
        } else if (sharedPreferences != null) {
            refreshFavourites();
            ArrayList<Anime> favourites = new ArrayList<>();
            favourites.addAll(favouritesMap.values());
            return favourites;
        }
        return null;
    }

    public void saveFavourites () {
        Set<String> favourites = new HashSet<>(favouritesMap.size());
        for (Anime anime : favouritesMap.values()) {
            favourites.add(serializeAnime(anime));
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(FAVOURITES_PREF, favourites);
        editor.apply();
    }

    public boolean isInFavourites (String url)  throws IllegalStateException {
        if (favouritesMap == null && sharedPreferences != null) {
            refreshFavourites();
        }
        if (favouritesMap != null) {
            return (favouritesMap.containsKey(url));
        }
        throw new IllegalStateException("Can't find favourites.");
    }

    public void addToFavourites (Anime anime) {
        if (favouritesMap == null) {
            refreshFavourites();
        }
        favouritesMap.put(anime.getUrl(), anime);
    }

    public void removeFromFavourites (Anime anime) {
        if (favouritesMap == null) {
            refreshFavourites();
        }
        favouritesMap.remove(anime.getUrl());
    }

    // Returns true if a favourite was updated. False if not.
    public boolean updateFavourite (Anime favourite) {
        if (favouritesMap.keySet().contains(favourite.getUrl())) {
            favouritesMap.put(favourite.getUrl(), favourite);
            return true;
        }
        return false;
    }


    /*
    *
    *               LAST ANIME
    *
    */


    public Anime getLastAnime () {
        return lastAnime;
    }

    public void saveNewLastAnime (Anime anime) {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(LAST_ANIME_PREF, serializeAnime(anime));
            editor.apply();
        }
    }


    /*
    *
    *               UTILS
    *
    */


    private String serializeAnime (Anime anime) {
        return GeneralUtils.serializeAnime(anime);
    }

    private Anime deserializeAnime (String serializedAnime) {
        return GeneralUtils.deserializeAnime(serializedAnime);
    }

    /*
    *
    *               HUMMINGBIRD
    *
    */

    public void clearAuthToken () {
        hbAuthToken = null;
        sharedPreferences.edit().putString(HB_AUTH_TOKEN_PREF, null).apply();
    }

    public void refreshHbAuthToken() {
        hbAuthToken = sharedPreferences.getString(HB_AUTH_TOKEN_PREF, null);
    }

    public String getHbDisplayName () {
        if (hbUser != null) {
            return hbUser.getName();
        }
        return null;
    }

    public HBUser getHbUser() {
        return hbUser;
    }

    public void refreshHbDisplayNameAndUser() {
        hbUser = null;
        String usernameOrEmail = sharedPreferences.getString(HB_USERNAME_PREF, null);

        if (usernameOrEmail != null) {
            if (usernameOrEmail.contains("@") && hbAuthToken != null) {
                    hummingbirdApi.getUserFromAuthToken(hbAuthToken)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<HBUser>() {
                            @Override
                            public void onNext(HBUser user) {
                                hbUser = user;
                                EventBus.getDefault().post(new HbUserEvent());
                                System.out.println("@@@@@@@" + user.getName());
                            }

                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                System.out.println("@@@ username fail");
                                EventBus.getDefault().post(new HbUserEvent());
                            }
                        });
                } else { //doesn't contain @ && authtoken is null
                if (!usernameOrEmail.contains("@")) {
                    refreshHummingbirdUser(usernameOrEmail);
                } else {
                    String password = sharedPreferences.getString(HB_PASSWORD_PREF, null);

                    if (password != null && !password.isEmpty()) {
                        loginHummingbird(usernameOrEmail, password);
                    } else {
                        EventBus.getDefault().post(new HbUserEvent());
                    }
                }
            }
        } else {
            EventBus.getDefault().post(new HbUserEvent());
        }
    }

    private void refreshHummingbirdUser (String displayName) {
        hbUser = null;
        if (displayName != null && !displayName.isEmpty()) {
            hummingbirdApi.getUser(displayName)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<HBUser>() {
                        @Override
                        public void onNext(HBUser user) {
                            hbUser = user;
                            EventBus.getDefault().post(new HbUserEvent());
                        }

                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            System.out.println("refreshHummingbirdUser failed");

                            EventBus.getDefault().post(new HbUserEvent());
                        }
                    });
        } else {
            EventBus.getDefault().post(new HbUserEvent());
        }
    }

    public void loginHummingbird() {
        loginHummingbird(
                sharedPreferences.getString(HB_USERNAME_PREF, null),
                sharedPreferences.getString(HB_PASSWORD_PREF, null)
        );
    }

    public void loginHummingbird(String usernameOrEmail, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        hbAuthToken = null;

        if (usernameOrEmail != null && !usernameOrEmail.isEmpty() && password != null && !password.isEmpty()) {
            hummingbirdApi.getAuthToken(usernameOrEmail, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onNext(String token) {
                        hbAuthToken = token;

                        editor.putString(HB_AUTH_TOKEN_PREF, hbAuthToken);
                        editor.apply();

                        refreshHbDisplayNameAndUser();
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        System.out.println("loginHummingbird failed");

                        editor.putString(HB_AUTH_TOKEN_PREF, hbAuthToken);
                        editor.apply();

                        refreshHummingbirdUser(null);
                        EventBus.getDefault().post(new SnackbarEvent("Error: Hummingbird login failed. Check your details."));
                    }
                });
        } else {
            editor.putString(HB_AUTH_TOKEN_PREF, null);
            editor.apply();

            if (usernameOrEmail != null && !usernameOrEmail.isEmpty()
                    && (password == null || password.isEmpty())) {
                EventBus.getDefault().post(new SnackbarEvent("Attention: No Hummingbird password entered."));
            }

            refreshHbDisplayNameAndUser();
        }

    }

    public void updateHbLibraryEntry (String id, String status, String privacy, int episodesWatched) {
        hummingbirdApi.updateLibraryEntry(id, hbAuthToken, status, privacy, episodesWatched)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HBLibraryEntry>() {
                    @Override
                    public void onNext(HBLibraryEntry entry) {
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
    }

}
