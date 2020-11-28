package ntk.android.base.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import ntk.android.base.R;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.polling.PollingContentModel;
import ntk.android.base.entitymodel.polling.PollingOptionModel;
import ntk.android.base.entitymodel.polling.PollingVoteModel;
import ntk.android.base.services.pooling.PollingVoteService;
import ntk.android.base.utill.FontManager;


public class PoolRadioAdapter extends RecyclerView.Adapter<PoolRadioAdapter.ViewHolder> {

    private List<PollingOptionModel> arrayList;
    private Context context;
    private int lastSelectedPosition = -1;
    private PollingContentModel PC;
    private Button BtnChart;

    public PoolRadioAdapter(Context context, List<PollingOptionModel> arrayList, PollingContentModel pc, Button chart) {
        this.arrayList = arrayList;
        this.context = context;
        this.BtnChart = chart;
        this.PC = pc;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_recycler_pool_radio, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.LblTitle.setText(arrayList.get(position).Option);
        holder.Radio.setChecked(lastSelectedPosition == position);
        holder.Radio.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                ArrayList<PollingVoteModel> votes = new ArrayList<>();
                PollingVoteModel vote = new PollingVoteModel();
//                vote.OptionId = Long.parseLong(String.valueOf(arrayList.get(position).Id));
                vote.LinkPollingOptionId = Long.parseLong(String.valueOf(arrayList.get(position).Id));
                vote.OptionScore = 1;
                vote.LinkPollingContentId = arrayList.get(position).LinkPollingContentId;
                votes.add(vote);
                new PollingVoteService(context).addBatch(votes).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new NtkObserver<ErrorException<PollingVoteModel>>() {

                            @Override
                            public void onNext(@NonNull ErrorException<PollingVoteModel> poolingSubmitResponse) {
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
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView LblTitle;
        RadioButton Radio;

        public ViewHolder(View view) {
            super(view);
            Radio = view.findViewById(R.id.RadioRecyclerPoolRadio);
            LblTitle=view.findViewById(R.id.lblRecyclerPoolRadio);
            LblTitle.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            Radio.setOnClickListener(v -> {
                lastSelectedPosition = getAdapterPosition();
                notifyDataSetChanged();
            });
        }
    }
}