package ntk.android.base.adapter;


import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */

public abstract class BaseRcAdapter<E> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected List<E> list;

    public BaseRcAdapter(ArrayList<E> list) {
        this.list = list;
    }

    public BaseRcAdapter(List<E> list) {
        this.list = list;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void AddItems(List<E> listItems) {
        list.addAll(listItems);
        notifyDataSetChanged();
    }

    public void ClearAll() {
        list.clear();
        notifyDataSetChanged();
    }

    public View inflate(ViewGroup viewGroup, @LayoutRes int layout) {
        return LayoutInflater.from(viewGroup.getContext()).inflate(layout, viewGroup, false);
    }

    public void AddtoEnd(E item) {
        list.add(item);
        notifyItemInserted(list.size() - 1);
    }

    public List<E> List() {
        return list;
    }

    public E getItem(int position) {
        return list.get(position);
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

}
