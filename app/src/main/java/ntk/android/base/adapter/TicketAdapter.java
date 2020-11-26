package ntk.android.base.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


import ntk.android.base.Extras;
import ntk.android.base.R;
import ntk.android.base.activity.ticketing.TicketAnswerActivity;
import ntk.android.base.entitymodel.base.FilterDataModel;
import ntk.android.base.entitymodel.base.Filters;
import ntk.android.base.entitymodel.ticketing.TicketingTaskModel;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.FontManager;


;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.ViewHolder> {

    private List<TicketingTaskModel> arrayList;
    private Context context;

    public TicketAdapter(Context context, ArrayList<TicketingTaskModel> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ticket_row_recycler, viewGroup, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.Lbls.get(0).setText(arrayList.get(position).Title);
        holder.Lbls.get(2).setText(AppUtill.GregorianToPersian(arrayList.get(position).CreatedDate) + "");//todo seem to be bug for create date
        switch (arrayList.get(position).TicketStatus) {
            case 1:
                holder.Lbls.get(1).setBackgroundResource(R.drawable.circlegreen);
                holder.Lbls.get(1).setText("پاسخ داده شد");
                break;
            case 2:
                holder.Lbls.get(1).setBackgroundResource(R.drawable.circlered);
                holder.Lbls.get(1).setText("در حال رسیدگی");
                break;
            case 3:
                holder.Lbls.get(1).setBackgroundResource(R.drawable.circle_oranje);
                holder.Lbls.get(1).setText("انتظار پاسخ");
                break;
            case 4:
                holder.Lbls.get(1).setBackgroundResource(R.drawable.circle_oranje);
                holder.Lbls.get(1).setText("پاسخ مشتری");
                break;
            case 5:
                holder.Lbls.get(1).setBackgroundResource(R.drawable.circle_blue_full);
                holder.Lbls.get(1).setText("بسته شد");
                break;
        }
        holder.Root.setOnClickListener(v -> {
            FilterDataModel request = new FilterDataModel();
            Filters f = new Filters();
            f.PropertyName = "LinkTicketId";
            f.IntValue1 = arrayList.get(position).Id;
            request.addFilter(f);

            Intent intent = new Intent(context, TicketAnswerActivity.class);
            intent.putExtra(Extras.EXTRA_FIRST_ARG, new Gson().toJson(request));
            intent.putExtra(Extras.EXTRA_SECOND_ARG, arrayList.get(position).Id);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        List<TextView> Lbls;
        LinearLayout Root;

        public ViewHolder(View view) {
            super(view);
            Lbls = new ArrayList() {
                {
                    add(view.findViewById(R.id.lblNameRecyclerTicket));
                    add(view.findViewById(R.id.lblStateRecyclerTicket));
                    add(view.findViewById(R.id.lblDateRecyclerTicket));
                }
            };
            Root=  view.findViewById(R.id.rootTicket);
            Lbls.get(0).setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            Lbls.get(1).setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            Lbls.get(2).setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
        }
    }
}
