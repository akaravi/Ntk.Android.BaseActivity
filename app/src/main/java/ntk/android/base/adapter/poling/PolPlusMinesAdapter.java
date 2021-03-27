package ntk.android.base.adapter.poling;

import android.content.Context;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


public class PolPlusMinesAdapter extends BaseRecyclerAdapter<PollingOptionModel, PolPlusMinesAdapter.ViewHolder> {

    private Context context;
    private PollingContentModel PC;
    private final View BtnSend;
    private final View BtnChart;
    private int Score = 0;
    private Map<Long, Integer> MapVote;

    public PolPlusMinesAdapter(Context context, List<PollingOptionModel> arrayList, PollingContentModel pc, View chart, View send, View clear) {
        super(arrayList);
        this.context = context;
        this.PC = pc;
        this.BtnSend = send;
        this.BtnChart = chart;
        MapVote = new HashMap<>();
        clear.setOnClickListener(v -> {
            MapVote = new HashMap<>();

            notifyDataSetChanged();
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = inflate(viewGroup, R.layout.row_recycler_pool_plus_minse);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        PollingOptionModel optionModel = getItem(position);
        holder.Title.setText(optionModel.Option);
        if (MapVote.containsKey(optionModel.Id)) {
            Integer value = MapVote.get(optionModel.Id);
            holder.Number.setText(String.valueOf(value));
                holder.Minus.setVisibility(View.VISIBLE);
                holder.Number.setVisibility(View.VISIBLE);
        }else{
            holder.Minus.setVisibility(View.GONE);
            holder.Number.setVisibility(View.GONE);
        }
        holder.Plus.setOnClickListener(v -> {
            Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibe.vibrate(1000);
            Score = 0;
            int val = Integer.parseInt(holder.Number.getText().toString());
            for (Map.Entry<Long, Integer> map : MapVote.entrySet()) {
                Score = Score + map.getValue();
            }
            if (Score < PC.MaxVoteForThisContent) {
                if (val < PC.MaxVoteForEachOption) {
                    val = val + 1;
                    holder.Number.setText(String.valueOf(val));
                    MapVote.put(Long.parseLong(String.valueOf(optionModel.Id)), val);
                    notifyDataSetChanged();
                } else {
                    Toasty.warning(context, context.getString(R.string.max_vote_each_item) + PC.MaxVoteForEachOption +context.getString(R.string.is_string), Toasty.LENGTH_LONG, true).show();
                }
            } else {
                Toasty.warning(context, context.getString(R.string.max_vote_poll) + PC.MaxVoteForThisContent + context.getString(R.string.is_string), Toasty.LENGTH_LONG, true).show();
            }
        });
        holder.Minus.setOnClickListener(v -> {
            Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibe.vibrate(1000);
            int val = Integer.parseInt(holder.Number.getText().toString());
            if (val == 0) {
                Toasty.warning(context, R.string.no_negative_vote, Toasty.LENGTH_LONG, true).show();
            } else {
                val = val - 1;
                holder.Number.setText(String.valueOf(val));
                if (val == 0)
                    MapVote.remove(Long.parseLong(String.valueOf(optionModel.Id)));
                else
                    MapVote.put(Long.parseLong(String.valueOf(optionModel.Id)), (val));

                notifyDataSetChanged();
            }
        });
        BtnSend.setOnClickListener(v -> {
            if (MapVote.size() == 0) {
                Toasty.warning(context, R.string.no_value_selected, Toasty.LENGTH_LONG, true).show();
                return;
            }
            BtnSend.setEnabled(false);
            ArrayList<PollingVoteModel> requests = new ArrayList<>();

            for (Map.Entry<Long, Integer> map : MapVote.entrySet()) {
                PollingVoteModel vote = new PollingVoteModel();
                vote.LinkPollingOptionId = map.getKey();
                vote.LinkPollingContentId = optionModel.LinkPollingContentId;
                vote.OptionScore = map.getValue();
                requests.add(vote);
            }
            ServiceExecute.execute(new PollingVoteService(context).addBatch(requests))
                    .subscribe(new NtkObserver<ErrorException<PollingVoteModel>>() {

                        @Override
                        public void onNext(ErrorException<PollingVoteModel> poolingSubmitResponse) {
                            BtnSend.setEnabled(true);
                            if (poolingSubmitResponse.IsSuccess) {
                                Toasty.info(context, R.string.success_comment, Toasty.LENGTH_LONG, true).show();
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
                            Toasty.warning(context,  R.string.error_raised, Toasty.LENGTH_LONG, true).show();
                        }


                    });
        });
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView Number;
        TextView Title;
        ImageView Plus;
        ImageView Minus;

        public ViewHolder(View view) {
            super(view);
            Number = view.findViewById(R.id.lblRecyclerPoolPlus);
            Title = view.findViewById(R.id.lblTitleRecyclerPoolPlus);
            Plus = view.findViewById(R.id.imgPlusRecyclerPoolPlus);
            Minus = view.findViewById(R.id.imgMinusRecyclerPoolPlus);
            Number.setTypeface(FontManager.T1_Typeface(context));
            Title.setTypeface(FontManager.T1_Typeface(context));
        }
    }
}