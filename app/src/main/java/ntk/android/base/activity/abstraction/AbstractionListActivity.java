package ntk.android.base.activity.abstraction;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import java9.util.function.Function;
import ntk.android.base.Extras;
import ntk.android.base.R;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterDataModel;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.EndlessRecyclerViewScrollListener;
import ntk.android.base.utill.FontManager;

public abstract class AbstractionListActivity<TEntity> extends BaseActivity {
    protected TextView LblTitle;
    RecyclerView Rv;

    SwipeRefreshLayout Refresh;

    private int Total = 0;
    protected List<TEntity> models = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    protected FilterDataModel request;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abstraction_list);
        request = new FilterDataModel();
        request.RowPerPage = 20;
        if (getIntent() != null)
            if (getIntent().getExtras() != null) {
                String reqString = getIntent().getExtras().getString(Extras.EXTRA_FIRST_ARG, "");
                if (!reqString.equalsIgnoreCase("")) {
                    request = new Gson().fromJson(reqString, FilterDataModel.class);
                }
            }
        init();
    }

    protected RecyclerView.LayoutManager getRvLayoutManager() {
        return new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    }

    private void init() {
        LblTitle = findViewById(R.id.lblTitle);
        Rv = findViewById(R.id.recycler);
        Refresh = findViewById(R.id.swipRefresh);
        findViewById(R.id.imgBack).setOnClickListener(v -> ClickBack());
        findViewById(R.id.imgSearch).setOnClickListener(v -> ClickSearch());
        LblTitle.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Rv.setHasFixedSize(true);
        RecyclerView.LayoutManager LMC = getRvLayoutManager();
        Rv.setLayoutManager(LMC);
        adapter = createAdapter();
        Rv.setAdapter(adapter);

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(LMC) {

            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalItemsCount <= Total) {
                    RestCall((page + 1));
                }
            }
        };
        Rv.addOnScrollListener(scrollListener);

        RestCall(1);

        Refresh.setOnRefreshListener(() -> {
            models.clear();
            init();
            Refresh.setRefreshing(false);
        });
        afterInit();
    }

    public void afterInit() {

    }

    private void RestCall(int i) {
        if (AppUtill.isNetworkAvailable(this)) {
            switcher.showProgressView();
            request.CurrentPageNumber = i;

            getService().apply(request).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new NtkObserver<ErrorException<TEntity>>() {
                        @Override
                        public void onNext(@NonNull ErrorException<TEntity> newsContentResponse) {
                            if (newsContentResponse.IsSuccess) {
                                models.addAll(newsContentResponse.ListItems);
                                Total = newsContentResponse.TotalRowCount;
                                adapter.notifyDataSetChanged();
                                if (Total > 0)
                                    switcher.showContentView();
                                else
                                    switcher.showEmptyView();

                            } else
                                switcher.showErrorView(newsContentResponse.ErrorMessage, () -> init());
                        }


                        @Override
                        public void onError(@NonNull Throwable e) {
                            switcher.showErrorView("خطای سامانه مجددا تلاش کنید", () -> init());
                        }
                    });
        } else {
            switcher.showErrorView("عدم دسترسی به اینترنت", () -> init());

        }
    }


    public abstract Function<FilterDataModel, Observable<ErrorException<TEntity>>> getService();

    public abstract RecyclerView.Adapter createAdapter();

    public void ClickBack() {
        finish();
    }

    public abstract void ClickSearch();
}