package com.example.photoapp2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class MovePhoto extends AppCompatActivity {
    public static final String PHOTO_INDEX = "photo_pos";
    public static final String ALBUM_INDEX = "album_pos";

    private ListView albumList;
    private ArrayList<Album> albums;
    private String filePath;
    private int photoIndex, albumIndex;
    private Photo photoToMove;
    private Album currAlbum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.move_photo);
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
            currAlbum = albums.get(albumIndex);
            photoToMove = currAlbum.getPhotos().get(photoIndex);
        }

        albumList = findViewById(R.id.album_list_move);
        albumList.setAdapter(new ArrayAdapter<Album>(this, R.layout.album, albums));

        // show movie for possible edit when tapped
        albumList.setOnItemClickListener((p, V, pos, id) -> move(pos));
    }

    private void move(int pos) {
        // make Bundle
        Bundle bundle = new Bundle();
        bundle.putInt(PHOTO_INDEX, photoIndex); //photo to move index
        bundle.putInt(ALBUM_INDEX, pos); //photo move destination album index
        bundle.putInt("currAlbum", albumIndex); //photo source album index

        // send back to caller
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK,intent);
        finish(); // pops activity from the call stack, returns to AlbumView
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
