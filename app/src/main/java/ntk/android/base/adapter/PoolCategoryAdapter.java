package ntk.android.base.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.List;

import ntk.android.base.Extras;
import ntk.android.base.R;
import ntk.android.base.activity.poling.PolingDetailActivity;
import ntk.android.base.entitymodel.base.FilterDataModel;
import ntk.android.base.entitymodel.base.Filters;
import ntk.android.base.entitymodel.polling.PollingCategoryModel;
import ntk.android.base.utill.FontManager;


public class PoolCategoryAdapter extends RecyclerView.Adapter<PoolCategoryAdapter.ViewHolder> {

    private List<PollingCategoryModel> arrayList;
    private Context context;

    public PoolCategoryAdapter(Context context, List<PollingCategoryModel> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_recycler_pooling, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.LblTitle.setText(arrayList.get(position).Title);
        holder.Root.setOnClickListener(v -> {

            Intent intent = new Intent(context, PolingDetailActivity.class);
            intent.putExtra(Extras.EXTRA_FIRST_ARG,arrayList.get(position).Id);
            intent.putExtra(Extras.EXTRA_SECOND_ARG, arrayList.get(position).Title);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView LblTitle;
        LinearLayout Root;

        public ViewHolder(View view) {
            super(view);
            LblTitle = view.findViewById(R.id.lblRowRecyclerPooling);


            Root = view.findViewById(R.id.rootPooling);

            LblTitle.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
        }
    }
}