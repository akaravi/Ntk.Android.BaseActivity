package ntk.android.base.activity.ticketing;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.annotations.NonNull;
import ntk.android.base.Extras;
import ntk.android.base.R;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.adapter.BaseRecyclerAdapter;
import ntk.android.base.adapter.common.TicketAnswerAdapter;
import ntk.android.base.adapter.common.TicketAttachAdapter;
import ntk.android.base.config.GenericErrors;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.config.ServiceExecute;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterModel;
import ntk.android.base.entitymodel.base.FilterDataModel;
import ntk.android.base.entitymodel.file.FileUploadModel;
import ntk.android.base.entitymodel.ticketing.TicketingAnswerModel;
import ntk.android.base.event.RemoveAttachEvent;
import ntk.android.base.service.FileManagerService;
import ntk.android.base.services.file.FileUploaderService;
import ntk.android.base.services.ticketing.TicketingAnswerService;
import ntk.android.base.utill.AppUtil;
import ntk.android.base.utill.FontManager;
import ntk.android.base.utill.prefrense.Preferences;
import ntk.android.base.view.NViewUtils;


public class TicketAnswerActivity extends BaseActivity {

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
    public void EventRemove(RemoveAttachEvent event) {
        attaches.remove(event.GetPosition());
        fileId.remove(event.GetPosition());
        AdAtach.notifyDataSetChanged();
        if (adapter.getItemCount() == 0)
            findViewById(R.id.linearAttachment).setVisibility(View.GONE);
    }

    private void init() {
        Lbl = findViewById(R.id.lblTitleActTicketAnswer);
        txt = findViewById(R.id.txtMessageActTicketAnswer);
        Btn = findViewById(R.id.btnSubmitActTicketAnswer);
        findViewById(R.id.imgBackActTicketAnswer).setOnClickListener(v -> ClickBack());
        findViewById(R.id.btnSubmitActTicketAnswer).setOnClickListener(v -> ClickSubmit());
        findViewById(R.id.RippleAttachActTicketAnswer).setOnClickListener(v -> ClickAttach());
        Lbl.setTypeface(FontManager.T1_Typeface(this));
        ((TextView) findViewById(R.id.txtNoAnswers)).setTypeface(FontManager.T1_Typeface(this));
        ticketId = getIntent().getExtras().getLong(Extras.EXTRA_FIRST_ARG);
        Lbl.setText(getString(R.string.ticket_answer) + ticketId);
        RecyclerView answersRv = findViewById(R.id.recyclerAnswer);
        answersRv.setHasFixedSize(true);
        answersRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        Long userId = Preferences.with(this).UserInfo().userId();
        Long memberId = Preferences.with(this).UserInfo().memberId();
        adapter = new TicketAnswerAdapter(this, this.getSupportFragmentManager(), tickets, userId, memberId);
        answersRv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        ((TextInputEditText) findViewById(R.id.txtMessageActTicketAnswer)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                findViewById(R.id.RippleAttachActTicketAnswer).setAlpha((float) 0.4);
            }

            @Override
            public void afterTextChanged(Editable s) {
//                findViewById(R.id.RippleAttachActSendTicket).setVisibility(View.INVISIBLE);
            }
        });
        ((TextInputEditText) findViewById(R.id.txtMessageActTicketAnswer)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    findViewById(R.id.RippleAttachActTicketAnswer).setAlpha((float) 1);
                } else
                    findViewById(R.id.RippleAttachActTicketAnswer).setAlpha((float) 0.4);
            }
        });
        HandelData(1);
        RecyclerView attachesRc = findViewById(R.id.RecyclerAttachTicketAnswer);
        attachesRc.setHasFixedSize(true);
        attachesRc.setLayoutManager(new GridLayoutManager(this, BaseRecyclerAdapter.getScreenWidth() / NViewUtils.dpToPx(this, 150)));
        AdAtach = new TicketAttachAdapter(this, attaches);
        attachesRc.setAdapter(AdAtach);
        AdAtach.notifyDataSetChanged();
    }

    private void HandelData(int i) {
        if (AppUtil.isNetworkAvailable(this)) {
            switcher.showProgressView();
            FilterModel request = new FilterModel();
            request.RowPerPage = 100;
            request.addFilter(new FilterDataModel().setPropertyName("LinkTaskId").setIntValue(ticketId));

            ServiceExecute.execute(new TicketingAnswerService(this).getAll(request))
                    .subscribe(new NtkObserver<ErrorException<TicketingAnswerModel>>() {
                        @Override
                        public void onNext(ErrorException<TicketingAnswerModel> model) {
                            if (model.IsSuccess) {
                                switcher.showContentView();
                                tickets.addAll(model.ListItems);
                                if (tickets.size() == 0) {
                                    findViewById(R.id.viewNoAnswer).setVisibility(View.VISIBLE);
                                    findViewById(R.id.recyclerAnswer).setVisibility(View.GONE);
                                }
                                adapter.notifyDataSetChanged();
                            } else
                                switcher.showErrorView();

                        }

                        @Override
                        public void onError(Throwable e) {
                            switcher.showErrorView();
                            Snackbar.make(layout, R.string.error_raised, Snackbar.LENGTH_INDEFINITE).setAction(R.string.try_again, v -> init()).show();
                        }


                    });
        } else {
            Snackbar.make(layout, R.string.per_no_net, Snackbar.LENGTH_INDEFINITE).setAction(R.string.try_again, v -> init()).show();
        }
    }

    public void ClickBack() {
        finish();
    }

    public void ClickSubmit() {
        if (txt.getText().toString().isEmpty()) {

        } else {
            if (AppUtil.isNetworkAvailable(this)) {
                TicketingAnswerModel request = new TicketingAnswerModel();
                request.HtmlBody = txt.getText().toString();
                request.LinkTaskId = ticketId;
                request.UploadFileGUID = fileId;

                ServiceExecute.execute(new TicketingAnswerService(this).add(request))
                        .subscribe(new NtkObserver<ErrorException<TicketingAnswerModel>>() {

                            @Override
                            public void onNext(ErrorException<TicketingAnswerModel> model) {
                                if (model.IsSuccess) {
                                    Toasty.success(TicketAnswerActivity.this,  R.string.per_success, Toasty.LENGTH_LONG, true).show();
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
                Toasty.warning(this, R.string.per_no_net, Toasty.LENGTH_LONG, true).show();
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
        if (AppUtil.isNetworkAvailable(this)) {
            ServiceExecute.execute(new FileUploaderService(this).uploadFile(url))
                    .subscribe(new NtkObserver<FileUploadModel>() {
                        @Override
                        public void onNext(@NonNull FileUploadModel fileUploadModel) {
                            adapter.notifyDataSetChanged();
                            findViewById(R.id.linearAttachment).setVisibility(View.VISIBLE);
                            fileId.add(fileUploadModel.FileKey);
                            Btn.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Btn.setVisibility(View.VISIBLE);
                            attaches.remove(attaches.size() - 1);
                            adapter.notifyDataSetChanged();
                            Snackbar.make(layout, R.string.error_raised, Snackbar.LENGTH_INDEFINITE).setAction(R.string.try_again, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    init();
                                }
                            }).show();

                        }
                    });

        } else {
            Btn.setVisibility(View.VISIBLE);
            Toasty.warning(this, R.string.per_no_net, Toasty.LENGTH_LONG, true).show();
        }
    }
}