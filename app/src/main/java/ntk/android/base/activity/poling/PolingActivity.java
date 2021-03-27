package ntk.android.base.activity.poling;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.reactivex.annotations.NonNull;
import ntk.android.base.R;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.adapter.poling.PolCategoryAdapter;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.config.ServiceExecute;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterModel;
import ntk.android.base.entitymodel.polling.PollingCategoryModel;
import ntk.android.base.services.pooling.PollingCategoryService;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.FontManager;

public class PolingActivity extends BaseActivity {

    TextView LblTitle;
    RecyclerView Rv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_pooling);

        init();
    }

    private void init() {
        LblTitle = findViewById(R.id.lblTitleActPooling);
        Rv = findViewById(R.id.recyclerPooling);
        findViewById(R.id.imgBackActPooling).setOnClickListener(v -> ClickBack());
        LblTitle.setTypeface(FontManager.T1_Typeface(this));
        Rv.setHasFixedSize(true);
        Rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        if (AppUtill.isNetworkAvailable(this)) {
            // show loading
            switcher.showProgressView();
            ServiceExecute.execute(new PollingCategoryService(this).getAll(new FilterModel()))
                    .subscribe(new NtkObserver<ErrorException<PollingCategoryModel>>() {
                        @Override
                        public void onNext(@NonNull ErrorException<PollingCategoryModel> poolingCategoryResponse) {
                            if (poolingCategoryResponse.IsSuccess) {
                                PolCategoryAdapter adapter = new PolCategoryAdapter(PolingActivity.this, poolingCategoryResponse.ListItems);
                                Rv.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                if (adapter.getItemCount() > 0)
                                    switcher.showContentView();
                                else
                                    switcher.showEmptyView();

                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            switcher.showErrorView(getString(R.string.error_raised), () -> init());
                        }
                    });
        } else {
            switcher.showErrorView(getString(R.string.per_no_net), () -> init());

        }
    }

    public void ClickBack() {
        finish();
    }
}
