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
    public MultiChooseAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MultiChooseAdapter.VH(inflate(parent, R.layout.row__multi_choose_view));
    }

    @Override
    public void onBindViewHolder(@NonNull MultiChooseAdapter.VH holder, int position) {
        String item = getItem(position);
        holder.title.setText(item);
        if (index.contains(item))
            holder.cb.setChecked(true);
        else
            holder.cb.setChecked(false);

        holder.itemView.setOnClickListener(view -> ((MaterialCheckBox) view.findViewById(R.id.cb)).toggle());
        holder.cb.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked) {
                index.add(item);
            }else
                index.remove(item);
            notifyItemChanged(list().indexOf(item));
//            notifyDataSetChanged();
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
