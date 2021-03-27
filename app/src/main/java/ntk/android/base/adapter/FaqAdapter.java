package ntk.android.base.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


import ntk.android.base.R;
import ntk.android.base.entitymodel.ticketing.TicketingFaqModel;
import ntk.android.base.utill.FontManager;

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.ViewHolder> {

    private List<TicketingFaqModel> arrayList;
    private Context context;

    public FaqAdapter(Context context, List<TicketingFaqModel> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.faq_item_recycler, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.Lbls.get(0).setText(arrayList.get(position).Question);
        holder.Lbls.get(1).setText(arrayList.get(position).Answer);
        holder.itemView.findViewById(R.id.rootFaq).setOnClickListener(view -> {
            if (holder.Web.getVisibility() == View.GONE) {
                holder.Web.loadData("<html dir=\"rtl\" lang=\"\"><body>" + arrayList.get(position).Answer + "</body></html>", "text/html; charset=utf-8", "UTF-8");
                holder.Web.setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.rowDetailFaq).setVisibility(View.VISIBLE);
            } else {
                holder.Web.setVisibility(View.GONE);
                holder.itemView.findViewById(R.id.rowDetailFaq).setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        List<TextView> Lbls;

        WebView Web;

        @SuppressLint("SetJavaScriptEnabled")
        public ViewHolder(View view) {
            super(view);

            Lbls = new ArrayList() {{
                add(view.findViewById(R.id.lblTitleFaq));
                add(view.findViewById(R.id.lblMessageFaq));
            }};


            Web = view.findViewById(R.id.WebViewFaqList);
            Lbls.get(0).setTypeface(FontManager.T1_Typeface(context));
            Lbls.get(1).setTypeface(FontManager.T1_Typeface(context));
            Web.getSettings().setJavaScriptEnabled(true);
            Web.getSettings().setBuiltInZoomControls(true);
            Web.setVisibility(View.GONE);
        }
    }
}
