package com.example.photoapp2;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.io.*;
import java.util.ArrayList;

public class AlbumView extends AppCompatActivity {

    private ListView listView;
    private ArrayList<Album> albums;
    private String filePath;
    public static final int EDIT_ALBUM_CODE = 1;
    public static final int ADD_ALBUM_CODE = 2;
    private static final int DELETE_ALBUM_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_list);

        String pathToFolder = getExternalFilesDir(null).getAbsolutePath();
        filePath = pathToFolder + File.separator + "album.data";
        File data = new File(filePath);
        if(!(data.exists() || data.isFile())){
            // serialize the data
            System.out.println("Created File");
            albums = new ArrayList<>();
            try {
                data.createNewFile();
                FileOutputStream fos = new FileOutputStream(filePath);
                ObjectOutputStream ous = new ObjectOutputStream(fos);
                ous.writeObject(albums);
                ous.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            System.out.println("Found File");
            try {
                FileInputStream fis = new FileInputStream(filePath);
                ObjectInputStream ois = new ObjectInputStream(fis);
                albums = (ArrayList<Album>) ois.readObject();
                ois.close();
                fis.close();
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }

        listView = findViewById(R.id.album_list);
        listView.setAdapter(new ArrayAdapter<Album>(this, R.layout.album, albums));

        // show movie for possible edit when tapped
        listView.setOnItemClickListener((p, V, pos, id) -> showAlbum(pos));
    }

    protected  void onResume() {
        super.onResume();
        listView.setAdapter(new ArrayAdapter<Album>(this, R.layout.album, albums));
    }
    protected void onStop() {
        super.onStop();
        updateData();
    }

    private void showAlbum(int pos) {
        Bundle bundle = new Bundle();
        Album album = albums.get(pos);
        bundle.putInt(AddEditAlbum.ALBUM_INDEX, pos);
        bundle.putString(AddEditAlbum.ALBUM_NAME, album.getAlbumName());
        bundle.putInt(AddEditAlbum.ALBUM_OPEN_FLAG, 9);
        bundle.putString(AddEditAlbum.UPDATE_DATA, filePath);
        bundle.putSerializable(AddEditAlbum.ALBUM_LIST, albums);
        Intent intent = new Intent(this, AddEditAlbum.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, EDIT_ALBUM_CODE);
    }

    private void addAlbum() {
        Intent intent = new Intent(this, AddEditAlbum.class);
        startActivityForResult(intent, ADD_ALBUM_CODE);
    }

    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent intent) {

        super.onActivityResult(requestCode, resultCode, intent);
        /*
        if (resultCode != RESULT_OK) {
            return;
        }
         */
        if(resultCode == RESULT_CANCELED)
            return;
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return;
        }
        // gather all info passed back by launched activity
        String name = bundle.getString(AddEditAlbum.ALBUM_NAME);
        int index = bundle.getInt(AddEditAlbum.ALBUM_INDEX);

        /*
        If delete button is clicked from add_edit screen
         */
        if(resultCode == DELETE_ALBUM_CODE){
            //delete album
            albums.remove(index);
        }
        else if(resultCode == RESULT_OK){
            /*
            If save button is clicked from add_edit screen, either
            1. change name
            2. add new album
             */
            if (requestCode == EDIT_ALBUM_CODE) {
                Album album = albums.get(index);
                album.setAlbumName(name);
            } else {
                albums.add(new Album(name));
            }
        }

        // redo the adapter to reflect change^K
        listView.setAdapter(
                new ArrayAdapter<Album>(this, R.layout.album, albums));

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_album_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                addAlbum();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Update data into data.dat file for serialization
     */
    private void updateData() {
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
