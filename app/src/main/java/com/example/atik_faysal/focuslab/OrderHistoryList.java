package com.example.atik_faysal.focuslab;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import backend.SaveInfoBackgroundTask;

/**
 * Created by user on 10/28/2017.
 */

public class OrderHistoryList extends AppCompatActivity
{
    private List<OrderHistory> orderHistories;
    private ListView listView;
    private Toolbar toolbar;
    private OrderHistoryAdapter adapter;

    private final static String url = "http://app.focusdigitalcolorlab.com//myOrder.php";
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
        listView = (ListView)findViewById(R.id.listView);
        preference = new AllSharedPreference(this);
        setToolbar();
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
        getMyOrders();
    }


    private void getMyOrders()
    {
        try
        {
            data = URLEncoder.encode("phone","UTF-8")+"="+URLEncoder.encode(preference.getPreferencePhone(PREF_NAME),"UTF-8")+"&"
                    +URLEncoder.encode("action","UTF-8")+"="+URLEncoder.encode("paid","UTF-8");
            backgroundTask = new SaveInfoBackgroundTask(this);
            backgroundTask.setOnResultListener(onAsyncTaskInterface);
            backgroundTask.execute(url,data);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    private void processJsonData(String jsonData)
    {
        orderHistories = new ArrayList<>();
        String amount, photoId, trxId, name, copy, size, date;
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObject.optJSONArray("order");
            int count=0;
            while (count< jsonArray.length())
            {
                JSONObject jObject = jsonArray.getJSONObject(count);
                name = jObject.getString("name");
                amount = jObject.getString("amount");
                photoId = jObject.getString("id");
                size = jObject.getString("size");
                copy = jObject.getString("copy");
                date = jObject.getString("date");
                trxId = jObject.getString("trx");
                orderHistories.add(new OrderHistory(name, photoId, trxId, copy, size, amount, date));
                count++;
            }
            adapter = new OrderHistoryAdapter(OrderHistoryList.this, orderHistories);
            adapter.notifyDataSetChanged();
            listView.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    SaveInfoBackgroundTask.OnAsyncTaskInterface onAsyncTaskInterface = new SaveInfoBackgroundTask.OnAsyncTaskInterface() {
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
/*
   public static void setPhoneNumber(String phone) {
        phoneNumber = phone;
    }

    public void setDataInList()
    {

        orderHistories = new ArrayList<>();
        boolean flag = getIntent().getExtras().getBoolean("flag");
        String phone = getIntent().getExtras().getString("phone");
        com.google.firebase.database.Query query;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("order");
        if(flag)query = databaseReference.orderByChild("facebookId").equalTo(phone);
        else query = databaseReference.orderByChild("phone").equalTo(phone);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String amount, photoId, trxId, name, copy, photoSize, date;
                name = dataSnapshot.child("name").getValue(String.class);
                photoId = dataSnapshot.child("photoId").getValue(String.class);
                amount = dataSnapshot.child("totalAmount").getValue(String.class);
                trxId = dataSnapshot.child("trxId").getValue(String.class);
                photoSize = dataSnapshot.child("photoSize").getValue(String.class);
                date = dataSnapshot.child("date").getValue(String.class);
                copy = dataSnapshot.child("totalCopy").getValue(String.class);
                try
                {
                    if(!trxId.equals("Due")) {
                        orderHistories.add(new OrderHistory(name, photoId, trxId, copy, photoSize, amount, date));
                        OrderHistoryAdapter adapter;
                        adapter = new OrderHistoryAdapter(OrderHistoryList.this, orderHistories);
                        adapter.notifyDataSetChanged();
                        listView.setAdapter(adapter);
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

 */