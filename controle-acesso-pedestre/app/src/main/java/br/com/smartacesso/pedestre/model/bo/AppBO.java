package br.com.smartacesso.pedestre.model.bo;

import android.content.Context;

import java.text.SimpleDateFormat;

import br.com.smartacesso.pedestre.utils.AppConstants;


/**
 * Created by gustavo on 08/02/18.
 */

public class AppBO {

    protected Context context;

    protected SimpleDateFormat sdfJson = new SimpleDateFormat(AppConstants.JSON_DATE_FORMAT);

    public AppBO(Context context){
        this.context = context;
    }

}
