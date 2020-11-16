package ntk.android.base.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ntk.android.base.R;
import ntk.android.base.entitymodel.ticketing.TicketingAnswerModel;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.FontManager;


public class TicketAnswerAdapter extends RecyclerView.Adapter<TicketAnswerAdapter.ViewHolder> {

    private List<TicketingAnswerModel> arrayList;
    private Context context;

    public TicketAnswerAdapter(Context context, List<TicketingAnswerModel> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ticket_answer_row_recycler, viewGroup, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.Lbls.get(0).setText(Html.fromHtml(arrayList.get(position).HtmlBody
                .replace("<p>", "")
                .replace("</p>", "")));
        holder.Lbls.get(1).setText(AppUtill.GregorianToPersian(arrayList.get(position).CreatedDate) + "");
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        List<TextView> Lbls;

        public ViewHolder(View view) {
            super(view);
            Lbls=new ArrayList<TextView>(){{
                add(view.findViewById(R.id.lblTypeRecyclerTicketAnswer));
                add(view.findViewById(R.id.lblDateRecyclerTicketAnswer));
            }};
            Lbls.get(0).setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            Lbls.get(1).setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
        }
    }
}
