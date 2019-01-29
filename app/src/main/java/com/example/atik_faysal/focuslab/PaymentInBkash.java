package com.example.atik_faysal.focuslab;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import backend.SaveInfoBackgroundTask;
import backend.SaveInfoBackgroundTask.OnAsyncTaskInterface;
import static android.content.ContentValues.TAG;

/**
 * Created by user on 10/13/2017.
 */

public class PaymentInBkash extends AppCompatActivity {
    private TextView txt_ammount;
    private EditText e_trxId;
    private static String Name, Phone, Email, PhotoId, PhotoSize, TotalCopy, TotalAmmount,Date,UserId,action;


    private CheckInternet internet;
    private SaveInfoBackgroundTask backgroundTask;
    private Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_in_bkash);
        initComponent();
        setSupportActionBar(toolbar);
        setToolbar();
    }

    private void initComponent()
    {
        txt_ammount = (TextView) findViewById(R.id.txt_ammount);
        e_trxId = (EditText) findViewById(R.id.e_trxId);
        toolbar = (Toolbar)findViewById(R.id.toolbar);

        internet = new CheckInternet(this);
        txt_ammount.setText(TotalAmmount);
    }

    public static void paymentInfo(String act,String id,String amount)
    {
        action = act;
        PhotoId = id;
        TotalAmmount = amount;
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

    public void onButtonClick(View view) {
        if (view.getId() == R.id.b_payment) {
            backgroundTask = new SaveInfoBackgroundTask(this);
               if(internet.check_internet())
               {
                   if(action.equals("saveOrder"))
                       paymentThisOrder();
                   else if(action.equals("newOrder"))
                       newOrderPayment();
               }
        }else if (view.getId() == R.id.b_cancel)
            finish();
    }


    public static void getAllDataList(String act,String name, String phone, String email, String photoId, String Size, String copy, String amount, String date) {
        action = act;
        Name = name;
        Phone = phone;
        Email = email;
        PhotoId = photoId;
        PhotoSize = Size;
        TotalCopy = copy;
        TotalAmmount = amount;
        Date = date;
    }

    private void paymentThisOrder()
    {
        String url = "http://app.focusdigitalcolorlab.com/paymentOrder.php";

        try {
            String data = URLEncoder.encode("action","UTF-8")+"="+URLEncoder.encode("sOrder","UTF-8")+"&"
                    +URLEncoder.encode("id","UTF-8")+"="+URLEncoder.encode(PhotoId,"UTF-8")+"&"
                    +URLEncoder.encode("trx","UTF-8")+"="+URLEncoder.encode(e_trxId.getText().toString(),"UTF-8");
            backgroundTask.setOnResultListener(onAsyncTaskInterface);
            backgroundTask.execute(url,data);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void newOrderPayment()
    {
        String url = "http://app.focusdigitalcolorlab.com/paymentOrder.php";

        try {
            String data = URLEncoder.encode("action")+"="+URLEncoder.encode("newOrder","UTF-8")+"&"
                    +URLEncoder.encode("name")+"="+URLEncoder.encode(Name,"UTF-8")+"&"
                    +URLEncoder.encode("phone")+"="+URLEncoder.encode(Phone,"UTF-8")+"&"
                    +URLEncoder.encode("email")+"="+URLEncoder.encode(Email,"UTF-8")+"&"
                    +URLEncoder.encode("size")+"="+URLEncoder.encode(PhotoSize,"UTF-8")+"&"
                    +URLEncoder.encode("copy")+"="+URLEncoder.encode(TotalCopy,"UTF-8")+"&"
                    +URLEncoder.encode("amount")+"="+URLEncoder.encode(TotalAmmount,"UTF-8")+"&"
                    +URLEncoder.encode("date")+"="+URLEncoder.encode(Date,"UTF-8")+"&"
                    +URLEncoder.encode("trx")+"="+URLEncoder.encode(e_trxId.getText().toString(),"UTF-8")+"&"
                    +URLEncoder.encode("id")+"="+URLEncoder.encode(PhotoId,"UTF-8");
            backgroundTask.setOnResultListener(onAsyncTaskInterface);
            backgroundTask.execute(url,data);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void setConfirmationMessage() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final ProgressDialog ringProgressDialog = ProgressDialog.show(PaymentInBkash.this, "Please wait", "Processing...", true);
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

                View view = LayoutInflater.from(PaymentInBkash.this).inflate(R.layout.delivary_message_window, null);
                builder.setTitle("Congratulation");
                builder.setView(view);
                TextView txtDate;
                txtDate = (TextView)view.findViewById(R.id.txtDate);
                txtDate.setText(delivaryDate());
                Button button = (Button) view.findViewById(R.id.B_Done);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setAlertDialog();
                        alertDialog.dismiss();
                    }
                });
            }
        });
    }

    private String delivaryDate()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        String date = dateFormat.format(calendar.getTime());
        return date;
    }

    private void setAlertDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Attention");
        builder.setMessage("Do you want to Exit? or Give another order?");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), LogIn_Activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("flag",true);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                closeActivity(PaymentInBkash.this,HomePage.class);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private static void closeActivity(Activity context, Class<?> clazz) {
        Intent intent = new Intent(context, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        context.finish();
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
                            setConfirmationMessage();
                            break;
                    }
                }
            });
        }
    };
}