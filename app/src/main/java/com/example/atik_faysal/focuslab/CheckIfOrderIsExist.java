package com.example.atik_faysal.focuslab;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import backend.SaveInfoBackgroundTask;
import backend.SaveInfoBackgroundTask.OnAsyncTaskInterface;

/**
 * Created by USER on 1/10/2018.
 */

public class CheckIfOrderIsExist
{
        private SaveInfoBackgroundTask backgroundTask;
        private Context context;
        private String phone;
        private Activity activity;
        public CheckIfOrderIsExist(Context context,String phone)
        {
                this.context = context;
                this.phone = phone;
                this.activity = (Activity)context;
        }


        public void checkOrderIsExist(String url,String action)
        {
                try {
                        String data = URLEncoder.encode("phone","UTF-8")+"="+URLEncoder.encode(phone,"UTF-8")+"&"
                                +URLEncoder.encode("action","UTF-8")+"="+URLEncoder.encode(action,"UTF-8");
                        backgroundTask = new SaveInfoBackgroundTask(context);
                        backgroundTask.setOnResultListener(onAsyncTaskInterface);
                        backgroundTask.execute(url,data);
                } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                }
        }

        OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String message) {
                        activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        switch (message)
                                        {
                                                case "due":
                                                        context.startActivity(new Intent(context,ShowMyOrderList.class));
                                                        break;
                                                case "paid":
                                                        context.startActivity(new Intent(context,OrderHistoryList.class));
                                                        break;
                                                default:
                                                        context.startActivity(new Intent(context,NoOrderMessage.class));
                                                        break;
                                        }
                                }
                        });
                }
        };
}
