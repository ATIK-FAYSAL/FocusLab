package com.example.atik_faysal.focuslab;

/**
 * Created by user on 10/25/2017.
 */

public class MyOrderInformation
{
    String ammount,photoId,trxId,name,copy,photoSize,date;
    public MyOrderInformation(String ammount,String photoId,String name,String copy,String photoSize,String date)
    {
        this.ammount = ammount;
        this.photoId = photoId;
        this.name = name;
        this.copy = copy;
        this.photoSize = photoSize;
        this.date = date;
    }

    public String getAmmount() {
        return ammount;
    }

    public void setAmmount(String ammount) {
        this.ammount = ammount;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCopy() {
        return copy;
    }

    public void setCopy(String copy) {
        this.copy = copy;
    }

    public String getPhotoSize() {
        return photoSize;
    }

    public void setPhotoSize(String photoSize) {
        this.photoSize = photoSize;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
