package ntk.android.base.activity.abstraction;

import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import java9.util.function.Function;
import ntk.android.base.R;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.api.utill.NTKUtill;
import ntk.android.base.config.ErrorExceptionObserver;
import ntk.android.base.config.GenericErrors;
import ntk.android.base.config.ServiceExecute;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterDataModel;
import ntk.android.base.entitymodel.base.FilterModel;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.EndlessRecyclerViewScrollListener;
import ntk.android.base.utill.FontManager;

public abstract class AbstractSearchActivity<TEntity> extends BaseActivity {
    EditText Txt;
    RecyclerView Rv;


    protected ArrayList<TEntity> models = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    boolean searchLock;
    protected boolean loadingMore = true;
    protected int Total = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abstraction_search_content);
        init();
    }

    private void init() {
        switcher.setLoadMore(findViewById(R.id.loadMoreProgress));
        Txt = findViewById(R.id.txtSearchActSearch);
        Rv = findViewById(R.id.recyclerSearch);

        findViewById(R.id.imgBackActSearch).setOnClickListener(v -> ClickBack());
        Rv.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = getLayoutManager();
        Rv.setLayoutManager(layoutManager);

        Txt.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Txt.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                models.clear();
                SearchPage(1);
                return true;
            }
            return false;
        });
        adapter = getAdapter();
        Rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {

            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (loadingMore && totalItemsCount <= Total) {
                    SearchPage((page + 1));
                }
            }

            @Override
            public void onScrolled(RecyclerView view, int dx, int dy) {
                super.onScrolled(view, dx, dy);

            }

            @Override
            public void onScrollStateChanged(@androidx.annotation.NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }
        };
        Rv.addOnScrollListener(scrollListener);

    }

    private GridLayoutManager getLayoutManager() {
        return new GridLayoutManager(this, 2);
    }

    protected abstract RecyclerView.Adapter getAdapter();

    public abstract Function<FilterModel, Observable<ErrorException<TEntity>>> getService();

    FilterModel request;

    public void SearchPage(int nextPage) {
        if (!searchLock) {
            searchLock = true;
            if (AppUtill.isNetworkAvailable(this)) {

                if (nextPage == 1)
                    switcher.showProgressView();
                else
                    switcher.showLoadMore();
                request = getDefaultFilterDataModel(Txt.getText().toString());
                request.CurrentPageNumber = nextPage;
                Search();
            } else {
                searchLock = false;
                new GenericErrors().netError(switcher::showErrorView, () -> SearchPage(nextPage));
            }
        }
    }

    private void Search() {

        ServiceExecute.execute(getService().apply(request))
                .subscribe(new ErrorExceptionObserver<TEntity>(switcher::showErrorView) {

                    @Override
                    public void onNext(@NonNull ErrorException<TEntity> response) {
                        searchLock = false;
                        super.onNext(response);
                    }

                    @Override
                    protected void SuccessResponse(ErrorException<TEntity> response) {
                        Total = response.TotalRowCount;
                        models.addAll(response.ListItems);
                        if (response .ListItems.size() < request.RowPerPage) {
                            loadingMore = false;
                        }if (models.size() > 0) {
                            switcher.showContentView();
                            switcher.hideLoadMore();
                            adapter.notifyDataSetChanged();
                        } else
                            switcher.showEmptyView();
                    }

                    @Override
                    protected Runnable tryAgainMethod() {
                        return AbstractSearchActivity.this::Search;
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        searchLock = false;
                        super.onError(e);
                    }
                });

    }

    @NotNull
    protected FilterModel getDefaultFilterDataModel(String stringValue) {
        FilterModel request = new FilterModel();
        FilterDataModel ft = new FilterDataModel();
        ft.PropertyName = "Title";
        ft.setStringValue(stringValue);
        ft.ClauseType = NTKUtill.ClauseType_Or;
        ft.SearchType = NTKUtill.Search_Type_Contains;
        request.addFilter(ft);

        FilterDataModel fd = new FilterDataModel();
        fd.PropertyName = "Description";
        ft.setStringValue(stringValue);
        fd.ClauseType = NTKUtill.ClauseType_Or;
        fd.SearchType = NTKUtill.Search_Type_Contains;
        request.addFilter(fd);

        FilterDataModel fb = new FilterDataModel();
        fb.PropertyName = "Body";
        ft.setStringValue(stringValue);
        fb.ClauseType = NTKUtill.ClauseType_Or;
        fb.SearchType = NTKUtill.Search_Type_Contains;
        request.addFilter(fb);
        return request;
    }


    public void ClickBack() {
        finish();
    }

}
