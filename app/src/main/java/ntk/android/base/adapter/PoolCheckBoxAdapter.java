package ntk.android.base.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ntk.android.base.R;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.config.ServiceExecute;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.polling.PollingContentModel;
import ntk.android.base.entitymodel.polling.PollingOptionModel;
import ntk.android.base.entitymodel.polling.PollingVoteModel;
import ntk.android.base.services.pooling.PollingVoteService;
import ntk.android.base.utill.FontManager;


public class PoolCheckBoxAdapter extends RecyclerView.Adapter<PoolCheckBoxAdapter.ViewHolder> {

    private List<PollingOptionModel> arrayList;
    private Context context;
    private PollingContentModel PC;
    private Button BtnSend;
    private Button BtnChart;
    private int Score = 0;
    private Map<Long, Integer> MapVote;

    public PoolCheckBoxAdapter(Context context, List<PollingOptionModel> arrayList, PollingContentModel pc, Button send, Button chart) {
        this.arrayList = arrayList;
        this.context = context;
        this.PC = pc;
        this.BtnSend = send;
        this.BtnChart = chart;
        MapVote = new HashMap<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_recycler_pool_check_box, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.LblTitle.setText(arrayList.get(position).Option);

        holder.Radio.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Score = 0;
                for (Map.Entry<Long, Integer> map : MapVote.entrySet()) {
                    Score = Score + map.getValue();
                }
                if (Score < PC.MaxVoteForThisContent) {
                    MapVote.put(Long.parseLong(String.valueOf(arrayList.get(position).Id)), 1);
                    holder.Radio.setChecked(true);
                } else {
                    Toasty.warning(context, "تعداد پاسخ مجاز برای این نظر سنجی " + PC.MaxVoteForThisContent, Toasty.LENGTH_LONG, true).show();
                    holder.Radio.setChecked(false);
                }
            } else {
                Score = 0;
                for (Map.Entry<Long, Integer> map : MapVote.entrySet()) {
                    Score = Score + map.getValue();
                }
                if (Score > 0) {
                    MapVote.remove(Long.parseLong(String.valueOf(arrayList.get(position).Id)));
                    Score = Score - 1;
                    holder.Radio.setChecked(false);
                }
            }
        });

        BtnSend.setOnClickListener(v -> {
//            request.ContentId = arrayList.get(position).linkPollingContentId;
            ArrayList<PollingVoteModel> votes = new ArrayList<>();
            for (Map.Entry<Long, Integer> map : MapVote.entrySet()) {
                PollingVoteModel vote = new PollingVoteModel();
                vote.LinkPollingOptionId = map.getKey();
                vote.LinkPollingContentId = arrayList.get(position).LinkPollingContentId;
                vote.OptionScore = map.getValue();
                votes.add(vote);
            }

            ServiceExecute.execute(new PollingVoteService(context).addBatch(votes))
                    .subscribe(new NtkObserver<ErrorException<PollingVoteModel>>() {

                        @Override
                        public void onNext(ErrorException<PollingVoteModel> poolingSubmitResponse) {
                            if (poolingSubmitResponse.IsSuccess) {
                                Toasty.info(context, "نظر شما با موققثیت ثبت شد", Toasty.LENGTH_LONG, true).show();
                                if (PC.ViewStatisticsAfterVote) {
                                    BtnChart.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Toasty.warning(context, poolingSubmitResponse.ErrorMessage, Toasty.LENGTH_LONG, true).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toasty.warning(context, "خطای سامانه", Toasty.LENGTH_LONG, true).show();
                        }

                    });
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView LblTitle;
        CheckBox Radio;

        public ViewHolder(View view) {
            super(view);
            LblTitle = view.findViewById(R.id.lblRecyclerPoolCheckBox);


            Radio = view.findViewById(R.id.RadioRecyclerPoolCheckBox);
            LblTitle.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
        }
    }
}