package com.example.atik_faysal.focuslab;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import backend.RegisterDeviceToken;
import backend.SaveInfoBackgroundTask;
import backend.SaveInfoBackgroundTask.OnAsyncTaskInterface;

import static droidninja.filepicker.FilePickerConst.REQUEST_CODE;

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private static String phone;
    private Uri imageUri;
    private View view;
    private StorageReference storageReference;
    private CheckInternet checkInternet;
    private static String facebookId,facebookName;
    private FirebaseAuth firebaseAuth;
    private AllSharedPreference allSharedPreference;
    private NavigationView navigationView;
    private static final String PREF_NAME = "session";

    private CheckIfOrderIsExist checkIfOrderIsExist;
    private TextView txtName,txtPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        allSharedPreference = new AllSharedPreference(this);
        checkIfOrderIsExist = new CheckIfOrderIsExist(this,allSharedPreference.getPreferencePhone(PREF_NAME));

        navigationView =  findViewById(R.id.nav_view);
        view = navigationView.inflateHeaderView(R.layout.nav_header_home_page);
        txtPhone = view.findViewById(R.id.userPhone);
        txtName = view.findViewById(R.id.userName);
        clossApp();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getCurrentUserInfo();

        FirebaseMessaging.getInstance().subscribeToTopic("test");
        String token = FirebaseInstanceId.getInstance().getToken();

        RegisterDeviceToken.registerToken(token,allSharedPreference.getPreferencePhone(PREF_NAME));
    }

    //Default Method Start from here

    @Override
    public void onBackPressed() {
       AlertDialog.Builder builder = new AlertDialog.Builder(this);
       builder.setTitle("Exit");
       builder.setMessage("Want to exit ?");
       builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
               Intent intent = new Intent(getApplicationContext(), HomePage.class);
               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
               intent.putExtra("flag",true);
               startActivity(intent);
           }
       });
       builder.setNegativeButton("no",null);
       AlertDialog alertDialog = builder.create();
       alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }


    //get phone from Login activity


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        checkInternet = new CheckInternet(this);
        //String phone =  getIntent().getExtras().getString("phone");
        Intent page;
        if(id==R.id.focus_point)
        {
            if(checkInternet.check_internet())
            {
                Intent googleMap = new Intent(Intent.ACTION_VIEW);
                googleMap.setData(Uri.parse("https://www.google.com.bd/maps/place/Focus+Digital+Color+Lab/@23.7420176,90.4104514,17z/data=!4m5!3m4!1s0x3755b88a897daebf:0x83af8a47160e30e4!8m2!3d23.7416935!4d90.4116423?hl=en"));
                startActivity(googleMap);
            }else Toast.makeText(this,"Please check your Internet connection!",Toast.LENGTH_SHORT).show();
        }
        else if(id==R.id.new_order)
        {
            Intent intent = new Intent(HomePage.this,MakeOrder.class);
            intent.putExtra("key","official");
            startActivity(intent);
        }else if (id==R.id.last_order)
        {
            if(checkInternet.check_internet())
            {
                String url = "http://app.focusdigitalcolorlab.com/checkOrder.php";
                checkIfOrderIsExist.checkOrderIsExist(url,"paid");
            }else Toast.makeText(this,"Please check your Internet connection!",Toast.LENGTH_SHORT).show();
        }else if(id==R.id.payment)
        {
            if(checkInternet.check_internet())
            {
                String url = "http://app.focusdigitalcolorlab.com/checkOrder.php";
                checkIfOrderIsExist.checkOrderIsExist(url,"due");
            }else Toast.makeText(this,"Please check your Internet connection!",Toast.LENGTH_SHORT).show();
        }else if(id==R.id.support)
        {
            page = new Intent(HomePage.this,Support.class);
            startActivity(page);
        }else if(id==R.id.feedback)
        {
            page = new Intent(HomePage.this,Feedback.class);
            page.putExtra("phone",phone);
            startActivity(page);
        }else if(id==R.id.aboutus)
        {
            page = new Intent(HomePage.this,AboutUs.class);
            startActivity(page);
        }else if(id==R.id.signout)signOut();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Default Method finished here


    //For Official photo order
    public void officialPhotoButtonClick(View view)
    {
        Intent page = new Intent(HomePage.this,MakeOrder.class);
        page.putExtra("key","official");
        startActivity(page);
    }

    //For print photo From gallery
    public void printPhotofromGallery(View view)
    {

        Intent page = new Intent(HomePage.this,MultipleImageSelecter.class);
        //int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int storagePermission =  ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(storagePermission!= PackageManager.PERMISSION_GRANTED)//||cameraPermission!= PackageManager.PERMISSION_GRANTED)
        {
            //if(cameraPermission!= PackageManager.PERMISSION_GRANTED)ActivityCompat.requestPermissions(HomePage.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE);
            if(storagePermission!= PackageManager.PERMISSION_GRANTED)ActivityCompat.requestPermissions(HomePage.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
        else
        {
            page.putExtra("user",phone);
            if(phone.equals("facebook"))
            {
                page.putExtra("facebookName",facebookName);
                page.putExtra("facebookEmail",facebookEmail);
                page.putExtra("facebookId",facebookId);
            }
            startActivity(page);
        }
    }

    private static void Close_activity(Activity context, Class<?> clazz) {
        Intent intent = new Intent(context, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        context.finish();
    }

    //sign out for facebook user
    protected void signOut() {

        if(allSharedPreference.getPreferenceSession(PREF_NAME))
        {
            allSharedPreference.savePreferenceData(PREF_NAME,false);
            final ProgressDialog ringProgressDialog = ProgressDialog.show(HomePage.this, "Please wait", "Log out", true);
            ringProgressDialog.setCancelable(true);
            new Thread( new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2500);
                    } catch (Exception e) {
                    }
                    ringProgressDialog.dismiss();
                }
            }).start();
            ringProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    Close_activity(HomePage.this,LogIn_Activity.class);
                }
            });
        }
    }

    private void clossApp()
    {
        if(getIntent().getBooleanExtra("flag",false))finish();
    }


    //get current user info

    private void getCurrentUserInfo()
    {
        String url = "http://app.focusdigitalcolorlab.com/currentUser.php";
        try {
            String data = URLEncoder.encode("phone","UTF-8")+"="+URLEncoder.encode(allSharedPreference.getPreferencePhone(PREF_NAME));
            SaveInfoBackgroundTask backgroundTask = new SaveInfoBackgroundTask(this);
            backgroundTask.setOnResultListener(onAsyncTaskInterface);
            backgroundTask.execute(url,data);
            txtPhone.setText(allSharedPreference.getPreferencePhone(PREF_NAME));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
        @Override
        public void onResultSuccess(final String message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!message.equals("failed"))
                    {
                        txtName.setText(message);
                        allSharedPreference.currentUserName(message);
                    }
                }
            });
        }
    };
}
