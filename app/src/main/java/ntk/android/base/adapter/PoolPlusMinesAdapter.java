package ntk.android.base.adapter;

import android.content.Context;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.polling.PollingContentModel;
import ntk.android.base.entitymodel.polling.PollingOptionModel;
import ntk.android.base.entitymodel.polling.PollingVoteModel;
import ntk.android.base.services.pooling.PollingVoteService;
import ntk.android.base.utill.FontManager;


public class PoolPlusMinesAdapter extends RecyclerView.Adapter<PoolPlusMinesAdapter.ViewHolder> {

    private List<PollingOptionModel> arrayList;
    private Context context;
    private PollingContentModel PC;
    private Button BtnSend;
    private Button BtnChart;
    private int Score = 0;
    private Map<Long, Integer> MapVote;

    public PoolPlusMinesAdapter(Context context, List<PollingOptionModel> arrayList, PollingContentModel pc, Button send, Button chart) {
        this.arrayList = arrayList;
        this.context = context;
        this.PC = pc;
        this.BtnSend = send;
        this.BtnChart = chart;
        MapVote = new HashMap<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_recycler_pool_plus_minse, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.Title.setText(arrayList.get(position).Option);
        holder.Plus.setOnClickListener(v -> {
            Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibe.vibrate(100);
            Score = 0;
            int val = Integer.parseInt(holder.Number.getText().toString());
            for (Map.Entry<Long, Integer> map : MapVote.entrySet()) {
                Score = Score + map.getValue();
            }
            if (Score < PC.MaxVoteForThisContent) {
                if (val < PC.MaxVoteForEachOption) {
                    val = val + 1;
                    holder.Number.setText(String.valueOf(val));
                    MapVote.put(Long.parseLong(String.valueOf(arrayList.get(position).Id)), val);
                } else {
                    Toasty.warning(context, "تعداد پاسخ مجاز برای این گزینه " + PC.MaxVoteForEachOption, Toasty.LENGTH_LONG, true).show();
                }
            } else {
                Toasty.warning(context, "تعداد پاسخ مجاز برای این نظر سنجی " + PC.MaxVoteForThisContent, Toasty.LENGTH_LONG, true).show();
            }
        });
        holder.Minus.setOnClickListener(v -> {
            Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibe.vibrate(100);
            int val = Integer.parseInt(holder.Number.getText().toString());
            if (val == 0) {
                Toasty.warning(context, "امکان دادن امتیاز منفی وجود ندارد", Toasty.LENGTH_LONG, true).show();
            } else {
                val = val - 1;
                holder.Number.setText(String.valueOf(val));
                MapVote.put(Long.parseLong(String.valueOf(arrayList.get(position).Id)), (val));
            }
        });
        BtnSend.setOnClickListener(v -> {
//            PoolingSubmitRequest request = new PoolingSubmitRequest();
//            request.ContentId = arrayList.get(position).LinkPollingContentId;
            ArrayList<PollingVoteModel> votes = new ArrayList<>();

            for (Map.Entry<Long, Integer> map : MapVote.entrySet()) {
                PollingVoteModel vote = new PollingVoteModel();
                vote.LinkPollingOptionId = map.getKey();
                vote.LinkPollingContentId = arrayList.get(position).LinkPollingContentId;
                vote.OptionScore = map.getValue();
                votes.add(vote);
            }

            new PollingVoteService(context).addBatch(votes)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
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
            Number.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            Title.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
        }
    }
}