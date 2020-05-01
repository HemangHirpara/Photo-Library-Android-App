package com.example.photoapp2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

//list all photos in album opened
public class PhotoView extends AppCompatActivity {

    //keys to get and send photo/album info
    public static final String PHOTO_INDEX = "photo_pos";
    public static final String ALBUM_INDEX = "album_pos";
    //keys to get and send tag info
    private static final String PERSON_TAG = "person_tag";
    private static final String LOCATION_TAG = "location_tag";
    //result codes from activities
    private static final int ADD_PHOTO_CODE = 11;
    private static final int EDIT_PHOTO_CODE = 22;
    private static final int DELETE_PHOTO_CODE = 33;
    private static final int PHOTO_VIEW_CANCELED = 44;

    private int albumIndex;
    private ArrayList<Photo> photos;
    private ArrayList<Album> albums;
    private ListView photoListView;
    private String filePath;
    private CustomListViewAdapter adapter;
    Uri selectedImageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo);
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
            albumIndex = bundle.getInt(ALBUM_INDEX);
            photos = (ArrayList<Photo>) albums.get(albumIndex).getPhotos();
        }
        setTitle(albums.get(albumIndex).getAlbumName() + ": Photo List");
        photoListView = findViewById(R.id.photo_list);
        adapter = new CustomListViewAdapter(this,
                R.layout.photo_lv_item, photos);
        photoListView.setAdapter(adapter);

        // show photo display for possible edit when tapped
        photoListView.setOnItemClickListener((p, V, pos, id) -> showPhoto(pos));
    }

    protected  void onResume() {
        super.onResume();
        adapter = new CustomListViewAdapter(this,
                R.layout.photo_lv_item, photos);
        photoListView.setAdapter(adapter);
    }

    private void showPhoto(int pos) {
        //transition to display photo view to add tags/move photo
        Bundle bundle = new Bundle();
        Photo photo = photos.get(pos);
        bundle.putInt(AddEditPhoto.PHOTO_INDEX, pos);
        bundle.putInt(AddEditPhoto.ALBUM_INDEX, albumIndex);
        Intent intent = new Intent(this, AddEditPhoto.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, EDIT_PHOTO_CODE);
    }

    private void addPhoto(){
        //open image gallery, and add selected image to photos
        //implement select image inside onResultActivity method
        selectImage(this);
        updateData();
    }

    private void selectImage(Context context) {
        final CharSequence[] options = {"Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose photo to add to album");

        builder.setItems(options, (dialog, item) -> {

            if (options[item].equals("Choose from Gallery")) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), ADD_PHOTO_CODE);

            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_PHOTO_CODE) {
            selectedImageUri = data.getData();
            Photo p = new Photo(selectedImageUri.toString());
            //get file name to display as caption
            Cursor cursor = getContentResolver().query(selectedImageUri, null, null, null, null);
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            cursor.moveToFirst();
            p.setCaption(cursor.getString(nameIndex));
            photos.add(p);
            updateData();
        }
        if(resultCode == EDIT_PHOTO_CODE){
            Bundle bundle = data.getExtras();
            if (bundle == null) {
                return;
            }
            int index = bundle.getInt(PHOTO_INDEX);
            String personVal = bundle.getString(PERSON_TAG);
            String locationVal = bundle.getString(LOCATION_TAG);
            Photo p = photos.get(index);
            p.setPersonTag(personVal);
            p.setLocationTag(locationVal);
        }
        if(resultCode == DELETE_PHOTO_CODE){
            //bundle
            Bundle bundle = data.getExtras();
            if (bundle == null) {
                return;
            }
            int index = bundle.getInt(PHOTO_INDEX);
            photos.remove(photos.get(index));
        }
        if(resultCode == PHOTO_VIEW_CANCELED){
            return;
        }
        adapter = new CustomListViewAdapter(this,
                R.layout.photo_lv_item, photos);
        photoListView.setAdapter(adapter);
        updateData();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_photo_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_photo:
                addPhoto();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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

