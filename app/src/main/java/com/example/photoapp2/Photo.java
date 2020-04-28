package com.example.photoapp2;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;
import java.io.Serializable;

public class Photo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String caption;
    private String photoUri;
    private String personTag;
    private String locationTag;
    /**
     * Constructor for photo object
     * @param photoFile File object for photo
     */
    public Photo(String photoFile){
        this.caption = "";
        this.photoUri = photoFile;
        this.personTag = "";
        this.locationTag = "";
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getPhotoFile() {
        return photoUri;
    }

    public void setPhotoFile(String photoFile) {
        this.photoUri = photoFile;
    }

    public String getPersonTag() {
        return personTag;
    }

    public void setPersonTag(String personTag) {
        this.personTag = personTag;
    }

    public String getLocationTag() {
        return locationTag;
    }

    public void setLocationTag(String locationTag) {
        this.locationTag = locationTag;
    }

    /**
     * Equals method to compare two photo objects based on objects
     * @param o
     * @return
     */
    public boolean equals(Object o){
        if (o == null || !(o instanceof Photo))
            return false;
        Photo op = (Photo) o;
        return this.photoUri.equals(op.photoUri);
    }
}
