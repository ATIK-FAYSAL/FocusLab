package com.example.atik_faysal.focuslab;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import backend.SaveInfoBackgroundTask;
import backend.SaveInfoBackgroundTask.OnAsyncTaskInterface;

/**
 * Created by user on 10/18/2017.
 */

public class Feedback extends AppCompatActivity
{
    private EditText txtName,ePhone;
    private EditText eFeedback;
    private String phone;
    private Toolbar toolbar;

    private SaveInfoBackgroundTask backgroundTask;
    private AllSharedPreference sharedPreference;
    private static final String PREF_NAME = "session";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);
        sharedPreference = new AllSharedPreference(this);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        eFeedback = (EditText)findViewById(R.id.e_feedback);
        onSaveButtonClick();
        setSupportActionBar(toolbar);
        setToolbar();
        ePhone = (EditText)findViewById(R.id.ePhone);
        txtName = (EditText)findViewById(R.id.txt_name);
        txtName.setText(sharedPreference.getCurrentUserName());
        ePhone.setText(sharedPreference.getPreferencePhone(PREF_NAME));
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

    private void saveFeedback()
    {
        backgroundTask = new SaveInfoBackgroundTask(this);
        if(eFeedback.getText().toString().isEmpty()) Toast.makeText(this,"Please write something",Toast.LENGTH_SHORT).show();
        else
        {
            if(txtName.getText().toString().isEmpty()||ePhone.getText().toString().isEmpty())Toast.makeText(this,"Please enter name and phone number",Toast.LENGTH_SHORT).show();
            else
            {
                if(new CheckInternet(this).check_internet())
                {
                    String url = "http://app.focusdigitalcolorlab.com/feedback.php";
                    try {
                        String data = URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(txtName.getText().toString(),"UTF-8")+"&"
                                    +URLEncoder.encode("phone","UTF-8")+"="+URLEncoder.encode(ePhone.getText().toString(),"UTF-8")+"&"
                                    +URLEncoder.encode("feedback","UTF-8")+"="+URLEncoder.encode(eFeedback.getText().toString(),"UTF-8")+"&"
                                    +URLEncoder.encode("date","UTF-8")+"="+URLEncoder.encode(getDate(),"UTF-8");

                        backgroundTask.setOnResultListener(onAsyncTaskInterface);
                        backgroundTask.execute(url,data);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    protected String getDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        return dateFormat.format(calendar.getTime());
    }

    protected void onSaveButtonClick()
    {
        Button saveFeedbackButton = (Button) findViewById(R.id.b_feedback);
        saveFeedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFeedback();
            }
        });
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
                            Toast.makeText(Feedback.this,"Thank you for your feedback.",Toast.LENGTH_SHORT).show();
                            finish();
                            break;
                        default:
                            Toast.makeText(Feedback.this,"added failed.",Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });
        }
    };

}
