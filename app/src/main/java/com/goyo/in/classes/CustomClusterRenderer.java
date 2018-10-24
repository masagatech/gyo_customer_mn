package com.goyo.in.classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.goyo.in.ModelClasses.MarkerItem;
import com.goyo.in.R;

/**
 * Created by mis on 06-Jun-18.
 */
public class CustomClusterRenderer extends DefaultClusterRenderer<MarkerItem> {

    private final IconGenerator mClusterIconGenerator;
    private final Context mContext;

    public CustomClusterRenderer(Context context, GoogleMap map,
                                 ClusterManager<MarkerItem> clusterManager) {
        super(context, map, clusterManager);

        mContext = context;
        mClusterIconGenerator = new IconGenerator(mContext.getApplicationContext());
    }

    @Override
    protected void onBeforeClusterItemRendered(MarkerItem item,
                                               MarkerOptions markerOptions) {

        //final BitmapDescriptor markerDescriptor =
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
        if (item.getIcon() != null) {
            markerOptions.icon(item.getIcon()); //Here you retrieve BitmapDescriptor from ClusterItem and set it as marker icon
        }
       // markerOptions.icon(markerDescriptor).snippet(item.getTitle());
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<MarkerItem> cluster,
                                           MarkerOptions markerOptions) {

        mClusterIconGenerator.setBackground(
                ContextCompat.getDrawable(mContext, R.drawable.background_circle));

        mClusterIconGenerator.setTextAppearance(R.color.white);

        final Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
    }
}