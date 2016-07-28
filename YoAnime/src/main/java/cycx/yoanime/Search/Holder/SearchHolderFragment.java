package cycx.yoanime.Search.Holder;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import cycx.yoanime.Models.Anime;
import cycx.yoanime.R;
import cycx.yoanime.Settings.SettingsFragment;
import cycx.yoanime.Utils.Events.SearchEvent;

public class SearchHolderFragment extends Fragment{
    public static int SEARCH_GRID_TYPE = 0;
    public static List<List<Anime>> searchResultsCache = new ArrayList<>(1);

    public SearchHolderFragment () {
        if (searchResultsCache.isEmpty()) {
            //searchResultsCache.add(Anime.RUSH, new ArrayList<>(0));
            searchResultsCache.add(Anime.KISS, new ArrayList<>(0));

           // searchResultsCache.add(Anime.RAM, new ArrayList<>(0));
            //searchResultsCache.add(Anime.BAM, new ArrayList<>(0));

        }
    }

    ViewPager searchPager;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
        
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SEARCH_GRID_TYPE = sharedPreferences.getInt(SettingsFragment.SEARCH_GRID_PREFERENCE, 0);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_holder_fragment, container, false);

        searchPager = (ViewPager) view.findViewById(R.id.search_view_pager);
       // TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);

        SearchHolderAdapter searchHolderAdapter = new SearchHolderAdapter(getChildFragmentManager());

        searchPager.setAdapter(searchHolderAdapter);
      //  tabLayout.setupWithViewPager(searchPager);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (getView() != null) {
            super.onCreateOptionsMenu(menu, inflater);

            MenuItem searchItem = menu.findItem(R.id.search_item);

            if (searchItem == null) {
                inflater.inflate(R.menu.search_menu, menu);
                searchItem = menu.findItem(R.id.search_item);
            }

            SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

            searchView.setMaxWidth(10000000); //god...
            searchView.setQueryHint(getString(R.string.search_item));
            searchView.setIconifiedByDefault(false);
            searchView.setIconified(false);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {

                    if (!query.isEmpty()) {
                        EventBus.getDefault().postSticky(new SearchEvent(query));
                        searchView.clearFocus();
                        searchPager.requestFocus();
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
            searchView.clearFocus();
            searchPager.requestFocus();
        }
    }

}
