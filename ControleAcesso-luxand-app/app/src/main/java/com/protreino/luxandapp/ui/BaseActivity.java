package com.protreino.luxandapp.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;

import com.protreino.luxandapp.R;
import com.protreino.luxandapp.ui.components.DialogoSelecaoListAdapter;

import java.util.List;

public class BaseActivity extends AppCompatActivity {

    protected ProgressDialog p = null;


    public void dialogoCampoSelecao(String nome, List<String> items, AdapterView.OnItemClickListener acao) {

        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        DialogoSelecaoListAdapter adapter =
                new DialogoSelecaoListAdapter(this, R.layout.simple_item_list, items, this);

        alertDialogBuilder.setAdapter(adapter, null);

        androidx.appcompat.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                acao.onItemClick(adapterView, view, i, l);
                alertDialog.dismiss();
            }
        });

        //alertDialog.setTitle(nome);
        alertDialog.show();
    }

    @UiThread
    public void createProcessDialog(String msg) {
        p = new ProgressDialog(this);
        p.setCancelable(false);
        p.setCanceledOnTouchOutside(false);
        p.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        p.getWindow().setBackgroundDrawableResource(R.drawable.dialog_rounded_background);
        p.setMessage(msg);

        p.show();
    }

    @UiThread
    public void destroyProcessDialog() {
        if (p != null) {
            p.dismiss();
            p = null;
        }
    }

    public void openConfirmDialog(String msg, Activity activity,
                                  DialogInterface.OnClickListener actionOk,
                                  DialogInterface.OnClickListener actionCancel) {
        android.app.AlertDialog.Builder confirmar = new android.app.AlertDialog.Builder(activity);

        confirmar.setTitle("Confirmar");
        confirmar.setMessage(msg);
        confirmar.setPositiveButton("OK", actionOk);
        confirmar.setNegativeButton("Cancelar", actionCancel);

        confirmar.show();
    }

}
