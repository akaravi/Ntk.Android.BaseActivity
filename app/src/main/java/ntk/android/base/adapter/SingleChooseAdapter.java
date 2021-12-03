package ntk.android.base.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ntk.android.base.R;
import ntk.android.base.utill.FontManager;

public class SingleChooseAdapter extends BaseRecyclerAdapter<String, SingleChooseAdapter.VH> {
    String index = null;

    public SingleChooseAdapter(List<String> list) {
        super(list);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(inflate(parent, R.layout.row__single_choose_view));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        String item = getItem(position);
        holder.title.setText(item);
        if (item.equals(index))
            holder.rb.setChecked(true);
        else
            holder.rb.setChecked(false);
        View.OnClickListener listener=   v -> {
            ((RadioButton) v.findViewById(R.id.rb)).setChecked(true);
            notifyItemChanged(list().indexOf(index));
            index = item;
            notifyDataSetChanged();
        };

        holder.itemView.setOnClickListener(listener);
        holder.rb.setOnClickListener(listener);
    }

    public String getIndex() {
        return index;
    }


    public class VH extends RecyclerView.ViewHolder {
        TextView title;
        RadioButton rb;

        public VH(@NonNull View itemView) {
            super(itemView);
            rb = (RadioButton) itemView.findViewById(R.id.rb);
            title = itemView.findViewById(R.id.rbText);
            title.setTypeface(FontManager.T1_Typeface(itemView.getContext()));
        }
    }
}
