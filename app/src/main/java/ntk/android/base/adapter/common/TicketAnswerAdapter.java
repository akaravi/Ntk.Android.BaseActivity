package ntk.android.base.adapter.common;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ntk.android.base.R;
import ntk.android.base.dialog.DownloadFileDialog;
import ntk.android.base.entitymodel.ticketing.TicketingAnswerModel;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.FontManager;
import ntk.android.base.view.NViewUtils;

import static ntk.android.base.adapter.BaseRecyclerAdapter.getScreenWidth;


public class TicketAnswerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ME_TYPE = 0;
    private static final int YOU_TYPE = 1;
    private final Long memberId;
    private final Long userId;
    private List<TicketingAnswerModel> arrayList;
    private Context context;
    int iconSize;
    FragmentManager fm;

    public TicketAnswerAdapter(Context context, FragmentManager fm, List<TicketingAnswerModel> arrayList, Long userId, Long memberId) {
        this.arrayList = arrayList;
        this.context = context;
        this.fm = fm;
        iconSize = NViewUtils.dpToPx(context, 50 + 32);
        this.userId = userId;
        this.memberId = memberId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == ME_TYPE) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ticket_answer_me_row_recycler, viewGroup, false);
            return new MeHolder(view);
        } else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ticket_answer_you_row_recycler, viewGroup, false);
            return new YouHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder bh, int position) {
        TicketingAnswerModel model = arrayList.get(position);
        int itemViewType = getItemViewType(position);
        if (itemViewType == ME_TYPE) {
            MeHolder holder = (MeHolder) bh;
            holder.desc.setText(Html.fromHtml(model.HtmlBody
                    .replace("<p>", "")
                    .replace("</p>", "")));
            holder.date.setText(AppUtill.GregorianToPersian(model.CreatedDate) + "");
            if (model.LinkFileIdsSrc != null && model.LinkFileIdsSrc.size() != 0) {
                holder.attach.setVisibility(View.VISIBLE);
                holder.attach.setOnClickListener(v -> DownloadFileDialog.SHOW_DIALOG(v.getContext(), fm, model.LinkFileIdsSrc));
            } else
                holder.attach.setVisibility(View.GONE);
        } else {
            YouHolder holder = (YouHolder) bh;
            holder.desc.setText(Html.fromHtml(model.HtmlBody
                    .replace("<p>", "")
                    .replace("</p>", "")));
            holder.date.setText(AppUtill.GregorianToPersian(model.CreatedDate) + "");
            if (model.LinkFileIdsSrc != null && model.LinkFileIdsSrc.size() != 0) {
                holder.attach.setVisibility(View.VISIBLE);
                holder.attach.setOnClickListener(v -> DownloadFileDialog.SHOW_DIALOG(v.getContext(), fm, model.LinkFileIdsSrc));
            } else
                holder.attach.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (memberId == arrayList.get(position).LinkMemberUserId)
            return ME_TYPE;
        else if (userId == arrayList.get(position).CreatedBy)
            return ME_TYPE;
        else return YOU_TYPE;

    }

    public class MeHolder extends RecyclerView.ViewHolder {

        TextView desc;
        TextView date;
        View attach;

        public MeHolder(View view) {
            super(view);
            desc = (view.findViewById(R.id.lblTypeRecyclerTicketAnswer));
            date = (view.findViewById(R.id.lblDateRecyclerTicketAnswer));
            attach = view.findViewById(R.id.answerAttaches);
            desc.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            date.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
        }
    }

    public class YouHolder extends RecyclerView.ViewHolder {
        TextView desc;
        TextView date;
        View attach;

        public YouHolder(View view) {
            super(view);

            desc = (view.findViewById(R.id.lblTypeRecyclerTicketAnswer));
            date = (view.findViewById(R.id.lblDateRecyclerTicketAnswer));
            attach = view.findViewById(R.id.answerAttaches);
            desc.setMaxWidth(getScreenWidth() - iconSize - 100);
            desc.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            date.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
        }
    }
}
