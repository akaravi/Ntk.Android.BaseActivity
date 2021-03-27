package ntk.android.base.fragment.abstraction;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import java9.util.function.Function;
import ntk.android.base.Extras;
import ntk.android.base.R;
import ntk.android.base.config.ErrorExceptionObserver;
import ntk.android.base.config.GenericErrors;
import ntk.android.base.config.ServiceExecute;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterModel;
import ntk.android.base.fragment.BaseFragment;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.EndlessRecyclerViewScrollListener;
import ntk.android.base.utill.FontManager;

public abstract class AbstractionListFragment<TEntity> extends BaseFragment {
    TextView LblTitle;
    String title = "";
    boolean toolbarShow = false;
    private int Total = 0;
    protected List<TEntity> models = new ArrayList<>();
    protected RecyclerView.Adapter adapter;
    protected FilterModel request;
    private boolean loadingMore = true;

    @Override
    public void onCreated() {
        super.onCreated();
        //can customize for future
    }

    @Override
    public void onCreateFragment() {
        setContentView(R.layout.abstraction_list);
    }

    @Override
    public final void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        request = new FilterModel();
        request.RowPerPage = 20;
        if (getArguments() != null) {
            String reqString = getArguments().getString(Extras.EXTRA_FIRST_ARG, "");
            if (!reqString.equalsIgnoreCase("")) {
                request = new Gson().fromJson(reqString, FilterModel.class);
            }
            String name = getArguments().getString(Extras.EXTRA_SECOND_ARG, "");
            if (!name.equalsIgnoreCase(""))
                title = name;
            toolbarShow = getArguments().getBoolean(Extras.Extra_THIRD_ARG, true);
        }
        if (!withToolbar() || !toolbarShow) {
            findViewById(R.id.ToolbarRv).setVisibility(View.GONE);
            findViewById(R.id.toolbarShadow).setVisibility(View.GONE);
        }
        switcher.setLoadMore(findViewById(R.id.loadMoreProgress));
        init();
        afterInit();
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


    public void afterInit() {

    }

    private void RestCall(int nextPage) {
        if (AppUtill.isNetworkAvailable(getContext())) {
            if (nextPage == 1)
                switcher.showProgressView();
            else
                switcher.showLoadMore();
            request.CurrentPageNumber = nextPage;
            ServiceExecute.execute(getService().apply(request))
                    .subscribe(new ErrorExceptionObserver<TEntity>(switcher::showErrorView) {

                        @Override
                        protected void SuccessResponse(ErrorException<TEntity> newsContentResponse) {
                            models.addAll(newsContentResponse.ListItems);
                            Total = newsContentResponse.TotalRowCount;
                            if (newsContentResponse.ListItems.size() < request.RowPerPage) {
                                loadingMore = false;
                            }
                            adapter.notifyDataSetChanged();
                            if (models.size() > 0) {
                                switcher.showContentView();
                                switcher.hideLoadMore();
                                onListCreate();
                            } else
                                switcher.showEmptyView();

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

    protected void onListCreate() {


    }


    public abstract Function<FilterModel, Observable<ErrorException<TEntity>>> getService();

    public boolean withToolbar() {
        return toolbarShow;
    }

    public abstract RecyclerView.Adapter createAdapter();

    public void ClickBack() {
        getActivity().finish();
    }

    public void ClickSearch() {
    }

    public abstract static class IntegrationView {
        public abstract boolean isShown();

        public abstract void changeVisibility(boolean isVisible);
    }
}
