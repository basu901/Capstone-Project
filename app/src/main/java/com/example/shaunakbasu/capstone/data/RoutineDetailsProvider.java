package com.example.shaunakbasu.capstone.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by shaunak basu on 11-11-2016.
 */
@ContentProvider(authority = RoutineDetailsProvider.AUTHORITY, database = RoutineDetailsDatabase.class)
public class RoutineDetailsProvider {
    public static final String AUTHORITY = "com.example.shaunakbasu.capstone.data.RoutineDetailsProvider";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path{
        String ROUTINE= "routine";
    }

    private static Uri buildUri(String... paths){
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path:paths){
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = RoutineDetailsDatabase.ROUTINE)
    public static class Routine{
        @ContentUri(
                path = Path.ROUTINE,
                type = "vnd.android.cursor.dir/routine"
        )
        public static final Uri CONTENT_URI = buildUri(Path.ROUTINE);

        @InexactContentUri(
                name = "ROUTINE_ID",
                path = Path.ROUTINE + "/*",
                type = "vnd.android.cursor.item/routine",
                whereColumn = RoutineDetailsColumns._ID,
                pathSegment = 1
        )
        public static Uri withID(int id){
            return buildUri(Path.ROUTINE , Integer.toString(id));
        }
    }
}

