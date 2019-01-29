package com.example.atik_faysal.focuslab;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.support.v7.widget.Toolbar;


public class PaymentOption extends AppCompatActivity
{
    private Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_layout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
    }
    public void choosePayment(View view)
    {
        if(view.getId()==R.id.b_bkash)
        {
            Intent page = new Intent(PaymentOption.this,PaymentInBkash.class);
            startActivity(page);
        }
        if (view.getId()==R.id.b_nexus)setAlertDialog();
        if (view.getId()==R.id.b_masterCard)setAlertDialog();
        if(view.getId()==R.id.b_paypal)setAlertDialog();
    }


    private void setAlertDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Sorry! This payment method is now under construction.");
        builder.setPositiveButton("Ok",null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
