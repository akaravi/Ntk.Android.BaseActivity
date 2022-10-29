package ntk.android.base.activity.common;

import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import io.reactivex.Observable;
import java9.util.function.BiFunction;
import java9.util.function.Function;
import ntk.android.base.Extras;
import ntk.android.base.R;
import ntk.android.base.activity.abstraction.AbstractListActivity;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterModel;
import ntk.android.base.entitymodel.enums.EnumSortType;

public abstract class BaseFilterModelCategoryUsedListActivity<TEntity, TID> extends AbstractListActivity<FilterModel, TEntity> {
    String title = "";
    public Object id;


    @Override
    protected void requestOnIntent() {
        request = new FilterModel();
        request.RowPerPage = 20;
        if (sortFilter == null) {
            request.SortColumn = "Id";
            request.SortType = EnumSortType.Descending.index();
        }
        if (getIntent() != null) if (getIntent().getExtras() != null) {
            String reqString = getIntent().getExtras().getString(Extras.EXTRA_FIRST_ARG, "");
            if (!reqString.equalsIgnoreCase("")) {
                request = new Gson().fromJson(reqString, FilterModel.class);
            }
            if (sortFilter != null) {
                request.SortColumn = sortFilter.getSortColumn();
                request.SortType = sortFilter.getSortType();
            }
            id = getIntent().getExtras().get(Extras.EXTRA_SECOND_ARG);
            title = getIntent().getExtras().getString(Extras.Extra_THIRD_ARG, "");
        }

    }

    @Override
    public void afterInit() {
        ((TextView) findViewById(R.id.lblTitle)).setText(title);
        findViewById(R.id.imgSearch).setVisibility(View.GONE);
    }

    @Override
    protected Function<Integer, Observable<ErrorException<TEntity>>> apiService() {
        return newPage -> {
            request.CurrentPageNumber = newPage;
            if (sortFilter != null) {
                request.SortColumn = sortFilter.getSortColumn();
                request.SortType = sortFilter.getSortType();
            }
            return getService().apply(convertID(), request);
        };
    }

    public abstract TID convertID();

    @Override
    protected final void onSuccessNext(ErrorException<TEntity> response) {
        models.addAll(response.ListItems);
        Total = response.TotalRowCount;
        if (response.ListItems.size() < request.RowPerPage) {
            loadingMore = false;
        }
        adapter.notifyDataSetChanged();
        if (models.size() > 0) {
            switcher.showContentView();
            switcher.hideLoadMore();
            onListCreate();
        } else switcher.showEmptyView();
    }

    public abstract BiFunction<TID, FilterModel, Observable<ErrorException<TEntity>>> getService();

    protected void onListCreate() {
    }
}