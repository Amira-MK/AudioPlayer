package com.example.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Item implements Parcelable{
    public String nom ;
    public String contenu;
    public int image;

    String shortDescription = "";

    ArrayList<Item> itemList = new ArrayList<Item>();

    @Override
    public int describeContents() {
        return itemList != null ? itemList.size() : 0;
    }
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nom);
        dest.writeString(contenu);
        dest.writeInt(image);

    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    Item(String nom, String contenu, int image) {
        this.nom = nom;
        this.contenu = contenu;
        this.image = image;
    }

    protected Item(Parcel in) {
         nom = in.readString();
        contenu= in.readString();
        image = in.readInt();
    }




}
