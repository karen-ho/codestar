package com.ho.karen.codestar.factories;

import android.os.Environment;

import java.io.File;

/**
 * Created by Karen on 2/14/2016.
 */
public class AlbumStorageDirectoryFactoryImpl extends  AlbumStorageDirectoryFactory {

    // Standard storage location for digital camera files
    private static final String CAMERA_DIR = "/dcim/";

    @Override
    public File getAlbumStorageDirectory(String albumName) {
        return new File (
                Environment.getExternalStorageDirectory()
                        + CAMERA_DIR
                        + albumName
        );
    }
}
