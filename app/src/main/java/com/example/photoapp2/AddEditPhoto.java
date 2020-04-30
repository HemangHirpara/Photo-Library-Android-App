package com.example.photoapp2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
    private static final String PERSON_TAG = "person_tag";
    private static final String LOCATION_TAG = "location_tag";
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
    private EditText personField;
    private EditText locationField;
    private Button deleteBtn;

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
            photoList = (ArrayList<Photo>) albums.get(albumIndex).getPhotos();
            photo = photoList.get(photoIndex);
            photoName = photo.getCaption();
            photoUri = Uri.parse(photo.getPhotoFile());
        }
        setTitle("Photo Display");
        imageView = findViewById(R.id.ssImageView);
        imageView.setImageURI(photoUri);
        captionView = findViewById(R.id.photo_caption);
        captionView.setText(photoName);
        personField = findViewById(R.id.person_tag_field);
        personField.setText(photo.getPersonTag());
        locationField = findViewById(R.id.location_tag_field);
        locationField.setText(photo.getLocationTag());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void cancelPhoto(View view){
        setResult(PHOTO_VIEW_CANCELED);
        finish();
    }
    public void savePhoto(View view){
        // gather all data from text fields
        String personValue = personField.getText().toString();
        String locationValue = locationField.getText().toString();

        // make Bundle
        Bundle bundle = new Bundle();
        bundle.putInt(PHOTO_INDEX, photoIndex);
        bundle.putString(PERSON_TAG, personValue);
        bundle.putString(LOCATION_TAG, locationValue);

        // send back to caller
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(22,intent);
        finish(); // pops activity from the call stack, returns to PhotoView
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

    public void slideshowBtn(View view){
        Bundle bundle = new Bundle();
        bundle.getInt(PHOTO_INDEX, photoIndex);
        bundle.putInt(ALBUM_INDEX, albumIndex);
        Intent intent = new Intent(this, Slideshow.class);
        intent.putExtras(bundle);
        startActivity(intent);
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

    /**
     * Hide soft keyboard by clicking on window
     * @param event
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }
}

