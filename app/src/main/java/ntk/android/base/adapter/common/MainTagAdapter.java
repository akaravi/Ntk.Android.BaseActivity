package ntk.android.base.adapter.common;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.util.List;

import ntk.android.base.R;
import ntk.android.base.adapter.BaseRecyclerAdapter;

public class MainTagAdapter extends BaseRecyclerAdapter<String, MainTagAdapter.VH> {
    int selectPos = -1;
    View.OnClickListener r;

    public MainTagAdapter(List<String> list, View.OnClickListener runnable) {
        super(list);
        r = runnable;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(inflate(parent, R.layout.chip_row_item));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Chip c = (Chip) holder.itemView.findViewById(R.id.chip);
        if (selectPos == position)
            c.setChecked(true);
        else
            c.setChecked(false);
        (c).setText(getItem(position));
        c.setTag(position);
        c.setOnCheckedChangeListener((compoundButton, b) ->{
         if (position==selectPos)
         {
             compoundButton.setChecked(true);
             r.onClick(compoundButton);
         }
          else  if (b) {
                selectPos = position;
                notifyDataSetChanged();
                r.onClick(compoundButton);
            }
        });
    }

    public class VH extends RecyclerView.ViewHolder {
        public VH(@NonNull View itemView) {
            super(itemView);
        }
    }
}
