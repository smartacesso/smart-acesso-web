package br.com.smartacesso.pedestre.utils;

import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gustavo on 29/01/18.
 */

public class DataHolder {

    private static DataHolder instance;

    public static DataHolder getInstance(){
        if(instance == null) {
            Log.e("NOVO_OBJ", "vazio!!!");
            instance = new DataHolder();
        }
        return instance;
    }


    private Map<String, WeakReference<Object>> data;

    private DataHolder(){
        data = new HashMap<String, WeakReference<Object>>();
    }

    public void save(String id, Object object) {
        data.put(id, new WeakReference<Object>(object));
    }

    public Object retrieve(String id) {
        WeakReference<Object> objectWeakReference = data.get(id);
        if(objectWeakReference != null)
            return objectWeakReference.get();
        else
            return null;
    }
}
