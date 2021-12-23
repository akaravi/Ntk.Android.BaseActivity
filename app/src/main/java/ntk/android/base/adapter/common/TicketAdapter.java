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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import java.util.ArrayList;
import java.util.List;

import ntk.android.base.Extras;
import ntk.android.base.R;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.activity.ticketing.TicketAnswerActivity;
import ntk.android.base.dialog.DownloadFileDialog;
import ntk.android.base.entitymodel.ticketing.TicketingTaskModel;
import ntk.android.base.utill.AppUtil;
import ntk.android.base.utill.FontManager;

;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.ViewHolder> {

    private List<TicketingTaskModel> arrayList;
    private Context context;
    FragmentManager fm;

    public TicketAdapter(BaseActivity act, List<TicketingTaskModel> arrayList) {
        this.arrayList = arrayList;
        this.context = act;
        fm = act.getSupportFragmentManager();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ticket_row_recycler, viewGroup, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        TicketingTaskModel model = arrayList.get(position);
        holder.id.setText(context.getString(R.string.number_string) + model.Id);
        holder.Lbls.get(0).setText(model.Title);
        holder.Lbls.get(2).setText(AppUtil.GregorianToPersian(model.CreatedDate) + "");
        holder.webView.loadData("<html dir=\"rtl\" lang=\"\"><body>" + model.HtmlBody + "</body></html>", "text/html; charset=utf-8", "UTF-8");
        if (model.LinkFileIdsSrc != null && model.LinkFileIdsSrc.size() != 0) {
            holder.attachment.setVisibility(View.VISIBLE);
            holder.attachment.setOnClickListener(v -> {
                DownloadFileDialog.SHOW_DIALOG(v.getContext(), fm, model.LinkFileIdsSrc);
            });
        } else
            holder.attachment.setVisibility(View.GONE);
        switch (model.TicketStatus) {
            case 1:
                holder.Lbls.get(1).setBackgroundResource(R.drawable.circle_green);
                holder.Lbls.get(1).setText(R.string.answer_mode);
                break;
            case 2:
                holder.Lbls.get(1).setBackgroundResource(R.drawable.circle_red);
                holder.Lbls.get(1).setText(R.string.inProcess_mode);
                break;
            case 3:
                holder.Lbls.get(1).setBackgroundResource(R.drawable.circle_oranje);
                holder.Lbls.get(1).setText(R.string.waiting_mode);
                break;
            case 4:
                holder.Lbls.get(1).setBackgroundResource(R.drawable.circle_oranje);
                holder.Lbls.get(1).setText(R.string.customer_answer_mode);
                break;
            case 5:
                holder.Lbls.get(1).setBackgroundResource(R.drawable.circle_blue_full);
                holder.Lbls.get(1).setText(R.string.close_mode);
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
        TextView id;
        List<TextView> Lbls;
        CardView Root;
        LinearLayout ll;
        WebView webView;
        ImageView arrow;
        ImageView attachment;

        public ViewHolder(View view) {
            super(view);
            Lbls = new ArrayList() {
                {
                    add(view.findViewById(R.id.lblNameRecyclerTicket));
                    add(view.findViewById(R.id.lblStateRecyclerTicket));
                    add(view.findViewById(R.id.lblDateRecyclerTicket));
                    add(view.findViewById(R.id.ticketId));
                }
            };
            id = view.findViewById(R.id.ticketId);
            Root = view.findViewById(R.id.rootTicket);
            Lbls.get(0).setTypeface(FontManager.T1_BOLD_Typeface(context));
            Lbls.get(1).setTypeface(FontManager.T1_Typeface(context));
            Lbls.get(2).setTypeface(FontManager.T1_Typeface(context));
            Lbls.get(3).setTypeface(FontManager.T2_BOLD_Typeface(context));
            ll = view.findViewById(R.id.rvTitle);
            webView = view.findViewById(R.id.webView);
            attachment = view.findViewById(R.id.btnMoreFile);
            arrow = view.findViewById(R.id.imgArrow);
        }
    }
}
