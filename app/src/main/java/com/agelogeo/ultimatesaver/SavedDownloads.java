package com.agelogeo.ultimatesaver;

import java.util.ArrayList;

public final class SavedDownloads {

    public static ArrayList<Download> staticDownloads = new ArrayList<Download>();

    public static ArrayList<Download> getStaticDownloads() {
        return staticDownloads;
    }

    public static void setStaticDownloads(ArrayList<Download> staticDownloads) {
        SavedDownloads.staticDownloads = staticDownloads;
    }

    public static void addOnStaticDownloads(Download download){
        staticDownloads.add(download);
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
}
