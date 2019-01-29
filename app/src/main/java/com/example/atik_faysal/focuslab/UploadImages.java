package com.example.atik_faysal.focuslab;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import static android.content.ContentValues.TAG;
import java.util.ArrayList;




/**
 * Created by USER on 11/21/2017.
 */

public class UploadImages extends AsyncTask<String,Void,Void>
{
        private Context context;
        private ArrayList<Uri>imageUri;
        private StorageReference storageReference;

        private ProgressBar progressBar;
        private TextView textView;
        private int progressStatus;
        protected String folderName;
        private Handler handler = new Handler();

        private static final String PHOTO_ID = "photoId";
        private AllSharedPreference allSharedPreference;
        String photoId,newFolderName;
        private String facebookName,facebookId,facebookEmail,user;

        public UploadImages(Context context,ArrayList<Uri>imageUri,String user,String facebookName,String facebookEmail,String facebookId,String newFolderName)
        {
                this.context = context;
                this.imageUri = imageUri;
                this.facebookName = facebookName;
                this.facebookEmail = facebookEmail;
                this.facebookId = facebookId;
                this.user = user;
                this.newFolderName = newFolderName;
                storageReference = FirebaseStorage.getInstance().getReference();
                allSharedPreference =  new AllSharedPreference(context);
        }

        @Override
        protected void onPreExecute() {
                setProgressDialog();
        }

        @Override
        protected Void doInBackground(String... strings)
        {
                final Firebase firebase = new Firebase("https://focus-lab.firebaseio.com/lastPhotoId");
                folderName = newFolderName;
                allSharedPreference.putPhotoId(PHOTO_ID,folderName);
                StorageReference filepath;
                for(int i=0;i<imageUri.size();i++)
                {
                        filepath = storageReference.child(folderName).child(imageUri.get(i).getLastPathSegment());
                        filepath.putFile(imageUri.get(i)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                                {
                                        firebase.setValue(newFolderName);
                                }
                        }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                        });
                }
                return null;
        }

        private void setProgressDialog()
        {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(context).inflate(R.layout.progress_bar,null);
                textView = (TextView)view.findViewById(R.id.text);
                progressBar = (ProgressBar)view.findViewById(R.id.bar);
                progressBar.setMax(imageUri.size());
                builder.setTitle("Please Wait");
                builder.setView(view);
                progressStatus = 0;
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
                new Thread(new Runnable() {
                        @Override
                        public void run() {
                                while (progressStatus<=imageUri.size())
                                {
                                        handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                        progressBar.setProgress(progressStatus);
                                                        textView.setText("Uploading : "+progressStatus+" / "+imageUri.size());
                                                }
                                        });
                                        try {
                                                Thread.sleep(2500);
                                        }catch (InterruptedException e)
                                        {
                                                e.printStackTrace();
                                        }
                                        progressStatus++;
                                }
                                alertDialog.dismiss();
                                Log.d(TAG,"id :"+facebookId);
                                Intent page = new Intent(context,MakeOrder.class);
                                page.putExtra("photoPath",folderName);
                                if(user.equals("facebook"))
                                {
                                        page.putExtra("phone","facebook");
                                        page.putExtra("userId",facebookId);
                                        page.putExtra("userName",facebookName);
                                        page.putExtra("userEmail",facebookEmail);
                                        page.putExtra("key","gallery");
                                        Log.d(TAG,"user1 :"+user);
                                }
                                else if(user.equals("guest"))
                                {
                                        page.putExtra("phone","guest");
                                        page.putExtra("userId","");
                                        page.putExtra("key","gallery");
                                        Log.d(TAG,"user2 :"+user);
                                }
                                else
                                {
                                        page.putExtra("phone",user);
                                        page.putExtra("userId","");
                                        page.putExtra("key","gallery");
                                        Log.d(TAG,"user3 :"+user);
                                }
                                context.startActivity(page);
                        }
                }).start();

        }


}
