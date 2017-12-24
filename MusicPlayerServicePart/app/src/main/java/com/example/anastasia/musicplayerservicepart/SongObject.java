package com.example.anastasia.musicplayerservicepart;

/**
 * Created by Anastasia on 11/18/2017.
 */

public class SongObject {
    /**
     * The title of the audio file
     */
    private String title;

    /**
     * The file path of the audio file
     */
    private String filePath;
    private String album;

    public SongObject(String title, String filePath){
        super();
        this.title = title;
        this.filePath = filePath;

    }

    public String getTitle() {
        return title;
    }
    public String getAlbum() {return album;}

    public void setTitle(String title) {
        this.title = title;
    }
    public void setAlbum (String album) {this.album = album;}

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
