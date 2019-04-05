package com.agelogeo.ultimatesaver;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public final class SavedDownloads {

    public static RecyclerView recyclerView ;
    public static ArrayList<Download> staticDownloads = new ArrayList<Download>();

    public static ArrayList<Download> getStaticDownloads() {
        return staticDownloads;
    }

    public static void setStaticDownloads(ArrayList<Download> staticDownloads) {
        SavedDownloads.staticDownloads = staticDownloads;
    }

    public static void addOnStaticDownloads(Download download){
        staticDownloads.add(download);
        //recyclerView.smoothScrollToPosition(0);
    }

    public static void addOnStaticDownloads(int position,Download download){
        staticDownloads.add(position,download);
    }

    public static Download getItemFromStaticDownloads(int position){
        return staticDownloads.get(position);
    }

    public static void removeFromStaticDownloads(int position){
        staticDownloads.remove(position);
    }

    public static void clearStaticDownloads(){
        staticDownloads.clear();
    }

    public static void setRecyclerView(RecyclerView recycler){
        recyclerView = recycler;
    }
}
