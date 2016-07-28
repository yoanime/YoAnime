package cycx.yoanime.Settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import de.greenrobot.event.EventBus;
import cycx.yoanime.BuildConfig;
import cycx.yoanime.MainActivity;
import cycx.yoanime.MainModel;
import cycx.yoanime.R;






public class SettingsFragment extends Fragment {
    //TODO: REFACTOR THIS INTO MVP STRUCTURE
    public static final String THEME_PREFERENCE = "theme_preference";
    public static final String SEARCH_GRID_PREFERENCE = "search_grid_preference";
    public static final String DOWNLOAD_LOCATION_PREFERENCE = "download_location_preference";


    public static final int DOWNLOAD_LOCATION_REQUEST_CODE = 1;

    private TextView downloadLocationSummary;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private CharSequence[] themeTitles;
    private String downloadLocationFilePath;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        themeTitles = getResources().getStringArray(R.array.theme_entries);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);
        setToolbarTitle(getString(R.string.settings_item));

        //RelativeLayout hummingbirdItem = (RelativeLayout) view.findViewById(R.id.hummingbird_preference_item);
        //hummingbirdItem.setOnClickListener(new View.OnClickListener() {
        //@Override
        // public void onClick(View v) {
        // EventBus.getDefault().post(new HummingbirdSettingsEvent());
        // }
        // });

        RelativeLayout themeItem = (RelativeLayout) view.findViewById(R.id.theme_preference_item);
        TextView themeSummary = (TextView) themeItem.findViewById(R.id.preference_summary_text);
        themeSummary.setText(getSummary(THEME_PREFERENCE));
        themeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.theme_dialog_title)
                        .items(themeTitles)

                        .itemsCallbackSingleChoice(sharedPreferences.getInt(THEME_PREFERENCE, 0), new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {

                                editor.putInt(THEME_PREFERENCE, i);
                                editor.apply();

                                getActivity().recreate();
                                return false;
                            }
                        })
                        .show();
            }
        });


        //RelativeLayout searchGridItem = (RelativeLayout) view.findViewById(R.id.search_grid_preference_item);
        //TextView searchGridSummary = (TextView) searchGridItem.findViewById(R.id.preference_summary_text);
        //searchGridSummary.setText(getSummary(SEARCH_GRID_PREFERENCE));
        //searchGridItem.setOnClickListener(new View.OnClickListener() {
        // @Override
        // public void onClick(View view) {
        // new MaterialDialog.Builder(getActivity())
        //  .title(getString(R.string.search_grid_preference_title))
        // .items(getResources().getStringArray(R.array.search_grid_options))
        // .itemsCallbackSingleChoice(sharedPreferences.getInt(SEARCH_GRID_PREFERENCE, 0), new MaterialDialog.ListCallbackSingleChoice() {
        //   @Override
        //   public boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {

        //   editor.putInt(SEARCH_GRID_PREFERENCE, i);
        //   editor.apply();

        //  searchGridSummary.setText(searchGridSummaryUpdate(i) + " " + getString(R.string.requires_restart));

        //   return false;
        //   }
        //   })
        //  .show();
        //   }
        //  });

        RelativeLayout externalDownloadItem = (RelativeLayout) view.findViewById(R.id.external_download_item);
        CheckBox externalDownloadCheckBox = (CheckBox) externalDownloadItem.findViewById(R.id.preference_check_box);
        externalDownloadCheckBox.setClickable(false);
        TextView externalDownloadSummary = (TextView) externalDownloadItem.findViewById(R.id.preference_summary_text);
        externalDownloadSummary.setText(yesNoSummaryUpdate(MainModel.externalDownload));
        externalDownloadCheckBox.setChecked(MainModel.externalDownload);
        externalDownloadCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                MainModel.externalDownload = b;

                editor.putBoolean(MainModel.EXTERNAL_DOWNLOAD_PREF, b);
                editor.apply();

                externalDownloadSummary.setText(yesNoSummaryUpdate(b));
            }
        });
        externalDownloadItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                externalDownloadCheckBox.toggle();
            }
        });

        //RelativeLayout downloadLocationItem = (RelativeLayout) view.findViewById(R.id.download_location_item);
        // downloadLocationSummary = (TextView) downloadLocationItem.findViewById(R.id.preference_summary_text);
        // downloadLocationFilePath = getSummary(DOWNLOAD_LOCATION_PREFERENCE);
        // downloadLocationSummary.setText(downloadLocationFilePath);
        // downloadLocationItem.setOnClickListener(new View.OnClickListener() {
        //  @Override
        //  public void onClick(View v) {
        // Intent i = new Intent(getContext(), FilePickerActivity.class);

        //   i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
        // i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        //  i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);
        //  i.putExtra(FilePickerActivity.EXTRA_START_PATH, downloadLocationFilePath);

        //  startActivityForResult(i, DOWNLOAD_LOCATION_REQUEST_CODE);
        //  }
        // });


        RelativeLayout openToLastAnimeItem = (RelativeLayout) view.findViewById(R.id.last_viewed_anime);
        CheckBox openToLastAnimeCheckBox = (CheckBox) openToLastAnimeItem.findViewById(R.id.preference_check_box);
        openToLastAnimeCheckBox.setClickable(true);
        TextView openToLastAnimeSummary = (TextView) openToLastAnimeItem.findViewById(R.id.preference_summary_text);
        openToLastAnimeSummary.setText(yesNoSummaryUpdate(MainModel.openToLastAnime));
        openToLastAnimeCheckBox.setChecked(MainModel.openToLastAnime);
        openToLastAnimeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                MainModel.openToLastAnime = b;

                editor.putBoolean(MainModel.OPEN_TO_LAST_ANIME_PREF, b);
                editor.apply();

                openToLastAnimeSummary.setText(yesNoSummaryUpdate(b));
            }
        });

        openToLastAnimeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openToLastAnimeCheckBox.toggle();
            }
        });

    RelativeLayout licencesItem = (RelativeLayout) view.findViewById(R.id.licences_preference_item);
    licencesItem.setOnClickListener(new View.OnClickListener()

    {
        @Override
       public void onClick (View v){
        new MaterialDialog.Builder(getActivity())
                //.title(getString(R.string.licences_preference_summary))
                .content(R.string.licences)
                .show();

    }
   }

    );

        //boolean shouldAutoUpdateVal = sharedPreferences.getBoolean(MainModel.AUTO_UPDATE_PREF, true);
        //RelativeLayout autoUpdateItem = (RelativeLayout) view.findViewById(R.id.auto_update_preference_item);
        //CheckBox autoUpdateCheckBox = (CheckBox) autoUpdateItem.findViewById(R.id.preference_check_box);
        //((TextView) autoUpdateItem.findViewById(R.id.preference_summary_text))
        //.setText("Current version: " + BuildConfig.VERSION_NAME);
        //autoUpdateCheckBox.setChecked(shouldAutoUpdateVal);
        // autoUpdateCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        // @Override
        // public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        // editor.putBoolean(MainModel.AUTO_UPDATE_PREF, b);
        //  editor.apply();
        // }
        //   });



           RelativeLayout contactItem = (RelativeLayout) view.findViewById(R.id.contact_preference_item);
           contactItem.setOnClickListener(new View.OnClickListener() {
           @Override
          public void onClick(View v) {
            String[] emails = new String[1];
            emails[0] = "yoanimefeedback@gmail.com";
           Intent contactIntent = new Intent(Intent.ACTION_SEND);
           contactIntent.setType("text/plain");
           contactIntent.putExtra(Intent.EXTRA_EMAIL, emails);
         contactIntent.putExtra(Intent.EXTRA_SUBJECT, "YoAnime! Feedback");
          if (contactIntent.resolveActivity(getActivity().getPackageManager()) != null) {
             startActivity(contactIntent);
          }
          }
         });



        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        //setToolbarTitle(getString(R.string.settings_item));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem searchItem = menu.findItem(R.id.search_item);

        if (searchItem != null) {
            menu.setGroupVisible(searchItem.getGroupId(), false);
        }

    }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
               if (requestCode == DOWNLOAD_LOCATION_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
                        downloadLocationFilePath = data.getData().getPath();
                        downloadLocationSummary.setText(downloadLocationFilePath);
                        editor.putString(DOWNLOAD_LOCATION_PREFERENCE, downloadLocationFilePath);
                        editor.apply();
                    }
            }


    public void setToolbarTitle (String title) {
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(title);
    }

    private String getSummary (String key) {
        if (key.equals(THEME_PREFERENCE)) {
            int themePref = sharedPreferences.getInt(THEME_PREFERENCE, 0);
            switch (themePref) {
               // case 1:
                   // return getActivity().getApplicationContext().getString(R.string.yo_light_theme);

                //case 2:
                    //return getActivity().getApplicationContext().getString(R.string.yo_red_theme_light);

                case 1:
                    return getActivity().getApplicationContext().getString(R.string.yo_red_theme_dark);

                //case 4:
                    //return getActivity().getApplicationContext().getString(R.string.yo_green_theme_light);

                case 2:
                    return getActivity().getApplicationContext().getString(R.string.yo_green_theme_dark);



                case 3:
                    return getActivity().getApplicationContext().getString(R.string.yo_pink_theme_dark);

               // case 8:
                  //  return getActivity().getApplicationContext().getString(R.string.yo_deep_purple_theme_light);

                 case 4:
                 return getActivity().getApplicationContext().getString(R.string.yo_orange_theme_dark);

                case 5:
                    return getActivity().getApplicationContext().getString(R.string.yo_purple_theme_dark);

                case 6:
                    return getActivity().getApplicationContext().getString(R.string.yo_cyan_theme_dark);

                case 7:
                    return getActivity().getApplicationContext().getString(R.string.yo_teal_theme_dark);

                case 8:
                    return getActivity().getApplicationContext().getString(R.string.yo_brown_theme_dark);

                case 9:
                    return getActivity().getApplicationContext().getString(R.string.yo_blue_grey_theme_dark);



                case 10:
                    return getActivity().getApplicationContext().getString(R.string.yo_deep_orange_theme_dark);



                case 11:
                    return getActivity().getApplicationContext().getString(R.string.yo_deep_purple_theme_dark);


                default:
                    return getActivity().getApplicationContext().getString(R.string.yo_dark_theme);


            }
        } else if (key.equals(SEARCH_GRID_PREFERENCE)) {
            int searchGridPref = sharedPreferences.getInt(SEARCH_GRID_PREFERENCE, 0);
            return searchGridSummaryUpdate(searchGridPref);
        }

         else if (key.equals(DOWNLOAD_LOCATION_PREFERENCE)) {

            String defaultDownloadLocationFilePath = Environment.getExternalStoragePublicDirectory("/YoAnime").getPath();
            return sharedPreferences.getString(DOWNLOAD_LOCATION_PREFERENCE, defaultDownloadLocationFilePath);


        }


        return null;
    }

    private String searchGridSummaryUpdate (int i) {
        switch (i) {
            case 0:
                return getString(R.string.search_grid);
            //case 1:
            //return getString(R.string.search_grid_option_card);
            default:
                return getString(R.string.search_grid);
        }
    }

    private String yesNoSummaryUpdate (boolean bool) {
        if (bool) {
            return getString(R.string.yes);
        } else {
            return getString(R.string.no);
        }
    }

}
