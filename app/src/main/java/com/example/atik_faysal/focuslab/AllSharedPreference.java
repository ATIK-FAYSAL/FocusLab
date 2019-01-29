package com.example.atik_faysal.focuslab;

import android.content.SharedPreferences;

import android.content.Context;

/**
 * Created by USER on 1/10/2018.
 */

public class AllSharedPreference
{
        private SharedPreferences sharedPreferences;
        private SharedPreferences.Editor editor;
        private Context context;


        public AllSharedPreference(Context context)
        {
                this.context = context;
        }


        public void savePreferenceData(String prName,boolean flag)
        {
                sharedPreferences = context.getSharedPreferences(prName,context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putBoolean("logIn",flag);
                editor.apply();
        }


        public void savePrefPhoneNumber(String prName,String phone)
        {
                sharedPreferences = context.getSharedPreferences(prName,context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString("phone",phone);
                editor.apply();

        }

        public String getPreferencePhone(String prefName)
        {
                String value;
                sharedPreferences = context.getSharedPreferences(prefName,context.MODE_PRIVATE);
                value = sharedPreferences.getString("phone","null");
                return value;
        }

        public void currentUserName(String name)
        {
                sharedPreferences = context.getSharedPreferences("userName",context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString("name",name);
                editor.apply();

        }

        public String getCurrentUserName()
        {
                String value;
                sharedPreferences = context.getSharedPreferences("userName",context.MODE_PRIVATE);
                value = sharedPreferences.getString("name","null");
                return value;
        }


        public boolean getPreferenceSession(String prefName)
        {
                sharedPreferences = context.getSharedPreferences(prefName,context.MODE_PRIVATE);
                if(sharedPreferences.getBoolean("logIn",false))return true;
                else return false;
        }

        public void putPhotoId(String prefName,String photoId)
        {
                sharedPreferences = context.getSharedPreferences(prefName,context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString("photoId",photoId);
                editor.apply();
        }

        public String getPhotoId(String prefName)
        {
                String value;
                sharedPreferences = context.getSharedPreferences(prefName,context.MODE_PRIVATE);
                value = sharedPreferences.getString("photoId","null");
                return value;
        }
}
