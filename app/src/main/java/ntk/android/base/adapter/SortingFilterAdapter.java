package ntk.android.base.adapter;


import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import java9.util.function.Consumer;
import ntk.android.base.R;
import ntk.android.base.entitymodel.base.SearchTypeModel;
import ntk.android.base.utill.FontManager;

public class SortingFilterAdapter extends BaseRecyclerAdapter<SearchTypeModel, SortingFilterAdapter.VH> {
    Consumer<SearchTypeModel> selectFunc;

    public SortingFilterAdapter(List<SearchTypeModel> list, Consumer<SearchTypeModel> func) {
        super(list);
        selectFunc = func;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(inflate(parent, R.layout.spinner_item));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        SearchTypeModel item = getItem(position);
        holder.tv.setText(item.getDisplayName());
        holder.itemView.setOnClickListener(view ->selectFunc.accept(item));
    }

    public class VH extends RecyclerView.ViewHolder {
        TextView tv;

        public VH(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv_spinner);
            tv.setTypeface(FontManager.T1_Typeface(itemView.getContext()));
        }
    }
}
