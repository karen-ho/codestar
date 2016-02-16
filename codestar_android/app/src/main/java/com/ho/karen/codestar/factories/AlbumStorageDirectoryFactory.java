package com.ho.karen.codestar.factories;

import java.io.File;

/**
 * Created by Karen on 2/14/2016.
 */
public abstract class AlbumStorageDirectoryFactory {
    public abstract File getAlbumStorageDirectory(String albumName);
}
