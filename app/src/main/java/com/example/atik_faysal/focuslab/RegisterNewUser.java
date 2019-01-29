package com.example.atik_faysal.focuslab;

import android.*;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import backend.SaveInfoBackgroundTask;
import backend.SaveInfoBackgroundTask.OnAsyncTaskInterface;

import static droidninja.filepicker.FilePickerConst.REQUEST_CODE;

public class RegisterNewUser extends AppCompatActivity
{
    private EditText e_name;
    private EditText e_email;
    private EditText e_password;
    private EditText e_conPassword;
    private EditText e_address;
    private Button b_save;
    private ProgressBar progressBar;

    CheckInternet checkInternet;
    SaveInfoBackgroundTask backgroundTask;
    AllSharedPreference preference;
    private String name,email,password,confirmPassword,address;

    private static final int FRAMEWORK_REQUEST_CODE = 1;

    private int nextPermissionsRequestCode = 4000;
    private final Map<Integer, OnCompleteListener> permissionsListeners = new HashMap<>();

    private final static String URL = "http://app.focusdigitalcolorlab.com/createNewUser.php";
    private static String DATA;
    private static final String PREF_NAME = "session";


    private interface OnCompleteListener {
        void onComplete();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_user);
        Firebase.setAndroidContext(this);
        checkInternet = new CheckInternet(this);
        initializeAllComponent();
        buttonClicked();
    }

    protected void initializeAllComponent()
    {
        e_name = (EditText)findViewById(R.id.e_userName);
        e_email = (EditText)findViewById(R.id.e_email);
        //e_phoneNumber = (EditText)findViewById(R.id.e_phoneNumber);
        e_password = (EditText)findViewById(R.id.e_password);
        e_conPassword = (EditText)findViewById(R.id.e_conPassword);
        e_address = (EditText)findViewById(R.id.e_address);
        b_save = (Button)findViewById(R.id.b_save);
        progressBar = findViewById(R.id.progress);
        preference = new AllSharedPreference(this);
    }

    private void buttonClicked()
    {
        b_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(ActivityCompat.checkSelfPermission(RegisterNewUser.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(RegisterNewUser.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE);
                if(ActivityCompat.checkSelfPermission(RegisterNewUser.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(RegisterNewUser.this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_CODE);
                if(ActivityCompat.checkSelfPermission(RegisterNewUser.this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(RegisterNewUser.this, new String[]{Manifest.permission.RECEIVE_SMS}, REQUEST_CODE);
                userInformation();
            }
        });
    }

    protected void userInformation()
    {
        name =  e_name.getText().toString();
        email = e_email.getText().toString();
        password = e_password.getText().toString();
        address = e_address.getText().toString();
        confirmPassword = e_conPassword.getText().toString();
        if(checkAllUserInformation(name,password,confirmPassword,email,address))
        {
            if(checkInternet.check_internet())onLogin(LoginType.PHONE);
            else Toast.makeText(RegisterNewUser.this,"Please Check your Internet Connection",Toast.LENGTH_LONG).show();
        }
    }

    protected boolean checkAllUserInformation(String name,String password,String cPassword,String email,String address)
    {
        boolean flag = true;
        if(e_name.getText().toString().isEmpty())
        {
            e_name.setError("Name can not be Empty");
            flag = false;
        }
        if(e_email.getText().toString().isEmpty())
        {
            e_email.setError("Email can not be Empty");
            flag = false;
        }
        if(password.isEmpty())
        {
            e_password.setError("Can not be Empty");
            flag = false;
        }
        if(cPassword.isEmpty())
        {
            e_conPassword.setError("Can not be Empty");
            flag = false;
        }
        if(e_address.getText().toString().isEmpty())
        {
            e_address.setError("Can not be Empty");
            flag = false;
        }
        Pattern ps = Pattern.compile("^[a-zA-Z ]+$+[0-9]");
        Matcher ms = ps.matcher(name);
        if(ms.matches())
        {
            e_name.setError("Invalid Name");
            flag = false;
        }

        if(!password.equals(cPassword))
        {
            e_conPassword.setError("Password does not match");
            flag = false;
        }
        if(password.length()<6||password.length()>25)
        {
            e_password.setError("Password must be in 6-25 Characters");
            flag = false;
        }
        if(flag==true)return true;
        else return false;
    }

    private void goToLogInPage()
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    Thread.sleep(2500);

                    //current user info,user log in status,user type save in shared preference
                    preference.savePreferenceData(PREF_NAME,true);
                    startActivity(new Intent(RegisterNewUser.this,HomePage.class));
                    finish();
                }catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }


    private void newUserRegistration(String phone)
    {
        try {
            DATA = URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(name,"UTF-8")+"&"
                    +URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(email,"UTF-8")+"&"
                    +URLEncoder.encode("phone","UTF-8")+"="+URLEncoder.encode(phone,"UTF-8")+"&"
                    +URLEncoder.encode("address","UTF-8")+"="+URLEncoder.encode(address,"UTF-8")+"&"
                    +URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8");

            backgroundTask = new SaveInfoBackgroundTask(this);
            backgroundTask.setOnResultListener(onAsyncTaskInterface);
            backgroundTask.execute(URL,DATA);
            preference.savePrefPhoneNumber(PREF_NAME,phone);
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
                    switch (message)
                    {
                        case "success":
                            goToLogInPage();
                        default:
                            Toast.makeText(RegisterNewUser.this,"error : "+message,Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });
        }
    };


    //verify phone number start using facebook account kit
    @Override
    protected void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != FRAMEWORK_REQUEST_CODE) {
            return;
        }

        final String toastMessage;
        final AccountKitLoginResult loginResult = AccountKit.loginResultWithIntent(data);
        if (loginResult == null || loginResult.wasCancelled())
        {
            toastMessage = "Cancelled";
            Toast.makeText(RegisterNewUser.this,toastMessage,Toast.LENGTH_SHORT).show();
        }
        else if (loginResult.getError() != null) {
            Toast.makeText(RegisterNewUser.this,"Error",Toast.LENGTH_SHORT).show();
        } else {
            final AccessToken accessToken = loginResult.getAccessToken();
            if (accessToken != null) {

                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                    @Override
                    public void onSuccess(final Account account) {

                            String phoneNumber = account.getPhoneNumber().toString();

                            if(phoneNumber.length()==14)
                                phoneNumber = phoneNumber.substring(3);
                            else phoneNumber = account.getPhoneNumber().toString();

                            newUserRegistration(phoneNumber);
                            progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(final AccountKitError error) {
                    }
                });
            } else {
                toastMessage = "Unknown response type";
                Toast.makeText(RegisterNewUser.this,toastMessage,Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onLogin(final LoginType loginType) {
        final Intent intent = new Intent(this, AccountKitActivity.class);
        final AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder = new AccountKitConfiguration.AccountKitConfigurationBuilder(loginType, AccountKitActivity.ResponseType.TOKEN);
        final AccountKitConfiguration configuration = configurationBuilder.build();
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configuration);
        OnCompleteListener completeListener = new OnCompleteListener() {
            @Override
            public void onComplete() {
                startActivityForResult(intent, FRAMEWORK_REQUEST_CODE);
            }
        };
        if (configuration.isReceiveSMSEnabled() && !canReadSmsWithoutPermission()) {
            final OnCompleteListener receiveSMSCompleteListener = completeListener;
            completeListener = new OnCompleteListener() {
                @Override
                public void onComplete() {
                    requestPermissions(
                            android.Manifest.permission.RECEIVE_SMS,
                            R.string.permissions_receive_sms_title,
                            R.string.permissions_receive_sms_message,
                            receiveSMSCompleteListener);
                }
            };
        }
        if (configuration.isReadPhoneStateEnabled() && !isGooglePlayServicesAvailable()) {
            final OnCompleteListener readPhoneStateCompleteListener = completeListener;
            completeListener = new OnCompleteListener() {
                @Override
                public void onComplete() {
                    requestPermissions(
                            Manifest.permission.READ_PHONE_STATE,
                            R.string.permissions_read_phone_state_title,
                            R.string.permissions_read_phone_state_message,
                            readPhoneStateCompleteListener);
                }
            };
        }
        completeListener.onComplete();
    }

    private boolean isGooglePlayServicesAvailable() {
        final GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int googlePlayServicesAvailable = apiAvailability.isGooglePlayServicesAvailable(this);
        return googlePlayServicesAvailable == ConnectionResult.SUCCESS;
    }

    private boolean canReadSmsWithoutPermission() {
        final GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int googlePlayServicesAvailable = apiAvailability.isGooglePlayServicesAvailable(this);
        if (googlePlayServicesAvailable == ConnectionResult.SUCCESS) {
            return true;
        }
        return false;
    }

    private void requestPermissions(
            final String permission,
            final int rationaleTitleResourceId,
            final int rationaleMessageResourceId,
            final OnCompleteListener listener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (listener != null) {
                listener.onComplete();
            }
            return;
        }

        checkRequestPermissions(permission, rationaleTitleResourceId, rationaleMessageResourceId, listener);
    }

    @TargetApi(23)
    private void checkRequestPermissions(final String permission, final int rationaleTitleResourceId, final int rationaleMessageResourceId, final OnCompleteListener listener) {
        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            if (listener != null) {
                listener.onComplete();
            }
            return;
        }

        final int requestCode = nextPermissionsRequestCode++;
        permissionsListeners.put(requestCode, listener);

        if (shouldShowRequestPermissionRationale(permission)) {
            new AlertDialog.Builder(this).setTitle(rationaleTitleResourceId).setMessage(rationaleMessageResourceId).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    requestPermissions(new String[] { permission }, requestCode);
                }
            }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    permissionsListeners.remove(requestCode);
                }
            }).setIcon(android.R.drawable.ic_dialog_alert).show();
        } else requestPermissions(new String[]{ permission }, requestCode);

    }

    @TargetApi(23)
    @SuppressWarnings("unused")
    @Override
    public void onRequestPermissionsResult(final int requestCode,final @NonNull String permissions[], final @NonNull int[] grantResults) {
        final OnCompleteListener permissionsListener = permissionsListeners.remove(requestCode);
        if (permissionsListener != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            permissionsListener.onComplete();
        }
    }
}
