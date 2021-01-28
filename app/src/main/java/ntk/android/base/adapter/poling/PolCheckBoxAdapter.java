package ntk.android.base.adapter.poling;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import ntk.android.base.R;
import ntk.android.base.adapter.BaseRecyclerAdapter;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.config.ServiceExecute;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.polling.PollingContentModel;
import ntk.android.base.entitymodel.polling.PollingOptionModel;
import ntk.android.base.entitymodel.polling.PollingVoteModel;
import ntk.android.base.services.pooling.PollingVoteService;
import ntk.android.base.utill.FontManager;


public class PolCheckBoxAdapter extends BaseRecyclerAdapter<PollingOptionModel, PolCheckBoxAdapter.ViewHolder> {

    private Context context;
    private PollingContentModel PC;
    private final View BtnSend;
    private final View BtnChart;
    private List<Long> votesList;

    public PolCheckBoxAdapter(Context context, List<PollingOptionModel> arrayList, PollingContentModel pc, View chart, View send, View clear) {
        super(arrayList);
        this.context = context;
        this.PC = pc;
        this.BtnSend = send;
        this.BtnChart = chart;
        votesList = new ArrayList<>();
        clear.setOnClickListener(v -> {
            votesList = new ArrayList<>();

            notifyDataSetChanged();
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = inflate(viewGroup, R.layout.row_recycler_pool_check_box);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        PollingOptionModel optionModel = getItem(position);
        holder.LblTitle.setText(optionModel.Option);
        if (votesList.contains(optionModel.Id)) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }
        holder.itemView.setOnClickListener(v -> {
            //if select checkbox before
            if (votesList.contains(optionModel.Id)) {
                votesList.remove(optionModel.Id);
                notifyDataSetChanged();
            } else {
                int Score = votesList.size();
                if (Score < PC.MaxVoteForThisContent) {
                    votesList.add(((optionModel.Id)));
                    notifyDataSetChanged();
                } else {
                    Toasty.warning(context, "حداکثز تعداد پاسخ مجاز برای این نظر سنجی " + PC.MaxVoteForThisContent + " می باشد", Toasty.LENGTH_LONG, true).show();
                }
            }
        });

        BtnSend.setOnClickListener(v -> {
            if (votesList.size() == 0) {
                Toasty.warning(context, "مقداری انتخاب نشده است", Toasty.LENGTH_LONG, true).show();
                return;
            }
            BtnSend.setEnabled(false);
            ArrayList<PollingVoteModel> requests = new ArrayList<>();
            for (Long id : votesList) {
                PollingVoteModel vote = new PollingVoteModel();
                vote.LinkPollingOptionId = id;
                vote.LinkPollingContentId = optionModel.LinkPollingContentId;
                vote.OptionScore = 1;
                requests.add(vote);
            }
            ServiceExecute.execute(new PollingVoteService(context).addBatch(requests))
                    .subscribe(new NtkObserver<ErrorException<PollingVoteModel>>() {

                        @Override
                        public void onNext(ErrorException<PollingVoteModel> poolingSubmitResponse) {
                            BtnSend.setEnabled(true);
                            if (poolingSubmitResponse.IsSuccess) {
                                Toasty.info(context, "نظر شما با موققیت ثبت شد", Toasty.LENGTH_LONG, true).show();
                                if (PC.ViewStatisticsAfterVote) {
                                    BtnChart.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Toasty.warning(context, poolingSubmitResponse.ErrorMessage, Toasty.LENGTH_LONG, true).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            BtnSend.setEnabled(true);
                            Toasty.warning(context, "خطا در ارسال اطلاعات، لطفا مجدد تلاش فرمایید", Toasty.LENGTH_LONG, true).show();
                        }

                    });
        });
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView LblTitle;
        CheckBox checkBox;

        public ViewHolder(View view) {
            super(view);
            LblTitle = view.findViewById(R.id.lblRecyclerPoolCheckBox);


            checkBox = view.findViewById(R.id.RadioRecyclerPoolCheckBox);
            LblTitle.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
        }
    }
}