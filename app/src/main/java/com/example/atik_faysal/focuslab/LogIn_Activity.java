package com.example.atik_faysal.focuslab;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.Manifest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import backend.SaveInfoBackgroundTask;
import backend.SaveInfoBackgroundTask.OnAsyncTaskInterface;

import static droidninja.filepicker.FilePickerConst.REQUEST_CODE;


public class LogIn_Activity extends AppCompatActivity implements TextWatcher,CompoundButton.OnCheckedChangeListener
{
    /*Declare Variable
    * All variable are declare here*/

    private EditText e_password,e_phoneNumber;
    private ProgressBar progressBar;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    AllSharedPreference preference;
    private static String u_name="atik",u_password="123",u_checkbox = "remember",pref = "pref";
    private CheckInternet checkInternet;
    private CheckBox checkBox;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private String facebookName,facebookEmail,userId;
    private FirebaseAuth firebaseAuth;

    private static final String PREF_NAME = "session";
    private static final String URL = "http://app.focusdigitalcolorlab.com/login.php";
    private static String DATA;
    private AllSharedPreference allSharedPreference;

    private SaveInfoBackgroundTask backgroundTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_log_in_);
        Firebase.setAndroidContext(this);
        initializeAllComponent();
        checkBox = (CheckBox)findViewById(R.id.C_remember);
        checkInternet = new CheckInternet(this);
        preference = new AllSharedPreference(this);
        callbackManager = CallbackManager.Factory.create();
        //LogInWithFacebook();
        initialize();
        clossApp();
    }


    @Override
    public void onStart() {
        firebaseAuth = FirebaseAuth.getInstance();
        super.onStart();

        //String phoneNumber;
        if(allSharedPreference.getPreferenceSession(PREF_NAME))
        {
            //phoneNumber = allSharedPreference.getPreferencePhone(PREF_NAME);
            Intent page = new Intent(LogIn_Activity.this,HomePage.class);
            //page.putExtra("phone",phoneNumber);
           // ShowMyOrderList.setPhoneNumber(phoneNumber);
            //HomePage.getPhone(phoneNumber);
           // OrderHistoryList.setPhoneNumber(phoneNumber);
            //allSharedPreference.savePreferenceData("session",true);
            startActivity(page);
        }else
        {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
           // updateUI(currentUser);
        }
    }

        //initialize all component here
    protected void initializeAllComponent()
    {
        e_phoneNumber = (EditText)findViewById(R.id.e_email);
        e_password = (EditText)findViewById(R.id.e_password);
        loginButton = (LoginButton)findViewById(R.id.login_button);
        allSharedPreference = new AllSharedPreference(this);
        progressBar = findViewById(R.id.progress);
        if(ActivityCompat.checkSelfPermission(LogIn_Activity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(LogIn_Activity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE);
        if(ActivityCompat.checkSelfPermission(LogIn_Activity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(LogIn_Activity.this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_CODE);
        if(ActivityCompat.checkSelfPermission(LogIn_Activity.this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(LogIn_Activity.this, new String[]{Manifest.permission.RECEIVE_SMS}, REQUEST_CODE);
    }


    //For all button click
    public void onClick(View view)
    {
        if (view.getId()==R.id.b_login)
        {
            String phone,password;
            phone = e_phoneNumber.getText().toString();
            password = e_password.getText().toString();
            if(!(phone.isEmpty()||password.isEmpty()))
            {
                if(checkInternet.check_internet())
                {
                    try {
                        DATA = URLEncoder.encode("phone","UTF-8")+"="+URLEncoder.encode(phone,"UTF-8")+"&"
                                +URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8");
                        backgroundTask = new SaveInfoBackgroundTask(this);
                        backgroundTask.setOnResultListener(onAsyncTaskInterface);
                        backgroundTask.execute(URL,DATA);
                    }catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }else Toast.makeText(this,"Please check your Internet connection!",Toast.LENGTH_SHORT).show();
            }else Toast.makeText(this,"Please enter phone number and password",Toast.LENGTH_SHORT).show();
        }else if(view.getId()==R.id.txt_forgotPassword)
        {
            if(checkInternet.check_internet())
            {
                Intent page = new Intent(LogIn_Activity.this,ForgetPassword.class);
                startActivity(page);
            }else Toast.makeText(this,"Please check your Internet connection!",Toast.LENGTH_SHORT).show();
        }else if(view.getId()==R.id.txt_register)
        {
            if(checkInternet.check_internet())
            {
                Intent page = new Intent(LogIn_Activity.this,RegisterNewUser.class);
                startActivity(page);
            }else Toast.makeText(this,"Please check your Internet connection!",Toast.LENGTH_SHORT).show();
        }
    }


    OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
        @Override
        public void onResultSuccess(final String message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (message)
                    {
                        case "success":
                            progressBar.setVisibility(View.VISIBLE);
                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try
                                    {
                                        Thread.sleep(2500);

                                        //current user info,user log in status,user type save in shared preference
                                        preference.savePreferenceData(PREF_NAME,true);
                                        preference.savePrefPhoneNumber(PREF_NAME,e_phoneNumber.getText().toString());
                                        startActivity(new Intent(LogIn_Activity.this,HomePage.class));
                                        finish();
                                    }catch (InterruptedException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            thread.start();
                            break;
                        default:
                            Toast.makeText(LogIn_Activity.this, "Log in failed.", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });
        }
    };



                                                                 //Firebase authentication with facebook start from here
/*
    @Override
    public void onStart() {
        firebaseAuth = FirebaseAuth.getInstance();
        super.onStart();

        String phoneNumber;
        if(allSharedPreference.getPreferenceSession(PREF_NAME))
        {
            phoneNumber = allSharedPreference.getPreferencePhone(PREF_NAME);
            Intent page = new Intent(LogIn_Activity.this,HomePage.class);
            page.putExtra("phone",phoneNumber);
            ShowMyOrderList.setPhoneNumber(phoneNumber);
            HomePage.getPhone(phoneNumber);
            OrderHistoryList.setPhoneNumber(phoneNumber);
            allSharedPreference.savePreferenceData("session",true);
            startActivity(page);
        }else
        {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            updateUI(currentUser);
        }
    }

    private void LogInWithFacebook()
    {
        loginButton.setReadPermissions("email","public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(LogIn_Activity.this,"Log in Canceled",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LogIn_Activity.this,"Log in Failed",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleFacebookAccessToken(final AccessToken token) {

        final ProgressDialog ringProgressDialog = ProgressDialog.show(LogIn_Activity.this, "Please wait", "Authenticating", true);
        ringProgressDialog.setCancelable(true);
        new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4500);
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    updateUI(user);
                } catch (Exception e) {
                }
                ringProgressDialog.dismiss();
            }
        }).start();
       ringProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
           @Override
           public void onDismiss(DialogInterface dialog) {
               firebaseAuth = FirebaseAuth.getInstance();
               AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
               firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isComplete())
                       {
                           FirebaseUser user = firebaseAuth.getCurrentUser();
                           updateUI(user);
                       }
                       else
                       {
                           updateUI(null);
                           Toast.makeText(LogIn_Activity.this,"failed",Toast.LENGTH_SHORT).show();
                       }
                   }
               });
           }
       });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            userId = user.getUid();
            facebookName = user.getDisplayName();
            facebookEmail = user.getEmail();
            HomePage.getFacebookInformation(userId,facebookName,facebookEmail);
            Intent page = new Intent(LogIn_Activity.this,HomePage.class);
            HomePage.getPhone("facebook");
            startActivity(page);
        }else Toast.makeText(this,"null user",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }*/
                                                               //Firebase authentication with facebook finished here



                                                                //Remember phone and password code start here
    private void initialize()
    {
        sharedPreferences = getSharedPreferences(pref, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if(sharedPreferences.getBoolean(u_checkbox,false))checkBox.setChecked(true);
        else checkBox.setChecked(false);
        e_phoneNumber.setText(sharedPreferences.getString(u_name,""));
        e_password.setText(sharedPreferences.getString(u_password,""));
        e_phoneNumber.addTextChangedListener(this);
        e_password.addTextChangedListener(this);
        checkBox.setOnCheckedChangeListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {}

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        set_data();
    }

    private void set_data()
    {
        if(checkBox.isChecked())
        {
            editor.putString(u_name,e_phoneNumber.getText().toString().trim());
            editor.putString(u_password,e_password.getText().toString().trim());
            editor.putBoolean(u_checkbox,true);
            editor.apply();
        }
        else
        {
            editor.putBoolean(u_checkbox,false);
            editor.remove(u_name);
            editor.remove(u_password);
            editor.apply();
        }
    }

                                                                //Remember phone and password code finished here
    //For exit app

    private void clossApp()
    {
        if(getIntent().getBooleanExtra("flag",false))finish();
    }

}



