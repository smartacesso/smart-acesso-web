package br.com.smartacesso.pedestre.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import br.com.smartacesso.pedestre.R;
import br.com.smartacesso.pedestre.model.to.FotoTO;

/**
 * Created by gustavo on 27/01/18.
 */

public class FotoListViewAdapter extends ArrayAdapter<FotoTO> implements Filterable {


    private GenericListFilter<FotoTO> mFilter;



    public FotoListViewAdapter(@NonNull Context context, int resource, @NonNull List<FotoTO> objects) {
        super(context, resource, objects);
        mFilter = new GenericListFilter<FotoTO> (objects, "getFilterName", this);

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final FotoTO to = getItem(position);
        View v =  LayoutInflater.from(getContext()).inflate(R.layout.item_foto, null);

        TextView foto = v.findViewById(R.id.foto);
        foto.setText(to.getNome());

        CheckBox existe = v.findViewById(R.id.checkbox);
        existe.setChecked(to.getFoto() != null);

        return v;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return mFilter;
    }
}
