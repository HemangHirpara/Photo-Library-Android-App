package com.example.photoapp2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class MovePhoto extends AppCompatActivity {
    public static final String PHOTO_INDEX = "photo_pos";
    public static final String ALBUM_INDEX = "album_pos";

    private ArrayList<Album> albums;
    private int photoIndex, albumIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.move_photo);
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
            photoIndex = bundle.getInt(PHOTO_INDEX);
            albumIndex = bundle.getInt(ALBUM_INDEX);
        }

        ListView albumList = findViewById(R.id.album_list_move);
        albumList.setAdapter(new ArrayAdapter<>(this, R.layout.album, albums));

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
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
