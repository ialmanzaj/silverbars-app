package com.app.app.silverbarsapp;

/**
 * Created by isaacalmanza on 01/07/17.
 */

public class Constants {

    public static final String PACKAGE = "com.app.app.silverbarsapp";

    public static final String API_URL = "https://silverbars.herokuapp.com";

    public static final String MUSCLE_BODY_TEMPLATE = "silverbarsmedias3/html/index.html";

    public static final String AMAZON_S3_URL = "https://s3-ap-northeast-1.amazonaws.com/";

    public static final String BODY_URL = "https://s3-ap-northeast-1.amazonaws.com/silverbarsmedias3/html/index.html";

    public static final String DATABASE_NAME = "SILVERBARS";

    public static final int DATABASE_VERSION = 1;

    /**
     * API
     */
    public static final String CONSUMER_KEY = "kifIn2frPiZ5TvPsg5oRb3raVNCST68EgMYmBtAf";
    public static final String CONSUMER_SECRET = "CwXWya5eZfaUpdOES9H3xlS3SBF2FuaUD4xcXC6WPUK08d5GEIpmRNPICrjq3any1ymYhWx57ZtroC1nW6YGg42dOX6Zwt0HR8kYTGK3Ykkg6aPJ2lC1zK5nGXH6mI4u";

    /**
     * SPOTIFY API
     */
    public static final int REQUEST_CODE = 1337;
    public static final String CLIENT_ID = "20823679749441aeacf4e601f7d12270";
    public static final String REDIRECT_URI = "testschema://callback";

    public static final int BETTER = 2;
    public static final int EQUAL = 1;
    public static final int WORST = 0;

    public  static final int DAILY = 0;
    public  static final int WEEK = 1;
    public  static final int MONTH = 2;

    public static final class MusicTypes {
        public static final int SPOTIFY = 1;
        public static final int LOCAL_MUSIC = 2;
    }


    /**
     * SPOTIFY APP
     */
    public static final String SPOTIFY_PACKAGE = "com.spotify.music";
    public static final String SPOTIFY_MAIN_ACTIVITY = SPOTIFY_PACKAGE + ".MainActivity";

    public static final class BroadcastTypes {
        public static final String PLAYBACK_STATE_CHANGED = SPOTIFY_PACKAGE + ".playbackstatechanged";
        public static final String QUEUE_CHANGED = SPOTIFY_PACKAGE + ".queuechanged";
        public static final String METADATA_CHANGED = SPOTIFY_PACKAGE + ".jsonchanged";
        public static final String METADATA_CHANGED_1 = SPOTIFY_PACKAGE + ".metadatachanged";
    }

}
