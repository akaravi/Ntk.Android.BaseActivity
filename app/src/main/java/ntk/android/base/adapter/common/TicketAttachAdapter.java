package ntk.android.base.adapter.common;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import ntk.android.base.R;
import ntk.android.base.adapter.BaseRecyclerAdapter;
import ntk.android.base.event.RemoveAttachEvent;
import ntk.android.base.utill.FontManager;

public class TicketAttachAdapter extends BaseRecyclerAdapter<String, TicketAttachAdapter.ViewHolder> {

    private Context context;

    public TicketAttachAdapter(Context context, List<String> arrayList) {
        super(arrayList);
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = inflate(viewGroup, R.layout.ticket_attach_item_recycler);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        String[] strs = getItem(position).split("/");
        String FileName = strs[strs.length - 1];
        holder.Lbl.setText(FileName);
        holder.Delete.setOnClickListener(v -> EventBus.getDefault().post(new RemoveAttachEvent(position)));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView Lbl;

        View Delete;

        ImageView Img;

        public ViewHolder(View view) {
            super(view);
            Lbl = view.findViewById(R.id.lblTitleRecyclerAttach);
            Delete = view.findViewById(R.id.imgRemveAttach);
            Img = view.findViewById(R.id.imgRecyclerAttach);
            Lbl.setTypeface(FontManager.T1_Typeface(context));
        }
    }
}
