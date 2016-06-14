package com.rathamrit.restaurantfinder;

import android.app.ProgressDialog;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

/**
 * Created by suyogcomputech on 14/06/16.
 */
public class RestaurantFragment extends Fragment {
    EditText etAddress;

    RecyclerView rvlist;
    LocationManager manager;
    Location location;
    String addresLine;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant, container, false);
        etAddress = (EditText) view.findViewById(R.id.et_address);
        rvlist = (RecyclerView) view.findViewById(R.id.rv_list);


        etAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                return false;
            }
        });
        if (AppHelper.isConnectingToInternet(getActivity())) {
            new GetRestaurantList().execute(AppConstants.URL);
        }
        return view;
    }

    public void setLocation(Location mCurrentLocation) {
        Log.i("loc", String.valueOf(mCurrentLocation.getLatitude()));
        location = mCurrentLocation;


    }

    public void setLocation(String addresLine) {
        if (addresLine != null && etAddress!=null)
            etAddress.setText(addresLine);
    }


    private class GetRestaurantList extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;
        ArrayList<Restaurant> list;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            try {
                Log.i("res", s);
                JSONArray jsonArray = new JSONArray(s);
                list = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject resJsonObject = jsonArray.getJSONObject(i);
                    String name = resJsonObject.getString("restaurant_name");
                    String address = resJsonObject.getString("address");
                    double latitude = Double.parseDouble(resJsonObject.getString("latitude"));
                    double longitude = Double.parseDouble(resJsonObject.getString("longitude"));
                    String phone = resJsonObject.getString("phone_number");
                    String imageUrl = resJsonObject.getString("logo_url");
                    Restaurant restaurant = new Restaurant(name, address, phone, null, imageUrl, latitude, longitude);
                    if (location != null) {
                        Location restrantLoc = new Location("");
                        restrantLoc.setLatitude(latitude);
                        restrantLoc.setLongitude(longitude);
                        int distance = (int) location.distanceTo(restrantLoc)/1000;
                        restaurant.setDistance(String.valueOf(distance)+"Km");
                    }

                    list.add(restaurant);
                }
                LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                rvlist.setLayoutManager(llm);
                RestaurantAdapter adapter = new RestaurantAdapter(getActivity(), list);
                rvlist.setAdapter(adapter);
                rvlist.requestFocus();
            } catch (NullPointerException e) {
                Toast.makeText(getActivity(), "Please check the network", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(AppConstants.URL)
                        .build();

                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (SocketTimeoutException e) {
                return null;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
