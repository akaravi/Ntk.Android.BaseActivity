package ntk.android.base.activity.ticketing;

import androidx.recyclerview.widget.RecyclerView;

import io.reactivex.Observable;
import java9.util.function.Function;
import ntk.android.base.activity.abstraction.AbstractSearchActivity;
import ntk.android.base.adapter.common.TicketAdapter;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterModel;
import ntk.android.base.entitymodel.ticketing.TicketingTaskModel;
import ntk.android.base.services.ticketing.TicketingTaskService;

public class TicketSearchActivity extends AbstractSearchActivity<TicketingTaskModel> {

    @Override
    protected RecyclerView.Adapter getAdapter() {
        return new TicketAdapter(this, models);
    }

    @Override
    public Function<FilterModel, Observable<ErrorException<TicketingTaskModel>>> getService() {
        return new TicketingTaskService(this)::getAll;
    }
}
