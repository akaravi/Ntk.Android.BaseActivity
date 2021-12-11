package ntk.android.base.fragment.abstraction;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import ntk.android.base.adapter.SortingFilterAdapter;
import ntk.android.base.config.ErrorExceptionObserver;
import ntk.android.base.config.GenericErrors;
import ntk.android.base.config.ServiceExecute;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.SearchTypeModel;
import ntk.android.base.entitymodel.enums.EnumSortType;
import ntk.android.base.fragment.BaseFragment;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.EndlessRecyclerViewScrollListener;
import ntk.android.base.utill.FontManager;

public abstract class AbstractionListFragment<TREq, TEntity> extends BaseFragment {
    final String EXTRA_SHOW_TOOLBAR = "ASF_ARG_TOOLBAR";
    TextView LblTitle;
    String title = "";
    boolean toolbarShow = false;
    protected int Total = 0;
    protected List<TEntity> models = new ArrayList<>();
    protected RecyclerView.Adapter adapter;
    protected TREq request;
    protected boolean loadingMore = true;
    protected SearchTypeModel sortFilter;

    @Override
    public void onCreated() {
        super.onCreated();
        //can customize for future
    }

    @Override
    public void onCreateFragment() {
        setContentView(R.layout.abstraction_list);
        requestOnIntent();
    }

    @Override
    public final void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null)
            toolbarShow = getArguments().getBoolean(EXTRA_SHOW_TOOLBAR, false);

        if (!withToolbar()) {
            findViewById(R.id.ToolbarRv).setVisibility(View.GONE);
            findViewById(R.id.toolbarShadow).setVisibility(View.GONE);
        }
        switcher.setLoadMore(findViewById(R.id.loadMoreProgress));
        init();

    }


    protected RecyclerView.LayoutManager getRvLayoutManager() {
        return new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    }

    private void init() {

        LblTitle = findViewById(R.id.lblTitle);
        if (!title.equalsIgnoreCase(""))
            LblTitle.setText(title);
        RecyclerView Rv = findViewById(R.id.recycler);
        SwipeRefreshLayout Refresh = findViewById(R.id.swipRefresh);
        findViewById(R.id.imgBack).setOnClickListener(v -> ClickBack());
        if (getSortList() == null)
            findViewById(R.id.imgSort).setVisibility(View.GONE);
        findViewById(R.id.imgSort).setOnClickListener(v -> showFilter());
        findViewById(R.id.imgSearch).setOnClickListener(v -> ClickSearch());
        LblTitle.setTypeface(FontManager.T1_Typeface(getContext()));
        Rv.setHasFixedSize(true);
        RecyclerView.LayoutManager LMC = getRvLayoutManager();
        Rv.setLayoutManager(LMC);
        adapter = createAdapter();

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
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
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
        Rv.setAdapter(adapter);
        afterInit();
    }

    protected IntegrationView viewSyncOnScrolling() {
        return new IntegrationView() {
            @Override
            public boolean isShown() {
                return false;
            }

            @Override
            public void changeVisibility(boolean isVisible) {

            }
        };
    }


    private void RestCall(int nextPage) {
        if (AppUtill.isNetworkAvailable(getContext())) {
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

    public boolean withToolbar() {
        return toolbarShow;
    }

    public void ClickBack() {
        getActivity().finish();
    }

    public void afterInit() {

    }

    public void ClickSearch() {
    }

    /**
     * this abstract method for creating Request Object
     * also can get from Intent
     */
    protected abstract void requestOnIntent();


    protected abstract void onSuccessNext(ErrorException<TEntity> response);

    protected abstract Function<Integer, Observable<ErrorException<TEntity>>> apiService();

    public abstract RecyclerView.Adapter createAdapter();

    public abstract static class IntegrationView {
        public abstract boolean isShown();

        public abstract void changeVisibility(boolean isVisible);
    }

    public List<SearchTypeModel> getSortList() {
        ArrayList<SearchTypeModel> objects = new ArrayList<>();
        objects.add(new SearchTypeModel().setSortColumn("Id").setSortType(EnumSortType.Ascending.index()).setDisplayName("جدیدترین"));
        objects.add(new SearchTypeModel().setSortColumn("Id").setSortType(EnumSortType.Descending.index()).setDisplayName("قدیمی ترین"));
        objects.add(new SearchTypeModel().setSortColumn("Id").setSortType(EnumSortType.Random.index()).setDisplayName("به صورت تصادفی"));
        return objects;
    }

    public void showFilter() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(R.layout.sort_filter_bottom_dialog);
        Typeface r1 = FontManager.T1_Typeface(getContext());
        ((TextView) bottomSheetDialog.findViewById(R.id.title)).setTypeface(r1);
        RecyclerView rc = bottomSheetDialog.findViewById(R.id.rc);
        rc.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
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
}
