package com.example.atik_faysal.focuslab;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;

/**
 * Created by USER on 11/20/2017.
 */

public class MultipleImageSelecter extends AppCompatActivity
{
        String photoId,newFolderName;
        ArrayList<String> filePaths=new ArrayList<String>();
        GridView gv;
        Button bOk;
        ArrayList<ModelForImage> spacecrafts;
        private ArrayList<String>imageName;
        private ArrayList<Uri>imageUri;
        private Toolbar toolbar;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.multiple_images);
                gv= (GridView) findViewById(R.id.gv);
                filePaths.clear();
                FilePickerBuilder.getInstance().setMaxCount(10).setSelectedFiles(filePaths).setActivityTheme(R.style.newTheme).pickPhoto(MultipleImageSelecter.this);
                bOk = (Button)findViewById(R.id.bOrderNow);
                spacecrafts=new ArrayList<>();
                imageName = new ArrayList<>();
                imageUri = new ArrayList<>();
                onButtonClick();
                toolbar = (Toolbar)findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                setToolbar();
                getPhotoId();
        }

        private void setToolbar()
        {
                toolbar.setTitleTextColor(getResources().getColor(R.color.white));
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                toolbar.setNavigationIcon(R.drawable.back_arrow);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                finish();
                        }
                });
        }

        private void onButtonClick()
        {
                bOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                UploadImages uploadImages;
                                if(spacecrafts.size()!=0)
                                {
                                        String facebookName = getIntent().getExtras().getString("facebookName");
                                        String facebookEmail = getIntent().getExtras().getString("facebookEmail");
                                        String facebookId = getIntent().getExtras().getString("facebookId");
                                        String user = getIntent().getExtras().getString("user");
                                        if(user.equals("facebook"))uploadImages = new UploadImages(MultipleImageSelecter.this,imageUri,user,facebookName,facebookEmail,facebookId,newFolderName);
                                        else if(user.equals("guest"))uploadImages = new UploadImages(MultipleImageSelecter.this,imageUri,user,"","","",newFolderName);
                                        else uploadImages = new UploadImages(MultipleImageSelecter.this,imageUri,user,"","","",newFolderName);
                                        uploadImages.execute();
                                }else Toast.makeText(MultipleImageSelecter.this,"No photos selected.\nPlease select photo",Toast.LENGTH_SHORT).show();
                        }
                });
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
                switch (requestCode)
                {
                        case FilePickerConst.REQUEST_CODE:
                                if(resultCode==RESULT_OK && data!=null)
                                {
                                        filePaths = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_PHOTOS);
                                        ModelForImage modelForImage;
                                        try
                                        {
                                                for (String path:filePaths) {
                                                        modelForImage=new ModelForImage();
                                                        imageName.add(path.substring(path.lastIndexOf("/")+1));
                                                        modelForImage.setUri(Uri.fromFile(new File(path)));
                                                        imageUri.add(Uri.fromFile(new File(path)));
                                                        spacecrafts.add(modelForImage);
                                                }
                                                gv.setAdapter(new ImageAdapter(this,spacecrafts));
                                        }catch (Exception e)
                                        {
                                                e.printStackTrace();
                                        }
                                }
                }
        }

        private void generatePhotoId(String photoId)
        {

                //

                String path = photoId.substring(1,photoId.length());
                int photoPath;
                try {
                        photoPath = Integer.parseInt(path);
                        photoPath+=1;
                        photoId = "P"+String.valueOf(photoPath);
                }catch (NumberFormatException e)
                {
                        e.printStackTrace();
                }
                newFolderName = photoId;
                //Log.d(TAG,"new id is : "+newFolderName);
        }

        private void getPhotoId()
        {

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                                String id = dataSnapshot.child("lastPhotoId").getValue(String.class);
                                photoId = id;
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                });
                new Thread(new Runnable() {
                        @Override
                        public void run() {
                                try {
                                        Thread.sleep(1000);
                                        generatePhotoId(photoId);
                                }catch (InterruptedException e)
                                {
                                        e.printStackTrace();
                                }
                        }
                }).start();
        }
}
