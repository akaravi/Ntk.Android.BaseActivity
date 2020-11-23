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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import java9.util.function.Function;
import ntk.android.base.R;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.api.utill.NTKUtill;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterDataModel;
import ntk.android.base.entitymodel.base.Filters;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.FontManager;

public abstract class AbstractionSearchActivity<TEntity> extends BaseActivity {
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
        Txt= findViewById(R.id.txtSearchActSearch);
        Rv=findViewById(R.id.recyclerSearch);

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

    protected abstract RecyclerView.Adapter getAdapter() ;
    public abstract Function< FilterDataModel, Observable<ErrorException<TEntity>>> getService();

    private void Search() {
        if (!searchLock) {
            searchLock = true;
            if (AppUtill.isNetworkAvailable(this)) {
                FilterDataModel request = getDefaultFilterDataModel(Txt.getText().toString());

                switcher.showProgressView();
                getService().apply(request).
                        observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new NtkObserver<ErrorException<TEntity>>() {
                            @Override
                            public void onNext(@NonNull ErrorException<TEntity> response) {
                                searchLock = false;
                                if (response.IsSuccess) {
                                    if (response.ListItems.size() != 0) {
                                        models.addAll(response.ListItems);
                                        adapter.notifyDataSetChanged();
                                        switcher.showContentView();
                                    } else {
                                        switcher.showEmptyView();
                                    }
                                } else {
                                    switcher.showErrorView(response.ErrorMessage, () -> init());
                                }
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                searchLock = false;
                                switcher.showErrorView("خطا در دسترسی به سامانه", () -> init());
                            }
                        });
            } else {
                searchLock = false;
                switcher.showErrorView("عدم دسترسی به اینترنت", () -> Search());
            }
        }
    }

    @NotNull
    protected FilterDataModel getDefaultFilterDataModel(String stringValue) {
        FilterDataModel request = new FilterDataModel();
        Filters ft = new Filters();
        ft.PropertyName = "Title";
        ft.StringValue = stringValue;
        ft.ClauseType = NTKUtill.ClauseType_Or;
        ft.SearchType = NTKUtill.Search_Type_Contains;
        request.addFilter(ft);

        Filters fd = new Filters();
        fd.PropertyName = "Description";
        fd.StringValue = stringValue;
        fd.ClauseType = NTKUtill.ClauseType_Or;
        fd.SearchType = NTKUtill.Search_Type_Contains;
        request.addFilter(fd);

        Filters fb = new Filters();
        fb.PropertyName = "Body";
        fb.StringValue =stringValue;
        fb.ClauseType = NTKUtill.ClauseType_Or;
        fb.SearchType = NTKUtill.Search_Type_Contains;
        request.addFilter(fb);
        return request;
    }


    public void ClickBack() {
        finish();
    }

}
