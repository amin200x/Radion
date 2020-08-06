package com.amin.radion.utilti;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import com.amin.radion.entity.Station;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class HistoryFavorite {

    private SharedPreferences sp;
    private String name;
    private JSONArray jsonArray;
    public HistoryFavorite(Context context, String name){
        sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        this.name = name;

    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void addToList(Station station){

        SharedPreferences.Editor editor = sp.edit();
        JSONObject jsonObject;
          try {
            jsonObject = createJSONObject(station);
            jsonArray = getJSonArray();
          // System.out.println("List Size: " + jsonArray.length() + "  " + name);
              int index = getStationIndex(station);
              if (index != -1) removeFromList(index);
              // Favorite and recently played list limited to 20
              if (jsonArray.length() > 19) removeFromList(0);

            jsonArray.put(jsonObject);

            editor.putString(name, jsonArray.toString());
            editor.apply();
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public List<Station> getStations(){
        try {
            JSONArray jsonArray = getJSonArray();
            List<Station> stationList = new ArrayList<>(jsonArray.length());
            Station station;
            for (int i = 0; i< jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                station =createStation(jsonObject);
                stationList.add(station);
            }
            Collections.reverse(stationList);
            return stationList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
     void removeFromList(int index){
        jsonArray.remove(index);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(name, jsonArray.toString());
        editor.apply();
        editor.commit();

        }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
     int getStationIndex(Station station){
        int index = -1;
        try {
           jsonArray = getJSonArray();
           for (int i = 0; i < jsonArray.length(); i++) {
                if (station.getUrl().equalsIgnoreCase(jsonArray.getJSONObject(i).getString("url"))
                && station.getName().equalsIgnoreCase(jsonArray.getJSONObject(i).getString("name"))) {
                    index = i;
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
       return index;
        }
    private JSONArray getJSonArray() throws JSONException {
        String historyContent = sp.getString(name, "[]");

        return new JSONArray(historyContent);
    }

    private JSONObject createJSONObject(Station station) throws JSONException {
       JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", station.getName());
        jsonObject.put("language", station.getLanguage());
        jsonObject.put("codec", station.getCodec());
        jsonObject.put("bitrate", station.getBitrate());
        jsonObject.put("logourl", station.getLogoUrl());
        jsonObject.put("url", station.getUrl());
        jsonObject.put("tag", station.getTag());

        return jsonObject;
    }

    private Station createStation(JSONObject jsonObject) throws JSONException {
        Station station = new Station();
        station.setName(jsonObject.getString("name"));
        station.setLanguage(jsonObject.getString("language"));
        station.setTag(jsonObject.getString("tag"));
        station.setCodec(jsonObject.getString("codec"));
        station.setBitrate(jsonObject.getString("bitrate"));
        station.setLogoUrl(jsonObject.getString("logourl"));
        station.setUrl(jsonObject.getString("url"));
        return station;
    }
}
