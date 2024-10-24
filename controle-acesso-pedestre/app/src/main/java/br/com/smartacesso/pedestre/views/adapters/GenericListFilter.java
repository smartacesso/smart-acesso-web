package br.com.smartacesso.pedestre.views.adapters;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gustavo on 27/01/18.
 */

public class GenericListFilter<T> extends Filter {

    // class properties
    private ArrayAdapter<T> mAdapterUsed;
    private List<T> mInternalList;
    private Method mCompairMethod;

    /**
     * Copycat constructor
     * @param list  the original list to be used
     */
    public GenericListFilter(List<T> list, String reflectMethodName, ArrayAdapter<T> adapter) {
        super ();

        mInternalList = new ArrayList<>(list);
        mAdapterUsed  = adapter;

        try {
            ParameterizedType stringListType = (ParameterizedType)
                    getClass().getField("mInternalList").getGenericType();
            mCompairMethod =
                    stringListType.getActualTypeArguments()[0].getClass().getMethod(reflectMethodName);
        }
        catch (Exception ex) {
            //Log.w("GenericListFilter", ex.getMessage(), ex);

            try {
                if (mInternalList.size() > 0) {
                    T type = mInternalList.get(0);
                    mCompairMethod = type.getClass().getMethod(reflectMethodName);
                }
            }
            catch (Exception e) {
                //Log.e("GenericListFilter", e.getMessage(), e);
            }

        }
    }

    /**
     * Let's filter the data with the given constraint
     * @param constraint
     * @return
     */
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        List<T> filteredContents = new ArrayList<>();

        if ( constraint.length() > 0 ) {
            try {
                for (T obj : mInternalList) {
                    String result = (String) mCompairMethod.invoke(obj);
                    if (result.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filteredContents.add(obj);
                    }
                }
            }
            catch (Exception ex) {
                Log.e("GenericListFilter", ex.getMessage(), ex);
            }
        }
        else {
            filteredContents.addAll(mInternalList);
        }

        results.values = filteredContents;
        results.count  = filteredContents.size();
        return results;
    }

    /**
     * Publish the filtering adapter list
     * @param constraint
     * @param results
     */
    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        mAdapterUsed.clear();
        mAdapterUsed.addAll((List<T>) results.values);

        if ( results.count == 0 ) {
            mAdapterUsed.notifyDataSetInvalidated();
        }
        else {
            mAdapterUsed.notifyDataSetChanged();
        }


    }

}
