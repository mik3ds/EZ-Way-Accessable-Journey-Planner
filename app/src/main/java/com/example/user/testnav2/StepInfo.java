package com.example.user.testnav2;

/**
 * Created by mark on 10/7/2018.
 */

public class StepInfo {
    private String name;
    private int imageId;

    public StepInfo(String name, int imageId) {
        this.name = name;
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }
    public int getImageId(){
        return imageId;
    }

}
