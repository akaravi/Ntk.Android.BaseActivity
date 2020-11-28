package ntk.android.base.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ntk.android.base.R;
import ntk.android.base.entitymodel.polling.PollingContentModel;
import ntk.android.base.utill.FontManager;

public class DetailPoolCategoryAdapter extends RecyclerView.Adapter<DetailPoolCategoryAdapter.ViewHolder> {

    private List<PollingContentModel> arrayList;
    private Context context;

    public DetailPoolCategoryAdapter(Context context, List<PollingContentModel> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_recycler_detail_pooling, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.LblTitle.setText(arrayList.get(position).Title);
        holder.LblDescription.setText(arrayList.get(position).Question);
        if (arrayList.get(position).ViewStatisticsBeforeVote) {
            holder.Chart.setVisibility(View.VISIBLE);
        }
        holder.Root.setOnClickListener(v -> {
            if (arrayList.get(position).MaxVoteForThisContent == 1) {
                if (holder.Rv.getVisibility() == View.GONE) {
                    PoolRadioAdapter adapter = new PoolRadioAdapter(context, arrayList.get(position).Options, arrayList.get(position), holder.Chart);
                    holder.Rv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    holder.Rv.setVisibility(View.VISIBLE);
                    holder.ImgDropDown.setRotation(180);
                } else {
                    holder.Rv.setVisibility(View.GONE);
                    holder.Rv.setAdapter(null);
                    holder.Rv.removeAllViews();
                    holder.ImgDropDown.setRotation(0);
                }
            } else if (arrayList.get(position).MaxVoteForThisContent > 1 && arrayList.get(position).MaxVoteForEachOption == 1) {
                if (holder.Rv.getVisibility() == View.GONE) {
                    PoolCheckBoxAdapter adapter = new PoolCheckBoxAdapter(context, arrayList.get(position).Options, arrayList.get(position), holder.Btn, holder.Chart);
                    holder.Rv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    holder.Rv.setVisibility(View.VISIBLE);
                    holder.ImgDropDown.setRotation(180);
                    holder.Btn.setVisibility(View.VISIBLE);
                } else {
                    holder.Rv.setVisibility(View.GONE);
                    holder.Btn.setVisibility(View.GONE);
                    holder.Rv.setAdapter(null);
                    holder.Rv.removeAllViews();
                    holder.ImgDropDown.setRotation(0);
                }
            } else {
                if (holder.Rv.getVisibility() == View.GONE) {
                    PoolPlusMinesAdapter adapter = new PoolPlusMinesAdapter(context, arrayList.get(position).Options, arrayList.get(position), holder.Btn, holder.Chart);
                    holder.Rv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    holder.Rv.setVisibility(View.VISIBLE);
                    holder.ImgDropDown.setRotation(180);
                    holder.Btn.setVisibility(View.VISIBLE);
                } else {
                    holder.Rv.setVisibility(View.GONE);
                    holder.Btn.setVisibility(View.GONE);
                    holder.Rv.setAdapter(null);
                    holder.Rv.removeAllViews();
                    holder.ImgDropDown.setRotation(0);
                }
            }
        });

        holder.Chart.setOnClickListener(v -> {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(true);
            Window window = dialog.getWindow();
            window.setLayout(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            dialog.setContentView(R.layout.dialog_pooling_statics);
            TextView Title = dialog.findViewById(R.id.lblTitleDialogPoolingStatic);
            Title.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            TextView Score = dialog.findViewById(R.id.lblScore);
            Score.setText( String.valueOf(arrayList.get(position).Options.get(0).ScoreOfVotes));
            Score.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            TextView Owner = dialog.findViewById(R.id.lblOwnerScore);
            Owner.setText(String.valueOf( arrayList.get(position).Options.get(0).NumberOfVotes));
            Owner.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView LblTitle;
        TextView LblDescription;
        ImageView ImgDropDown;
        RecyclerView Rv;
        Button Btn;
        Button Chart;
        LinearLayout Root;

        public ViewHolder(View view) {
            super(view);
            LblTitle = view.findViewById(R.id.lblTitleRowRecyclerDetailPooling);
            LblDescription = view.findViewById(R.id.lblDescriptionRowRecyclerDetailPooling);
            ImgDropDown = view.findViewById(R.id.imgDropDownRecyclerDetailPooling);
            Rv = view.findViewById(R.id.recyclerOptionPooling);
            Btn = view.findViewById(R.id.btnSendRecyclerDeialPooling);
            Chart = view.findViewById(R.id.btnChartRecyclerDeialPooling);
            Root = view.findViewById(R.id.rootDetailPooling);
            LblTitle.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            Rv.setHasFixedSize(true);
            RecyclerView.LayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            Rv.setLayoutManager(manager);
        }
    }
}
