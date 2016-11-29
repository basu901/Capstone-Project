package com.example.shaunakbasu.capstone.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by shaunak basu on 11-11-2016.
 */
@Database(version = RoutineDetailsDatabase.VERSION)
public class RoutineDetailsDatabase {

    private RoutineDetailsDatabase(){}

    public static final int VERSION = 1;

    @Table(RoutineDetailsColumns.class) public static final String ROUTINE = "routine";
}
