package ntk.android.base.activity.ticketing;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ntk.android.base.R;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.adapter.TicketAdapter;
import ntk.android.base.api.utill.NTKUtill;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterDataModel;
import ntk.android.base.entitymodel.base.Filters;
import ntk.android.base.entitymodel.ticketing.TicketingTaskModel;
import ntk.android.base.services.ticketing.TicketingTaskService;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.FontManager;

public class TicketSearchActivity extends BaseActivity {
    EditText Txt;
    RecyclerView Rv;
    Button btnRefresh;


    private ArrayList<TicketingTaskModel> tickets = new ArrayList<>();
    private TicketAdapter adapter;
    boolean searchLock;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ticket_search_activity);
        init();
    }

    private void init() {

        Txt = findViewById(R.id.txtSearchActSearch);
        Rv = findViewById(R.id.recyclerSearch);
        btnRefresh = findViewById(R.id.btnRefreshActSearch);

        findViewById(R.id.imgBackActSearch).setOnClickListener(v -> ClickBack());
        findViewById(R.id.btnRefreshActSearch).setOnClickListener(v -> ClickRefresh());
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
        adapter = new TicketAdapter(this, tickets);
        Rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void Search() {
        if (!searchLock) {
            searchLock = true;
            if (AppUtill.isNetworkAvailable(this)) {
                tickets.clear();

                FilterDataModel request = new FilterDataModel();

                Filters ft = new Filters();
                ft.PropertyName = "Title";
                ft.StringValue = Txt.getText().toString();
                ft.ClauseType = NTKUtill.ClauseType_Or;
                ft.SearchType = NTKUtill.Search_Type_Contains;
                request.addFilter(ft);

                Filters fd = new Filters();
                fd.PropertyName = "Description";
                fd.StringValue = Txt.getText().toString();
                fd.ClauseType = NTKUtill.ClauseType_Or;
                fd.SearchType = NTKUtill.Search_Type_Contains;
                request.addFilter(fd);

                Filters fb = new Filters();
                fb.PropertyName = "Body";
                fb.StringValue = Txt.getText().toString();
                fb.ClauseType = NTKUtill.ClauseType_Or;
                fb.SearchType = NTKUtill.Search_Type_Contains;
                request.addFilter(fb);

                switcher.showProgressView();

                new TicketingTaskService(this).getAll(request).
                        observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new NtkObserver<ErrorException<TicketingTaskModel>>() {

                            @Override
                            public void onNext(ErrorException<TicketingTaskModel> response) {
                                searchLock = false;
                                if (response.IsSuccess) {
                                    if (response.ListItems.size() != 0) {
                                        tickets.addAll(response.ListItems);
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
                            public void onError(Throwable e) {
                                searchLock = false;
                                btnRefresh.setVisibility(View.VISIBLE);
                                switcher.showErrorView("خطا در دسترسی به سامانه", () -> init());

                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            } else {
                btnRefresh.setVisibility(View.VISIBLE);
                searchLock = false;
                switcher.showErrorView("عدم دسترسی به اینترنت", () -> Search());
            }
        }
    }

    public void ClickBack() {
        finish();
    }

    public void ClickRefresh() {
        btnRefresh.setVisibility(View.GONE);
        init();
    }
}
