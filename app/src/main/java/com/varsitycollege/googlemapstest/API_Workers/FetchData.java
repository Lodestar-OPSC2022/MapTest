package com.varsitycollege.googlemapstest.API_Workers;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FetchData  {
//https://www.youtube.com/watch?v=e_YLWSNMfZg
    String googleNearByPlaces;
    GoogleMap googleMap;
    String url;

    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());

    public void execute(Object[] objects)
    {
        executor.execute(() -> {
            try
            {
                //Background work here
                googleMap = (GoogleMap) objects[0];
                url = (String)objects[1];
                DownloadURL downloadURL = new DownloadURL();
                googleNearByPlaces = downloadURL.retrieveURL(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            handler.post(() -> {
                //UI Thread work here

                try{
                    JSONObject jsonObject = new JSONObject(googleNearByPlaces);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");

                    for (int i=0; i<jsonArray.length();i++)
                    {
                        JSONObject currentObject = jsonArray.getJSONObject(i);
                        JSONObject getLocation = currentObject.getJSONObject("geometry")
                                .getJSONObject("location");

                        String lat = getLocation.getString("lat");
                        String lng = getLocation.getString("lng");

                        JSONObject getName =jsonArray.getJSONObject(i);
                        String name = getName.getString("name");

                        LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.title(name);
                        markerOptions.position(latLng);
                        googleMap.addMarker(markerOptions);
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            });
        });
    }



}
