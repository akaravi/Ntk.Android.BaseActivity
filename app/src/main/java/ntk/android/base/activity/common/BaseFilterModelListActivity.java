package ntk.android.base.activity.common;

import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import java9.util.function.Function;
import ntk.android.base.Extras;
import ntk.android.base.R;
import ntk.android.base.activity.abstraction.AbstractListActivity;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.config.ServiceExecute;
import ntk.android.base.dtomodel.core.CoreModuleReportAbuseDtoModel;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterModel;
import ntk.android.base.entitymodel.enums.EnumSortType;
import ntk.android.base.entitymodel.estate.EstatePropertyModel;
import ntk.android.base.services.estate.EstatePropertyService;
import ntk.android.base.utill.AppUtil;
import ntk.android.base.utill.FontManager;

public abstract class BaseFilterModelListActivity<TEntity> extends AbstractListActivity<FilterModel, TEntity> {
    @Override
    public void afterInit() {
        super.afterInit();
    }

    @Override
    protected void requestOnIntent() {
        request = new FilterModel();
        request.RowPerPage = 20;
        if (sortFilter == null) {
            request.SortColumn = "Id";
            request.SortType = EnumSortType.Descending.index();
        }
        if (getIntent() != null)
            if (getIntent().getExtras() != null) {
                String reqString = getIntent().getExtras().getString(Extras.EXTRA_FIRST_ARG, "");
                if (!reqString.equalsIgnoreCase("")) {
                    request = new Gson().fromJson(reqString, FilterModel.class);
                }
            }
        if (sortFilter != null ) {
            request.SortColumn = sortFilter.getSortColumn();
            request.SortType = sortFilter.getSortType();
        }
    }


    @Override
    protected Function<Integer, Observable<ErrorException<TEntity>>> apiService() {
        return newPage -> {
            request.CurrentPageNumber = newPage;
            if (sortFilter != null ) {
                request.SortColumn = sortFilter.getSortColumn();
                request.SortType = sortFilter.getSortType();
            }
            return getService().apply(request);
        };
    }


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
        } else
            switcher.showEmptyView();
    }

    public abstract Function<FilterModel, Observable<ErrorException<TEntity>>> getService();

    protected void onListCreate() {
    }


}
