package com.example.sanoop.deposit.fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.sanoop.deposit.Constant;
import com.example.sanoop.deposit.DashboardActivity;
import com.example.sanoop.deposit.R;
import com.example.sanoop.deposit.interfaces.NetworkInterface;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.sanoop.deposit.DashboardActivity.drawerLayout;

/**
 * Created by sanoop on 4/16/2017.
 */

public class LocateAtmFragment extends Fragment implements FragmentManager.OnBackStackChangedListener{
    private static final String TAG = "Map Screen";
    private static final LatLng DEF_POSITION = new LatLng(19.0810,72.8684);

    private GoogleMap map;
    LocationManager locationManager;
    LocationListener listener;
    Location current_location;
    Marker current_pos_marker;
    protected LatLng currentPoint;
    Button btnLocateAtm;
    String token = "";
    ArrayList<LatLng> markerList = new ArrayList<>();
    private NetworkInterface networkCallService = NetworkInterface.retrofit.create(NetworkInterface.class);

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.locate_atm_layout, container,
                false);
        btnLocateAtm = (Button) rootView.findViewById(R.id.btnLocateAtm);
        setHasOptionsMenu(true);
        MapView mMapView = (MapView) rootView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();// needed to get the map to display immediately
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        map = mMapView.getMap();
        map.getUiSettings().setZoomControlsEnabled(true);

        // set map type
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LatLng latlng = null;
        if (latlng != null) {
            // set camera position to last latlng object and animate it
            CameraPosition pos = new CameraPosition.Builder().target(latlng)
                    .zoom(100).build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(pos));
        }

        // if the last lat lng is null, set it to default position (center of
        // india)
        else {
            CameraPosition pos = new CameraPosition.Builder()
                    .target(DEF_POSITION).zoom(20).build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(pos));
        }

        locationManager = (LocationManager) getActivity().getSystemService(
                Context.LOCATION_SERVICE);

        // get last known location and place marker

        getLastKnownLocation();

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    current_location = location;
                }

                // create a LatLng object of new location
                currentPoint = new LatLng(current_location.getLatitude(),
                        current_location.getLongitude());

                if (current_pos_marker != null)
                    current_pos_marker.remove();

                // add marker at new position
                current_pos_marker = map.addMarker(new MarkerOptions()
                        .position(currentPoint)
                        .title("Current Location"));
            }
        };

        btnLocateAtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<JsonElement> fetchATMLocations= networkCallService.getAtmLocations(Constant.CLIENT_ID, Constant.TOKEN, "BRANCH", String.valueOf(currentPoint.latitude), String.valueOf(currentPoint.longitude));
                fetchATMLocations.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                        if (response.code() == 200){
                            Log.i(TAG, "onResponse: " + response.body().toString());
                            try {
                                JSONArray locationArray = new JSONArray(response.body().toString());
                                for (int i = 1; i<locationArray.length(); i++){
                                    JSONObject locationJson = (JSONObject) locationArray.get(i);
                                    Double atmLat = Double.valueOf(locationJson.getString("latitude"));
                                    Double atmLon = Double.valueOf(locationJson.getString("longitude"));
                                    LatLng atmLoc = new LatLng(atmLat, atmLon);
                                    markerList.add(atmLoc);
                                }
                                if (markerList.size() > 0){
                                    placeMarker();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonElement> call, Throwable t) {
                        Log.e(TAG, "onFailure: ", t);
                    }
                });
            }
        });

        return rootView;

    }

    private void placeMarker() {
        int i = 0;
        for (LatLng loc : markerList){
            map.addMarker(new MarkerOptions().position(loc).title(i + ""));
            i++;
        }
    }

    private void getLastKnownLocation() {
        Log.e(TAG,"Get Last known called");
        // get last known location and place it on the map
        Location lastLocation = null;

        List<String> providers = locationManager.getProviders(true);

        for (int i=providers.size()-1; i>=0; i--) {
            lastLocation = locationManager.getLastKnownLocation(providers.get(i));
            if (lastLocation != null) break;
        }

        if (lastLocation != null) {

            Log.v(TAG, "Last location found at " + lastLocation.getLatitude()
                    + "," + lastLocation.getLongitude());

            // create a LatLng object of location
            currentPoint = new LatLng(lastLocation.getLatitude(),
                    lastLocation.getLongitude());

            if (current_pos_marker != null)
                current_pos_marker.remove();

            // add marker at new position
            current_pos_marker = map.addMarker(new MarkerOptions().position(
                    currentPoint).title("Current Location"));
        }
    }

    @Override
    public void onBackStackChanged() {
        Intent intent = new Intent(getActivity(), DashboardActivity.class);
        startActivity(intent);
    }

}
