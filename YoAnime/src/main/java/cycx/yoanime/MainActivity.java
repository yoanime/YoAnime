

package cycx.yoanime;

        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.content.res.Resources;
        import android.support.annotation.NonNull;
        import android.support.annotation.Nullable;
        import android.support.design.widget.Snackbar;
        import android.support.v4.app.Fragment;
        import android.support.v4.app.FragmentManager;
        import android.support.v4.app.FragmentTransaction;
        import android.os.Bundle;
        import android.support.v4.view.GravityCompat;
        import android.support.v4.widget.DrawerLayout;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.support.v7.widget.Toolbar;
        import android.util.TypedValue;
        import android.view.View;
        import android.widget.FrameLayout;
        import android.widget.RelativeLayout;

        import java.util.List;

        import de.greenrobot.event.EventBus;
        import cycx.yoanime.Anime.AnimeFragment;
        import cycx.yoanime.Models.Anime;
        import cycx.yoanime.Models.SharedElementTransitionBundle;
        import cycx.yoanime.Search.Holder.SearchHolderFragment;
        import cycx.yoanime.Settings.SettingsFragment;
        import cycx.yoanime.Utils.Events.DrawerAdapterClickListener;
        import cycx.yoanime.Utils.Events.OpenAnimeEvent;
        import cycx.yoanime.Utils.Events.SnackbarEvent;
        import nucleus.factory.RequiresPresenter;
        import nucleus.view.NucleusAppCompatActivity;

        import com.google.android.gms.ads.AdRequest;
        import com.google.android.gms.ads.AdView;


@RequiresPresenter(MainPresenter.class)
public class MainActivity extends NucleusAppCompatActivity<MainPresenter> implements DrawerAdapterClickListener {
    private SharedPreferences sharedPreferences;
    private android.support.v4.app.FragmentManager fragmentManager;
    private FrameLayout parentLayout;
    private DrawerLayout drawerLayout;
    private RecyclerView favouritesList;
    private DrawerAdapter drawerAdapter;


    public static final String TRANSITION_NAME_KEY = "trans_name";
    public static final String SEARCH_FRAGMENT = "SEA";
    public static final String ANIME_FRAGMENT = "ANI";
    public static final String SETTINGS_FRAGMENT = "SET";
    public static final String HUMMINGBIRD_SETTINGS_FRAGMENT = "HUM";
    public static final String WELCOME_ACTIVITY = "Welcome";
    public static final String ABOUT_ACTIVITY = "About";

    private int avatarLength;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);



        //Locate the Banner Ad in activity_main.xml
        AdView adView = (AdView) this.findViewById(R.id.adView);

        // Request for Ads
        AdRequest adRequest = new AdRequest.Builder()

                // Add a test device to show Test Ads
               // .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
              //  .addTestDevice("CC5F2C72DF2B356BBF0DA198")
                .build();

        // Load ads into Banner Ads
        adView.loadAd(adRequest);

        sharedPreferences = getPreferences(MODE_PRIVATE);
        fragmentManager = getSupportFragmentManager();

        getPresenter().setSharedPreferences(sharedPreferences);

        parentLayout = (FrameLayout) findViewById(R.id.container);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        avatarLength = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, Resources.getSystem().getDisplayMetrics());

        RelativeLayout drawerSettingsButton = (RelativeLayout) findViewById(R.id.drawer_settings);
        RelativeLayout drawerSettingsButton2 = (RelativeLayout) findViewById(R.id.btn_play_again);




        drawerSettingsButton.setOnClickListener(new View.OnClickListener() {




            @Override
            public void onClick(View view) {
                requestFragment(MainActivity.SETTINGS_FRAGMENT, null);
                closeDrawer();





            }
        });

        drawerSettingsButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestFragment(MainActivity.WELCOME_ACTIVITY, null);
                closeDrawer();


            }
        });




        favouritesList = (RecyclerView) findViewById(R.id.drawer_recycler_view);
        favouritesList.setLayoutManager(new LinearLayoutManager(this));

        setFavouritesAdapter();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // must be after set as actionbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


        Intent openingIntent = getIntent();

        if (openingIntent.getAction() != null && openingIntent.getAction().equals(Intent.ACTION_SEND)) {
            String intentExtra = openingIntent.getStringExtra(Intent.EXTRA_TEXT);

            if (intentExtra != null) {
                intentExtra = intentExtra.toLowerCase();
                if (intentExtra.contains("hummingbird.me/anime/")) {
                    getPresenter().launchFromHbLink(intentExtra);
                } else if (intentExtra.contains("myanimelist.net/anime/")) {
                    getPresenter().launchFromMalLink(intentExtra);
                }
            }

        } else if (savedInstanceState == null) {
            getPresenter().onFreshStart(this);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainApplication.getRefWatcher(this).watch(this);
    }

    public void closeDrawer() {
        if (drawerLayout != null) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private void setTheme() {
        sharedPreferences = getPreferences(MODE_PRIVATE);
        int themePref = sharedPreferences.getInt(SettingsFragment.THEME_PREFERENCE, 0);

        switch (themePref) {
            //case 1:
               // setTheme(R.style.yolightTheme);
              //  break;

          //  case 2:
               // setTheme(R.style.yo_red_theme_light);
              //  break;

            case 1:
                setTheme(R.style.yo_red_theme_dark);
                break;

            //case 4:
               // setTheme(R.style.yo_green_theme_light);
               // break;

            case 2:
                setTheme(R.style.yo_green_theme_dark);
                break;



            case 3:
                setTheme(R.style.yo_pink_theme_dark);

                break;

             case 4:
             setTheme(R.style.yo_orange_theme_dark);
             break;

            case 5:
             setTheme(R.style.yo_purple_theme_dark);
             break;

            case 6:
                setTheme(R.style.yo_cyan_theme_dark);
                break;

            case 7:
                setTheme(R.style.yo_teal_theme_dark);
                break;

            case 8:
                setTheme(R.style.yo_brown_theme_dark);
                break;

            case 9:
                setTheme(R.style.yo_blue_grey_theme_dark);
                break;


            case 10:
                setTheme(R.style.yo_deep_orange_theme_dark);
                break;


            case 11:
                setTheme(R.style.yo_deep_purple_theme_dark);
                break;



            default:
                setTheme(R.style.yo_dark_Theme);

                break;
        }
    }

    public void requestFragment(@NonNull String tag, @Nullable SharedElementTransitionBundle transitionBundle) {
        boolean seaInBackStack = false;
        boolean aniInBackStack = false;
        boolean setInBackStack = false;
        boolean humInBackStack = false;
        boolean aboutInBackStack = false;

        int backstackEntryCount = fragmentManager.getBackStackEntryCount();

        for (int i = 0; i < backstackEntryCount; i++) {
            String name = fragmentManager.getBackStackEntryAt(i).getName();

            if (name.equals(SEARCH_FRAGMENT)) seaInBackStack = true;
            if (name.equals(ANIME_FRAGMENT)) aniInBackStack = true;
            if (name.equals(SETTINGS_FRAGMENT)) setInBackStack = true;
            if (name.equals(HUMMINGBIRD_SETTINGS_FRAGMENT)) humInBackStack = true;
            if (name.equals(ABOUT_ACTIVITY)) aboutInBackStack = true;
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null) {
                    String fragTag = fragment.getTag();

                    if (fragTag.equals(SEARCH_FRAGMENT) && !seaInBackStack && !tag.equals(SEARCH_FRAGMENT))
                        fragmentTransaction.addToBackStack(SEARCH_FRAGMENT);

                    else if (fragTag.equals(ANIME_FRAGMENT) && !aniInBackStack && !tag.equals(SEARCH_FRAGMENT) && !tag.equals(ANIME_FRAGMENT))
                        fragmentTransaction.addToBackStack(ANIME_FRAGMENT);

                    else if (fragTag.equals(SETTINGS_FRAGMENT) && !setInBackStack && !tag.equals(SEARCH_FRAGMENT) && !tag.equals(ANIME_FRAGMENT) && !tag.equals(SETTINGS_FRAGMENT))
                        fragmentTransaction.addToBackStack(SETTINGS_FRAGMENT);

                    else if (fragTag.equals(HUMMINGBIRD_SETTINGS_FRAGMENT) && !humInBackStack && !tag.equals(SEARCH_FRAGMENT) && !tag.equals(ANIME_FRAGMENT) && !tag.equals(SETTINGS_FRAGMENT) && !tag.equals(HUMMINGBIRD_SETTINGS_FRAGMENT))
                        fragmentTransaction.addToBackStack(HUMMINGBIRD_SETTINGS_FRAGMENT);

                    else if (fragTag.equals(ABOUT_ACTIVITY) && !aboutInBackStack && !tag.equals(SEARCH_FRAGMENT) && !tag.equals(ANIME_FRAGMENT) && !tag.equals(SETTINGS_FRAGMENT) && !tag.equals(ABOUT_ACTIVITY)
                       && !tag.equals(ABOUT_ACTIVITY))
                        fragmentTransaction.addToBackStack(ABOUT_ACTIVITY);
                }
            }
        }

        switch (tag) {

            case SEARCH_FRAGMENT:
                Fragment searchFragment = fragmentManager.findFragmentByTag(SEARCH_FRAGMENT);

                if (searchFragment == null) {

                    fragmentTransaction
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.container, new SearchHolderFragment(), SEARCH_FRAGMENT);

                } else {
                    fragmentManager.popBackStackImmediate(ANIME_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }

                break;

            case ANIME_FRAGMENT:
                Fragment animeFragment = fragmentManager.findFragmentByTag(ANIME_FRAGMENT);

                if (animeFragment == null) {
                    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                            && transitionBundle != null
                            && transitionBundle.baseName.equals(SearchFragment.POSTER_TRANSITION_BASE_NAME)) {

                        animeFragment = new AnimeFragment();
                        animeFragment.setArguments(transitionBundle.bundle);
                        animeFragment.setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.change_image_transform));
                        animeFragment.setEnterTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.explode));

                        fragmentTransaction
                                .addSharedElement(transitionBundle.sharedElement, transitionBundle.name)
                                .replace(R.id.container, animeFragment, ANIME_FRAGMENT);

                    } else {*/
                    fragmentTransaction
                            .setCustomAnimations(R.anim.slide_down_bounce, 0, 0, R.anim.slide_up)
                            .replace(R.id.container, new AnimeFragment(), ANIME_FRAGMENT);
                    //}

                } else {
                    fragmentManager.popBackStackImmediate(ANIME_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }

                break;

            case SETTINGS_FRAGMENT:
                Fragment settingsFragment = fragmentManager.findFragmentByTag(SETTINGS_FRAGMENT);

                if (settingsFragment == null) {

                    fragmentTransaction
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.container, new SettingsFragment(), SETTINGS_FRAGMENT);

                } else {
                    fragmentManager.popBackStackImmediate(SETTINGS_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }

                break;

            //case HUMMINGBIRD_SETTINGS_FRAGMENT:
            //Fragment hummingbirdSettingsFragment = fragmentManager.findFragmentByTag(HUMMINGBIRD_SETTINGS_FRAGMENT);

            //if (hummingbirdSettingsFragment == null) {

            //fragmentTransaction
            // .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            // .replace(R.id.container, new HummingbirdHolderFragment(), HUMMINGBIRD_SETTINGS_FRAGMENT);

            // } else {

            //fragmentManager.popBackStackImmediate(HUMMINGBIRD_SETTINGS_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            //}

            // break;

        }

        fragmentTransaction.commit();
    }

    public void showSnackBar(SnackbarEvent event) {
        if (event.actionTitle == null) {
            Snackbar.make(parentLayout, event.message, event.duration)
                    .show();
        } else {
            Snackbar.make(parentLayout, event.message, event.duration)
                    .setAction(event.actionTitle, event.onClickListener)
                    .setActionTextColor(event.actionColor)
                    .show();
        }
    }

    @Override
    public void onCLick(Anime item, @Nullable Integer position, @Nullable View view) {
        EventBus.getDefault().postSticky(new OpenAnimeEvent(item));

        requestFragment(MainActivity.ANIME_FRAGMENT, null);

        closeDrawer();
    }

    @Override
    public void onUserItemClicked() {
        requestFragment(HUMMINGBIRD_SETTINGS_FRAGMENT, null);
        closeDrawer();
    }

    private void setFavouritesAdapter() {
        drawerAdapter = new DrawerAdapter(this, this, getPresenter().getFavourites());
        drawerAdapter.setAvatarLength((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, Resources.getSystem().getDisplayMetrics()));
        favouritesList.setAdapter(drawerAdapter);
    }

    public void favouritesChanged(List<Anime> favourites) {
        if (drawerAdapter != null) {
            drawerAdapter.setFavourites(favourites);
            drawerAdapter.notifyDataSetChanged();
        } else {
            setFavouritesAdapter();
            drawerAdapter.notifyDataSetChanged();
        }
    }

    public void refreshDrawerUser(String hbUsername, String avatar, String cover) {
        if (hbUsername == null) {
            //hbUsername = getString(R.string.hummingbird_username_placeholder);
        }
        drawerAdapter.updateUserData(hbUsername, avatar, cover);
        drawerAdapter.notifyDataSetChanged();


        findViewById(R.id.btn_play_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // We normally won't show the help_slider slider again in real app
                // but this is for testing
                PrefManager prefManager = new PrefManager(getApplicationContext());

                // make first time launch TRUE
                prefManager.setFirstTimeLaunch(true);

                startActivity(new Intent(MainActivity.this, HelpSliderActivity.class));
                finish();
            }
        });



    }


    }

