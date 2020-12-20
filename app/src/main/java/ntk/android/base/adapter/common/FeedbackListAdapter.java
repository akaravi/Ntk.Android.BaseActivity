package ntk.android.base.adapter.common;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ntk.android.base.R;
import ntk.android.base.adapter.BaseRecyclerAdapter;
import ntk.android.base.entitymodel.application.ApplicationAppModel;

public class FeedbackListAdapter extends BaseRecyclerAdapter<ApplicationAppModel, FeedbackListAdapter.VH> {
    public FeedbackListAdapter(List<ApplicationAppModel> list) {
        super(list);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(inflate(parent, R.layout.feedback_item_recycler));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {

    }

    public class VH extends RecyclerView.ViewHolder {
        public VH(@NonNull View itemView) {
            super(itemView);
        }
    }
}
