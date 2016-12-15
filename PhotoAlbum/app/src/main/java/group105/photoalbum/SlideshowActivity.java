package group105.photoalbum;

/**
 *
 * Group 105
 *
 * @author Arifur Rahman    
 * @author Monique Gordon
 *
 *
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.R.layout;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import java.util.ArrayList;
import android.content.Context;
import android.app.Dialog;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Spinner;
import android.support.v7.widget.Toolbar.OnMenuItemClickListener;
import android.util.Log;

public class SlideshowActivity extends AppCompatActivity {

    Album currentAlbum;
    Photo currentPhoto;
    ListView tagsList;
    ListView albumList;
    ArrayAdapter<String> arrayAdapterTags;
    final Context context = this;
    String selectedDelItem;
    int delselection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.slideshow_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

        toolbar.inflateMenu(R.menu.slideshow_menu);

        final int albumindex = getIntent().getIntExtra("album_index", 0);
        final int photoindex = getIntent().getIntExtra("photo_index", 0);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SlideshowActivity.this, ThumbnailViewActivity.class);
                intent.putExtra("index", albumindex);
                startActivity(intent);
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch(menuItem.getItemId()) {

                    case R.id.add_tag:

                        final Dialog dialog = new Dialog(context);

                        dialog.setContentView(R.layout.addtag_dialog);
                        dialog.setTitle("Add a tag");

                        Button addTag = (Button) dialog.findViewById(R.id.dialogOK);
                        Button cancelAdd = (Button) dialog.findViewById(R.id.dialogCancel);

                        Spinner tagType = (Spinner) dialog.findViewById(R.id.dialog_spinner);

                        addTag.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditText tagValue = (EditText) dialog.findViewById(R.id.tagValue);
                                Spinner tagType = (Spinner) dialog.findViewById(R.id.dialog_spinner);
                                if(tagValue.getText().toString().trim().isEmpty()){
                                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                                    alert.setTitle("Invalid");
                                    alert.setMessage("Must input at least one character");
                                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            return;
                                        }
                                    });
                                    alert.show();
                                } else {
                                    currentPhoto.addTag(tagType.getSelectedItem().toString().trim(),tagValue.getText().toString().toLowerCase());
                                    populateListView();
                                    MainActivity.pa.saveToDisk(context);
                                    dialog.dismiss();
                                }
                            }
                        });
                        cancelAdd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        return true;

                    case R.id.delete_tag:
                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Pick tag to delete");
                        if(keyValueTogether().length == 0) {
                            populateListView();
                            return false;
                        }
                        builder.setSingleChoiceItems(keyValueTogether(), -1, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                delselection = which;
                            }
                        });
                            builder.setPositiveButton("Delete Tag", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {

                                if (keyValueTogether().length == 0) {
                                    populateListView();
                                    return;
                                }

                                String keyValueToDel = keyValueTogether()[delselection];

                                String[] parts = keyValueToDel.split(":");
                                String key = parts[0];
                                String value = parts[1].substring(1);

                                currentPhoto.removeTag(key, value);
                                MainActivity.pa.saveToDisk(context);
                                populateListView();
                            }
                        })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                         @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.show();

                        return true;
                }
                return false;
            }
        });


        currentAlbum = MainActivity.pa.albums.get(albumindex);
        currentPhoto = MainActivity.pa.albums.get(albumindex).getPhotos().get(photoindex);
        FloatingActionButton leftButton = (FloatingActionButton) findViewById(R.id.leftarrow);
        leftButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SlideshowActivity.this, SlideshowActivity.class);
                intent.putExtra("album_index", albumindex);
                int index;
                if (photoindex == 0) {
                    index = currentAlbum.getNumOfPhotos() - 1;
                } else {
                    index = photoindex - 1;
                }
                intent.putExtra("photo_index", index);
                startActivity(intent);
            }
        });

        FloatingActionButton rightButton = (FloatingActionButton) findViewById(R.id.rightarrow);
        rightButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SlideshowActivity.this, SlideshowActivity.class);
                intent.putExtra("album_index", albumindex);
                int index;
                if (photoindex == (currentAlbum.getNumOfPhotos() - 1)) {
                    index = 0;
                } else {
                    index = photoindex+1;
                }
                intent.putExtra("photo_index", index);
                startActivity(intent);
            }
        });


        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setText(currentPhoto.getCaption());

        populateListView();
        fillPicture();

    }


    private void populateListView() {
        String[][] tagKeyValue = currentPhoto.getTagsWithKeyValues();
        ArrayList<String> keyAndValueTogether = new ArrayList<String>();

        if (tagKeyValue[0].length == 0) {
            tagsList = (ListView) findViewById(R.id.tagslistView);
            tagsList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            arrayAdapterTags = new ArrayAdapter<String>(this, layout.simple_list_item_1, keyAndValueTogether);
            tagsList.setAdapter(arrayAdapterTags);
            return;
        }


        for (int i = 0; i < tagKeyValue[0].length; i++) {
            keyAndValueTogether.add(tagKeyValue[0][i] + ": " + tagKeyValue[1][i]);
        }

        tagsList = (ListView) findViewById(R.id.tagslistView);
        tagsList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        tagsList.setSelection(0);
        arrayAdapterTags = new ArrayAdapter<String>(this, layout.simple_list_item_1, keyAndValueTogether);
        tagsList.setAdapter(arrayAdapterTags);
    }

    private void fillPicture() {
        ImageView iv = (ImageView) findViewById(R.id.fullImageView);
        iv.setImageBitmap(currentPhoto.getImage());
    }

    private String[] keyValueTogether(){

        String[][] tagKeyValue = currentPhoto.getTagsWithKeyValues();
        String[] keyAndValueTogether = new String[tagKeyValue[0].length];
        for (int i = 0; i < tagKeyValue[0].length; i++) {
            keyAndValueTogether[i] = tagKeyValue[0][i] + ": " + tagKeyValue[1][i];
        }
        return keyAndValueTogether;
    }
}
