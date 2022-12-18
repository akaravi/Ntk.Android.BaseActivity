package ntk.android.base.activity.abstraction;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import java9.util.function.Function;
import ntk.android.base.R;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.adapter.SortingFilterAdapter;
import ntk.android.base.config.ErrorExceptionObserver;
import ntk.android.base.config.GenericErrors;
import ntk.android.base.config.ServiceExecute;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.SearchTypeModel;
import ntk.android.base.entitymodel.enums.EnumSortType;
import ntk.android.base.fragment.abstraction.AbstractionListFragment;
import ntk.android.base.utill.AppUtil;
import ntk.android.base.utill.EndlessRecyclerViewScrollListener;
import ntk.android.base.utill.FontManager;

public abstract class AbstractListActivity<TREq, TEntity> extends BaseActivity {
    protected TextView LblTitle;
    protected int Total = 0;
    protected List<TEntity> models = new ArrayList<>();
    protected RecyclerView.Adapter adapter;
    protected TREq request;
    protected boolean loadingMore = true;
    protected SearchTypeModel sortFilter;

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResourceLayout());
        requestOnIntent();
        init();
        onCreated();
    }

    public int getResourceLayout() {
        return R.layout.abstraction_list;
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
        if (getSortList() == null)
            findViewById(R.id.imgSort).setVisibility(View.GONE);
        findViewById(R.id.imgSort).setOnClickListener(v -> showFilter());
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
        if (!callOtherApi())
            RestCall(1);

        Refresh.setOnRefreshListener(() -> {
            models.clear();
            loadingMore = true;
            init();
            Refresh.setRefreshing(false);
        });
        if (showCategory()) {
            View fab = findViewById(R.id.fabCategory);
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(v -> showCategoryListDialog());
        }
        afterInit();
    }

    protected boolean callOtherApi() {
        return false;
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


    protected void RestCall(int nextPage) {
        if (AppUtil.isNetworkAvailable(this)) {
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

    public void ClickBack() {
        finish();
    }

    public void afterInit() {

    }

    public void ClickSearch() {
    }

    protected void onCreated() {

    }

    protected boolean showCategory() {
        return false;
    }

    /**
     * this abstract method for creating Request Object
     * also can get from Intent
     */
    protected abstract void requestOnIntent();

    protected abstract void onSuccessNext(ErrorException<TEntity> response);

    protected abstract Function<Integer, Observable<ErrorException<TEntity>>> apiService();

    public abstract RecyclerView.Adapter createAdapter();

    public List<SearchTypeModel> getSortList() {
        ArrayList<SearchTypeModel> objects = new ArrayList<>();
        objects.add(new SearchTypeModel().setSortColumn("Id").setSortType(EnumSortType.Ascending.index()).setDisplayName("جدیدترین"));
        objects.add(new SearchTypeModel().setSortColumn("Id").setSortType(EnumSortType.Descending.index()).setDisplayName("قدیمی ترین"));
        objects.add(new SearchTypeModel().setSortColumn("Id").setSortType(EnumSortType.Random.index()).setDisplayName("به صورت تصادفی"));
        return objects;
    }

    public void showFilter() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.sort_filter_bottom_dialog);
        Typeface r1 = FontManager.T1_Typeface(this);
        ((TextView) bottomSheetDialog.findViewById(R.id.title)).setTypeface(r1);
        RecyclerView rc = bottomSheetDialog.findViewById(R.id.rc);
        rc.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rc.setAdapter(new SortingFilterAdapter(getSortList(), searchTypeModel -> {
            sortFilter = searchTypeModel;
            //refresh list
            models.clear();
            loadingMore = true;
            init();
            bottomSheetDialog.dismiss();
        }));

        bottomSheetDialog.show();
    }

    public void showCategoryListDialog() {
    }
}