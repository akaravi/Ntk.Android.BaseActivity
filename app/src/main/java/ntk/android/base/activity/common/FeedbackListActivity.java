package ntk.android.base.activity.common;

import androidx.recyclerview.widget.RecyclerView;

import io.reactivex.Observable;
import java9.util.function.Function;
import ntk.android.base.adapter.common.FeedbackListAdapter;
import ntk.android.base.entitymodel.application.ApplicationAppModel;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterDataModel;
import ntk.android.base.services.application.ApplicationAppService;

public class FeedbackListActivity extends BaseFilterModelListActivity<ApplicationAppModel> {

    @Override
    public Function<FilterDataModel, Observable<ErrorException<ApplicationAppModel>>> getService() {
        return new ApplicationAppService(this)::getAll;
    }

    @Override
    public RecyclerView.Adapter createAdapter() {
        return new FeedbackListAdapter(models);
    }
}
