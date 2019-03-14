package com.agelogeo.ultimatesaver;
import java.util.ArrayList;

public class Download {
    ArrayList<String> links = new ArrayList<String>();
    ArrayList<String> file_paths = new ArrayList<String>();
    ArrayList<Boolean> isVideo = new ArrayList<Boolean>();
    String username ;
    String profile_url;
    long time;

    public Download() {
        this.time = System.currentTimeMillis();
        links = new ArrayList<String>();
        file_paths = new ArrayList<String>();
        isVideo = new ArrayList<Boolean>();
    }

    public Download(ArrayList<String> links, ArrayList<String> file_paths, String username, String profile_url) {
        this.links = links;
        this.file_paths = file_paths;
        this.username = username;
        this.profile_url = profile_url;
        this.time = System.currentTimeMillis();
    }

    public String getProfile_url() {
        return profile_url;
    }

    public void setProfile_url(String profile_url) {
        this.profile_url = profile_url;
    }

    public void addOnLinks(String link,Boolean isVideo){
        this.links.add(link);
        this.isVideo.add(isVideo);
    }

    public void addOnPaths(String path){
        this.file_paths.add(path);
    }

    public ArrayList<String> getLinks() {
        return links;
    }

    public void setLinks(ArrayList<String> links) {
        this.links = links;
    }

    public ArrayList<String> getFile_paths() {
        return file_paths;
    }

    public void setFile_paths(ArrayList<String> file_paths) {
        this.file_paths = file_paths;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void updateTime (long time){
        this.time = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        String array = "{";

        for(int i=0;i<links.size();i++){
            array+=links.get(i)+"  , "+isVideo.get(i)+"\n";
        }

        return "{"+username+","+profile_url+"}\n{"+array+"\n";
    }
}
