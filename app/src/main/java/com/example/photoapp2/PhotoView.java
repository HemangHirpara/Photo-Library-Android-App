package com.example.photoapp2;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.IOException;
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
    private static final int ADD_PHOTO_CODE = 1;
    private static final int EDIT_PHOTO_CODE = 2;
    private static final int DELETE_PHOTO_CODE = 3;

    private int photoIndex;
    private ArrayList<Photo> photos;
    private ListView photoListView;
    private String filePath;
    private CustomListViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // activates the up arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get data necessary to show album
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            photoIndex = bundle.getInt(ALBUM_INDEX);
            photos = (ArrayList<Photo>) ((ArrayList<Album>) bundle.getSerializable(ALBUM_LIST)).get(photoIndex).getPhotos();
            System.out.println("Photo List Found: " + photos.size());
        }

        photoListView = findViewById(R.id.photo_list);
        adapter = new CustomListViewAdapter(this,
                R.layout.photo_lv_item, photos);
        photoListView.setAdapter(adapter);

        // show movie for possible edit when tapped
        photoListView.setOnItemClickListener((p, V, pos, id) -> showPhoto(pos));
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
        //implement addPhoto using below lines
        //Intent intent = new Intent(this, AddEditPhoto.class);
        //startActivityForResult(intent, ADD_PHOTO_CODE);
    }

    private void selectImage(Context context) {
        final CharSequence[] options = {"Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose photot to add to album");

        builder.setItems(options, (dialog, item) -> {

            if (options[item].equals("Choose from Gallery")) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), 11);

            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 11) {
            Uri selectedImageUri = data.getData();
            Photo p = new Photo(selectedImageUri.toString());
            p.setCaption(selectedImageUri.getPath());
            photos.add(p);
        }
        if(requestCode == EDIT_PHOTO_CODE){

        }
        if(requestCode == DELETE_PHOTO_CODE){
            //bundle
            photos.remove(photos.get(photoIndex));

        }
        if(requestCode == ADD_PHOTO_CODE){
            //add photo to list
        }
        adapter = new CustomListViewAdapter(this,
                R.layout.photo_lv_item, photos);
        photoListView.setAdapter(adapter);
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
}
class CustomListViewAdapter extends ArrayAdapter<Photo> {

    Context context;

    public CustomListViewAdapter(Context context, int resourceId, //resourceId=your layout
                                 List<Photo> items) {
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
