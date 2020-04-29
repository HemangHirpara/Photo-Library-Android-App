package com.example.photoapp2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

//display tags similar to addEditAlbum
//resultCode 3 for delete
public class AddEditPhoto extends AppCompatActivity {

    public static final String PHOTO_INDEX = "photo_pos";
    public static final String PHOTO_NAME = "photo_name";
    public static final String PHOTO_OPEN_FLAG = "open_photo_flag";
    public static final String PHOTO_LIST = "photo_list";
    public static final String PHOTO_URI = "photo_uri";
    private static final int ADD_PHOTO_CODE = 11;
    private static final int EDIT_PHOTO_CODE = 22;
    private static final int DELETE_PHOTO_CODE = 33;
    private static final int PHOTO_VIEW_CANCELED = 44;

    private int photoIndex;
    private String photoName;
    private Uri photoUri;
    private ImageView imageView;
    private TextView captionView;
    private ArrayList<Photo> photoList;
    private int open_flag;

    private Button deleteBtn, editBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_photo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get data necessary to show album
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            photoIndex = bundle.getInt(PHOTO_INDEX);
            photoName = bundle.getString(PHOTO_NAME);
            photoList = (ArrayList<Photo>) bundle.getSerializable(PHOTO_LIST);
            photoUri = Uri.parse(bundle.getString(PHOTO_URI));
            open_flag = bundle.getInt(PHOTO_OPEN_FLAG);
        }
        //if you are visiting addedit photo page via add button dont show edit/delete button
        if(open_flag != 8){
            editBtn = findViewById(R.id.photo_edit);
            editBtn.setVisibility(View.GONE);
            deleteBtn = findViewById(R.id.photo_delete);
            deleteBtn.setVisibility(View.GONE);
        }
        imageView = findViewById(R.id.imageView);
        imageView.setImageURI(photoUri);
        captionView = findViewById(R.id.photo_caption);
        captionView.setText(photoName);
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

    }
    public void deletePhoto(View view) {
        // make Bundle
        Bundle bundle = new Bundle();
        bundle.putInt(PHOTO_INDEX, photoIndex);

        // send back to caller
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(33,intent); //resultcode is 3 to identify a delete
        finish(); // pops activity from the call stack, returns to AlbumView onResultActivity
    }
}

