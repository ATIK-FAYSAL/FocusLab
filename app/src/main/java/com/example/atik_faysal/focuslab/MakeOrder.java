package com.example.atik_faysal.focuslab;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import android.content.Context;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import backend.SaveInfoBackgroundTask;
import backend.SaveInfoBackgroundTask.OnAsyncTaskInterface;

import static android.content.ContentValues.TAG;
import static droidninja.filepicker.FilePickerConst.REQUEST_CODE;


public class MakeOrder extends AppCompatActivity
{
    private RelativeLayout relativeLayout;
    private FirebaseStorage firebaseStorage;
    private EditText e_name,e_phone,e_email,e_photoId;
    private Spinner s_totalCopy,s_photoSize;
    private TextView txt_ammount;
    private String name,phone,email,photoId,photoSize,totalCopy;
    private static String PHONENUMBER;
    public static String taka;
    private EditText txt_name,txt_phone,txt_email;
    private String userId;
    private int[] copyList = new int[40];
    private int[] priceList = new int[20];
    private Toolbar toolbar;

    private Button bSave,bPayment;

    private static final String URL = "http://app.focusdigitalcolorlab.com/createOrder.php";
    private static String DATA;
    private static final String PREF_NAME = "session";

    private SaveInfoBackgroundTask backgroundTask;
    private AllSharedPreference preference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_order);
        firebaseStorage = FirebaseStorage.getInstance();
        PHONENUMBER =  getIntent().getExtras().getString("phone");
        Firebase.setAndroidContext(this);
        initializeAllComponent();
        selectPhotoSize();
        selectTotalCopy();
        showSnackbar();
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setToolbar();
        onButtonClick();
    }

    private void initializeAllComponent()
    {
        e_name = (EditText)findViewById(R.id.e_UserName);
        e_phone = (EditText)findViewById(R.id.e_UserPhone);
        e_email = (EditText)findViewById(R.id.e_UserEmail);
        e_photoId = (EditText)findViewById(R.id.e_PhotoId);
        txt_ammount = (TextView)findViewById(R.id.txt_Ammount);
        s_photoSize = (Spinner)findViewById(R.id.s_PhotoSize);
        s_totalCopy = (Spinner)findViewById(R.id.s_TotalCopy);
        bSave = findViewById(R.id.b_SaveOrder);
        bPayment = findViewById(R.id.b_Pay);

        preference = new AllSharedPreference(this);

        String url = "http://app.focusdigitalcolorlab.com/userInfo.php";
        try {
            String data = URLEncoder.encode("phone","UTF-8")+"="+URLEncoder.encode(preference.getPreferencePhone(PREF_NAME));
            backgroundTask = new SaveInfoBackgroundTask(this);
            backgroundTask.setOnResultListener(taskInterface);
            backgroundTask.execute(url,data);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        bSave.setEnabled(false);
        bPayment.setEnabled(false);

        final Drawable style = getResources().getDrawable(R.drawable.button_style);
        Drawable disable = getResources().getDrawable(R.drawable.disable_button);
        bPayment.setBackgroundDrawable(disable);
        bSave.setBackgroundDrawable(disable);
        txt_ammount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(txt_ammount.getText().toString().isEmpty())
                {
                    bSave.setEnabled(false);
                    bPayment.setEnabled(false);
                }else
                {
                    if(!txt_ammount.getText().toString().isEmpty())
                    {
                        bSave.setEnabled(true);
                        bPayment.setEnabled(true);
                        bPayment.setBackgroundDrawable(style);
                        bSave.setBackgroundDrawable(style);
                    }
                }
            }
        });


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

    private boolean orderInfo()
    {
        name = e_name.getText().toString();
        phone = e_phone.getText().toString();
        email = e_email.getText().toString();
        photoId = e_photoId.getText().toString();
        if(checkInformation(name,phone,photoId))return true;
        else  return false;
    }

    private boolean checkInformation(String name,String phone,String photoId)
    {
        boolean flag = true;
        Pattern ps = Pattern.compile("^[a-zA-Z ]+$+[0-9]");
        Matcher ms = ps.matcher(name);
        if(ms.matches())
        {
            e_name.setError("Invalid Name");
            flag = false;
        }
        if(phone.length()!=11&&phone.length()!=14)
        {
            e_phone.setError("Invalid Phone number");
            flag = false;
        }

        if(name.isEmpty()||phone.isEmpty()||photoId.isEmpty())flag = false;
        if(name.isEmpty())
        {
            e_name.setError("Name can not empty");
            flag = false;
        }
        if(phone.isEmpty())
        {
            e_phone.setError("phone can not be empty");
            flag = false;
        }
        if(photoId.isEmpty())
        {
            e_photoId.setError("Photo id can not empty");
            flag = false;
        }
        if(flag==true)return true;
        else return false;
    }

    private void onButtonClick()
    {
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(orderInfo())setConfirmationWindow("save");
            }
        });


        bPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(orderInfo())setConfirmationWindow("payment");
            }
        });
    }

    private void setConfirmationWindow(final String action)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(MakeOrder.this).inflate(R.layout.confirm_order_window,null);
        builder.setTitle("Confirmation");
        builder.setView(view);
        final TextView txtName,txtPhone,txtEmail,txtSize,txtCopy,txtAmount;
        txtName = (TextView)view.findViewById(R.id.e_name);
        txtPhone = (TextView)view.findViewById(R.id.e_phone);
        txtEmail = (TextView)view.findViewById(R.id.e_email);
        txtSize = (TextView)view.findViewById(R.id.e_photoSize);
        txtCopy = (TextView)view.findViewById(R.id.e_totalCopy);
        txtAmount = (TextView)view.findViewById(R.id.e_ammount);
        txtName.setText(name);
        txtPhone.setText(phone);
        txtEmail.setText(email);
        txtSize.setText(photoSize);
        txtCopy.setText(totalCopy);
        txtAmount.setText(taka+".00/=");
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Button done = (Button)view.findViewById(R.id.b_done);
        done.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String amount = txtAmount.getText().toString();
                String[] value = amount.split("/");
               if(action.equals("save"))
               {
                   try {
                       DATA = URLEncoder.encode("name")+"="+URLEncoder.encode(txtName.getText().toString(),"UTF-8")+"&"
                                +URLEncoder.encode("phone")+"="+URLEncoder.encode(txtPhone.getText().toString(),"UTF-8")+"&"
                               +URLEncoder.encode("email")+"="+URLEncoder.encode(txtEmail.getText().toString(),"UTF-8")+"&"
                               +URLEncoder.encode("size")+"="+URLEncoder.encode(txtSize.getText().toString(),"UTF-8")+"&"
                               +URLEncoder.encode("copy")+"="+URLEncoder.encode(txtCopy.getText().toString(),"UTF-8")+"&"
                               +URLEncoder.encode("amount")+"="+URLEncoder.encode(value[0],"UTF-8")+"&"
                               +URLEncoder.encode("date")+"="+URLEncoder.encode(getDate(),"UTF-8")+"&"
                               +URLEncoder.encode("id")+"="+URLEncoder.encode(e_photoId.getText().toString(),"UTF-8");

                       backgroundTask = new SaveInfoBackgroundTask(MakeOrder.this);
                       backgroundTask.setOnResultListener(onAsyncTaskInterface);
                       backgroundTask.execute(URL,DATA);

                   } catch (UnsupportedEncodingException e) {
                       e.printStackTrace();
                   }
                   alertDialog.dismiss();
               }else if(action.equals("payment"))
               {
                   PaymentInBkash.getAllDataList("newOrder",name,phone,email,photoId,photoSize,totalCopy,value[0],getDate());
                   Intent intent = new Intent(MakeOrder.this,PaymentOption.class);
                   startActivity(intent);
                   alertDialog.dismiss();
               }
            }
        });
    }

    private void selectPhotoSize()
    {
        ArrayAdapter<CharSequence> adapter;
        String key = getIntent().getExtras().getString("key");
        if(key.equals("official"))
        {
            adapter = ArrayAdapter.createFromResource(this,R.array.photoSize1,android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            s_photoSize.setAdapter(adapter);
        }else if(key.equals("gallery"))
        {
            adapter = ArrayAdapter.createFromResource(this,R.array.photoSize2,android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            s_photoSize.setAdapter(adapter);
        }
        s_photoSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                photoSize = parent.getItemAtPosition(position).toString();
                if(!photoSize.equals("Photo Size"))
                {
                    if(!totalCopy.equals("Total Copy"))
                    {
                        txt_ammount.setText(calculateTotalBill()+".00/=");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void selectTotalCopy()
    {
        ArrayAdapter<CharSequence> adapter;
        final String key = getIntent().getExtras().getString("key");
        if(key.equals("official"))
        {
            adapter = ArrayAdapter.createFromResource(this,R.array.Photo_Quantity,android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            s_totalCopy.setAdapter(adapter);
        }else if(key.equals("gallery"))
        {
            adapter = ArrayAdapter.createFromResource(this,R.array.Photo_Quantity1,android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            s_totalCopy.setAdapter(adapter);
        }
        s_totalCopy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                totalCopy = parent.getItemAtPosition(position).toString();
                if(!totalCopy.equals("Total Copy"))
                {
                    int copy = Integer.parseInt(totalCopy);
                    if(!photoSize.equals("Photo Size"))
                    {
                        if(photoSize.equals("Passport and Stamp")&&totalCopy.equals("4"))
                        {
                            setAlertDialog("6");
                        }
                        else if((photoSize.equals("Passport")||photoSize.equals("Passport and Stamp")||photoSize.equals("35mm x 45mm - Matt")||
                                photoSize.equals("35mm x 45mm - Glossy")||photoSize.equals("50mm x 50mm - Matt")||photoSize.equals("35mm x 50mm - Glossy"))&&(copy<10)&&(key.equals("gallery")))
                        {
                            setAlertDialog("10");
                        }

                        else
                        {
                            txt_ammount.setText(calculateTotalBill()+".00/=");
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void showSnackbar()
    {
        String photoPath = getIntent().getExtras().getString("photoPath");
        e_photoId.setText(photoPath);
        try
        {
            if(photoPath!=null)
            {
                relativeLayout = (RelativeLayout)findViewById(R.id.relativeLayout);
                Snackbar.make(relativeLayout,"Your last photo id : "+photoPath, Snackbar.LENGTH_SHORT).show();

            }
        }catch (NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    protected String getDate()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        String date = dateFormat.format(calendar.getTime());
        return date;
    }

    private void setAlertDialog(String str)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please select at least "+str+" copies");
        builder.setPositiveButton("Ok",null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private String calculateTotalBill()
    {
        copyList = new int[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,22,24,26,28,30,32,34,36,38,40};
        priceList = new int[]{8,10,15,20,25,40,50,130,160,300,350,450,600,750,950,1600};
        int i=0,TAKA=0;
        try
        {
            int copyX = Integer.parseInt(totalCopy);
            if(photoSize.equals("Passport")||photoSize.equals("Passport and Stamp")||photoSize.equals("4R 4 x 6"))
            {
                while(copyList[i]!=copyX)i++;
                TAKA = copyList[i]*priceList[1];
                i=0;
            }else if(photoSize.equals("35mm x 45mm - Matt")||photoSize.equals("35mm x 50mm - Matt")||photoSize.equals("50mm x 50mm - Matt"))
            {
                while(copyList[i]!=copyX)i++;
                TAKA = copyList[i]*priceList[3];
                i=0;
            }else if(photoSize.equals("50mm x 50mm - Glossy")||photoSize.equals("35mm x 45mm - Glossy")||photoSize.equals("35mm x 50mm - Glossy"))
            {
                while(copyList[i]!=copyX)i++;
                TAKA = copyList[i]*priceList[2];
                i=0;
            }else if(photoSize.equals("3R 3.5 x 5"))
            {
                while(copyList[i]!=copyX)i++;
                TAKA = copyList[i]*priceList[0];
                i=0;
            }else if(photoSize.equals("3R 3.5 x 5"))
            {
                while(copyList[i]!=copyX)i++;
                TAKA = copyList[i]*priceList[0];
                i=0;
            }else if(photoSize.equals("5R 5 x 7"))
            {
                while(copyList[i]!=copyX)i++;
                TAKA = copyList[i]*priceList[4];
                i=0;
            }else if(photoSize.equals("6R 6 x 8"))
            {
                while(copyList[i]!=copyX)i++;
                TAKA = copyList[i]*priceList[5];
                i=0;
            }else if(photoSize.equals("6L 6 x 9"))
            {
                while(copyList[i]!=copyX)i++;
                TAKA = copyList[i]*priceList[6];
                i=0;
            }else if(photoSize.equals("8R 8 x 10"))
            {
                while(copyList[i]!=copyX)i++;
                TAKA = copyList[i]*priceList[7];
                i=0;
            }else if(photoSize.equals("10R 10 x 12"))
            {
                while(copyList[i]!=copyX)i++;
                TAKA = copyList[i]*priceList[8];
                i=0;
            }else if(photoSize.equals("10L 10 x 15"))
            {
                while(copyList[i]!=copyX)i++;
                TAKA = copyList[i]*priceList[9];
                i=0;
            }else if(photoSize.equals("12R 12 x 16"))
            {
                while(copyList[i]!=copyX)i++;
                TAKA = copyList[i]*priceList[10];
                i=0;
            }else if(photoSize.equals("12L 12 x 18"))
            {
                while(copyList[i]!=copyX)i++;
                TAKA = copyList[i]*priceList[11];
                i=0;
            }else if(photoSize.equals("16R 16 x 20"))
            {
                while(copyList[i]!=copyX)i++;
                TAKA = copyList[i]*priceList[12];
                i=0;
            }else if(photoSize.equals("16R 16 x 20"))
            {
                while(copyList[i]!=copyX)i++;
                TAKA = copyList[i]*priceList[12];
                i=0;
            }else if(photoSize.equals("20R 20 x 24"))
            {
                while(copyList[i]!=copyX)i++;
                TAKA = copyList[i]*priceList[13];
                i=0;
            }else if(photoSize.equals("20L 20 x 30"))
            {
                while(copyList[i]!=copyX)i++;
                TAKA = copyList[i]*priceList[14];
                i=0;
            }else if(photoSize.equals("30L 30 x 40"))
            {
                while(copyList[i]!=copyX)i++;
                TAKA = copyList[i]*priceList[15];
                i=0;
            }
        }catch (NumberFormatException e)
        {
            e.printStackTrace();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        taka = TAKA+"";
        return taka;
    }

    private void processJsonData(String jsonData)
    {
        //myOrderInformations = new ArrayList<>();
        String name,phone,email;
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObject.optJSONArray("info");
            int count=0;
            while (count< jsonArray.length())
            {
                JSONObject jObject = jsonArray.getJSONObject(count);
                name = jObject.getString("name");
                phone = jObject.getString("phone");
                email = jObject.getString("email");
                e_name.setText(name);
                e_phone.setText(phone);
                e_email.setText(email);
                count++;
            }

        } catch (JSONException e) {
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
                            final ProgressDialog ringProgressDialog = ProgressDialog.show(MakeOrder.this, "Please wait", "Saving your order...", true);
                            ringProgressDialog.setCancelable(true);
                            new Thread(new Runnable() {
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
                                    startActivity(new Intent(MakeOrder.this,ShowMyOrderList.class));
                                    finish();
                                }
                            });
                            break;
                        default:
                            Toast.makeText(MakeOrder.this,"Exectution failed,please try again",Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });
        }
    };

    OnAsyncTaskInterface taskInterface = new OnAsyncTaskInterface() {
        @Override
        public void onResultSuccess(final String message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(message!=null)
                        processJsonData(message);
                }
            });
        }
    };

}