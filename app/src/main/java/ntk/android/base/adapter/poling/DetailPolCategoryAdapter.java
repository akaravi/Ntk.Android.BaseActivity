package ntk.android.base.adapter.poling;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
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
import ntk.android.base.adapter.BaseRecyclerAdapter;
import ntk.android.base.entitymodel.polling.PollingContentModel;
import ntk.android.base.utill.FontManager;

public class DetailPolCategoryAdapter extends BaseRecyclerAdapter<PollingContentModel, DetailPolCategoryAdapter.ViewHolder> {

    private Context context;

    public DetailPolCategoryAdapter(Context context, List<PollingContentModel> arrayList) {
        super(arrayList);
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = inflate(viewGroup, R.layout.row_recycler_detail_pooling);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        PollingContentModel opModel = getItem(position);
        holder.LblTitle.setText(opModel.Title);
        holder.LblDescription.setText(opModel.Question);
        if (opModel.ViewStatisticsBeforeVote) {
            holder.Chart.setVisibility(View.VISIBLE);
        }
        holder.Root.setOnClickListener(v -> {
            //user can vote max 1 item
            if (opModel.MaxVoteForThisContent == 1) {
                if (holder.linOptions.getVisibility() == View.GONE) {
                    PolRadioAdapter adapter = new PolRadioAdapter(context, opModel.Options, opModel, holder.Chart, holder.sendBtn, holder.clearBtn);
                    holder.Rv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    holder.linOptions.setVisibility(View.VISIBLE);
                    holder.ImgDropDown.setRotation(180);
                } else {
                    holder.linOptions.setVisibility(View.GONE);
                    holder.Rv.setAdapter(null);
                    holder.Rv.removeAllViews();
                    holder.ImgDropDown.setRotation(0);
                }
                //user can vote >1 and each item have 1 score
            } else if (opModel.MaxVoteForThisContent > 1 && opModel.MaxVoteForEachOption == 1) {
                if (holder.linOptions.getVisibility() == View.GONE) {
                    PolCheckBoxAdapter adapter = new PolCheckBoxAdapter(context, opModel.Options, opModel, holder.Chart, holder.sendBtn, holder.clearBtn);
                    holder.Rv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    holder.linOptions.setVisibility(View.VISIBLE);
                    holder.ImgDropDown.setRotation(180);
                } else {
                    holder.linOptions.setVisibility(View.GONE);
                    holder.Rv.setAdapter(null);
                    holder.Rv.removeAllViews();
                    holder.ImgDropDown.setRotation(0);
                }
            } else {
                if (holder.linOptions.getVisibility() == View.GONE) {
                    PolPlusMinesAdapter adapter = new PolPlusMinesAdapter(context, opModel.Options, opModel, holder.Chart, holder.sendBtn, holder.clearBtn);
                    holder.Rv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    holder.linOptions.setVisibility(View.VISIBLE);
                    holder.ImgDropDown.setRotation(180);
                } else {
                    holder.linOptions.setVisibility(View.GONE);
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
            Score.setText(String.valueOf(opModel.Options.get(0).ScoreOfVotes));
            Score.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            TextView Owner = dialog.findViewById(R.id.lblOwnerScore);
            Owner.setText(String.valueOf(opModel.Options.get(0).NumberOfVotes));
            Owner.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            dialog.show();
        });
    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        TextView LblTitle;
        TextView LblDescription;
        ImageView ImgDropDown;
        RecyclerView Rv;
        Button Chart;
        LinearLayout Root;
        LinearLayout linOptions;
        View sendBtn;
        View clearBtn;

        public ViewHolder(View view) {
            super(view);
            LblTitle = view.findViewById(R.id.lblTitleRowRecyclerDetailPooling);
            LblDescription = view.findViewById(R.id.lblDescriptionRowRecyclerDetailPooling);
            ImgDropDown = view.findViewById(R.id.imgDropDownRecyclerDetailPooling);
            linOptions = view.findViewById(R.id.linOptions);
            sendBtn = view.findViewById(R.id.btnSendPoolResult);
            clearBtn = view.findViewById(R.id.btnClearPool);
            Rv = view.findViewById(R.id.recyclerOptionPooling);
            Chart = view.findViewById(R.id.btnChartRecyclerDeialPooling);
            Root = view.findViewById(R.id.rootDetailPooling);
            LblTitle.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            Rv.setHasFixedSize(true);
            RecyclerView.LayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            Rv.setLayoutManager(manager);
        }
    }
}
