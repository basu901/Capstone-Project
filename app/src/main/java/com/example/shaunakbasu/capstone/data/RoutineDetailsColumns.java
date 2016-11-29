package com.example.shaunakbasu.capstone.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by shaunak basu on 11-11-2016.
 */
public class RoutineDetailsColumns {

    @DataType(DataType.Type.INTEGER) @PrimaryKey
    @AutoIncrement
    public static final String _ID = "_id";
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String ROUTINE_HEADER = "routine_header";
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String WORK_OUT_DETAILS = "work_out_details";
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String CHECKED= "checked";

}
