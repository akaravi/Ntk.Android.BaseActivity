package ntk.android.base.activity.ticketing;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import ntk.android.base.R;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.adapter.common.TicketAdapter;
import ntk.android.base.api.utill.NTKUtill;
import ntk.android.base.config.ErrorExceptionObserver;
import ntk.android.base.config.GenericErrors;
import ntk.android.base.config.ServiceExecute;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterDataModel;
import ntk.android.base.entitymodel.ticketing.TicketingTaskModel;
import ntk.android.base.services.ticketing.TicketingTaskService;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.EndlessRecyclerViewScrollListener;


public class TicketListActivity extends BaseActivity {
    private static final int NEW_TICKET = 1004;
    RecyclerView Rv;
    FloatingActionButton Fab;
    SwipeRefreshLayout Refresh;
    private ArrayList<TicketingTaskModel> tickets = new ArrayList<>();
    private TicketAdapter adapter;

    private EndlessRecyclerViewScrollListener scrollListener;
    private int TotalTag = 0;
    private boolean loadingMore = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDirectContentViewWithSwicher(R.layout.ticket_list_activity, R.id.recyclerFrSupport);
        init();
    }

    private void init() {
        Rv = findViewById(R.id.recyclerFrSupport);
        Fab = findViewById(R.id.FabFrSupport);
        Refresh = findViewById(R.id.RefreshTicket);
        findViewById(R.id.FabFrSupport).setOnClickListener(v -> ClickSendTicket());
        findViewById(R.id.imgToolbarBack).setOnClickListener(v -> finish());
        Rv.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        Rv.setLayoutManager(manager);

        adapter = new TicketAdapter(this, tickets);
        Rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        scrollListener = new EndlessRecyclerViewScrollListener(manager) {

            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (loadingMore && totalItemsCount <= TotalTag) {
                    HandelData((page + 1));
                }
            }

            @Override
            public void onScrolled(RecyclerView view, int dx, int dy) {
                if (dy > 0 || dy < 0 && Fab.isShown())
                    Fab.hide();
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Fab.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        };

        Rv.addOnScrollListener(scrollListener);



        Refresh.setOnRefreshListener(() -> {
            refresh();
        });
        switcher.setLoadMore(findViewById(R.id.loadMoreProgress));
        HandelData(1);
    }

    private void refresh() {
        tickets.clear();
        loadingMore = true;
        HandelData(1);
        Refresh.setRefreshing(false);
    }


    private void HandelData(int nextPage) {
        if (AppUtill.isNetworkAvailable(this)) {

            FilterDataModel request = new FilterDataModel();
            request.RowPerPage = 10;
            request.CurrentPageNumber = nextPage;
            request.SortType = NTKUtill.Descnding_Sort;
            request.SortColumn = "Id";
            switcher.showProgressView();
            ServiceExecute.execute(new TicketingTaskService(this).getAll(request))
                    .subscribe(new ErrorExceptionObserver<TicketingTaskModel>(switcher::showErrorView) {

                        @Override
                        protected void SuccessResponse(ErrorException<TicketingTaskModel> model) {
                            tickets.addAll(model.ListItems);

                            TotalTag = model.TotalRowCount;
                            if (model.ListItems.size() < request.RowPerPage) {
                                loadingMore = false;
                            }
                            adapter.notifyDataSetChanged();
                            if (model.ListItems.size() > 0) {
                                switcher.showContentView();
                                switcher.hideLoadMore();
                            } else {
                                switcher.showEmptyView();

                            }
                        }

                        @Override
                        protected Runnable tryAgainMethod() {

                            return () -> HandelData(nextPage);
                        }
                    });
        } else {
            new GenericErrors().netError(switcher::showErrorView, () -> HandelData(nextPage));

        }
    }


    public void ClickSendTicket() {
        startActivityForResult(new Intent(this, NewTicketActivity.class), NEW_TICKET);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            refresh();
        }
    }
}