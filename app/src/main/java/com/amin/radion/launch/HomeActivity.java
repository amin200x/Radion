package com.amin.radion.launch;

import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amin.radion.R;
import com.amin.radion.constant.Country;
import com.amin.radion.entity.Station;
import com.amin.radion.utilti.CountryAdapter;
import com.amin.radion.utilti.CustomAdapter;
import com.amin.radion.utilti.HistoryFavorite;
import com.amin.radion.utilti.LoadJSonFromAsset;
import com.amin.radion.utilti.StationAdapter;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HomeActivity extends AppCompatActivity {
    public static  final  String  HISTORY = "history";
    public static  final  String  FAVORITE = "favorite";
    private enum TabType {STATION, COUNTRY, LANGUAGE, GENRE, History_Favorite};
    public static final String Broadcast_PLAY_NEW_STATION = "com.amin.radion.PlayNewStation";
    private String searchValue = "";
    private String searchKey = "";
    private StationAdapter stationAdapter;
    private ListView listView;
    private RelativeLayout progressbarRelativeLayout;
    private static ImageButton playImageButton;
    private String stationAddress;
    private LinearLayout playLinearLayout;
    private static ProgressBar stationLoadingProgressbar;
    private RadioService radioService;
    private boolean serviceBound = false;
    private Map<String, Integer> mapGenre;
    private Map<Country, Integer> mapCountry;
    private List<Station> stations;
    private List<Station> allSelectedStations;
    private ImageButton imageButtonStation;
    private ImageButton imageButtonLanguage;
    private ImageButton imageButtonCountry;
    private ImageButton imageButtonGenre;
    private TabType tabType = TabType.STATION;
    private CountryAdapter countryAdapter;
    private CustomAdapter customAdapter;
    private Map<String, Integer> mapLanguage;
    private static Station selectedStation;
    private  JSONArray stationJSonArray;
    private Map<String, Integer> subMapLanguage;
    private Map<Country, Integer> subMapCountry;
    private EditText editTextSearch;
    private LinearLayout linearLayoutMenu;
    private RelativeLayout relativeLayoutGif;
    //Main Player page objects
    private static ImageButton playImageButton2;
    private LinearLayout mainPlayPageLinearLayout;
    private ListView recenStationstListView;
    private List<Station> recentlyPlayedList;
    private static ProgressBar stationLoadingProgressbar2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_home);
        listView = findViewById(R.id.stationListView);
        progressbarRelativeLayout = findViewById(R.id.progressbarRelativeLayout);
        playImageButton = findViewById(R.id.playImageButton);
        playLinearLayout = findViewById(R.id.playerLinearLayout);
        stationLoadingProgressbar = findViewById(R.id.stationLoadingProgressBar);
        imageButtonStation = findViewById(R.id.imageButtonStation);
        imageButtonLanguage = findViewById(R.id.imageButtonLanguage);
        imageButtonCountry = findViewById(R.id.imageButtonCountry);
        imageButtonGenre = findViewById(R.id.imageButtonGenre);
        editTextSearch = findViewById(R.id.editTextSearch);
        linearLayoutMenu = findViewById(R.id.linearLayoutMenu);
        relativeLayoutGif =  findViewById(R.id.radionGifRelativeLayout);
        // Main Play Page objects
        playImageButton2 = findViewById(R.id.playimageButton2);
        recenStationstListView = findViewById(R.id.recentStaionListView);
        mainPlayPageLinearLayout = findViewById(R.id.mainPlayPageLinearLayout);
        stationLoadingProgressbar2 = findViewById(R.id.stationLoadingProgressBar2);

        editTextSearch.setVisibility(View.GONE);
        playLinearLayout.setVisibility(View.GONE);
        linearLayoutMenu.setVisibility(View.GONE);
        mainPlayPageLinearLayout.setVisibility(View.GONE);

        //Hiding keypad when app loaded
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);

        // loadin all stations from stations.json before using app
        new Thread(() ->
            stationJSonArray = new LoadJSonFromAsset(HomeActivity.this).loadStationFile()
        ).start();

        // Loading stations from recently if is not from favorite and id is not go to country tab and
        //- select stations from country
        allSelectedStations = new HistoryFavorite(this, HISTORY).getStations();
        if (allSelectedStations.size() == 0){
            allSelectedStations = new HistoryFavorite(this, FAVORITE).getStations();
            if (allSelectedStations.size() == 0)
                goCountryTab();
            else {
                stations = allSelectedStations;
                showInList(stations);
                changeTabColor(TabType.STATION);
            }
        } else {
            stations = allSelectedStations;
            showInList(stations);
            changeTabColor(TabType.STATION);
        }

        // searching from listview by text changing TextView
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                String value = s.toString().trim().toLowerCase();
                switch (tabType){
                    case STATION:
                        /*stations= allSelectedStations.stream()
                                        .filter(o -> o.getName().toLowerCase().contains(value))
                                        .collect(Collectors.toList());*/
                        stations = new ArrayList<>();
                        for (Station station : allSelectedStations){
                            if (station.getName().toLowerCase().contains(value)){
                                stations.add(station);
                            }
                        }
                        showInList(stations);
                        break;

                    case LANGUAGE:
                       /* subMapLanguage = mapLanguage.entrySet().stream()
                                .filter( o -> o.getKey().toLowerCase().contains(value))
                                .sorted(Comparator.comparing( o-> o.getKey()))
                                .collect(Collectors.toMap(e -> e.getKey(), e-> e.getValue(), (e1, e2) -> e1, LinkedHashMap::new));
                        customAdapter = new CustomAdapter(HomeActivity.this, subMapLanguage);*/

                        subMapLanguage = new LinkedHashMap<>();
                        for (Map.Entry<String, Integer> obj : mapLanguage.entrySet()){
                            if (obj.getKey().toLowerCase().contains(value)){
                                subMapLanguage.put(obj.getKey(), obj.getValue());
                            }
                        }

                        customAdapter = new CustomAdapter(HomeActivity.this, subMapLanguage);
                        listView.setAdapter(customAdapter);

                        break;

                    case COUNTRY:
                           /*subMapCountry = mapCountry.entrySet().stream()
                                .filter(o -> o.getKey().toString().replace("_", " ").toLowerCase().contains(value))
                                .sorted(Comparator.comparing(o -> o.getKey().toString()))
                                .collect(Collectors.toMap(e-> e.getKey(), e->e.getValue(), (e1, e2) -> e1, LinkedHashMap::new));
                                    */
                            subMapCountry = new LinkedHashMap<>();
                            for (Map.Entry<Country, Integer> obj : mapCountry.entrySet()){
                             if (obj.getKey().toString().replace("_", " ").toLowerCase().contains(value)
                             || obj.getKey().getFarsiName().contains(value)){
                                 subMapCountry.put(obj.getKey(), obj.getValue());
                             }
                         }
                        countryAdapter = new CountryAdapter(HomeActivity.this, subMapCountry);
                        listView.setAdapter(countryAdapter);
                        break;

                    case GENRE:
                        break;

                        default:
                             /*stations = allSelectedStations.stream()
                                    .filter(o -> o.getName().toLowerCase().contains(value))
                                    .collect(Collectors.toList());*/
                            stations = new ArrayList<>();
                            for (Station station : allSelectedStations){
                                if (station.getName().toLowerCase().contains(value)){
                                    stations.add(station);
                                }
                            }
                            showInList(stations);
                            break;

                }            }
        });
        // Countries loading tab
        imageButtonCountry.setOnClickListener((v) ->{
            editTextSearch.setText("");
            goCountryTab();


        });
        // Languages loading tab
        imageButtonLanguage.setOnClickListener((v) ->{
           if (tabType != TabType.LANGUAGE) {
               editTextSearch.setText("");
               tabType = TabType.LANGUAGE;
               changeTabColor(tabType);
               progressbarRelativeLayout.setVisibility(View.VISIBLE);
               if (mapLanguage != null) {
                   customAdapter = new CustomAdapter(HomeActivity.this, mapLanguage);
                   listView.setAdapter(customAdapter);
                   customAdapter.notifyDataSetChanged();
                   progressbarRelativeLayout.setVisibility(View.INVISIBLE);
               } else {
                   Thread languageThread = new Thread(new GetLanguages());
                   languageThread.start();

               }
           }
        });
        // Stations loading tab
        imageButtonStation.setOnClickListener((v) ->{
            if (tabType != TabType.STATION && stations != null) {
                editTextSearch.setText("");
                tabType = TabType.STATION;
                changeTabColor(tabType);
                progressbarRelativeLayout.setVisibility(View.VISIBLE);
                if (!searchValue.isEmpty() && !searchKey.isEmpty()) {
                    Thread stationThread = new Thread(new GetStationJSonJob());
                    stationThread.start();
                }else {
                    if (stations != null)
                         showInList(stations);
                }

            }
        });
        // Stations count and kind loading tab
        imageButtonGenre.setOnClickListener((v) ->{
            editTextSearch.setVisibility(View.GONE);
            if (tabType != TabType.GENRE) {
                editTextSearch.setText("");
                tabType = TabType.GENRE;
                changeTabColor(tabType);
                progressbarRelativeLayout.setVisibility(View.VISIBLE);
                Thread genreThread = new Thread(new GetGenres());
                 genreThread.start();


            }
        });

        // Radio play button invisible at first time
        playImageButton.setVisibility(View.INVISIBLE);

        // play and pause radio if is playing
        playImageButton.setOnClickListener((view) ->  playPause((ImageButton) view));
        playImageButton2.setOnClickListener((view) ->  playPause((ImageButton) view));

        // loading stations from country, language and playing radion_anim by clicking listview item
        listView.setOnItemClickListener((parent, view, position, id) -> {
            {
                im.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
                switch (tabType){

                    case STATION:
                    case History_Favorite:
                        selectedStation = stations.get(position);
                        playLinearLayout.setVisibility(View.VISIBLE);
                        playImageButton.setImageResource(android.R.drawable.ic_media_play);
                        stationAddress = selectedStation.getUrl();
                        if (!stationAddress.isEmpty()) {
                            playRadio(stationAddress);
                            ImageView  logo  = view.findViewById(R.id.logoImageView);
                            setPlayersObjects(selectedStation, logo);
                        }
                        break;

                    case COUNTRY:
                        listView.setAdapter(null);
                        progressbarRelativeLayout.setVisibility(View.VISIBLE);
                        List<Country> list  ;
                         if (subMapCountry != null){
                             list = new ArrayList<>(subMapCountry.keySet());
                         }else {
                           list = new ArrayList<>(mapCountry.keySet());
                        }
                        searchValue = list.get(position).getAbbreviation();
                        searchKey = "countrycode";
                        Thread stationWorker1 = new Thread(new GetStationJSonJob());
                        stationWorker1.start();
                        editTextSearch.setText("");
                        tabType = TabType.STATION;
                        subMapCountry = null;
                        changeTabColor(tabType);
                        break;

                      case GENRE:
                        editTextSearch.setVisibility(View.GONE);
                        if (mapGenre != null){
                            listView.setAdapter(null);
                            progressbarRelativeLayout.setVisibility(View.VISIBLE);
                            List<String> list2 = new ArrayList<>(mapGenre.keySet());
                            String value = list2.get(position).toLowerCase();
                            if (value.equalsIgnoreCase("all")){
                                stations = allSelectedStations;
                                showInList(stations);

                            }else {
                                /*stations = allSelectedStations.stream()
                                        .filter(o -> o.getTag().toLowerCase().contains(value))
                                        .collect(Collectors.toList());
                                        */
                                stations = new ArrayList<>();
                                for (Station station : allSelectedStations){
                                    if (station.getTag().toLowerCase().contains(value)){
                                        stations.add(station);
                                    }
                                }
                                showInList(stations);
                            }

                        }
                        tabType = TabType.STATION;
                        changeTabColor(tabType);
                        break;

                    case LANGUAGE:
                        progressbarRelativeLayout.setVisibility(View.VISIBLE);
                        listView.setAdapter(null);
                        List<String> list1;
                        if (subMapLanguage != null){
                           list1 = new ArrayList<>(subMapLanguage.keySet());
                        }
                        else {
                            list1 = new ArrayList<>(mapLanguage.keySet());
                            }

                        searchValue = list1.get(position);
                        searchKey = "language";
                        stationWorker1 = new Thread(new GetStationJSonJob());
                        stationWorker1.start();
                        editTextSearch.setText("");
                        subMapLanguage = null;
                        tabType = TabType.STATION;
                        changeTabColor(tabType);

                        break;
                }


            }
        });
        recenStationstListView.setOnItemClickListener((parent, view, position, id)->{
            selectedStation = recentlyPlayedList.get(position);
            ImageView  logo  = view.findViewById(R.id.logoImageView);
            stationAddress = selectedStation.getUrl();
           if (!stationAddress.isEmpty()) {
               playRadio(stationAddress);
               playImageButton2.setImageResource(android.R.drawable.ic_media_play);
               setPlayersObjects(selectedStation, logo);

           }

        });
        appStart();

    }

    private void playPause(ImageButton imageButton) {
        if (radioService.mediaPlayer.isPlaying()){
            radioService.mediaPlayer.pause();
            imageButton.setImageResource(android.R.drawable.ic_media_play);
        }else if (! radioService.mediaPlayer.isPlaying()){
            radioService.mediaPlayer.start();
            imageButton.setImageResource(android.R.drawable.ic_media_pause);
        }
    }

    private void setPlayersObjects(Station station, ImageView logo) {

        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(station.getName())
                .append("\nLanguage: ").append(station.getLanguage())
                .append("\nFormat: ").append(station.getCodec()).append(" - ").append(station.getBitrate())
                .append("\nType: ").append(station.getTag());
        ((TextView)findViewById(R.id.textViewStationDetails)).setText(sb.toString());
        ((TextView)findViewById(R.id.selectedStationNameTextView)).setText(station.getName());

        ImageView  logoImgView  = findViewById(R.id.logoImgView);
        ImageView  logoImgViewMainPage  = findViewById(R.id.imageViewPlayPageLogo);
        if (logo.getDrawable() != null) {
            logoImgView.setImageBitmap(((BitmapDrawable) logo.getDrawable()).getBitmap());
            logoImgViewMainPage.setImageBitmap(((BitmapDrawable) logo.getDrawable()).getBitmap());
        }else {
            logoImgView.setImageResource(R.drawable.no_logo);
            logoImgViewMainPage.setImageResource(R.drawable.no_logo);
        }
        stationLoadingProgressbar.setVisibility(View.VISIBLE);
        playImageButton.setVisibility(View.GONE);

        playImageButton2.setVisibility(View.GONE);
        stationLoadingProgressbar2.setVisibility(View.VISIBLE);
    }

    public void showMainPlayePage(View view) {
        recentlyPlayedList = new HistoryFavorite(this, HISTORY).getStations();
        if (recentlyPlayedList.size() > 0 || radioService.mediaPlayer.isPlaying()) {
            StationAdapter stationAdapter = new StationAdapter(this, recentlyPlayedList);
            recenStationstListView.setAdapter(stationAdapter);
            linearLayoutMenu.setVisibility(View.GONE);
             playLinearLayout.setVisibility(View.GONE);
             mainPlayPageLinearLayout.setVisibility(View.VISIBLE);
        }
    }
    // Playing gif animation and cheking if network is connected at starting app
    private void appStart(){

        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                goNetDialogIfNoConnection();
            }
        }).start();
    }

	private void goNetDialogIfNoConnection()	{
		if (isNetworkConnected()) {
                    runOnUiThread(() -> {
                        // progressbarRelativeLayout.setVisibility(View.VISIBLE);
                        linearLayoutMenu.setVisibility(View.VISIBLE);
                        relativeLayoutGif.setVisibility(View.GONE);
                    });

                }else {
                    runOnUiThread(() -> showNetworkDialog());
                }
			}
    // dialog showing if network is not connected
    private void showNetworkDialog(){

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
            alertDialog.setTitle("Internet Connectivity")
                  .setMessage("You must have internet connection to use this app!")
                    .setCancelable(false)
                  .setPositiveButton("Settings", (dialog,  which) -> {
                 {
                    Intent i = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                    startActivityForResult(i, 1);
                     //appStart();
                }
            })
            .setNegativeButton("Exit", (dialog, which) ->{
             finish();
              System.exit(0);
            });
            alertDialog.show();



    }

    // after activing or back button pressed after going network settings
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RESULT_CANCELED here means back button is clicked
        if (resultCode == RESULT_CANCELED){
            goNetDialogIfNoConnection();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager  = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }
    private void goCountryTab() {
        if (tabType != TabType.COUNTRY) {
            tabType = TabType.COUNTRY;
            changeTabColor(tabType);
            progressbarRelativeLayout.setVisibility(View.VISIBLE);
            if (mapCountry != null) {
                countryAdapter = new CountryAdapter(HomeActivity.this, mapCountry);
                listView.setAdapter(countryAdapter);
                countryAdapter.notifyDataSetChanged();
                progressbarRelativeLayout.setVisibility(View.INVISIBLE);
            } else {
                Thread countryThread = new Thread(new GetCountries());
                countryThread.start();

            }
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            RadioService.LocalBinder binder = (RadioService.LocalBinder) service;
            radioService = binder.getService();
            serviceBound = true;
            Toast.makeText(HomeActivity.this, "Service Ready to Play!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    private void playRadio(String stationUrl){

        if (!serviceBound){
            Intent radioPlayerIntent = new Intent(this, RadioService.class);

            radioPlayerIntent.putExtra("stationUrl", stationUrl);
            startService(radioPlayerIntent);
            bindService(radioPlayerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }else {

            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_STATION);
            broadcastIntent.putExtra("stationUrl", stationUrl);
            sendBroadcast(broadcastIntent);
        }

    }
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound){
            unbindService(serviceConnection);
            radioService.stopSelf();
        }
    }

    class GetStationJSonJob implements Runnable{
        @Override
        public void run() {
          allSelectedStations = new  LoadJSonFromAsset(HomeActivity.this).getStationsJSonObjects(stationJSonArray, searchKey, searchValue);
            runOnUiThread(() -> {
                        {
                            stations = allSelectedStations;
                            showInList(stations);
                        }

                    });
                }
        }


    @Override
    public void onBackPressed() {
        if (mainPlayPageLinearLayout.getVisibility() == View.VISIBLE){
            linearLayoutMenu.setVisibility(View.VISIBLE);
            playLinearLayout.setVisibility(View.VISIBLE);
            mainPlayPageLinearLayout.setVisibility(View.GONE);
        }else {
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_favorite:
                if(mainPlayPageLinearLayout.getVisibility() == View.VISIBLE) break;
                tabType = TabType.History_Favorite;
                changeTabColor(tabType);
                allSelectedStations = new HistoryFavorite(HomeActivity.this, FAVORITE).getStations();
                stations = allSelectedStations;
                showInList(stations);
                break;
            case R.id.action_recently:
                if(mainPlayPageLinearLayout.getVisibility() == View.VISIBLE) break;
                tabType = TabType.History_Favorite;
                changeTabColor(tabType);
                allSelectedStations = new HistoryFavorite(HomeActivity.this, HISTORY).getStations();
                stations = allSelectedStations;
                showInList(stations);
                break;
            case R.id.action_search:
                if (editTextSearch.getVisibility() == View.VISIBLE) {
                    editTextSearch.setVisibility(View.GONE);
                    InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    im.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
                }else {
                    editTextSearch.setVisibility(View.VISIBLE);
                }
                if (tabType == TabType.GENRE)
                    editTextSearch.setVisibility(View.GONE);

                break;
            case R.id.action_about:
                showAboutDialog();
                break;
                default:
                    break;
        }

        return super.onOptionsItemSelected(item);
    }

    class  GetGenres implements Runnable{
       @Override
        public void run() {

            mapGenre = new LinkedHashMap<>();
            String[] genreArray = {"all", "dance", "rock", "jazz", "dubstep", "blues", "techno", "country", "electron", "indie", "pop", "chillout", "news", "talk", "adult", "classic", "sport"};
            mapGenre.put(genreArray[0].toUpperCase(), allSelectedStations.size());
            for (int j = 1; j < genreArray.length; j++) {
                int count = 0;
                for (Station station : allSelectedStations) {
                     if (station.getTag().toLowerCase().contains(genreArray[j])) {
                         count ++;
                             }
                }
                if (count != 0)
                    mapGenre.put(genreArray[j].toUpperCase(), count);

            }
            runOnUiThread(() -> {
                {
                    /*mapGenre =  mapGenre.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                            .collect(Collectors.toMap(e -> e.getKey(), e-> e.getValue(), (e1, e2) -> e1, LinkedHashMap::new));
                    */

                    customAdapter = new CustomAdapter(HomeActivity.this, mapGenre);
                    listView.setAdapter(customAdapter);
                    progressbarRelativeLayout.setVisibility(View.INVISIBLE);

                }
            });
        }

    }

    private void showInList(List<Station> stationList) {

        stationAdapter = new StationAdapter(HomeActivity.this, stationList);
        listView.setAdapter(stationAdapter);
        progressbarRelativeLayout.setVisibility(View.INVISIBLE);
    }


   private class GetCountries implements Runnable {
        @Override
        public void run() {
            mapCountry = new LoadJSonFromAsset(HomeActivity.this).getCountryJSonObjects(LoadJSonFromAsset.COUNTRY_CODE);

            runOnUiThread(() -> {
                {
                   /* mapCountry =  mapCountry.entrySet().stream().sorted(Comparator.comparing(o -> o.getKey().toString()))
                            .collect(Collectors.toMap(e -> e.getKey(), e-> e.getValue(), (e1, e2) -> e1, LinkedHashMap::new));
                    */
                    countryAdapter = new CountryAdapter(HomeActivity.this, mapCountry);
                    listView.setAdapter(countryAdapter);
                    countryAdapter.notifyDataSetChanged();
                    progressbarRelativeLayout.setVisibility(View.INVISIBLE);

                }
            });
        }
    }
 private  class  GetLanguages implements Runnable{

        @Override
        public void run() {

            mapLanguage= new LoadJSonFromAsset(HomeActivity.this).getLanguageJSonObjects(LoadJSonFromAsset.LANGUAGE);
            runOnUiThread(() -> {
                {
                    customAdapter = new CustomAdapter(HomeActivity.this, mapLanguage);
                    listView.setAdapter(customAdapter);
                    customAdapter.notifyDataSetChanged();
                    progressbarRelativeLayout.setVisibility(View.INVISIBLE);

                }
            });

        }

    }
    private void changeTabColor(TabType tabType){
        switch (tabType){

            case GENRE:
                imageButtonStation.setImageResource(R.drawable.grey_radio);
                imageButtonCountry.setImageResource(R.drawable.grey_flag);
                imageButtonLanguage.setImageResource(R.drawable.grey_language);
                imageButtonGenre.setImageResource(R.drawable.genre);
                break;

            case COUNTRY:
                imageButtonStation.setImageResource(R.drawable.grey_radio);
                imageButtonCountry.setImageResource(R.drawable.green_flag);
                imageButtonLanguage.setImageResource(R.drawable.grey_language);
                imageButtonGenre.setImageResource(R.drawable.grey_genre);
                break;

            case STATION:
            case History_Favorite:
                imageButtonStation.setImageResource(R.drawable.yello_radio);
                imageButtonCountry.setImageResource(R.drawable.grey_flag);
                imageButtonLanguage.setImageResource(R.drawable.grey_language);
                imageButtonGenre.setImageResource(R.drawable.grey_genre);
                break;

            case LANGUAGE:
                imageButtonStation.setImageResource(R.drawable.grey_radio);
                imageButtonCountry.setImageResource(R.drawable.grey_flag);
                imageButtonLanguage.setImageResource(R.drawable.green_language);
                imageButtonGenre.setImageResource(R.drawable.grey_genre);
                break;


        }

    }
    private void showAboutDialog(){
       AlertDialog.Builder aboutDialog = new  AlertDialog.Builder(this);
       TextView contentTextView = new TextView(this);
       ImageView imageView = new ImageView(this);
       LinearLayout linearLayout = new LinearLayout(this);
       linearLayout.setOrientation(LinearLayout.VERTICAL);
       linearLayout.setGravity(Gravity.CENTER);
       imageView.setImageResource(R.drawable.about);
       contentTextView.setTextSize( 20f);
       contentTextView.setGravity(Gravity.CENTER);
       contentTextView.setText("Radion is Developed in 2019 \n\t Developer:  A.Ghadimian \n\tVersion: 1.0\nStations Source: www.radio-browser.info\n I Hope You Enjoy It :)\n");
       linearLayout.addView(imageView);
       linearLayout.addView(contentTextView);

       aboutDialog.setView(linearLayout)

               .setTitle("About Radion")
               .setCancelable(false)
               .setPositiveButton("OK", (dialog,  which) -> dialog.dismiss()).show();


    }

    public static class RadioService extends Service implements MediaPlayer.OnCompletionListener,
            MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
            MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener,

            AudioManager.OnAudioFocusChangeListener {

        private IBinder iBinder = new LocalBinder();
        private MediaPlayer mediaPlayer;
        private String stationUrl;
        private AudioManager audioManager;
        private boolean ongoingCall = false;
        private PhoneStateListener phoneStateListener;
        private TelephonyManager telephonyManager;
        public RadioService(){super();}
        private BroadcastReceiver playNewStation = new BroadcastReceiver() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onReceive(Context context, Intent intent) {
                stationUrl = intent.getStringExtra("stationUrl");
                if (stationUrl.isEmpty())
                    stopSelf();

                stopMedia();
                mediaPlayer.reset();
                initialMediaPlayer();

            }
        };

        @Override
        public void onCreate() {
            // Perform one-time setup procedures

            // Manage incoming phone calls during playback.
            // Pause MediaPlayer on incoming call,
            // Resume on hangup.
            callStateListener();
            //ACTION_AUDIO_BECOMING_NOISY -- change in audio outputs -- BroadcastReceiver
            registerBecomingNoisyReciver();
            //Listen for new Audio to play -- BroadcastReceiver
            register_playNewAudio();
        }

        private void register_playNewAudio() {
            //Register playNewMedia receiver
            IntentFilter filter = new IntentFilter(HomeActivity.Broadcast_PLAY_NEW_STATION);
            registerReceiver(playNewStation, filter);
        }
        private void  callStateListener(){
            telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            phoneStateListener = new PhoneStateListener(){
                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    switch (state){
                        case TelephonyManager.CALL_STATE_OFFHOOK:
                        case TelephonyManager.CALL_STATE_RINGING:
                            if (mediaPlayer != null){
                                pauseMedia();
                                ongoingCall = true;

                            }
                            break;
                        case TelephonyManager.CALL_STATE_IDLE:
                            if (mediaPlayer != null){
                                ongoingCall = false;
                                resumeMedia();
                            }
                            break;
                    }
                    super.onCallStateChanged(state, incomingNumber);
                }
            };
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
        private BroadcastReceiver becomingNoisyReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                pauseMedia();
            }
        };

        private void registerBecomingNoisyReciver(){
            IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
            registerReceiver(becomingNoisyReciver, intentFilter);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onAudioFocusChange(int focusState) {
            //Invoked when the audio focus of the system is updated.
            switch (focusState) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    // resume playback
                    if (mediaPlayer == null) initialMediaPlayer();
                    else if (!mediaPlayer.isPlaying()) mediaPlayer.start();
                    mediaPlayer.setVolume(1.0f, 1.0f);
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    // Lost focus for an unbounded amount of time: stop playback and release media player
                    if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    // Lost focus for a short time, but we have to stop
                    // playback. We don't release the media player because playback
                    // is likely to resume
                    if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    // Lost focus for a short time, but it's ok to keep playing
                    // at an attenuated level
                    if (mediaPlayer.isPlaying()) mediaPlayer.setVolume(0.1f, 0.1f);
                    break;
            }
        }

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {

        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            stopMedia();
            stopSelf();
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            switch (what) {
                case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                    Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
                    break;
                case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                    Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + extra);
                    break;
                case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                    Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
                    break;
            }
            return false;


        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            try {
                //An audio file is passed to the service through putExtra();
                stationUrl = intent.getExtras().getString("stationUrl");
            } catch (NullPointerException e) {
                stopSelf();
            }
            //Request audio focus
            if (requestAudioFocus()) {
                //Could not gain focus
                stopSelf();
            }

            if (! stationUrl.isEmpty())
                initialMediaPlayer();

            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            return false;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onPrepared(MediaPlayer mp) {
            playMedia();
            playImageButton.setImageResource(android.R.drawable.ic_media_pause);
            playImageButton2.setImageResource(android.R.drawable.ic_media_pause);
            stationLoadingProgressbar.setVisibility(View.GONE);
            playImageButton.setVisibility(View.VISIBLE);
            playImageButton2.setVisibility(View.VISIBLE);
            stationLoadingProgressbar2.setVisibility(View.GONE);
            new HistoryFavorite(this, HISTORY).addToList(selectedStation);

        }

        @Override
        public void onSeekComplete(MediaPlayer mp) {

        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return iBinder;
        }

        public class LocalBinder extends Binder {
            public RadioService getService() {
                return  RadioService.this;
            }
        }
        @Override
        public void onDestroy() {
            super.onDestroy();
            if (mediaPlayer != null) {
                stopMedia();
                mediaPlayer.release();
            }
            removeAudioFocus();
            if (phoneStateListener != null) {
                telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
            }
            //unregister BroadcastReceivers
            unregisterReceiver(becomingNoisyReciver);
            unregisterReceiver(playNewStation);

            //clear cached playlist

        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
         private void initialMediaPlayer(){
            mediaPlayer =  new MediaPlayer();
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnSeekCompleteListener(this);
            mediaPlayer.setOnInfoListener(this);
            mediaPlayer.reset();

          // mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            mediaPlayer.setAudioAttributes(audioAttributes);
            try {
                mediaPlayer.setDataSource(stationUrl);
                mediaPlayer.prepareAsync();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        private void playMedia(){
            if (!mediaPlayer.isPlaying()) {
               mediaPlayer.start();
            }
        }
        private void stopMedia(){
            if (mediaPlayer == null) return;
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
        }
        private void pauseMedia(){
            if (mediaPlayer.isPlaying()) mediaPlayer.pause();
        }
        private void resumeMedia(){
            if (! mediaPlayer.isPlaying()) mediaPlayer.start();
        }
        private boolean requestAudioFocus() {
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            //Focus gained
            return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
            //Could not gain focus
        }
        private boolean removeAudioFocus() {
            return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                  audioManager.abandonAudioFocus(this);
        }
    }


}
