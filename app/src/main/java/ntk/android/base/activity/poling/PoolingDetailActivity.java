package ntk.android.base.activity.poling;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import ntk.android.base.Extras;
import ntk.android.base.R;
import ntk.android.base.adapter.DetailPoolCategoryAdapter;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterDataModel;
import ntk.android.base.entitymodel.polling.PollingContentModel;
import ntk.android.base.services.pooling.PollingContentService;
import ntk.android.base.utill.FontManager;

public class PoolingDetailActivity extends AppCompatActivity {

    TextView LblTitle;

    RecyclerView Rv;

    private String RequestStr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_detail_pooling);

        init();
    }

    private void init() {
        LblTitle = findViewById(R.id.lblTitleActDetailPooling);
        Rv = findViewById(R.id.recyclerDetailPooling);
        findViewById(R.id.imgBackActDetailPooling).setOnClickListener(v -> ClickBack());
        LblTitle.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        LblTitle.setText(getIntent().getStringExtra("Title"));
        Rv.setHasFixedSize(true);
        Rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));


        RequestStr = getIntent().getExtras().getString(Extras.EXTRA_FIRST_ARG);

        HandelData(1, new Gson().fromJson(RequestStr, FilterDataModel.class));
    }

    private void HandelData(int i, FilterDataModel request) {
        new PollingContentService(this).getAll(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new NtkObserver<ErrorException<PollingContentModel>>() {

                    @Override
                    public void onNext(@NonNull ErrorException<PollingContentModel> poolingContentListResponse) {
                        if (poolingContentListResponse.IsSuccess) {
                            DetailPoolCategoryAdapter adapter = new DetailPoolCategoryAdapter(PoolingDetailActivity.this, poolingContentListResponse.ListItems);
                            Rv.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toasty.warning(PoolingDetailActivity.this, "خطای سامانه", Toasty.LENGTH_LONG, true).show();

                    }
                });
    }

    public void ClickBack() {
        finish();
    }
}
