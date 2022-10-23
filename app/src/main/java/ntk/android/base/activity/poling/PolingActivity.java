package ntk.android.base.activity.poling;

import ntk.android.base.entitymodel.base.FilterModel;
import ntk.android.base.entitymodel.enums.EnumSortType;

public class PolingActivity extends PolingDetailActivity {

    @Override
    protected FilterModel createFilterModel() {
        FilterModel request = new FilterModel();
        request.setRowPerPage(1000).setSortType(EnumSortType.Descending.index()).setSortColumn("Id");
        return request;
    }
}
