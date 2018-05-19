package com.home.khalil.employeedirectory;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by khalil on 5/5/18.
 */

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Activity context;

    public CustomInfoWindowAdapter(Activity context){
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View view = context.getLayoutInflater().inflate(R.layout.info_window_layout, null);

        CircleImageView image= (CircleImageView) view.findViewById(R.id.info_image) ;

        String place = marker.getSnippet();
        if (place.equals("London")){
            image.setImageResource(R.drawable.london);
        }else if (place.equals("Manchester")){
            image.setImageResource(R.drawable.manchester);
        }else{
            image.setImageResource(R.drawable.soton);
        }


        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = context.getLayoutInflater().inflate(R.layout.info_window_layout, null);

        CircleImageView image= (CircleImageView) view.findViewById(R.id.info_image) ;

        String place = marker.getSnippet();
        if (place.equals("London")){
            image.setImageResource(R.drawable.london);
        }else if (place.equals("Manchester")){
            image.setImageResource(R.drawable.manchester);
        }else{
            image.setImageResource(R.drawable.soton);
        }


        return view;
    }


}
