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
import ntk.android.base.config.ServiceExecute;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterModel;
import ntk.android.base.entitymodel.base.FilterDataModel;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.FontManager;
import ntk.android.base.config.GenericErrors;

public abstract class AbstractSearchActivity<TEntity> extends BaseActivity {
    EditText Txt;
    RecyclerView Rv;


    protected ArrayList<TEntity> models = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    boolean searchLock;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abstraction_search_content);
        init();
    }

    private void init() {
        Txt = findViewById(R.id.txtSearchActSearch);
        Rv = findViewById(R.id.recyclerSearch);

        findViewById(R.id.imgBackActSearch).setOnClickListener(v -> ClickBack());
        Rv.setHasFixedSize(true);
        Rv.setLayoutManager(new GridLayoutManager(this, 2));

        Txt.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Txt.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                Search();
                return true;
            }
            return false;
        });
        adapter = getAdapter();
        Rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    protected abstract RecyclerView.Adapter getAdapter();

    public abstract Function<FilterModel, Observable<ErrorException<TEntity>>> getService();

    private void Search() {
        if (!searchLock) {
            searchLock = true;
            if (AppUtill.isNetworkAvailable(this)) {
                FilterModel request = getDefaultFilterDataModel(Txt.getText().toString());

                switcher.showProgressView();
                ServiceExecute.execute(getService().apply(request))
                        .subscribe(new ErrorExceptionObserver<TEntity>(switcher::showErrorView) {

                            @Override
                            public void onNext(@NonNull ErrorException<TEntity> response) {
                                searchLock = false;
                                super.onNext(response);
                            }

                            @Override
                            protected void SuccessResponse(ErrorException<TEntity> response) {
                                if (response.ListItems.size() != 0) {
                                    models.addAll(response.ListItems);
                                    adapter.notifyDataSetChanged();
                                    switcher.showContentView();
                                } else {
                                    switcher.showEmptyView();
                                }
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
            } else {
                searchLock = false;
                new GenericErrors().netError(switcher::showErrorView, this::Search);
            }
        }
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
