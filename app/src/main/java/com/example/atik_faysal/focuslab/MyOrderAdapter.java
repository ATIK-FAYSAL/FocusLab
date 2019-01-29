package com.example.atik_faysal.focuslab;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.deser.Deserializers;
import com.firebase.client.Firebase;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.ExecutionException;

import backend.SaveInfoBackgroundTask;
import backend.SaveInfoBackgroundTask.OnAsyncTaskInterface;

/**
 * Created by user on 10/25/2017.
 */

public class MyOrderAdapter extends BaseAdapter
{

    private List<MyOrderInformation>myOrderInformations;
    private SaveInfoBackgroundTask backgroundTask;
    Context context;

    public MyOrderAdapter(Context context,List<MyOrderInformation>myOrderInformations)
    {
        this.context = context;
        this.myOrderInformations = myOrderInformations;
    }

    @Override
    public int getCount() {
        return myOrderInformations.size();
    }

    @Override
    public Object getItem(int position) {
        return myOrderInformations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.order_list_textview,parent,false);
        final TextView txtAmount,txtPhotoId;
        final Button bPayment,bDelete;
        final TextView txtName, txtdate, trxId,txtPhotoSize,txtCopy;
        txtName = (TextView)view.findViewById(R.id.txtName);
        txtdate = (TextView)view.findViewById(R.id.txtDate);
        txtPhotoId = (TextView)view.findViewById(R.id.txtPhotoId);
        txtAmount = (TextView)view.findViewById(R.id.txtAmount);
        txtPhotoSize = (TextView)view.findViewById(R.id.txtSize);
        txtCopy = (TextView)view.findViewById(R.id.txtCopy);
        txtName.setText(myOrderInformations.get(position).getName());
        txtdate.setText(myOrderInformations.get(position).getDate());
        txtCopy.setText(myOrderInformations.get(position).getCopy()+" "+"Copies");
        txtPhotoSize.setText(" "+myOrderInformations.get(position).getPhotoSize());



        //txtAmount = (TextView) view.findViewById(R.id.txtBill);
        bPayment = (Button)view.findViewById(R.id.bPayment);
        bDelete = (Button)view.findViewById(R.id.bDelete);
        //txtPhotoId = (TextView)view.findViewById(R.id.txtPhotoId);
        txtAmount.setText(myOrderInformations.get(position).getAmmount());
        txtPhotoId.setText(myOrderInformations.get(position).getPhotoId());
        bPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentInBkash.paymentInfo("saveOrder",myOrderInformations.get(position).getPhotoId(),myOrderInformations.get(position).getAmmount());
                Intent intent = new Intent(context,PaymentOption.class);
                context.startActivity(intent);
            }
        });
        bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteOrder(myOrderInformations.get(position).photoId);
            }
        });
        return view;
    }


    private void deleteOrder(String id)
    {
        String data;
        String url = "http://app.focusdigitalcolorlab.com/deleteOrder.php";

        try {
            data = URLEncoder.encode("id","UTF-8")+"="+URLEncoder.encode(id,"UTF-8");
            backgroundTask = new SaveInfoBackgroundTask(context);
            backgroundTask.setOnResultListener(onAsyncTaskInterface);
            backgroundTask.execute(url,data);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
        @Override
        public void onResultSuccess(String message) {
                  switch (message)
                  {
                      case "successful":
                          context.startActivity(new Intent(context,ShowMyOrderList.class));
                          ((Activity)context).finish();
                          break;
                      default:
                          Toast.makeText(context,"Execution failed,please try again",Toast.LENGTH_SHORT).show();
                          break;
                  }
        }
    };

}
