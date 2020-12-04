package ntk.android.base.activity.common;

import com.google.gson.Gson;

import io.reactivex.Observable;
import java9.util.function.Function;
import ntk.android.base.Extras;
import ntk.android.base.activity.abstraction.AbstractionListActivity;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterDataModel;

public abstract class BaseFilterModelListActivity<TEntity> extends AbstractionListActivity<FilterDataModel, TEntity> {
    @Override
    protected void requestOnIntent() {
        request = new FilterDataModel();
        request.RowPerPage = 20;
        if (getIntent() != null)
            if (getIntent().getExtras() != null) {
                String reqString = getIntent().getExtras().getString(Extras.EXTRA_FIRST_ARG, "");
                if (!reqString.equalsIgnoreCase("")) {
                    request = new Gson().fromJson(reqString, FilterDataModel.class);
                }
            }
    }
    

    @Override
    protected Function<Integer, Observable<ErrorException<TEntity>>> apiService() {
        return newPage -> {
            request.CurrentPageNumber = newPage;
            return getService().apply(request);
        };
    }

    public abstract Function<FilterDataModel, Observable<ErrorException<TEntity>>> getService();

    @Override
    protected void onSuccessNext(ErrorException<TEntity> newsContentResponse) {
        models.addAll(newsContentResponse.ListItems);
        Total = newsContentResponse.TotalRowCount;
        if (newsContentResponse.ListItems.size() < request.RowPerPage) {
            loadingMore = false;
        }
        adapter.notifyDataSetChanged();
        if (models.size() > 0)
            switcher.showContentView();
        else
            switcher.showEmptyView();
    }
}
