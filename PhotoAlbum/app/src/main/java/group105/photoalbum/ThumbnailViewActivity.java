package group105.photoalbum;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import java.util.ArrayList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.database.Cursor;
import android.widget.ImageView;
import android.graphics.drawable.BitmapDrawable;
import java.io.File;
import android.content.Context;
import android.content.ContentResolver;
import android.widget.TextView;


/**
 *
 * Group 105
 *
 * @author Arifur Rahman    
 * @author Monique Gordon
 *
 *
 */

public class ThumbnailViewActivity extends AppCompatActivity {

    private GridView gridView;
    private ThumbnailAdapter gridViewAdapter;
    private Album currentAlbum;
    private static final int SELECT_PHOTO = 1;
    private ThumbnailAdapter adapter;
    Context context = this;
    TextView toolbarTitle;
    int selection;


    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thumbnail_view);

        gridView = (GridView) findViewById(R.id.gridView);

        adapter = new ThumbnailAdapter(this, getPhotos());

        gridView.setAdapter(adapter);

        int index = getIntent().getIntExtra("index", 0);
        currentAlbum = MainActivity.pa.albums.get(index);
        final int albumIndex = index;

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);


        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Album: "+currentAlbum.getName()+" - "+currentAlbum.getNumOfPhotos()+" photo(s)");
        //Deleting a photo

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ThumbnailViewActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            FloatingActionButton delete = (FloatingActionButton) findViewById(R.id.delete);
            FloatingActionButton moveAlbum = (FloatingActionButton) findViewById(R.id.movealbum);

            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                gridView.requestFocusFromTouch();
                final int position2 = position;
                gridView.setSelection(position);
                delete.setVisibility(View.VISIBLE);
                moveAlbum.setVisibility(View.VISIBLE);
                moveAlbum.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        AlertDialog.Builder moveAlbumDialog = new AlertDialog.Builder(context);
                        moveAlbumDialog.setTitle("Move Photo to Album:");
                        moveAlbumDialog.setSingleChoiceItems(albumNames(), -1, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selection = which;
                            }
                        });

                        moveAlbumDialog.setPositiveButton("Move", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Photo photoToAdd = MainActivity.pa.albums.get(getIntent().getIntExtra("index", 0)).getPhotos().get(position2);
                                MainActivity.pa.albums.get(selection).addOnePhoto(photoToAdd);
                                MainActivity.pa.albums.get(getIntent().getIntExtra("index", 0)).getPhotos().remove(position2);
                                adapter = new ThumbnailAdapter(context, getPhotos());
                                gridView.setAdapter(adapter);
                                toolbarTitle.setText("Album: "+currentAlbum.getName()+" - "+currentAlbum.getNumOfPhotos()+" photo(s)");
                                MainActivity.pa.saveToDisk(context);
                                delete.setVisibility(View.INVISIBLE);
                                moveAlbum.setVisibility(View.INVISIBLE);
                            }
                        });

                        moveAlbumDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                delete.setVisibility(View.INVISIBLE);
                                moveAlbum.setVisibility(View.INVISIBLE);
                            }
                        });

                        moveAlbumDialog.show();
                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Delete");
                        builder.setMessage("Are you sure you want to delete this photo?");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.pa.albums.get(getIntent().getIntExtra("index", 0)).getPhotos().remove(position2);
                                adapter = new ThumbnailAdapter(context, getPhotos());
                                gridView.setAdapter(adapter);
                                toolbarTitle.setText("Album: "+currentAlbum.getName()+" - "+currentAlbum.getNumOfPhotos()+" photo(s)");
                                MainActivity.pa.saveToDisk(context);
                                delete.setVisibility(View.INVISIBLE);
                                moveAlbum.setVisibility(View.INVISIBLE);
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                delete.setVisibility(View.INVISIBLE);
                                moveAlbum.setVisibility(View.INVISIBLE);
                                dialog.cancel();
                            }
                        });
                        builder.show();

                    }
                });

                return true;
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Allow user to select photo

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_PHOTO);

                gridView.setAdapter(adapter);
                // Add to current album

            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ThumbnailViewActivity.this, SlideshowActivity.class);
                intent.putExtra("album_index", albumIndex);
                intent.putExtra("photo_index", position);
                startActivity(intent);
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {

        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if(resultCode == RESULT_OK){

                    Uri selectedImage = imageReturnedIntent.getData();

                    ImageView iv = new ImageView(this);

                    iv.setImageURI(selectedImage);

                    BitmapDrawable drawable = (BitmapDrawable) iv.getDrawable();
                    Bitmap selectedImageGal = drawable.getBitmap();

                    Photo photoToAdd = new Photo();
                    photoToAdd.setImage(selectedImageGal);

                    File f = new File(selectedImage.getPath());
                    String pathID = f.getAbsolutePath();
                    String filename = pathToFileName(selectedImage);
                    photoToAdd.setCaption(filename);


                    currentAlbum.addOnePhoto(photoToAdd);
                    MainActivity.pa.saveToDisk(context);

                    gridView.setAdapter(adapter);
                    TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
                    toolbarTitle.setText("Album: "+currentAlbum.getName()+" - "+currentAlbum.getNumOfPhotos()+" photo(s)");

        }
    }

    private String pathToFileName(Uri selectedImage){


        String filename = "not found";

        String[] column = {MediaStore.MediaColumns.DISPLAY_NAME};

        ContentResolver contentResolver = getApplicationContext().getContentResolver();

        Cursor cursor = contentResolver.query(selectedImage, column,
                                            null, null, null);

        if(cursor != null) {
           try {
               if (cursor.moveToFirst()){
                   filename = cursor.getString(0);
               }
           } catch (Exception e){

            }
        }

        return filename;

    }


    private ArrayList getPhotos(){
        int index = getIntent().getIntExtra("index", 0);
        return MainActivity.pa.albums.get(index).getPhotos();
    }

    private String[] albumNames() {
        String[] albumNames = new String[MainActivity.pa.albums.size()];
        for(int i = 0; i < MainActivity.pa.albums.size(); i++){
            albumNames[i] = MainActivity.pa.albums.get(i).getName();
        }
        return albumNames;
    }


}
