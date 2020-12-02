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
import ntk.android.base.fragment.abstraction.AbstractionListFragment;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.EndlessRecyclerViewScrollListener;
import ntk.android.base.utill.FontManager;
public abstract class AbstractionListActivity<TEntity> extends BaseActivity {
    protected TextView LblTitle;


    private int Total = 0;
    protected List<TEntity> models = new ArrayList<>();
    protected RecyclerView.Adapter adapter;
    protected FilterDataModel request;
    private boolean loadingMore=true;

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
        RecyclerView  Rv = findViewById(R.id.recycler);
        SwipeRefreshLayout Refresh = findViewById(R.id.swipRefresh);
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
                if (loadingMore&&totalItemsCount <= Total) {
                    RestCall((page + 1));
                }
            }
            @Override
            public void onScrolled(RecyclerView view, int dx, int dy) {
                super.onScrolled(view,dx,dy);
                if (dy > 0 || dy < 0 && viewSyncOnScrolling().isShown())
                    viewSyncOnScrolling().changeVisibility(false);

            }

            @Override
            public void onScrollStateChanged(@androidx.annotation.NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    viewSyncOnScrolling().changeVisibility(true);
                }

            }
        };
        Rv.addOnScrollListener(scrollListener);

        RestCall(1);

        Refresh.setOnRefreshListener(() -> {
            models.clear();
            loadingMore=true;
            init();
            Refresh.setRefreshing(false);
        });
        afterInit();
    }
    protected AbstractionListFragment.IntegrationView viewSyncOnScrolling() {
        return new AbstractionListFragment.IntegrationView() {
            @Override
            public boolean isShown() {
                return false;
            }
            @Override
            public void changeVisibility(boolean isVisible) {

            }
        };
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
                                if (newsContentResponse.ListItems.size() < request.RowPerPage) {
                                    loadingMore=false;
                                }
                                adapter.notifyDataSetChanged();
                                if (models.size() > 0)
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