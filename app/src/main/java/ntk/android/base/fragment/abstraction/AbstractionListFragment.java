package ntk.android.base.fragment.abstraction;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java9.util.function.Function;
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
    RecyclerView Rv;

    SwipeRefreshLayout Refresh;
    private int Total = 0;
    protected List<TEntity> models = new ArrayList<>();
    private RecyclerView.Adapter adapter;

    @Override
    public void onCreateFragment() {
        setContentView(R.layout.abstraction_list);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!withToolbar()) {
            findViewById(R.id.ToolbarRv).setVisibility(View.GONE);
            findViewById(R.id.toolbarShadow).setVisibility(View.GONE);
        }
        init();
    }

    private void init() {

        LblTitle = findViewById(R.id.lblTitle);
        Rv = findViewById(R.id.recycler);
        Refresh = findViewById(R.id.swipRefresh);
        LblTitle.setTypeface(FontManager.GetTypeface(getContext(), FontManager.IranSans));
        Rv.setHasFixedSize(true);
        LinearLayoutManager LMC = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        Rv.setLayoutManager(LMC);
        adapter = createAdapter();
        Rv.setAdapter(adapter);
        findViewById(R.id.imgBack).setOnClickListener(v -> ClickBack());
        findViewById(R.id.imgSearch).setOnClickListener(v -> ClickSearch());
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
    }


    private void RestCall(int i) {
        if (AppUtill.isNetworkAvailable(getContext())) {
            switcher.showProgressView();

            FilterDataModel request = new FilterDataModel();
            request.RowPerPage = 20;
            request.CurrentPageNumber = i;

            getService().apply(request).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new NtkObserver<ErrorException<TEntity>>() {
                        @Override
                        public void onNext(@NonNull ErrorException<TEntity> newsContentResponse) {
                            if (newsContentResponse.IsSuccess) {
                                models.addAll(newsContentResponse.ListItems);
                                Total = newsContentResponse.RowPerPage;
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

    public abstract boolean withToolbar();

    public abstract RecyclerView.Adapter createAdapter();

    public void ClickBack() {
        getActivity().finish();
    }

    public abstract void ClickSearch();
}
