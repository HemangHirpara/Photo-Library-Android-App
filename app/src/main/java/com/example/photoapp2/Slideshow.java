package com.example.photoapp2;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class Slideshow extends AppCompatActivity {

    public static final String ALBUM_INDEX = "album_pos";
    public static final String PHOTO_INDEX = "photo_pos";
    public int photoIndex, albumIndex;
    private ArrayList<Photo> photoList;
    private ArrayList<Album> albums;
    private ImageView ssImageView;
    private TextView ssCaption;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slideshow);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String filePath = getExternalFilesDir(null).getAbsolutePath() + File.separator + "album.data";
        try {
            FileInputStream fis = new FileInputStream(filePath);
            ObjectInputStream ois = new ObjectInputStream(fis);
            albums = (ArrayList<Album>) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        //get data necessary to show album
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            albumIndex = bundle.getInt(ALBUM_INDEX);
            photoList = (ArrayList<Photo>) albums.get(albumIndex).getPhotos();
            photoIndex = bundle.getInt(PHOTO_INDEX);
        }
        setTitle("Slideshow: " + albums.get(bundle.getInt(ALBUM_INDEX)).getAlbumName());
        ssImageView = findViewById(R.id.ssImageView);
        ssImageView.setImageURI(Uri.parse(photoList.get(photoIndex).getPhotoFile()));
        ssCaption = findViewById(R.id.ssCaption);
        ssCaption.setText(photoList.get(photoIndex).getCaption());
    }

    public void nextBtnAction(View view){
        if(photoIndex+1<photoList.size()){
            photoIndex++;
            ssImageView.setImageURI(Uri.parse(photoList.get(photoIndex).getPhotoFile()));
            ssCaption.setText(photoList.get(photoIndex).getCaption());
        }
    }
    public void prevBtnAction(View view){
        if(photoIndex-1>=0){
            photoIndex--;
            ssImageView.setImageURI(Uri.parse(photoList.get(photoIndex).getPhotoFile()));
            ssCaption.setText(photoList.get(photoIndex).getCaption());
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
