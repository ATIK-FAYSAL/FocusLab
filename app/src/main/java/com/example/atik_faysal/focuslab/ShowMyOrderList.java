package com.example.atik_faysal.focuslab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import backend.SaveInfoBackgroundTask;
import backend.SaveInfoBackgroundTask.OnAsyncTaskInterface;

/**
 * Created by user on 10/25/2017.
 */

public class ShowMyOrderList extends AppCompatActivity
{

    private List<MyOrderInformation>myOrderInformations;
    private ListView listView;
    private Toolbar toolbar;

    private MyOrderAdapter adapter;

    private final static String url = "http://app.focusdigitalcolorlab.com/myOrder.php";//"http://app.focusdigitalcolorlab.com/myOrder.php";
    private static String data;
    private static final String PREF_NAME = "session";

    private SaveInfoBackgroundTask backgroundTask;
    private AllSharedPreference preference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_my_order);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setToolbar();
        listView = (ListView)findViewById(R.id.listView);
        preference = new AllSharedPreference(this);
        getMyOrders();
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
                Close_activity(ShowMyOrderList.this,HomePage.class);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Close_activity(this,HomePage.class);
    }

    private static void Close_activity(Activity context, Class<?> clazz) {
        Intent intent = new Intent(context, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        context.finish();
    }



    private void getMyOrders()
    {
        try
        {
            data = URLEncoder.encode("phone","UTF-8")+"="+URLEncoder.encode(preference.getPreferencePhone(PREF_NAME),"UTF-8")+"&"
                    +URLEncoder.encode("action","UTF-8")+"="+URLEncoder.encode("due","UTF-8");
            backgroundTask = new SaveInfoBackgroundTask(this);
            backgroundTask.setOnResultListener(onAsyncTaskInterface);
            backgroundTask.execute(url,data);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    private void processJsonData(String jsonData)
    {
        myOrderInformations = new ArrayList<>();
        String name,amount,id,size,copy,date;
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObject.optJSONArray("order");
            int count=0;
            while (count< jsonArray.length())
            {
                JSONObject jObject = jsonArray.getJSONObject(count);
                name = jObject.getString("name");
                amount = jObject.getString("amount");
                id = jObject.getString("id");
                size = jObject.getString("size");
                copy = jObject.getString("copy");
                date = jObject.getString("date");
                myOrderInformations.add(new MyOrderInformation(amount,id,name,copy,size,date));
                count++;
            }
            adapter = new MyOrderAdapter(ShowMyOrderList.this,myOrderInformations);
            adapter.notifyDataSetChanged();
            listView.setAdapter(adapter);

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
                    if(message!=null)
                        processJsonData(message);
                }
            });
        }
    };

}


