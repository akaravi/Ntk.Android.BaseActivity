package ntk.android.base.adapter;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import ntk.android.base.R;

/**
 *
 */

public abstract class BaseRecyclerAdapter<Entity, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    protected List<Entity> list;
    protected int drawable = R.mipmap.ic_launcher;

    public BaseRecyclerAdapter(ArrayList<Entity> list) {
        this.list = list;
    }

    public BaseRecyclerAdapter(List<Entity> list) {
        this.list = list;
    }


    @Override
    public final int getItemCount() {
        return list.size();
    }

    public void clearAll() {
        list.clear();
        notifyDataSetChanged();
    }

    public void addToEnd(Entity item) {
        list.add(item);
        notifyItemInserted(list.size() - 1);
    }

    public List<Entity> List() {
        return list;
    }

    public void loadImage(String path, ImageView holder) {
        loadImage(path, holder, null);
    }

    public void loadImage(String path, ImageView holder, View loading) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnFail(drawable).cacheOnDisk(true).build();
        ImageLoader.getInstance().displayImage(path, holder, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if (loading != null) loading.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if (loading != null) loading.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (loading != null) loading.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

    }

    public View inflate(ViewGroup viewGroup, @LayoutRes int layout) {
        return LayoutInflater.from(viewGroup.getContext()).inflate(layout, viewGroup, false);
    }


    public Entity getItem(int position) {
        return list.get(position);
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

}
