package ntk.android.base.adapter.poling;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.annotations.NonNull;
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


public class PolRadioAdapter extends BaseRecyclerAdapter<PollingOptionModel, PolRadioAdapter.ViewHolder> {

    private final Context context;
    private int lastSelectedPosition = -1;
    PollingVoteModel vote;
    private final PollingContentModel pollingContentModel;
    private final View BtnChart;
    private final View BtnSend;

    public PolRadioAdapter(Context context, List<PollingOptionModel> arrayList, PollingContentModel pc, View chart, View send, View clear) {
        super(arrayList);
        this.context = context;
        this.BtnChart = chart;
        this.BtnSend = send;
        this.pollingContentModel = pc;
        vote = new PollingVoteModel();
        clear.setOnClickListener(v -> {
            lastSelectedPosition = -1;
            vote = new PollingVoteModel();
            notifyDataSetChanged();
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = inflate(viewGroup, R.layout.row_recycler_pool_radio);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        PollingOptionModel optionModel = getItem(position);

        holder.LblTitle.setText(optionModel.Option);
        holder.Radio.setChecked(lastSelectedPosition == position);
        holder.itemView.setOnClickListener(v -> {
            lastSelectedPosition = position;
            notifyDataSetChanged();
        });
        BtnSend.setOnClickListener(v -> {
            if (lastSelectedPosition == -1) {
                Toasty.warning(context, "مقداری انتخاب نشده است", Toasty.LENGTH_LONG, true).show();
                return;
            }
            BtnSend.setEnabled(false);
            vote.LinkPollingOptionId = Long.parseLong(String.valueOf(optionModel.Id));
            vote.OptionScore = 1;
            vote.LinkPollingContentId = optionModel.LinkPollingContentId;
            ServiceExecute.execute(new PollingVoteService(context).add(vote))
                    .subscribe(new NtkObserver<ErrorException<PollingVoteModel>>() {

                        @Override
                        public void onNext(@NonNull ErrorException<PollingVoteModel> poolingSubmitResponse) {
                            BtnSend.setEnabled(true);
                            if (poolingSubmitResponse.IsSuccess) {
                                Toasty.info(context, "نظر شما با موققیت ثبت شد", Toasty.LENGTH_LONG, true).show();
                                //if user can see statics after vote
                                if (pollingContentModel.ViewStatisticsAfterVote) {
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
        RadioButton Radio;

        public ViewHolder(View view) {
            super(view);
            Radio = view.findViewById(R.id.RadioRecyclerPoolRadio);
            LblTitle = view.findViewById(R.id.lblRecyclerPoolRadio);
            LblTitle.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));

        }
    }
}