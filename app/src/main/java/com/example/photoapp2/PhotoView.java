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

    //keys to get and send photo info
    public static final String PHOTO_INDEX = "photo_pos";
    public static final String PHOTO_LIST = "photo_list";
    //keys to get and send album info
    public static final String ALBUM_LIST = "album_list";
    public static final String ALBUM_INDEX = "album_pos";
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
    private TextView photo_view_album_name;
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
        photo_view_album_name = findViewById(R.id.photo_view_album_name);
        photo_view_album_name.setText(albums.get(albumIndex).getAlbumName());
        //System.out.println("AlbumList size: " + albums.size() + "\n" + "PhotoList size: " + photos.size());
        photoListView = findViewById(R.id.photo_list);
        adapter = new CustomListViewAdapter(this,
                R.layout.photo_lv_item, photos);
        photoListView.setAdapter(adapter);

        // show photo display for possible edit when tapped
        photoListView.setOnItemClickListener((p, V, pos, id) -> showPhoto(pos));
        System.out.println("ONCREATE EXECUTED: PHOTOVIEW\nALBUM SIZE: " + photos.size());
    }

    protected  void onResume() {
        super.onResume();
        adapter = new CustomListViewAdapter(this,
                R.layout.photo_lv_item, photos);
        photoListView.setAdapter(adapter);
        System.out.println("ONRESUME EXECUTED: PHOTOVIEW\nALBUM SIZE: " + photos.size());
    }

    private void showPhoto(int pos) {
        //transition to display photo view to add tags/move photo
        Bundle bundle = new Bundle();
        Photo photo = photos.get(pos);
        bundle.putInt(AddEditPhoto.PHOTO_INDEX, pos);
        bundle.putString(AddEditPhoto.PHOTO_NAME, photo.getCaption());
        bundle.putString(AddEditPhoto.PHOTO_URI, photo.getPhotoFile());
        bundle.putSerializable(AddEditPhoto.PHOTO_LIST, photos);
        bundle.putInt(AddEditPhoto.PHOTO_OPEN_FLAG, 8);
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

    public void onBackPressed(){
        System.out.println("Passing album index back to addeditalbum");
        // make Bundle
        Bundle bundle = new Bundle();
        bundle.putInt(ALBUM_INDEX, albumIndex);

        // send back to caller
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK,intent);
        finish(); // pops activity from the call stack, returns to AddEditAlbum
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
            //albums.get(albumIndex).addPhoto(p);
            System.out.println("*****PHOTO ADDED*****");
            updateData();
        }
        if(resultCode == EDIT_PHOTO_CODE){
            System.out.println("*****PHOTO EDITED*****");
        }
        if(resultCode == DELETE_PHOTO_CODE){
            //bundle
            Bundle bundle = data.getExtras();
            if (bundle == null) {
                return;
            }
            int index = bundle.getInt(PHOTO_INDEX);
            photos.remove(photos.get(index));
            System.out.println("*****PHOTO DELETED*****");
        }
        if(resultCode == PHOTO_VIEW_CANCELED){
            return;
        }
        adapter = new CustomListViewAdapter(this,
                R.layout.photo_lv_item, photos);
        photoListView.setAdapter(adapter);
        updateData();
        //finish();
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

class CustomListViewAdapter extends ArrayAdapter<Photo> {

    Context context;

    public CustomListViewAdapter(Context context, int resourceId, List<Photo> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView txtTitle;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Photo p = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.photo_lv_item, null);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.caption);
            holder.imageView = (ImageView) convertView.findViewById(R.id.thumbnail);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.txtTitle.setText(p.getCaption());
        holder.imageView.setImageURI(Uri.parse(p.getPhotoFile()));

        return convertView;
    }
}

