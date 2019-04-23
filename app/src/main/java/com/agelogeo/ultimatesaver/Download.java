package com.agelogeo.ultimatesaver;
import java.io.Serializable;
import java.util.ArrayList;

public class Download implements Serializable {
    String link ;
    String preview ;
    String file_path ;
    Boolean isVideo ;
    String username = "username";
    String profile_url;
    long time;

    public Download() {
        this.time = System.currentTimeMillis();

    }

    public Download(String preview,String link, String file_path, String username, String profile_url) {
        this.link = link;
        this.file_path = file_path;
        this.username = username;
        this.profile_url = profile_url;
        this.time = System.currentTimeMillis();
        this.preview = preview;
    }

    public String getProfile_url() {
        return profile_url;
    }

    public void setProfile_url(String profile_url) {
        this.profile_url = profile_url;
    }

    public void addOnLinks(String link,Boolean isVideo, String preview){
        this.link=link;
        this.isVideo=isVideo;
        this.preview=preview;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public Boolean isVideo() {
        return isVideo;
    }

    public void setVideo(Boolean video) {
        isVideo = video;
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
        return "{"+username+","+profile_url+"}\n{"+link+"  , "+isVideo+"\n"+"\n";
    }
}
