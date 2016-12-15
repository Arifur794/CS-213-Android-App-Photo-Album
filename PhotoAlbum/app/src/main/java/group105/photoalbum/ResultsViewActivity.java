package group105.photoalbum;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;
import java.util.ArrayList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.database.Cursor;
import android.widget.ImageView;
import android.graphics.drawable.BitmapDrawable;
import java.io.File;
import android.content.Context;
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

public class ResultsViewActivity extends AppCompatActivity {

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
        setContentView(R.layout.results_view);

        gridView = (GridView) findViewById(R.id.gridView);

        adapter = new ThumbnailAdapter(this, getPhotos());

        gridView.setAdapter(adapter);

        currentAlbum = PhotoAlbum.searchResults;

        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setText(currentAlbum.getName()+" - "+currentAlbum.getNumOfPhotos()+" photo(s)");

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
            String filename = pathToFileName(pathID);
            photoToAdd.setCaption(filename);


            currentAlbum.addOnePhoto(photoToAdd);
            MainActivity.pa.saveToDisk(context);

            gridView.setAdapter(adapter);
            TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
            toolbarTitle.setText(currentAlbum.getName()+" - "+currentAlbum.getNumOfPhotos()+" photo(s)");

        }
    }

    private String pathToFileName(String pathID){

        String id = pathID.split(":")[1];
        String[] column = {MediaStore.Images.Media.DATA};
        String selector = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,  column,
                selector, new String[]{id}, null);

        String filePath = "/not found";
        int columnIndex = 0;
        if (cursor != null) cursor.getColumnIndex(column[0]);

        if (cursor != null && cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }

        if (cursor != null) cursor.close();

        String filename = filePath.substring(filePath.lastIndexOf('/')+1);
        return filename;

    }


    private ArrayList getPhotos(){
        return PhotoAlbum.searchResults.getPhotos();
    }

    private String[] albumNames() {
        String[] albumNames = new String[MainActivity.pa.albums.size()];
        for(int i = 0; i < MainActivity.pa.albums.size(); i++){
            albumNames[i] = MainActivity.pa.albums.get(i).getName();
        }
        return albumNames;
    }


}
