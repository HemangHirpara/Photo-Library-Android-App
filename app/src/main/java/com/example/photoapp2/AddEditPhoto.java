package com.example.photoapp2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.*;
import java.util.ArrayList;

//display tags similar to addEditAlbum
//resultCode 3 for delete
public class AddEditPhoto extends AppCompatActivity {

    public static final String PHOTO_INDEX = "photo_pos";
    public static final String ALBUM_INDEX = "album_pos";
    public static final String PHOTO_NAME = "photo_name";
    public static final String PHOTO_OPEN_FLAG = "open_photo_flag";
    public static final String PHOTO_LIST = "photo_list";
    public static final String PHOTO_URI = "photo_uri";
    private static final int ADD_PHOTO_CODE = 11;
    private static final int EDIT_PHOTO_CODE = 22;
    private static final int DELETE_PHOTO_CODE = 33;
    private static final int PHOTO_VIEW_CANCELED = 44;

    private int photoIndex, albumIndex;
    private Photo photo;
    private ArrayList<Photo> photoList;
    private ArrayList<Album> albums;
    private int open_flag;

    private String photoName, filePath;
    private Uri photoUri;
    private ImageView imageView;
    private TextView captionView;

    private Button deleteBtn, editBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_photo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        filePath = getExternalFilesDir(null).getAbsolutePath() + File.separator + "album.data";
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
            photoIndex = bundle.getInt(PHOTO_INDEX);
            albumIndex = bundle.getInt(ALBUM_INDEX);
            open_flag = bundle.getInt(PHOTO_OPEN_FLAG);
            photoList = (ArrayList<Photo>) albums.get(albumIndex).getPhotos();
            System.out.println("GETTING PHOTO INFO TO DISPLAY listsize: " + photoList.size());
            photo = photoList.get(photoIndex);
            photoName = photo.getCaption();
            photoUri = Uri.parse(photo.getPhotoFile());
        }
        //if you are visiting addedit photo page via add button dont show edit/delete button
        if(open_flag != 8){
            deleteBtn = findViewById(R.id.photo_delete);
            deleteBtn.setVisibility(View.GONE);
        }
        imageView = findViewById(R.id.imageView);
        imageView.setImageURI(photoUri);
        captionView = findViewById(R.id.photo_caption);
        captionView.setText(photoName);
        System.out.println("ONCREATE EXECUTED: ADDEDITPHOTO");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.out.println("RESUMING LIST of PHOTOS, bypass oncreating?");
    }

    public void cancelPhoto(View view){
        setResult(PHOTO_VIEW_CANCELED);
        finish();
    }
    public void savePhoto(View view){

    }
    public void editPhoto(View view){
        //setResult(22, intent); //resultCode is 22 to identify editphoto
    }
    public void deletePhoto(View view) {
        // make Bundle
        Bundle bundle = new Bundle();
        bundle.putInt(PHOTO_INDEX, photoIndex);

        // send back to caller
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(33,intent); //resultcode is 33 to identify a deletephoto
        finish(); // pops activity from the call stack, returns to AlbumView onResultActivity
    }

    /**
     * Update data into album.data file for serialization
     */
    private void updateData() {
        String pathToFolder = getExternalFilesDir(null).getAbsolutePath();
        filePath = pathToFolder + File.separator + "album.data";
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            ObjectOutputStream ous = new ObjectOutputStream(fos);
            ous.writeObject(albums);
            ous.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

