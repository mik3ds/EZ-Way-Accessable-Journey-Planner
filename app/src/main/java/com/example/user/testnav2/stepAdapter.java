package com.example.user.testnav2;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by mark on 10/7/2018.
 */

public class stepAdapter extends ArrayAdapter{
    private final int resourceid;

    public stepAdapter(Context context, int textViewResourceId, List<StepInfo> objects){
        super(context, textViewResourceId, objects);
        resourceid = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        StepInfo stepinfo = (StepInfo) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceid, null);
        ImageView stepinfoimage = (ImageView) view.findViewById(R.id.infoimage);
        TextView textview = (TextView) view.findViewById(R.id.info);
        TextView textview2 = (TextView) view.findViewById(R.id.infotime);
        stepinfoimage.setImageResource(stepinfo.getImageId());
        textview.setText(stepinfo.getName());
        textview2.setText(stepinfo.getTime());
        return view;
    }
}
