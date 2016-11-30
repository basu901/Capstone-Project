package com.example.shaunakbasu.capstone.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.shaunakbasu.capstone.R;
import com.example.shaunakbasu.capstone.data.RoutineDetailsColumns;

/**
 * Created by shaunak basu on 11-11-2016.
 */
public class RoutineParentAdapter extends CursorAdapter {

    public deleteRoutineListener deleteRoutineListener;

    public RoutineParentAdapter(Context context, Cursor c, int flags, deleteRoutineListener deleteRoutineListener) {
        super(context, c, flags);
        this.deleteRoutineListener = deleteRoutineListener;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.routine_header.setText(cursor.getString(cursor.getColumnIndex(RoutineDetailsColumns.ROUTINE_HEADER)));
        viewHolder.delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String header = viewHolder.routine_header.getText().toString();
                deleteRoutineListener.onDeleteClick(view, header);

            }
        });

    }

    public interface deleteRoutineListener {
        void onDeleteClick(View view, String header);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        int layoutId = R.layout.routine_item;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    public static class ViewHolder {

        public final TextView routine_header;
        public final ImageButton delete_button;

        public ViewHolder(View view) {
            routine_header = (TextView) view.findViewById(R.id.routine_header_text);
            delete_button = (ImageButton) view.findViewById(R.id.routine_delete);
        }
    }


}