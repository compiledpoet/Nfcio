package com.nfcio;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ASANDA on 2018/05/04.
 * for Pandaphic
 */
public class RecordAdapter extends ArrayAdapter<RecordAdapter.Record>{

    public RecordAdapter(@NonNull Context context, int resource, @NonNull List<Record> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.record_item, parent, false);
        }

        final Record record = this.getItem(position);

        Button btnDel = convertView.findViewById(R.id.btn_del);
        TextView txtInfo = convertView.findViewById(R.id.txt_record_info);
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordAdapter.this.remove(record);
                RecordAdapter.this.notifyDataSetChanged();
            }
        });

        txtInfo.setText(record.Value);

        return convertView;
    }

    public static class Record{
        public final String Value;

        public Record(String value){
            this.Value = value;
        }
    }
}


