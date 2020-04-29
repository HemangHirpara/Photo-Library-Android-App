package com.example.photoapp2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class AddEditAlbum extends AppCompatActivity {

    public static final String ALBUM_INDEX = "album_pos";
    public static final String ALBUM_NAME = "album_name";
    public static final String ALBUM_OPEN_FLAG = "album_open";
    public static final String ALBUM_LIST = "album_list";
    public static final String UPDATE_DATA = "update_data";

    private static final int OPEN_ALBUM_CODE = 123;

    private Button openBtn, deleteBtn;
    private int albumIndex;
    private int openButtonFlag;
    private EditText albumName;
    private String filePath;
    private ArrayList<Album> albums;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_album);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // activates the up arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get the fields
        albumName = findViewById(R.id.album_name);

        // see if info was passed in to populate fields
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            albumIndex = bundle.getInt(ALBUM_INDEX);
            openButtonFlag = bundle.getInt(ALBUM_OPEN_FLAG);
            albumName.setText(bundle.getString(ALBUM_NAME));
            filePath = bundle.getString(UPDATE_DATA);
            albums = (ArrayList<Album>) bundle.getSerializable(ALBUM_LIST);
        }
        //only show the open button if show/editing an album
        if(openButtonFlag != 9) {
            openBtn = findViewById(R.id.album_open);
            openBtn.setVisibility(View.GONE);
            deleteBtn = findViewById(R.id.album_delete);
            deleteBtn.setVisibility(View.GONE);
        }
    }

    public void cancel(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void save(View view) {
        // gather all data from text fields
        String name = albumName.getText().toString();

        // pop up dialog if errors in input, and return
        // name and year are mandatory
        if (name == null || name.length() == 0) {
            Bundle bundle = new Bundle();
            bundle.putString(PhotoDialogFragment.MESSAGE_KEY,
                    "Name and year are required");
            DialogFragment newFragment = new PhotoDialogFragment();
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(), "badfields");
            return; // does not quit activity, just returns from method
        }

        // make Bundle
        Bundle bundle = new Bundle();
        bundle.putInt(ALBUM_INDEX, albumIndex);
        bundle.putString(ALBUM_NAME,name);

        // send back to caller
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK,intent);
        finish(); // pops activity from the call stack, returns to parent
    }

    public void delete(View view) {
        // gather all data from text fields
        String name = albumName.getText().toString();

        // pop up dialog if errors in input
        if (name.length() == 0) {
            Bundle bundle = new Bundle();
            bundle.putString(PhotoDialogFragment.MESSAGE_KEY,
                    "Name of Album to Delete Required");
            DialogFragment newFragment = new PhotoDialogFragment();
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(), "badfields");
            return; // does not quit activity, just returns from method
        }

        // make Bundle
        Bundle bundle = new Bundle();
        bundle.putInt(ALBUM_INDEX, albumIndex);
        bundle.putString(ALBUM_NAME,name);

        // send back to caller
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(3,intent); //resultcode is 3 to identify a delete
        finish(); // pops activity from the call stack, returns to AlbumView onResultActivity
    }

    public void open(View view){
        Bundle bundle = new Bundle();
        bundle.putInt(AddEditAlbum.ALBUM_INDEX, albumIndex);
        bundle.putSerializable(AddEditAlbum.ALBUM_LIST, albums);

        Intent intent = new Intent(this, PhotoView.class);
        intent.putExtras(bundle);
        System.out.println("opening album in photoView");
        startActivityForResult(intent, OPEN_ALBUM_CODE);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        //do stuff after returning from open album such as deleted photo
        if(requestCode == OPEN_ALBUM_CODE){
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                albumIndex = bundle.getInt(ALBUM_INDEX);
                openButtonFlag = bundle.getInt(ALBUM_OPEN_FLAG);
                albumName.setText(bundle.getString(ALBUM_NAME));
                filePath = bundle.getString(UPDATE_DATA);
                albums = (ArrayList<Album>) bundle.getSerializable(ALBUM_LIST);
            }
            updateData();
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
}
