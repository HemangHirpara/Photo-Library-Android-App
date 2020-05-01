package com.example.photoapp2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.*;
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

public class Search extends AppCompatActivity {

    private ArrayList<Photo> allPhotoList;
    private ArrayList<Album> albums;
    private ImageView ssImageView;
    private TextView ssCaption;
    private String filePath;
    private EditText searchPerson, searchLocation;
    private ListView searchList;
    private CustomListViewAdapter adapter1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
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
        allPhotoList = new ArrayList<>();
        for(Album a : albums){
            for(Photo p : a.getPhotos()){
                allPhotoList.add(p);
            }
        }
        //get data necessary to show album
        setTitle("Searching Albums");
        searchPerson = findViewById(R.id.searchPerson);
        searchLocation = findViewById(R.id.searchLocation);
        searchList=findViewById(R.id.search_list);
        searchList.setTextFilterEnabled(true);

        adapter1 = new CustomListViewAdapter(this,
                R.layout.photo_lv_item, allPhotoList);
        searchList.setAdapter(adapter1);

        searchPerson.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
                Search.this.adapter1.getFilter().filter(arg0);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }
        });
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
