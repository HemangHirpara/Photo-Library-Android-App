package com.example.photoapp2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class Search extends AppCompatActivity {

    private ArrayList<Album> albums;
    private EditText searchPerson;
    private EditText searchLocation;
    private SearchAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
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
        ArrayList<Photo> allPhotoList = new ArrayList<>();
        for(Album a : albums){
            allPhotoList.addAll(a.getPhotos());
        }
        //get data necessary to show album
        setTitle("Searching Albums");
        searchPerson = findViewById(R.id.searchPerson);
        searchLocation = findViewById(R.id.searchLocation);
        ListView searchList = findViewById(R.id.search_list);
        searchList.setTextFilterEnabled(true);
        adapter = new SearchAdapter(this, allPhotoList);
        searchList.setAdapter(adapter);

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
                        adapter.getFilter().filter("PERSON" + s);
                    else if(et == searchLocation)
                        adapter.getFilter().filter("LOCATION" + s);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }

    public void advSearch(View view) {
        //if advSearch is clicked go to new activity with two textfields and two radio buttons and/or
        Intent intent = new Intent(this, AdvanceSearch.class);
        startActivity(intent);
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
