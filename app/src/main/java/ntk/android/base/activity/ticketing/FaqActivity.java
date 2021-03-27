package ntk.android.base.activity.ticketing;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ntk.android.base.R;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.adapter.FaqAdapter;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.config.ServiceExecute;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterModel;
import ntk.android.base.entitymodel.ticketing.TicketingFaqModel;
import ntk.android.base.services.ticketing.TicketingFaqService;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.FontManager;

public class FaqActivity extends BaseActivity {

    TextView Lbl;
    RecyclerView Rv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faq_activity);
        init();
    }

    private void init() {

        Lbl = findViewById(R.id.lblTitleActFaq);
        Rv = findViewById(R.id.recyclerFaq);

        findViewById(R.id.imgBackActFaq).setOnClickListener(v -> ClickBack());
        findViewById(R.id.imgSearchActFaq).setOnClickListener(v -> ClickSearch());
        Lbl = findViewById(R.id.lblTitleActFaq);
        Rv = findViewById(R.id.recyclerFaq);
        Lbl.setTypeface(FontManager.T1_Typeface(this));
        Lbl.setText(R.string.faq);
        Rv.setHasFixedSize(true);
        Rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        if (AppUtill.isNetworkAvailable(this)) {
            // show loading
            switcher.showProgressView();

            FilterModel request = new FilterModel();
            request.RowPerPage = 1000;

            ServiceExecute.execute(new TicketingFaqService(this).getAll(request))
                    .subscribe(new NtkObserver<ErrorException<TicketingFaqModel>>() {
                        @Override
                        public void onNext(ErrorException<TicketingFaqModel> model) {
                            FaqAdapter adapter = new FaqAdapter(FaqActivity.this, model.ListItems);
                            Rv.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            if (adapter.getItemCount() > 0)
                                switcher.showContentView();
                            else
                                switcher.showEmptyView();

                        }

                        @Override
                        public void onError(Throwable e) {
                            switcher.showErrorView(getString(R.string.per_no_net), () -> init());

                        }
                    });
        } else {
            switcher.showErrorView(getString(R.string.per_no_net), () -> init());

        }
    }


    public void ClickBack() {
        finish();
    }


    public void ClickSearch() {
        startActivity(new Intent(this, FaqSearchActivity.class));
    }
}
