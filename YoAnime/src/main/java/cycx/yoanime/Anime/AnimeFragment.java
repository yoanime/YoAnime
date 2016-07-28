package cycx.yoanime.Anime;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import cycx.yoanime.MainActivity;
import cycx.yoanime.MainApplication;
import cycx.yoanime.Models.Anime;
import cycx.yoanime.Models.Episode;
import cycx.yoanime.Models.Source;
import cycx.yoanime.Models.Video;
import cycx.yoanime.R;
import cycx.yoanime.Utils.AdapterClickListener;
import cycx.yoanime.Utils.Events.SearchSubmittedEvent;
import cycx.yoanime.Utils.GeneralUtils;
import nucleus.factory.RequiresPresenter;
import nucleus.view.NucleusSupportFragment;

@RequiresPresenter(AnimePresenter.class)
public class AnimeFragment extends NucleusSupportFragment<AnimePresenter> implements AdapterClickListener<Episode> {
    private AnimeAdapter episodesAdapter;
    private RelativeLayout relativeLayout;
    private SwipeRefreshLayout refreshLayout;
    private SearchView searchView;
    public Integer position;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.yoanime_fragment, container, false);

        TypedValue colorPrimary = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorPrimary, colorPrimary, true);

        episodesAdapter = new AnimeAdapter(new ArrayList<>(), this, getResources().getColor(android.R.color.black), getResources().getColor(colorPrimary.resourceId));
/*
        if (getArguments() != null) {
            episodesAdapter.setTransitionName(getArguments().getString(MainActivity.TRANSITION_NAME_KEY));
        }
*/

        relativeLayout = (RelativeLayout) view.findViewById(R.id.anime_fragment_top_level);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext(), LinearLayout.VERTICAL, false));
        recyclerView.setAdapter(episodesAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.dark);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPresenter().fetchAnime(true);
            }
        });

        updateRefreshing();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (searchView != null) {
            searchView.setOnQueryTextListener(null);
        }
        MainApplication.getRefWatcher(getActivity()).watch(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (getView() != null) {
            super.onCreateOptionsMenu(menu, inflater);

           // MenuItem shareItem = menu.findItem(R.id.share_item);
            MenuItem searchItem = menu.findItem(R.id.search_item);

            if (searchItem == null || searchItem == null) {
                inflater.inflate(R.menu.search_menu, menu);
               // searchItem = menu.findItem(R.id.share_item);
                searchItem = menu.findItem(R.id.search_item);
            }

            searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (getPresenter() != null && getPresenter().lastAnime != null) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_TEXT, getPresenter().lastAnime.getUrl());
                        intent.setType("text/plain");
                        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    }
                    return true;
                }
            });

            searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

            searchView.setMaxWidth(10000000); //god...
            searchView.setQueryHint(getString(R.string.search_item));
            searchView.setIconifiedByDefault(true);
            searchView.setIconified(true);
            searchView.setAlpha(1.0f);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if (!query.isEmpty()) {
                        EventBus.getDefault().post(new SearchSubmittedEvent(query));
                        searchView.clearFocus();
                        refreshLayout.requestFocus();
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
            searchView.clearFocus();
            refreshLayout.requestFocus();
        }
    }

    public void setAnime (@NonNull Anime anime) {
        Picasso.with(getActivity()).invalidate(anime.getImageUrl());
        episodesAdapter.setAnime(anime.getEpisodes(), isInFavourites(anime));
        setToolbarTitle(anime.getTitle());
        getPresenter().setNeedToGiveFavourite(false);
        updateRefreshing();
    }

    // returns false if it cannot check.
    public boolean isInFavourites(Anime anime) {
        try {
            return ((MainActivity) getActivity()).getPresenter().getModel().isInFavourites(anime.getUrl());
        } catch (IllegalStateException e) {
            getPresenter().postError(e);
            return false;
        }
    }


    public void notifyAdapter () {
        episodesAdapter.notifyDataSetChanged();
    }

    public void updateRefreshing () {
        if (!isRefreshing() && getPresenter().isRefreshing) {
            TypedValue typedValue = new TypedValue();
            getActivity().getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typedValue, true);
            refreshLayout.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typedValue.resourceId));
            refreshLayout.setRefreshing(true);
        } else if (isRefreshing() && !getPresenter().isRefreshing){
            refreshLayout.setRefreshing(false);
        }
    }

    public boolean isRefreshing () {
        return refreshLayout.isRefreshing();
    }

    public void setToolbarTitle (String title) {
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(title);
    }

    public void setFavouriteChecked(boolean isInFavourites) {
        episodesAdapter.setFabChecked(isInFavourites);
        getPresenter().setNeedToGiveFavourite(false);
    }


    private void setRefreshLayoutStatus (boolean setEnabled) {
        refreshLayout.setEnabled(setEnabled);
    }

    @Override
    public void onCLick(Episode episode, @Nullable Integer position, @Nullable View view) {
        getPresenter().fetchSources(episode.getUrl());
        this.position = position;
    }

    @Override
    public void onLongClick(Episode item, @Nullable Integer position) {
        getPresenter().flipWatched(position);
    }

    public void showSourcesDialog (List<Source> sources) {
        if (sources.size() >= 1) {
            TypedValue typedValue = new TypedValue();
            getActivity().getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
            int accentColor = typedValue.data;

            new MaterialDialog.Builder(getActivity())
                    .title(getString(R.string.sources))
                    .items(getSourcesAsCharSequenceArray(sources))
                    .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                            return true;
                        }
                    })
                    .callback(new MaterialDialog.ButtonCallback() {

                        @Override
                        public void onPositive(MaterialDialog dialog) { //stream
                            super.onPositive(dialog);
                            getPresenter().fetchVideo(sources.get(dialog.getSelectedIndex()), false);
                            if (position != null) {
                                episodesAdapter.setWatched(position);
                            }
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) { //download
                            super.onNegative(dialog);
                            RxPermissions.getInstance(getContext())
                                    .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    .subscribe(granted -> {
                                        if (granted) {
                                            getPresenter().fetchVideo(sources.get(dialog.getSelectedIndex()), true);
                                            if (position != null) {
                                                episodesAdapter.setWatched(position);
                                            }
                                        }
                                    });
                        }

                        @Override
                        public void onNeutral(MaterialDialog dialog) {
                            super.onNeutral(dialog);
                            position = null;
                        }

                    })
                    .widgetColor(accentColor)
                    .positiveText(R.string.stream)
                    .positiveColor(accentColor)
                    .negativeText(R.string.download)
                    .negativeColor(accentColor)
                    .neutralText(R.string.cancel)
                    .neutralColorRes(R.color.grey_darkestXX)
                    .cancelable(true)
                    .show();
        } else {
            getPresenter().postError(new Throwable("No sources found."));
        }
    }

    private void showVideosDialog (List<Video> videos, boolean download) {
        new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.quality))
                .items(getVideosAsCharSequenceArray(videos))
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        downloadOrStream(videos.get(i), download);
                    }
                })
                .show();
    }

    public void showImageDialog () {
        if (getPresenter().lastAnime != null) {
            getActivity().getLayoutInflater().inflate(R.layout.image_view, relativeLayout);

            ImageView imageView = (ImageView) getActivity().findViewById(R.id.image_dialog_image_view);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    relativeLayout.removeView(imageView);
                }
            });

            Picasso.with(getActivity())
                    .load(getPresenter().lastAnime.getImageUrl())
                    .fit()
                    .centerInside()
                    .into(imageView);
        } else {
            getPresenter().postError(new Throwable("Can't find the image."));
        }
    }

    private CharSequence[] getSourcesAsCharSequenceArray (List<Source> sources) {
        CharSequence[] sourcesAsArray = new CharSequence[sources.size()];
        for (int i = 0; i < sources.size(); i++) {
            sourcesAsArray[i] = sources.get(i).getTitle();
        }
        return sourcesAsArray;
    }

    private CharSequence[] getVideosAsCharSequenceArray(List<Video> videos) {
        CharSequence[] videosAsArray = new CharSequence[videos.size()];
        for (int i = 0; i < videos.size(); i++) {
            videosAsArray[i] = videos.get(i).getTitle();
        }
        return videosAsArray;
    }

    public void shareVideo (Source source, boolean download) {
        if (source.getVideos().size() == 1) {
            downloadOrStream(source.getVideos().get(0), download);
        } else {
            showVideosDialog(source.getVideos(), download);
        }
    }

        private void downloadOrStream (Video video, boolean download) {
                if (download) {
                        getPresenter().download(video.getUrl(), getPresenter().lastAnime.getEpisodes().get(position).getTitle() + ".mp4");
                    } else {
                        getPresenter().postIntent(video.getUrl());
                    }
            }

}
