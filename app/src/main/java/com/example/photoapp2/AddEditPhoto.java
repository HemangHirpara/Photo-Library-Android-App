package com.example.photoapp2;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

//display tags similar to addEditAlbum
//resultCode 3 for delete
public class AddEditPhoto extends AppCompatActivity {

    public static final String PHOTO_INDEX = "photo_index";
    public static final String PHOTO_NAME = "photo_name";
    public static final String PHOTO_OPEN_FLAG = "open_photo_flag";
    public static final String PHOTO_LIST = "photo_list";
    public static final String PHOTO_URI = "photo_uri";

    private int photoIndex;
    private String photoName;
    private Uri photoUri;
    private ImageView imageView;
    private ArrayList<Photo> photoList;
    private int open_flag;

    private Button editBtn;
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
            open_flag = bundle.getInt("open_flag");
        }
        if(open_flag != 8){
            editBtn = findViewById(R.id.photo_edit);
        }
        imageView = findViewById(R.id.imageView);
        imageView.setImageURI(photoUri);
    }
}

