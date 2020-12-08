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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java9.util.function.Function;
import ntk.android.base.Extras;
import ntk.android.base.R;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterDataModel;
import ntk.android.base.fragment.BaseFragment;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.EndlessRecyclerViewScrollListener;
import ntk.android.base.utill.FontManager;

public abstract class AbstractionListFragment<TEntity> extends BaseFragment {
    TextView LblTitle;


    private int Total = 0;
    protected List<TEntity> models = new ArrayList<>();
    protected RecyclerView.Adapter adapter;
    protected FilterDataModel request;
    private boolean loadingMore = true;

    @Override
    public void onCreateFragment() {
        setContentView(R.layout.abstraction_list);
    }

    @Override
    public final void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!withToolbar()) {
            findViewById(R.id.ToolbarRv).setVisibility(View.GONE);
            findViewById(R.id.toolbarShadow).setVisibility(View.GONE);
        }
        request = new FilterDataModel();
        request.RowPerPage = 20;
        if (getArguments() != null) {
            String reqString = getArguments().getString(Extras.EXTRA_FIRST_ARG, "");
            if (!reqString.equalsIgnoreCase("")) {
                request = new Gson().fromJson(reqString, FilterDataModel.class);
            }
        }
        init();
        afterInit();
    }

    protected RecyclerView.LayoutManager getRvLayoutManager() {
        return new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    }

    private void init() {

        LblTitle = findViewById(R.id.lblTitle);
        RecyclerView Rv = findViewById(R.id.recycler);
        SwipeRefreshLayout Refresh = findViewById(R.id.swipRefresh);
        findViewById(R.id.imgBack).setOnClickListener(v -> ClickBack());
        findViewById(R.id.imgSearch).setOnClickListener(v -> ClickSearch());
        LblTitle.setTypeface(FontManager.GetTypeface(getContext(), FontManager.IranSans));
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

    private void RestCall(int i) {
        if (AppUtill.isNetworkAvailable(getContext())) {
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
                                    loadingMore = false;
                                }
                                adapter.notifyDataSetChanged();
                                if (models.size() > 0) {
                                    switcher.showContentView();
                                    onListCreate();
                                } else
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

    protected void onListCreate() {


    }


    public abstract Function<FilterDataModel, Observable<ErrorException<TEntity>>> getService();

    public abstract boolean withToolbar();

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
