package ntk.android.base.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;
import java.util.List;

import ntk.android.base.R;
import ntk.android.base.utill.FontManager;

public class MultiChooseAdapter extends BaseRecyclerAdapter<String, MultiChooseAdapter.VH> {
    List<String> index;

    public MultiChooseAdapter(List<String> list) {
        super(list);
        index = new ArrayList<>();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(inflate(parent, R.layout.row__multi_choose_view));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        String item = getItem(position);
        holder.title.setText(item);
        holder.cb.setOnCheckedChangeListener(null);
        if (index.contains(item))
            holder.cb.setChecked(true);
        else
            holder.cb.setChecked(false);

        holder.itemView.setOnClickListener(view -> ((MaterialCheckBox) view.findViewById(R.id.cb)).toggle());
        holder.cb.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked) {
                if (!index.contains(item)) {
                    index.add(item);
                    notifyItemChanged(list().indexOf(item));
                }
            } else
            if (index.contains(item)) {
                index.remove(item);
                notifyItemChanged(list().indexOf(item));
            }
        });
    }

    public List<String> getIndex() {
        return index;
    }


    public class VH extends RecyclerView.ViewHolder {
        TextView title;
        MaterialCheckBox cb;

        public VH(@NonNull View itemView) {
            super(itemView);
            cb = (MaterialCheckBox) itemView.findViewById(R.id.cb);
            title = itemView.findViewById(R.id.cbText);
            title.setTypeface(FontManager.T1_Typeface(itemView.getContext()));
        }
    }
}
