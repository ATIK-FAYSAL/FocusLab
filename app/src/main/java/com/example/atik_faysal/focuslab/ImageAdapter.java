package com.example.atik_faysal.focuslab;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by USER on 11/20/2017.
 */

public class ImageAdapter extends BaseAdapter
{
        private Context context;
        private ArrayList<ModelForImage>imageModel;
        public ImageAdapter(Context context,ArrayList<ModelForImage>modelForImages)
        {
                this.context = context;
                this.imageModel = modelForImages;
        }

        @Override
        public int getCount() {
                return imageModel.size();
        }

        @Override
        public Object getItem(int position) {
                return imageModel.get(position);
        }

        @Override
        public long getItemId(int position) {
                return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
                if (view == null) {
                        //INFLATE CUSTOM LAYOUT
                        view = LayoutInflater.from(context).inflate(R.layout.model, parent, false);
                }
                final ModelForImage model = (ModelForImage) this.getItem(position);
                ImageView img = (ImageView) view.findViewById(R.id.spacecraftImg);
                //BIND DATA
                Picasso.with(context).load(model.getUri()).placeholder(R.drawable.ic_camera).into(img);
                //VIEW ITEM CLICK
                view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                               // Toast.makeText(context,model.getImageName(), Toast.LENGTH_SHORT).show();
                        }
                });
                return view;
        }
}
