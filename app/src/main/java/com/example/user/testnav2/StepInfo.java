package com.example.user.testnav2;

/**
 * Created by mark on 10/7/2018.
 */

public class StepInfo {
    private String name;
    private int imageId;
    private String time;

    public StepInfo(String name, int imageId, String time) {
        this.name = name;
        this.imageId = imageId;
        this.time = time;
    }

    public String getName() {
        return name;
    }
    public int getImageId(){
        return imageId;
    }
    public String getTime(){
        return time;
    }


}
