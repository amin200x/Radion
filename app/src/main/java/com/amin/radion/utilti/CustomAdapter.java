package com.amin.radion.utilti;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.amin.radion.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomAdapter extends BaseAdapter {
    List<String> nameList;
    List<Integer> stationCount;
    LayoutInflater mInflate;

    public CustomAdapter(Context context, Map map){
        nameList = new ArrayList<>(map.keySet());
        stationCount = new ArrayList<>(map.values());
        mInflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return nameList.size();
    }

    @Override
    public Object getItem(int position) {
        return nameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mInflate.inflate(R.layout.custom_list_layout, null);
        ViewHolder holder = new ViewHolder();
        holder.languageTextView = view.findViewById(R.id.textViewLanguage);
        holder.stationCountTextView = view.findViewById(R.id.textViewStationCount);
          try {
            String lang = nameList.get(position);
              StringBuilder stringBuilder = new StringBuilder();
              stringBuilder.append(lang.substring(0, 1).toUpperCase()).append(lang.substring(1));
              holder.languageTextView.setText(stringBuilder.toString());
              holder.stationCountTextView.setText(stationCount.get(position).toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }
    private class ViewHolder{
        TextView languageTextView;
        TextView stationCountTextView;
    }
}

