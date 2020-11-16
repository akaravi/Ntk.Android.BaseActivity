package ntk.android.base.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import ntk.android.base.R;
import ntk.android.base.event.RemoveAttachEvent;
import ntk.android.base.utill.FontManager;

public class TicketAttachAdapter extends RecyclerView.Adapter<TicketAttachAdapter.ViewHolder> {

    private List<String> arrayList;
    private Context context;

    public TicketAttachAdapter(Context context, List<String> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ticket_attach_item_recycler, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        String[] strs = arrayList.get(position).split("/");
        String FileName = strs[strs.length - 1];
        holder.Lbl.setText(FileName);
        holder.Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new RemoveAttachEvent(position));
            }
        });
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView Lbl;

        Button Delete;

        ImageView Img;

        public ViewHolder(View view) {
            super(view);
            Lbl = view.findViewById(R.id.lblTitleRecyclerAttach);
            Delete = view.findViewById(R.id.imgRemoveRecyclerAttach);
            Img = view.findViewById(R.id.imgRecyclerAttach);
            Lbl.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
        }
    }
}
