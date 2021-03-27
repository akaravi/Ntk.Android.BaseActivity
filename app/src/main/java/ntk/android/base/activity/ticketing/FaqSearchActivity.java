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

import io.reactivex.disposables.Disposable;
import ntk.android.base.R;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.adapter.FaqAdapter;
import ntk.android.base.api.utill.NTKUtill;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.config.ServiceExecute;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterDataModel;
import ntk.android.base.entitymodel.base.FilterModel;
import ntk.android.base.entitymodel.ticketing.TicketingFaqModel;
import ntk.android.base.services.ticketing.TicketingFaqService;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.FontManager;

public class FaqSearchActivity extends BaseActivity {
    EditText Txt;
    RecyclerView Rv;
    Button btnRefresh;

    private ArrayList<TicketingFaqModel> faqs = new ArrayList<>();
    private FaqAdapter adapter;
    boolean searchLock;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faq_search_activity);
        init();
    }

    private void init() {
        Txt = findViewById(R.id.txtSearchActFaqSearch);
        Rv = findViewById(R.id.recyclerFaqSearch);
        btnRefresh = findViewById(R.id.btnRefreshActFaqSearch);
        findViewById(R.id.imgBackActFaqSearch).setOnClickListener(v -> ClickBack());
        findViewById(R.id.btnRefreshActFaqSearch).setOnClickListener(v -> ClickRefresh());
        Rv.setHasFixedSize(true);
        Rv.setLayoutManager(new GridLayoutManager(this, 2));

        Txt.setTypeface(FontManager.T1_Typeface(this));
        Txt.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                Search();
                return true;
            }
            return false;
        });
        adapter = new FaqAdapter(this, faqs);
        Rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void Search() {
        if (!searchLock) {
            searchLock = true;
            if (AppUtill.isNetworkAvailable(this)) {

                FilterModel request = new FilterModel();
                FilterDataModel fa = new FilterDataModel();
                fa.PropertyName = "Answer";
                fa.setStringValue(Txt.getText().toString());
                fa.ClauseType = NTKUtill.ClauseType_Or;
                fa.SearchType = NTKUtill.Search_Type_Contains;
                request.addFilter(fa);

                FilterDataModel fq = new FilterDataModel();
                fq.PropertyName = "Question";
                fq.setStringValue(Txt.getText().toString());
                fq.ClauseType = NTKUtill.ClauseType_Or;
                fq.SearchType = NTKUtill.Search_Type_Contains;
                request.addFilter(fq);
                switcher.showProgressView();
                ServiceExecute.execute(new TicketingFaqService(this).getAll(request))
                        .subscribe(new NtkObserver<ErrorException<TicketingFaqModel>>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(ErrorException<TicketingFaqModel> response) {
                                searchLock = false;
                                if (response.IsSuccess) {
                                    if (response.ListItems.size() != 0) {
                                        faqs.addAll(response.ListItems);
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
                                switcher.showErrorView(getString(R.string.error_raised), () -> init());

                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            } else {
                btnRefresh.setVisibility(View.VISIBLE);
                searchLock = false;
                switcher.showErrorView(getString(R.string.per_no_net), () -> Search());
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
