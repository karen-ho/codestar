package com.ho.karen.codestar.factories;

import android.os.Environment;

import java.io.File;

/**
 * Created by Karen on 2/14/2016.
 */
public class AlbumSotrageDirectoryFactoryFroyoImpl extends AlbumStorageDirectoryFactory {
    @Override
    public File getAlbumStorageDirectory(String albumName) {
        return new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                albumName);
    }
}
