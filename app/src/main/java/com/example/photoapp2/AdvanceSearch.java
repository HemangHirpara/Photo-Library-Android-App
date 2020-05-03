package com.example.photoapp2;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class AdvanceSearch extends AppCompatActivity {

    private ArrayList<Photo> allPhotoList;
    private ArrayList<Photo> resultList;
    private ArrayList<Album> albums;
    private EditText advSearchPerson;
    private EditText advSearchLocation;
    private RadioGroup radioGroup;
    private ListView searchList;
    private SearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advance_search);
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
        allPhotoList = new ArrayList<>();
        for(Album a : albums){
            allPhotoList.addAll(a.getPhotos());
        }

        //get data necessary to show album
        setTitle("Advance Search Albums");
        resultList = new ArrayList<>();
        advSearchPerson = findViewById(R.id.advSearchPerson);
        advSearchLocation = findViewById(R.id.advSearchLocation);
        radioGroup = findViewById(R.id.RGroup);
        searchList=findViewById(R.id.advSearch_list);
        searchList.setTextFilterEnabled(true);
        adapter = new SearchAdapter(this, resultList);
        searchList.setAdapter(adapter);
    }

    public void search(View view) {
        resultList = new ArrayList<>();
        adapter = new SearchAdapter(this, resultList);
        searchList.setAdapter(adapter);
        //if et1.getText == null || et1.gettext < 1 dispaly error
        String personVal = advSearchPerson.getText().toString();
        String locationVal = advSearchLocation.getText().toString();
        if(personVal == null || personVal.length() < 1 || locationVal == null || locationVal.length() < 1 ) {
            Toast.makeText(this, "No Results for Query", Toast.LENGTH_SHORT).show();
            return;
        }
        int selectedRadio = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedRadio);
        //AND
        if(radioButton.getText().equals("AND")) {
            for (Photo p : allPhotoList) {
                if (p.getPersonTag().toLowerCase().startsWith(personVal) &&
                        p.getLocationTag().toLowerCase().startsWith(locationVal)) {
                    resultList.add(p);
                }
            }
        }
        else {
            for (Photo p : allPhotoList) {
                if (p.getPersonTag().toLowerCase().startsWith(personVal) ||
                        p.getLocationTag().toLowerCase().startsWith(locationVal)) {
                    resultList.add(p);
                }
            }
        }
        if(resultList.size() == 0)
            Toast.makeText(this, "No Results for Query", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, resultList.size()+" Results for Query", Toast.LENGTH_SHORT).show();
        adapter = new SearchAdapter(this, resultList);
        searchList.setAdapter(adapter);
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
     * @param event Detect event to remove keyboard
     * @return true if successful hide
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
