package ntk.android.base.fragment.common;

import com.google.gson.Gson;

import io.reactivex.Observable;
import java9.util.function.Function;
import ntk.android.base.Extras;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterModel;
import ntk.android.base.fragment.abstraction.AbstractionListFragment;

public abstract class BaseFilterModelFragment<TEntity> extends AbstractionListFragment<FilterModel, TEntity> {
    @Override
    public void afterInit() {
        super.afterInit();
    }

    @Override
    protected void requestOnIntent() {
        request = new FilterModel();
        request.RowPerPage = 20;
        if (getArguments() != null) {
            String reqString = getArguments().getString(Extras.EXTRA_FIRST_ARG, "");
            if (!reqString.equalsIgnoreCase("")) {
                request = new Gson().fromJson(reqString, FilterModel.class);
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

    @Override
    protected final void onSuccessNext(ErrorException<TEntity> newsContentResponse) {
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

    protected void onListCreate() {
    }

    public abstract Function<FilterModel, Observable<ErrorException<TEntity>>> getService();


}
