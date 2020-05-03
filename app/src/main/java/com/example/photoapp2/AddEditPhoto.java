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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.*;
import java.util.ArrayList;

public class AddEditPhoto extends AppCompatActivity {

    public static final String PHOTO_INDEX = "photo_pos";
    public static final String ALBUM_INDEX = "album_pos";
    private static final String PERSON_TAG = "person_tag";
    private static final String LOCATION_TAG = "location_tag";
    private static final int PHOTO_VIEW_CANCELED = 44;
    private static final int MOVE_PHOTO_CODE = 55;

    private int photoIndex, albumIndex;
    private Photo photo;
    private ArrayList<Album> albums;

    private String photoName, filePath;
    private Uri photoUri;
    private EditText personField;
    private EditText locationField;

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
            ArrayList<Photo> photoList = (ArrayList<Photo>) albums.get(albumIndex).getPhotos();
            photo = photoList.get(photoIndex);
            photoName = photo.getCaption();
            photoUri = Uri.parse(photo.getPhotoFile());
        }
        setTitle("Photo Display");
        ImageView imageView = findViewById(R.id.ssImageView);
        imageView.setImageURI(photoUri);
        TextView captionView = findViewById(R.id.photo_caption);
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

    public void movePhoto(View view){
        Bundle bundle = new Bundle();
        bundle.putInt(AddEditPhoto.ALBUM_INDEX, albumIndex);
        bundle.putInt(AddEditPhoto.PHOTO_INDEX, photoIndex);
        Intent intent = new Intent(this, MovePhoto.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, MOVE_PHOTO_CODE);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(intent == null) return;
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return;
        }
        if(requestCode == MOVE_PHOTO_CODE){
            //Move photo from srcAlbum to desAlbum, removing it from srcAlbum
            Album desAlbum = albums.get(bundle.getInt(ALBUM_INDEX));
            Album srcAlbum = albums.get(bundle.getInt("currAlbum"));
            int photoToMoveIndex = bundle.getInt(PHOTO_INDEX);
            Photo p = srcAlbum.getPhotos().get(photoToMoveIndex);
            if(desAlbum.getPhotos().contains(p)) {
                Toast.makeText(this, "Photo Already in Album", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(AddEditPhoto.this, AlbumView.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
            else {
                srcAlbum.removePhoto(p);
                desAlbum.addPhoto(p);
                updateData();
                Toast.makeText(this, "Photo Moved", Toast.LENGTH_SHORT).show();
                //after moving photo, return to AlbumView to show reflected changes
                Intent i = new Intent(AddEditPhoto.this, AlbumView.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        }
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
        if (item.getItemId() == android.R.id.home) {
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

