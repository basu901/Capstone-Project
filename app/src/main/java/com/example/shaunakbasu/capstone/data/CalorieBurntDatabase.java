package com.example.shaunakbasu.capstone.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by shaunak basu on 15-11-2016.
 */
@Database(version= CalorieBurntDatabase.VERSION)
public class CalorieBurntDatabase {

    private CalorieBurntDatabase(){}

    public static final int VERSION = 1;

    @Table(CalorieBurntColumns.class) public static final String CALORIE_BURNT = "calorie_burnt";

}
