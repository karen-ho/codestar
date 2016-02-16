package com.ho.karen.codestar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.ho.karen.codestar.factories.AlbumSotrageDirectoryFactoryFroyoImpl;
import com.ho.karen.codestar.factories.AlbumStorageDirectoryFactory;
import com.ho.karen.codestar.factories.AlbumStorageDirectoryFactoryImpl;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int ANALYZE_PHOTO_ACTIVITY = 2;

    private static final String IMAGE_FILE_PREFIX = "CODE_IMAGE_";
    String currentPhotoPath;

    private AlbumStorageDirectoryFactory albumStorageDirFactory = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent(REQUEST_IMAGE_CAPTURE);
            }
        });

        albumStorageDirFactory = Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO
                ? new AlbumSotrageDirectoryFactoryFroyoImpl()
                : new AlbumStorageDirectoryFactoryImpl();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Intent intent = new Intent(this, AnalyzePhotoActivity.class);
            this.startActivityForResult(intent, ANALYZE_PHOTO_ACTIVITY);
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //mImageView.setImageBitmap(imageBitmap);
        }
    }

    private void dispatchTakePictureIntent(int actionCode) {
        // start an intent to capture the image
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        switch(actionCode) {
            case REQUEST_IMAGE_CAPTURE:
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File file = null;
                    try {
                        file = setUpPhotoFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                        file = null;
                        currentPhotoPath = null;
                    }

                    // Continue only if the File was successfully created
                    if (file != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                    }
                }
                break;
            default:
                break;
        }

        startActivityForResult(takePictureIntent, actionCode);
    }

    private File getAlbumDirectory() {
        File storageDirectory = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDirectory = albumStorageDirFactory.getAlbumStorageDirectory(getAlbumName());

            if (storageDirectory != null) {
                if (!storageDirectory.mkdir()) {
                    if (!storageDirectory.exists()) {
                        Log.d(getAlbumName(), "failed to create directory");
                        return null;
                    }
                }
            }
        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE");
        }

        return storageDirectory;
    }

    private String getAlbumName() {
        return getString(R.string.album_name);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = IMAGE_FILE_PREFIX + timeStamp;
        File storageDir = getAlbumDirectory();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    private File setUpPhotoFile() throws IOException {
        File file = createImageFile();
        currentPhotoPath = file.getAbsolutePath();

        return file;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
}
