package ntk.android.base.activity.poling;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.annotations.NonNull;
import ntk.android.base.Extras;
import ntk.android.base.R;
import ntk.android.base.adapter.poling.DetailPolCategoryAdapter;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.config.ServiceExecute;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterDataModel;
import ntk.android.base.entitymodel.base.Filters;
import ntk.android.base.entitymodel.polling.PollingContentModel;
import ntk.android.base.services.pooling.PollingContentService;
import ntk.android.base.utill.FontManager;

public class PolingDetailActivity extends AppCompatActivity {

    TextView LblTitle;
    Long id;
    String title;
    RecyclerView Rv;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_detail_pooling);
        id = 0L;
        title = "نظرسنجی ها";
        if (getIntent() != null && getIntent().getExtras() != null) {
            id = getIntent().getExtras().getLong(Extras.EXTRA_FIRST_ARG);
            title = getIntent().getStringExtra(Extras.EXTRA_SECOND_ARG);
        }
        init();
    }

    private void init() {
        LblTitle = findViewById(R.id.lblTitleActDetailPooling);
        Rv = findViewById(R.id.recyclerDetailPooling);
        findViewById(R.id.imgBackActDetailPooling).setOnClickListener(v -> ClickBack());
        LblTitle.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        LblTitle.setText(title);
        Rv.setHasFixedSize(true);
        Rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        HandelData();
    }

    private void HandelData() {
        FilterDataModel request = new FilterDataModel();
        Filters f = new Filters();
        if (id > 0) {
            f.PropertyName = "LinkCategoryId";
            f.IntValue1 = id;
            request.addFilter(f);
        }
        ServiceExecute.execute(new PollingContentService(this).getAll(request))
                .subscribe(new NtkObserver<ErrorException<PollingContentModel>>() {

                    @Override
                    public void onNext(@NonNull ErrorException<PollingContentModel> poolingContentListResponse) {
                        if (poolingContentListResponse.IsSuccess) {
                            List<PollingContentModel> arrays = new ArrayList<>();
                            for (PollingContentModel m :
                                    poolingContentListResponse.ListItems) {
                                if (m.Options.size() > 0)
                                    arrays.add(m);
                            }
                            DetailPolCategoryAdapter adapter = new DetailPolCategoryAdapter(PolingDetailActivity.this, arrays);
                            Rv.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toasty.warning(PolingDetailActivity.this, "خطای سامانه", Toasty.LENGTH_LONG, true).show();

                    }
                });
    }

    public void ClickBack() {
        finish();
    }
}
