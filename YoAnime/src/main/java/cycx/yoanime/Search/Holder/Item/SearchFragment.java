package cycx.yoanime.Search.Holder.Item;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
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

import de.greenrobot.event.EventBus;
import cycx.yoanime.MainActivity;
import cycx.yoanime.MainApplication;
import cycx.yoanime.Models.Anime;
import cycx.yoanime.R;
import cycx.yoanime.Search.Holder.SearchHolderAdapter;
import cycx.yoanime.Search.Holder.SearchHolderFragment;
import cycx.yoanime.Utils.AdapterClickListener;
import cycx.yoanime.Utils.Events.OpenAnimeEvent;
import cycx.yoanime.Utils.Events.SearchEvent;
import nucleus.factory.RequiresPresenter;
import nucleus.view.NucleusSupportFragment;

@RequiresPresenter(SearchPresenter.class)
public class SearchFragment extends NucleusSupportFragment<SearchPresenter> implements AdapterClickListener<Anime> {
    //public final static String POSTER_TRANSITION_BASE_NAME = "poster_transition_";

    //public String transitionName;

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView.Adapter searchAdapter;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getPresenter().setProviderType(getArguments().getInt(SearchHolderAdapter.PROVIDER_TYPE_KEY, Anime.RUSH));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment, container, false);

        RecyclerView searchResultsView = (RecyclerView) view.findViewById(R.id.recycler_view);
        switch (SearchHolderFragment.SEARCH_GRID_TYPE) {
            case 0:
                searchAdapter = new SearchGridAdapter(this);

                int columns = 2;

                switch (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_LAYOUTDIR_MASK) {
                    case Configuration.SCREENLAYOUT_SIZE_LARGE:
                        columns = 3;
                        break;
                    case Configuration.SCREENLAYOUT_SIZE_SMALL:
                        columns = 1;
                }

                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    columns = 3;
                }

                searchResultsView.setLayoutManager(new GridLayoutManager(container.getContext(), columns, GridLayoutManager.VERTICAL, false));
                break;

            case 1:
                searchAdapter = new SearchListAdapter(this);
                searchResultsView.setLayoutManager(new LinearLayoutManager(container.getContext(), LinearLayoutManager.VERTICAL, false));
                break;
        }

        ItemMarginsDecoration itemMarginsDecoration = new ItemMarginsDecoration(container.getContext(), R.dimen.item_margin);
        searchResultsView.addItemDecoration(itemMarginsDecoration);

        searchResultsView.setAdapter(searchAdapter);
        searchResultsView.setItemAnimator(new DefaultItemAnimator());

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.dark);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPresenter().search();
            }
        });

        updateRefreshing();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainApplication.getRefWatcher(getActivity()).watch(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem searchItem = menu.findItem(R.id.search_item);

        if (searchItem == null) {
            inflater.inflate(R.menu.search_menu, menu);
            searchItem = menu.findItem(R.id.search_item);
        }

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setQueryHint(getString(R.string.search_item));
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);
        searchView.setAlpha(1f);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (!query.isEmpty()) {
                    getPresenter().onEvent(new SearchEvent(query));
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

    public void updateSearchResults () {
        searchAdapter.notifyDataSetChanged();
        updateRefreshing();
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

    @Override
    public void onCLick(Anime anime, @Nullable Integer position, View view) {
        /*
        bundle = new Bundle();
        bundle.putString(MainActivity.TRANSITION_NAME_KEY, transitionName);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSharedElementReturnTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.change_image_transform));
            setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.explode));

            ((MainActivity) getActivity()).requestFragment(MainActivity.ANIME_FRAGMENT,
                    new SharedElementTransitionBundle(view, POSTER_TRANSITION_BASE_NAME, transitionName, bundle));
        } else {
        */
            ((MainActivity) getActivity()).requestFragment(MainActivity.ANIME_FRAGMENT, null);
        //}

        EventBus.getDefault().postSticky(new OpenAnimeEvent(anime));
    }

    @Override
    public void onLongClick(Anime item, @Nullable Integer position) {

    }

    private class ItemMarginsDecoration extends RecyclerView.ItemDecoration {

        private int itemMargin;

        public ItemMarginsDecoration (int itemMargin) {
            this.itemMargin = itemMargin;
        }

        public ItemMarginsDecoration (@NonNull Context context, @DimenRes int itemMargin) {
            this(context.getResources().getDimensionPixelSize(itemMargin));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(itemMargin, itemMargin, itemMargin, itemMargin);
        }

    }

}
