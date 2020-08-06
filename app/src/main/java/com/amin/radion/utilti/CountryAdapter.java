package com.amin.radion.utilti;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.amin.radion.R;
import com.amin.radion.constant.Country;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CountryAdapter extends BaseAdapter {
     List<Country> countryList;
     List<Integer> stationList;
     Context context;
     LayoutInflater layoutInflater;

    public CountryAdapter(Context context, Map<Country, Integer> map){
        this.context = context;
        this.countryList = new ArrayList<>(map.keySet());
        this.stationList = new ArrayList<>(map.values());
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return countryList.size();
    }

    @Override
    public Object getItem(int position) {
        return countryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = layoutInflater.inflate(R.layout.country_list_layout,null);
        ViewHolder holder = new ViewHolder();
        holder.nameTextView = view.findViewById(R.id.countryNametextView);
        holder.stationCountTextView = view.findViewById(R.id.stationCountTextView);
        holder.flagImageView = view.findViewById(R.id.flagImageView);
        view.setTag(holder);

        Country country = countryList.get(position);
        holder.nameTextView.setText(country.toString());
        holder.stationCountTextView.setText( stationList.get(position).toString() + "  ");
        holder.flagImageView.setImageResource(country.getFlag());

        return view;
    }

    private class ViewHolder{
        TextView nameTextView;
        TextView stationCountTextView;
        ImageView flagImageView;
    }
}
