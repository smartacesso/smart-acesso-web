package br.com.smartacesso.pedestre.views.listeners;

import android.widget.AbsListView;

/**
 * Created by gustavo on 27/01/18.
 */

public abstract class ListViewLazyLoader implements AbsListView.OnScrollListener {

    private boolean loading = true;
    private int previousTotal = 0 ;
    private int threshold = 10;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
//        if(loading) {
//            if(totalItemCount > previousTotal) {
//                // the loading has finished
//                loading = false ;
//                previousTotal = totalItemCount ;
//            }
//        }

        // check if the List needs more data
        if(((firstVisibleItem + visibleItemCount ) >= (totalItemCount))) {
            loading = true ;

            // List needs more data. Go fetch !!
            loadMore(view, firstVisibleItem,
                    visibleItemCount, totalItemCount);
        }
    }

    // Called when the user is nearing the end of the ListView
    // and the ListView is ready to add more items.
    public abstract void loadMore(AbsListView view, int firstVisibleItem,
                                  int visibleItemCount, int totalItemCount);
}