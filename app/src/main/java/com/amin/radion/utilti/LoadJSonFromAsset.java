package com.amin.radion.utilti;

import android.content.Context;

import com.amin.radion.constant.Country;
import com.amin.radion.constant.Language;
import com.amin.radion.entity.Station;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

// allocation tracker tab, heap tab, memory monitor
public class LoadJSonFromAsset {
    public static final String STATION = "stations";
    public static final String COUNTRY_CODE = "countrycodes";
    public static final String LANGUAGE = "languages";
    private Context context;

    public LoadJSonFromAsset(Context context) {
        this.context = context;

    }

    private final JSONArray loadJSONFromAsset(String filename) {
        final String strFileContent;

        JSONArray jsonArray = new JSONArray();

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(context.getAssets().open(filename + ".json"))) {

            byte[] buffer = new byte[bufferedInputStream.available()];
            bufferedInputStream.read(buffer, 0, buffer.length);

            strFileContent = new String(buffer, "UTF-8");
            jsonArray = new JSONArray(strFileContent);

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;

    }

    public JSONArray loadStationFile() {
        JSONArray jsonArray;
        jsonArray = loadJSONFromAsset(STATION);

        return jsonArray;
    }

    public final List<Station> getStationsJSonObjects(JSONArray stationJSonArray, String strSerachKey, String value) {

        // final JSONArray stationJSonArray =  loadJSONFromAsset(filename);
     /* final List<JSONObject> jsonObjectList;

        jsonObjectList = IntStream.range(0, stationJSonArray.length())
                        .mapToObj(i -> {
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = stationJSonArray.getJSONObject(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }finally {
                                return jsonObject;
                            }

                        }).filter(o -> {
                        boolean isConatins = false;

                        try {
                            isConatins = o.getString(strSerachKey).toLowerCase().contains(value.toLowerCase());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }finally {
                            return isConatins;
                        }

                    }).collect(Collectors.toList());
                     jsonObjectList.stream().forEach(o ->{
              Station station;

                    station = new Station();
            try {
                station.setName(o.getString("name"));
                station.setLanguage(o.getString("language"));
                station.setTag(o.getString("tags"));
                station.setCodec(o.getString("codec"));
                station.setBitrate(o.getString("bitrate"));
                station.setUrl(o.getString("url"));
                station.setLogoUrl(o.getString("favicon"));
                list.add(station);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        });
                    */
        final List<Station> list = new ArrayList<>();
        for (int i = 0; i < stationJSonArray.length(); i++) {
            try {
                JSONObject jsonObject = stationJSonArray.getJSONObject(i);
                if (jsonObject.getString(strSerachKey).toLowerCase().contains(value.toLowerCase())) {
                    Station station = new Station();
                    station.setName(jsonObject.getString("name"));
                    station.setLanguage(jsonObject.getString("language"));
                    station.setTag(jsonObject.getString("tags"));
                    station.setCodec(jsonObject.getString("codec"));
                    station.setBitrate(jsonObject.getString("bitrate"));
                    station.setUrl(jsonObject.getString("url"));
                    station.setLogoUrl(jsonObject.getString("favicon"));
                    list.add(station);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return list;

    }

    public final Map<Country, Integer> getCountryJSonObjects(String filename) {
        JSONArray jsonArray = loadJSONFromAsset(filename);
        List<Country> countryList = new ArrayList<>(Arrays.asList(Country.values()));
        Map<Country, Integer> mapCountry = new LinkedHashMap<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = null;
            try {
                jsonObject = (JSONObject) jsonArray.get(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (int j = 0; j < countryList.size(); j++) {
                try {
                    if (countryList.get(j).getAbbreviation().toUpperCase()
                            .equals(jsonObject.getString("name").toUpperCase())) {
                        Country country = countryList.remove(j);
                        if (!jsonObject.getString("stationcount").isEmpty())
                            mapCountry.put(country, Integer.valueOf(jsonObject.getString("stationcount")));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
        Map<Country, Integer> treeMap = new TreeMap<>(
                (o1, o2) -> o1.toString().compareTo(o2.toString()));

        treeMap.putAll(mapCountry);
        return treeMap;
    }

    public final Map<String, Integer> getLanguageJSonObjects(String filename) {
        JSONArray jsonArray = loadJSONFromAsset(filename);
        Map<String, Integer> map;
        List<Language> languageList = new ArrayList<>(Arrays.asList(Language.values()));
        map = new TreeMap<>((o1, o2) -> o1.compareTo(o2));
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = null;
            try {
                jsonObject = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (int j = 0; j < languageList.size(); j++) {
                String lang = languageList.get(j).toString().toLowerCase();
                try {
                    if (jsonObject.getString("name").toLowerCase().contains(languageList.get(j).toString().toLowerCase())) {
                        if (map.containsKey(lang)) {
                            Integer newCount = map.get(lang) + Integer.valueOf(jsonObject.getString("stationcount"));
                            map.put(lang, newCount);
                        } else
                            map.put(lang, Integer.valueOf(jsonObject.getString("stationcount")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return map;
    }


}
