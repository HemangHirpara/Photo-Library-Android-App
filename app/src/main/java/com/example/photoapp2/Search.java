package com.example.photoapp2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
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
    private EditText searchPerson;
    private EditText searchLocation;
    private ListView searchList;
    private SearchAdapter adapter2;
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
        adapter2 = new SearchAdapter(this, allPhotoList);
        searchList.setAdapter(adapter2);

        ArrayList<EditText> editTexts = new ArrayList<>();
        editTexts.add(searchPerson);
        editTexts.add(searchLocation);
        for(EditText et : editTexts){
            et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(et == searchPerson)
                        adapter2.getFilter().filter("PERSON" + s);
                    else if(et == searchLocation)
                        adapter2.getFilter().filter("LOCATION" + s);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
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
