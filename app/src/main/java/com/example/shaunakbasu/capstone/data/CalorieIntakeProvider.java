package com.example.shaunakbasu.capstone.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by shaunak basu on 14-11-2016.
 */
@ContentProvider(authority = CalorieIntakeProvider.AUTHORITY, database = CalorieIntakeDatabase.class)
public class CalorieIntakeProvider {
    public static final String AUTHORITY = "com.example.shaunakbasu.capstone.data.CalorieIntakeProvider";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path{
        String CALORIE_INTAKE= "calorie_intake";
    }

    private static Uri buildUri(String... paths){
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path:paths){
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = CalorieIntakeDatabase.CALORIE_INTAKE)
    public static class Calorie_Intake{
        @ContentUri(
                path = Path.CALORIE_INTAKE,
                type = "vnd.android.cursor.dir/calorie_intake"
        )
        public static final Uri CONTENT_URI = buildUri(Path.CALORIE_INTAKE);

        @InexactContentUri(
                name = "CALORIE_INTAKE_ID",
                path = Path.CALORIE_INTAKE + "/*",
                type = "vnd.android.cursor.item/calorie_intake",
                whereColumn = CalorieIntakeColumns._ID,
                pathSegment = 1
        )
        public static Uri withID(int id){
            return buildUri(Path.CALORIE_INTAKE , Integer.toString(id));
        }
    }
}

