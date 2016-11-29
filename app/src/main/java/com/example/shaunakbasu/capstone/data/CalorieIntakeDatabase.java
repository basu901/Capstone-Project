package com.example.shaunakbasu.capstone.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by shaunak basu on 14-11-2016.
 */

@Database(version= CalorieIntakeDatabase.VERSION)
public class CalorieIntakeDatabase {

    private CalorieIntakeDatabase(){}

    public static final int VERSION = 1;

    @Table(CalorieIntakeColumns.class) public static final String CALORIE_INTAKE = "calorie_intake";

}
