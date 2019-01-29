package com.example.atik_faysal.focuslab;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

/**
 * Created by user on 10/28/2017.
 */

public class OrderHistoryAdapter extends BaseAdapter {

    private List<OrderHistory> orderHistories;
    Context context;

    public OrderHistoryAdapter(Context context, List<OrderHistory> myOrderInformations) {
        this.context = context;
        this.orderHistories = myOrderInformations;
    }

    @Override
    public int getCount() {
        return orderHistories.size();
    }

    @Override
    public Object getItem(int position) {
        return orderHistories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.show_my_order_history, parent, false);
        final TextView txtName, txtdate, txtPhotoId, trxId,txtAmount,txtPhotoSize,txtCopy;
        txtName = (TextView)view.findViewById(R.id.txtName);
        txtdate = (TextView)view.findViewById(R.id.txtDate);
        txtPhotoId = (TextView)view.findViewById(R.id.txtPhotoId);
        trxId = (TextView)view.findViewById(R.id.txtTrx);
        txtAmount = (TextView)view.findViewById(R.id.txtAmount);
        txtPhotoSize = (TextView)view.findViewById(R.id.txtSize);
        txtCopy = (TextView)view.findViewById(R.id.txtCopy);
        txtName.setText(orderHistories.get(position).getName());
        txtdate.setText(orderHistories.get(position).getDate());
        txtPhotoId.setText(orderHistories.get(position).getPhotoId());
        trxId.setText(" "+orderHistories.get(position).getTrxId());
        txtAmount.setText(" "+orderHistories.get(position).getAmmount());
        txtCopy.setText(orderHistories.get(position).getCopy()+" "+"Copies");
        txtPhotoSize.setText(" "+orderHistories.get(position).getPhotoSize());
        return view;
    }
}
