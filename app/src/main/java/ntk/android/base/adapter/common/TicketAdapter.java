package ntk.android.base.adapter.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import java.util.ArrayList;
import java.util.List;

import ntk.android.base.Extras;
import ntk.android.base.R;
import ntk.android.base.activity.ticketing.TicketAnswerActivity;
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
        TicketingTaskModel model = arrayList.get(position);
        holder.Lbls.get(0).setText( model.Title);
        holder.Lbls.get(2).setText(AppUtill.GregorianToPersian(model.CreatedDate) + "");
        holder.webView.loadData("<html dir=\"rtl\" lang=\"\"><body>" + model.HtmlBody + "</body></html>", "text/html; charset=utf-8", "UTF-8");

        switch (model.TicketStatus) {
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
        holder.ll.setOnClickListener(v -> {

            if (holder.webView.getVisibility() == View.VISIBLE) {

                TransitionManager.beginDelayedTransition(holder.Root,
                        new AutoTransition());
                holder.webView.setVisibility(View.GONE);
                holder.arrow.setImageResource(R.drawable.ic_baseline_expand_more);
            }

            // If the CardView is not expanded, set its visibility
            // to visible and change the expand more icon to expand less.
            else {

                TransitionManager.beginDelayedTransition(holder.Root,
                        new AutoTransition());
                holder.webView.setVisibility(View.VISIBLE);
                holder.arrow.setImageResource(R.drawable.ic_baseline_expand_less);
            }
        });
        holder.Root.setOnClickListener(v -> {
            Intent intent = new Intent(context, TicketAnswerActivity.class);
            intent.putExtra(Extras.EXTRA_FIRST_ARG, model.Id);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        List<TextView> Lbls;
        CardView Root;
        LinearLayout ll;
        WebView webView;
        ImageView arrow;

        public ViewHolder(View view) {
            super(view);
            Lbls = new ArrayList() {
                {
                    add(view.findViewById(R.id.lblNameRecyclerTicket));
                    add(view.findViewById(R.id.lblStateRecyclerTicket));
                    add(view.findViewById(R.id.lblDateRecyclerTicket));
                }
            };
            Root = view.findViewById(R.id.rootTicket);
            Lbls.get(0).setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            Lbls.get(1).setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            Lbls.get(2).setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            ll = view.findViewById(R.id.rvTitle);
            webView = view.findViewById(R.id.webView);
            arrow = view.findViewById(R.id.imgArrow);
        }
    }
}
