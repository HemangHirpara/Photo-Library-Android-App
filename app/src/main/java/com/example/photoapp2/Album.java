package com.example.photoapp2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Album implements Serializable {
    private static final long serialVersionUID = 1L;
    private String albumName;
    private List<Photo> photos;

    public Album(String s) {
        this.albumName = s;
        this.photos = new ArrayList<>();
    }

    /**
     * Adds a new photo and updates dateRange
     * @param photo to Add
     */
    public boolean addPhoto(Photo photo){
        for(Photo p : photos){
            if(p.equals(photo))
                return false;
        }
        photos.add(photo);
        return true;
    }

    /**
     * Remove photo from photo list and recompute start and end dates
     * @param photo photo to be removed
     * @return true if photo can be removed, false if it does not exist
     */
    public boolean removePhoto(Photo photo){
        if(!photos.contains(photo))
            return false;
        photos.remove(photo);
        return true;
    }

    /**
     * setter -> set photo list for album
     * @param photos List<Photo> photos to set
     */
    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    /**
     * getter -> returns List of Photos in album
     * @return List<Photo>
     */
    public List<Photo> getPhotos(){return  this.photos; }

    public String toString() {return  this.albumName+"\n# of Photos: "+this.photos.size();}

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    /**
     * equals method to check if two albums are equal based on album name
     * @param that
     * @return
     */
    public boolean equals(Album that) {return this.getAlbumName().toLowerCase().equals(that.getAlbumName().toLowerCase());}
}
