package ntk.android.base.activity.abstraction;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import java9.util.function.Function;
import ntk.android.base.R;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.config.ErrorExceptionObserver;
import ntk.android.base.config.ServiceExecute;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.fragment.abstraction.AbstractionListFragment;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.EndlessRecyclerViewScrollListener;
import ntk.android.base.utill.FontManager;
import ntk.android.base.config.GenericErrors;

public abstract class AbstractListActivity<TREq, TEntity> extends BaseActivity {
    protected TextView LblTitle;


    protected int Total = 0;
    protected List<TEntity> models = new ArrayList<>();
    protected RecyclerView.Adapter adapter;
    protected TREq request;
    protected boolean loadingMore = true;

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abstraction_list);
        requestOnIntent();
        init();
        onCreated();
    }

    protected abstract void requestOnIntent();

    protected void onCreated() {

    }

    public RecyclerView.LayoutManager getRvLayoutManager() {
        return new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    }

    private void init() {
        switcher.setLoadMore(findViewById(R.id.loadMoreProgress));
        LblTitle = findViewById(R.id.lblTitle);
        RecyclerView Rv = findViewById(R.id.recycler);
        SwipeRefreshLayout Refresh = findViewById(R.id.swipRefresh);
        findViewById(R.id.imgBack).setOnClickListener(v -> ClickBack());
        findViewById(R.id.imgSearch).setOnClickListener(v -> ClickSearch());
        LblTitle.setTypeface(FontManager.T1_Typeface(this));
        Rv.setHasFixedSize(true);
        RecyclerView.LayoutManager LMC = getRvLayoutManager();
        Rv.setLayoutManager(LMC);
        adapter = createAdapter();
        Rv.setAdapter(adapter);

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(LMC) {

            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (loadingMore && totalItemsCount <= Total) {
                    RestCall((page + 1));
                }
            }

            @Override
            public void onScrolled(RecyclerView view, int dx, int dy) {
                super.onScrolled(view, dx, dy);
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
            loadingMore = true;
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

    private void RestCall(int nextPage) {
        if (AppUtill.isNetworkAvailable(this)) {
            if (nextPage == 1)
                switcher.showProgressView();
            else
                switcher.showLoadMore();
            ServiceExecute.execute(apiService().apply((nextPage)))
                    .subscribe(new ErrorExceptionObserver<TEntity>(switcher::showErrorView) {

                        @Override
                        protected void SuccessResponse(ErrorException<TEntity> response) {
                            onSuccessNext(response);
                        }

                        @Override
                        protected Runnable tryAgainMethod() {
                            return () -> RestCall(nextPage);
                        }
                    });
        } else {
            new GenericErrors().netError(switcher::showErrorView, () -> RestCall(nextPage));

        }
    }

    protected abstract void onSuccessNext(ErrorException<TEntity> response);

    protected abstract Function<Integer, Observable<ErrorException<TEntity>>> apiService();

    public abstract RecyclerView.Adapter createAdapter();

    public void ClickBack() {
        finish();
    }

    public  void ClickSearch(){}
}