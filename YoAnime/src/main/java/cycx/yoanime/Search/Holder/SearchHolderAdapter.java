package cycx.yoanime.Search.Holder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import cycx.yoanime.Models.Anime;
import cycx.yoanime.Search.Holder.Item.SearchFragment;

public class SearchHolderAdapter extends FragmentStatePagerAdapter{
    public static final String PROVIDER_TYPE_KEY = "PROVIDER_TYPE_KEY";

    public SearchHolderAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment searchFragment = new SearchFragment();
        Bundle args = new Bundle(1);

        switch(position) {
            // case 0:
            // args.putInt(PROVIDER_TYPE_KEY, Anime.RUSH);
            // break;
           case 0:
               args.putInt(PROVIDER_TYPE_KEY, Anime.KISS);
                break;

           // case 2:
              //  args.putInt(PROVIDER_TYPE_KEY, Anime.RAM);
               // break;
            //case 3:
              //  args.putInt(PROVIDER_TYPE_KEY, Anime.BAM);
              //  break;

        }

        searchFragment.setArguments(args);
        return searchFragment;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            //case 0:
               // return Anime.RUSH_TITLE;
            case 0:
                return Anime.KISS_TITLE;

           // case 2:
             //   return Anime.RAM_TITLE;
           // case 3:
               // return Anime.BAM_TITLE;

            default:
                throw new RuntimeException("No title for this tab number.");
        }
    }
}
