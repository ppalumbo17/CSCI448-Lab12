package android.bignerdranch.com.lab12;

import android.support.v4.app.Fragment;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.util.List;

/**
 * Created by Peter on 4/25/2016.
 */
public class LocatrFragment extends SupportMapFragment
{
    private static final String TAG = "LocatrFragment";

    //private ImageView mImageView;
    private GoogleApiClient mClient;
    private GoogleMap mMap;
    private Bitmap mMapImage;
    private GalleryItem mMapItem;
    private Location mCurrentLocation;
    private ProgressBar mProgressBar;
   // public boolean mSearching = false;

    public static LocatrFragment newInstance(){
        return new LocatrFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mClient = new GoogleApiClient.Builder(getActivity()).addApi(LocationServices.API).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onConnectionSuspended(int i) {
            }
        }).build();
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                updateUI();
            }
        });
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
//        View v = inflater.inflate(R.layout.fragment_locatr, container, false);
//        mImageView = (ImageView) v.findViewById(R.id.image);
//        mProgressBar = (ProgressBar) v.findViewById(R.id.progress);
//        //mProgressBar.setVisibility(View.INVISIBLE);
//        return v;
//    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().invalidateOptionsMenu();
        mClient.connect();
    }
    @Override
    public void onStop() {
        super.onStop();
        mClient.disconnect();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_locatr, menu);

        MenuItem searchItem = menu.findItem(R.id.action_locate);
        searchItem.setEnabled(mClient.isConnected());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_locate:
                //mProgressBar.setVisibility(View.VISIBLE);
               // mProgressBar.setMax(100);
               // mProgressBar.setElevation(20);
                findImage();
               // mProgressBar.setVisibility(View.GONE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void findImage() {
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);
        request.setInterval(0);
        try {
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(mClient, request, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            Log.i(TAG, "Got a fix: " + location);
//                            mProgressBar.setVisibility(View.VISIBLE);
//                            mProgressBar.setProgress(0);
                            new SearchTask().execute(location);
                            //mProgressBar.setProgress(100);
//                            mProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });
        }
        catch(Exception e){
            Log.i(TAG, "Exception: "+e);
        }
    }
    private void updateUI() {
        if (mMap == null || mMapImage == null) {
            return;
        }
        LatLng itemPoint = new LatLng(mMapItem.getLat(), mMapItem.getLon());
        LatLng myPoint = new LatLng(
                mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(itemPoint)
                .include(myPoint)
                .build();
        int margin = getResources().getDimensionPixelSize(R.dimen.map_inset_margin);
        CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, margin);
        mMap.animateCamera(update);
    }
    //Setter for Searching
//    public void setSearching(boolean bool){
//        mSearching = bool;
//    }

    private class SearchTask extends AsyncTask<Location,Void,Void> {
        //private ProgressDialog mProgressDialog;
        private GalleryItem mGalleryItem;
        private Bitmap mBitmap;
        private Location mLocation;
        //Set Whether Searching or not

        @Override
        protected Void doInBackground(Location... params) {
           // mProgressDialog = ProgressDialog.show(getActivity(), "Searching", "Please stand by", true);
            mLocation = params[0];
            FlickrFetchr fetchr = new FlickrFetchr();
            List<GalleryItem> items = fetchr.searchPhotos(params[0]);
            if (items.size() == 0) {
                return null;
            }
            mGalleryItem = items.get(0);
            try {
                byte[] bytes = fetchr.getUrlBytes(mGalleryItem.getUrl());
                mBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            } catch (IOException ioe) {
                Log.i(TAG, "Unable to download bitmap", ioe);
            }
            //mProgressDialog.dismiss();
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            mMapImage = mBitmap;
            mMapItem = mGalleryItem;
            mCurrentLocation = mLocation;

            updateUI();
            //mImageView.setImageBitmap(mBitmap);
        }
    }
}
