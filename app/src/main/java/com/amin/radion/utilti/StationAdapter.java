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
import com.amin.radion.entity.Station;
import com.amin.radion.launch.HomeActivity;
import com.squareup.picasso.Picasso;
import java.util.List;

public class StationAdapter extends BaseAdapter {
    private final List<Station> stationList;
    private  LayoutInflater mInflate;
    private HistoryFavorite favoriteStations;

    public StationAdapter(Context context, List stationList){
        mInflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.stationList = stationList;
        favoriteStations = new HistoryFavorite(context, HomeActivity.FAVORITE);

    }
    @Override
    public int getCount() {
        return stationList.size();
    }

    @Override
    public Object getItem(int position) {
        return stationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        holder = new ViewHolder();
        View view = mInflate.inflate(R.layout.station_list_layout, null);

        holder.stationTextView = view.findViewById(R.id.stationNametextView);
        holder. imageViewLogo = view.findViewById(R.id.logoImageView);
        holder. imageViewFavorite = view.findViewById(R.id.imageViewFavorite);
        view.setTag(holder);
        String logoUrl;
        Station station = stationList.get(position);
        holder.stationTextView.setText(station.toString());
        logoUrl = station.getLogoUrl();
        if (!logoUrl.isEmpty()) {
            Picasso.get().load(logoUrl)
                    .noFade().error(R.drawable.no_logo)
                    .resize(150, 150)
                    .into(holder.imageViewLogo);

               }

            int index = favoriteStations.getStationIndex(station);
            if (index != -1){
                holder.imageViewFavorite.setImageResource(R.drawable.red_star);
            }

        holder.imageViewFavorite.setOnClickListener((v) ->{
            int indexx = favoriteStations.getStationIndex(station);
            if (indexx == -1){
                favoriteStations.addToList(station);
                holder.imageViewFavorite.setImageResource(R.drawable.red_star);
            }else {
                favoriteStations.removeFromList(indexx);
                holder.imageViewFavorite.setImageResource(R.drawable.grey_star);
            }

        });

        return view;
    }
    private class ViewHolder{
        private TextView stationTextView;
        private ImageView imageViewLogo;
        private ImageView imageViewFavorite;
    }
}
