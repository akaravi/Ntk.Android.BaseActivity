package ntk.android.base.activity.ticketing;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.annotations.NonNull;
import ntk.android.base.Extras;
import ntk.android.base.R;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.adapter.common.TicketAnswerAdapter;
import ntk.android.base.adapter.common.TicketAttachAdapter;
import ntk.android.base.config.GenericErrors;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.config.ServiceExecute;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterDataModel;
import ntk.android.base.entitymodel.base.Filters;
import ntk.android.base.entitymodel.file.FileUploadModel;
import ntk.android.base.entitymodel.ticketing.TicketingAnswerModel;
import ntk.android.base.event.RemoveAttachEvent;
import ntk.android.base.service.FileManagerService;
import ntk.android.base.services.file.FileUploaderService;
import ntk.android.base.services.ticketing.TicketingAnswerService;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.FontManager;


public class TicketAnswerActivity extends BaseActivity {

    List<RecyclerView> Rvs;
    TextView Lbl;
    CoordinatorLayout layout;
    EditText txt;
    Button Btn;
    long ticketId;
    private ArrayList<TicketingAnswerModel> tickets = new ArrayList<>();
    private TicketAnswerAdapter adapter;
    private List<String> attaches = new ArrayList<>();
    private List<String> fileId = new ArrayList<>();
    private TicketAttachAdapter AdAtach;

    private static final int READ_REQUEST_CODE = 42;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ticket_answer_activity);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void EventBus(RemoveAttachEvent event) {
        attaches.remove(event.GetPosition());
        fileId.remove(event.GetPosition());
        AdAtach.notifyDataSetChanged();
    }

    private void init() {

        Rvs = new ArrayList() {{
            add(findViewById(R.id.recyclerAnswer));
            add(findViewById(R.id.RecyclerAttachTicketAnswer));
        }};

        Lbl = findViewById(R.id.lblTitleActTicketAnswer);
        txt = findViewById(R.id.txtMessageActTicketAnswer);
        Btn = findViewById(R.id.btnSubmitActTicketAnswer);
        findViewById(R.id.imgBackActTicketAnswer).setOnClickListener(v -> ClickBack());
        findViewById(R.id.btnSubmitActTicketAnswer).setOnClickListener(v -> ClickSubmit());
        findViewById(R.id.RippleAttachActTicketAnswer).setOnClickListener(v -> ClickAttach());
        Lbl.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        ticketId = getIntent().getExtras().getLong(Extras.EXTRA_FIRST_ARG);
        Lbl.setText("پاسخ تیکت  " + ticketId);
        Rvs.get(0).setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        Rvs.get(0).setLayoutManager(manager);

        adapter = new TicketAnswerAdapter(this, tickets);
        Rvs.get(0).setAdapter(adapter);
        adapter.notifyDataSetChanged();

        HandelData(1);

        Rvs.get(1).setHasFixedSize(true);
        Rvs.get(1).setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        AdAtach = new TicketAttachAdapter(this, attaches);
        Rvs.get(1).setAdapter(AdAtach);
        AdAtach.notifyDataSetChanged();
    }

    private void HandelData(int i) {
        if (AppUtill.isNetworkAvailable(this)) {
            FilterDataModel request = new FilterDataModel();
            request.addFilter(new Filters().setPropertyName("LinkTaskId").setIntValue1(ticketId));

            ServiceExecute.execute(new TicketingAnswerService(this).getAll(request))
                    .subscribe(new NtkObserver<ErrorException<TicketingAnswerModel>>() {
                        @Override
                        public void onNext(ErrorException<TicketingAnswerModel> model) {
                            if (model.IsSuccess) {
                                tickets.addAll(model.ListItems);
                                adapter.notifyDataSetChanged();
                            } else
                                switcher.showErrorView();

                        }

                        @Override
                        public void onError(Throwable e) {
                            Snackbar.make(layout, "خطای سامانه مجددا تلاش کنید", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", v -> init()).show();
                        }


                    });
        } else {
            Snackbar.make(layout, "عدم دسترسی به اینترنت", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", v -> init()).show();
        }
    }

    public void ClickBack() {
        finish();
    }

    public void ClickSubmit() {
        if (txt.getText().toString().isEmpty()) {

        } else {
            if (AppUtill.isNetworkAvailable(this)) {
                TicketingAnswerModel request = new TicketingAnswerModel();
                request.HtmlBody = txt.getText().toString();
                request.LinkTaskId = ticketId;
                request.UploadFileGUID = fileId;

                ServiceExecute.execute(new TicketingAnswerService(this).add(request))
                        .subscribe(new NtkObserver<ErrorException<TicketingAnswerModel>>() {

                            @Override
                            public void onNext(ErrorException<TicketingAnswerModel> model) {
                                if (model.IsSuccess) {
                                    Toasty.success(TicketAnswerActivity.this, "با موفقیت ثبت شد", Toasty.LENGTH_LONG, true).show();
                                    finish();
                                } else
                                    Toasty.error(TicketAnswerActivity.this, model.ErrorMessage).show();

                            }

                            @Override
                            public void onError(Throwable e) {
                                new GenericErrors().throwableException((error, tryAgain) -> Toasty.error(TicketAnswerActivity.this, error).show(), e, () -> {
                                });
                            }

                        });
            } else {
                Toasty.warning(this, "عدم دسترسی به اینترنت", Toasty.LENGTH_LONG, true).show();
            }
        }
    }

    public void ClickAttach() {
        new FileManagerService().clickAttach(this, READ_REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri;
            if (resultData != null) {
                uri = resultData.getData();
                if (uri != null) {
                    Btn.setVisibility(View.GONE);
                    attaches.add(FileManagerService.getPath(TicketAnswerActivity.this, uri));
                    AdAtach.notifyDataSetChanged();
                    UploadFileToServer(FileManagerService.getPath(TicketAnswerActivity.this, uri));
                }
            }
        }
    }


    private void UploadFileToServer(String url) {
        if (AppUtill.isNetworkAvailable(this)) {
            ServiceExecute.execute(new FileUploaderService(this).uploadFile(url))
                    .subscribe(new NtkObserver<FileUploadModel>() {
                        @Override
                        public void onNext(@NonNull FileUploadModel fileUploadModel) {
                            adapter.notifyDataSetChanged();
                            fileId.add(fileUploadModel.FileKey);
                            Btn.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Btn.setVisibility(View.VISIBLE);
                            attaches.remove(attaches.size() - 1);
                            adapter.notifyDataSetChanged();
                            Snackbar.make(layout, "خطای سامانه مجددا تلاش کنید", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    init();
                                }
                            }).show();

                        }
                    });

        } else {
            Btn.setVisibility(View.VISIBLE);
            Toasty.warning(this, "عدم دسترسی به اینترنت", Toasty.LENGTH_LONG, true).show();
        }
    }
}