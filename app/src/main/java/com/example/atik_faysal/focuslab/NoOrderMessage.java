package com.example.atik_faysal.focuslab;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

/**
 * Created by USER on 11/29/2017.
 */

public class NoOrderMessage extends AppCompatActivity
{

        private Toolbar toolbar;
        private TextView textView;
        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.no_order_message);
                toolbar = (Toolbar)findViewById(R.id.toolbar);
                textView = (TextView)findViewById(R.id.text);
                setToolbar();
        }

        private void setToolbar()
        {
                textView.setText("No orders found");
                toolbar.setTitle("No result");
                setSupportActionBar(toolbar);
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

}
