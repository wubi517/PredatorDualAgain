package com.it_tech613.predator.models;

import java.io.Serializable;

public class RecordingModel implements Serializable {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    private String name;
    private String date;
    private String time;
    private String url;
    private String size;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    private int duration;

    public boolean isIs_checked() {
        return is_checked;
    }

    public void setIs_checked(boolean is_checked) {
        this.is_checked = is_checked;
    }

    private boolean is_checked;
}
