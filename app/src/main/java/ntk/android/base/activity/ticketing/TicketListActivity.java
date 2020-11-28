package ntk.android.base.activity.ticketing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ntk.android.base.R;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.adapter.TicketAdapter;
import ntk.android.base.api.utill.NTKUtill;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterDataModel;
import ntk.android.base.entitymodel.ticketing.TicketingTaskModel;
import ntk.android.base.services.ticketing.TicketingTaskService;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.EndlessRecyclerViewScrollListener;



public class TicketListActivity extends BaseActivity {
    RecyclerView Rv;
    FloatingActionButton Fab;
    SwipeRefreshLayout Refresh;
    private ArrayList<TicketingTaskModel> tickets = new ArrayList<>();
    private TicketAdapter adapter;

    private EndlessRecyclerViewScrollListener scrollListener;
    private int TotalTag = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDirectContentViewWithSwicher(R.layout.ticket_list_activity,R.id.recyclerFrSupport);
        init();
    }

    private void init() {
        Rv=findViewById(R.id.recyclerFrSupport);
        Fab=findViewById(R.id.FabFrSupport);
        Refresh=findViewById(R.id.RefreshTicket);
        findViewById(R.id.FabFrSupport).setOnClickListener(v -> ClickSendTicket());
        Rv.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        Rv.setLayoutManager(manager);

        adapter = new TicketAdapter(this, tickets);
        Rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        scrollListener = new EndlessRecyclerViewScrollListener(manager) {

            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalItemsCount <= TotalTag) {
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

//        Refresh.setColorSchemeColors(
//                R.color.colorAccent,
//                R.color.colorAccent, //todo set
//                R.color.colorAccent);

        Refresh.setOnRefreshListener(() -> {
            tickets.clear();
            HandelData(1);
            Refresh.setRefreshing(false);
        });

        HandelData(1);
    }


    private void HandelData(int i) {
        if (AppUtill.isNetworkAvailable(this)) {


            FilterDataModel request = new FilterDataModel();
            request.RowPerPage = 10;
            request.CurrentPageNumber = i;
            request.SortType = NTKUtill.Descnding_Sort;
            request.SortColumn = "Id";
            switcher.showProgressView();
            new TicketingTaskService(this).getAll(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NtkObserver<ErrorException<TicketingTaskModel>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(ErrorException<TicketingTaskModel> model) {
                            tickets.addAll(model.ListItems);
                            adapter.notifyDataSetChanged();
                            TotalTag = model.TotalRowCount;

                            if (TotalTag > 0)
                                switcher.showContentView();
                            else {
                                switcher.showEmptyView();

                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            switcher.showErrorView("خطای سامانه مجددا تلاش کنید", () -> init());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            switcher.showErrorView("عدم دسترسی به اینترنت", () -> init());

        }
    }


    public void ClickSendTicket() {
        startActivity(new Intent(this, NewTicketActivity.class));
    }
}