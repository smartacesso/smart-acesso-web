package com.protreino.luxandapp.ui.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.protreino.luxandapp.R;

import java.util.List;


/**
 * Created by gustavo on 05/06/17.
 */

public class DialogoSelecaoListAdapter extends ArrayAdapter<String> {

    private AppCompatActivity activity;
    private int r;


    public DialogoSelecaoListAdapter(Context context, int resource) {
        super(context, resource);
    }

    public DialogoSelecaoListAdapter(Context context, int resource,
                                     List<String> itens, AppCompatActivity activity) {
        super(context, resource, itens);
        this.activity = activity;
        this.r = resource;

    }

    public DialogoSelecaoListAdapter(Context context, int resource, List<String> items) {
        super(context, resource, items);
        this.r = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String item = getItem(position);
        View v =  LayoutInflater.from(getContext()).inflate(r, null);

        TextView nomeUp = (TextView) v
                .findViewById(R.id.text);
        nomeUp.setText(item);

        if(position == getCount()-1){
            View divider = v.findViewById(R.id.divider);
            divider.setVisibility(View.GONE);
        }

        return v;
    }


}
