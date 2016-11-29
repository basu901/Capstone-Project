package com.example.shaunakbasu.capstone.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by shaunak basu on 15-11-2016.
 */
@ContentProvider(authority = CalorieBurntProvider.AUTHORITY, database = CalorieBurntDatabase.class)
public class CalorieBurntProvider {
    public static final String AUTHORITY = "com.example.shaunakbasu.capstone.data.CalorieBurntProvider";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path{
        String CALORIE_BURNT= "calorie_burnt";
    }

    private static Uri buildUri(String... paths){
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path:paths){
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = CalorieBurntDatabase.CALORIE_BURNT)
    public static class Calorie_Burnt{
        @ContentUri(
                path = Path.CALORIE_BURNT,
                type = "vnd.android.cursor.dir/calorie_burnt"
        )
        public static final Uri CONTENT_URI = buildUri(Path.CALORIE_BURNT);

        @InexactContentUri(
                name = "CALORIE_BURNT_ID",
                path = Path.CALORIE_BURNT + "/*",
                type = "vnd.android.cursor.item/calorie_burnt",
                whereColumn = CalorieBurntColumns._ID,
                pathSegment = 1
        )
        public static Uri withID(int id){
            return buildUri(Path.CALORIE_BURNT , Integer.toString(id));
        }
    }
}


