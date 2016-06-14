package com.rathamrit.restaurantfinder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by suyogcomputech on 14/06/16.
 */
public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {
    Context mContext;
    ArrayList<Restaurant> list;

    public RestaurantAdapter(Context context, ArrayList<Restaurant> list) {
        this.mContext = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_restaurant, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Restaurant restaurant = list.get(position);
        holder.tvAddress.setText(restaurant.getAddress());
        holder.tvName.setText(restaurant.getName());
        if (restaurant.getDistance() != null) {
            holder.tvDistance.setText(restaurant.getDistance());
        }
        Glide.with(mContext).load(restaurant.imageUrl).override(100, 100).error(android.R.drawable.ic_menu_gallery).into(holder.ivLogo);
        holder.ibMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Restaurant restaurant1 = list.get(holder.getAdapterPosition());
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", restaurant1.getLatitude(), restaurant1.getLongitude());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                mContext.startActivity(intent);
            }
        });
        holder.ibCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Restaurant restaurant1 = list.get(holder.getAdapterPosition());
                Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + restaurant1.getPhone()));
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivLogo;
        TextView tvName, tvAddress, tvDistance;
        ImageButton ibMap, ibCall;

        public ViewHolder(View itemView) {
            super(itemView);
            ivLogo = (ImageView) itemView.findViewById(R.id.iv_logo);
            tvAddress = (TextView) itemView.findViewById(R.id.tv_address);
            tvDistance = (TextView) itemView.findViewById(R.id.tv_dist);
            ibCall = (ImageButton) itemView.findViewById(R.id.ib_call);
            ibMap = (ImageButton) itemView.findViewById(R.id.ib_loc);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
        }
    }
}
