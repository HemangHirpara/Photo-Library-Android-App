package com.example.photoapp2;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

import java.io.*;
import java.util.ArrayList;

public class AlbumView extends AppCompatActivity {

    public static final int EDIT_ALBUM_CODE = 1;
    public static final int ADD_ALBUM_CODE = 2;
    private static final int DELETE_ALBUM_CODE = 3;

    private ListView listView;
    private ArrayList<Album> albums;
    private String filePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_list);

        String pathToFolder = getExternalFilesDir(null).getAbsolutePath();
        filePath = pathToFolder + File.separator + "album.data";
        File data = new File(filePath);
        if(!(data.exists() || data.isFile())){
            // serialize the data
            albums = new ArrayList<>();
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
        else{
            try {
                FileInputStream fis = new FileInputStream(filePath);
                ObjectInputStream ois = new ObjectInputStream(fis);
                albums = (ArrayList<Album>) ois.readObject();
                ois.close();
                fis.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        listView = findViewById(R.id.album_list);
        listView.setAdapter(new ArrayAdapter<>(this, R.layout.album, albums));

        // show movie for possible edit when tapped
        listView.setOnItemClickListener((p, V, pos, id) -> showAlbum(pos));
    }

    protected  void onResume() {
        super.onResume();
        listView.setAdapter(new ArrayAdapter<>(this, R.layout.album, albums));
    }

    protected void onStop() {
        super.onStop();
        updateData();
    }

    private void showAlbum(int pos) {
        Bundle bundle = new Bundle();
        bundle.putInt(AddEditAlbum.ALBUM_INDEX, pos);
        bundle.putInt(AddEditAlbum.ALBUM_OPEN_FLAG, 9);
        Intent intent = new Intent(this, AddEditAlbum.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, EDIT_ALBUM_CODE);
    }

    private void addAlbum() {
        Intent intent = new Intent(this, AddEditAlbum.class);
        startActivityForResult(intent, ADD_ALBUM_CODE);
    }


    private void search() {
        //change class to search class OR alert dialog box to navigate to either search by person or location
        Intent intent = new Intent(this, Search.class);
        startActivityForResult(intent, 4);
    }

    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent intent) {

        super.onActivityResult(requestCode, resultCode, intent);
        if(resultCode == RESULT_CANCELED)
            return;
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return;
        }
        String name = bundle.getString(AddEditAlbum.ALBUM_NAME);
        int index = bundle.getInt(AddEditAlbum.ALBUM_INDEX);

        if(resultCode == DELETE_ALBUM_CODE){
            albums.remove(index);
            Toast.makeText(this, "Album Deleted", Toast.LENGTH_SHORT).show();
        }
        else if(resultCode == RESULT_OK){
            for(Album a : albums)
                if(a.getAlbumName().equals(name)){
                    Bundle b = new Bundle();
                    b.putString(PhotoDialogFragment.MESSAGE_KEY,
                            "Name is duplicate");
                    DialogFragment newFragment = new PhotoDialogFragment();
                    newFragment.setArguments(b);
                    newFragment.show(getSupportFragmentManager(), "badfields");
                    return;
                }
            if (requestCode == EDIT_ALBUM_CODE) {
                Album album = albums.get(index);
                album.setAlbumName(name);
                Toast.makeText(this, "Album Edited", Toast.LENGTH_SHORT).show();
            } else {
                albums.add(new Album(name));
                Toast.makeText(this, "New Album Added", Toast.LENGTH_SHORT).show();
            }
        }

        // redo the adapter to reflect change
        listView.setAdapter(
                new ArrayAdapter<>(this, R.layout.album, albums));
        updateData();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_album_menu,menu);
        getMenuInflater().inflate(R.menu.search_album_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                addAlbum();
                return true;
            case R.id.action_search:
                search();
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
