package ntk.android.base.activity.ticketing;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.annotations.NonNull;
import ntk.android.base.R;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.adapter.BaseRecyclerAdapter;
import ntk.android.base.adapter.SpinnerAdapter;
import ntk.android.base.adapter.common.TicketAttachAdapter;
import ntk.android.base.api.member.model.MemberUserActAddRequest;
import ntk.android.base.config.GenericErrors;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.config.ServiceExecute;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterModel;
import ntk.android.base.entitymodel.file.FileUploadModel;
import ntk.android.base.entitymodel.ticketing.TicketingDepartemenModel;
import ntk.android.base.entitymodel.ticketing.TicketingTaskModel;
import ntk.android.base.event.RemoveAttachEvent;
import ntk.android.base.service.FileManagerService;
import ntk.android.base.services.file.FileUploaderService;
import ntk.android.base.services.ticketing.TicketingDepartemenService;
import ntk.android.base.services.ticketing.TicketingTaskService;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.FontManager;
import ntk.android.base.utill.Regex;
import ntk.android.base.utill.prefrense.Preferences;
import ntk.android.base.view.NViewUtils;

public class NewTicketActivity extends BaseActivity {
    //    List<MaterialAutoCompleteTextView> spinners;
//    List<EditText> Txts;
//    List<TextInputLayout> Inputs;
    Button Btn;
    RecyclerView Rv;
    CoordinatorLayout layout;

    private TicketingTaskModel request = new TicketingTaskModel();
    private MemberUserActAddRequest requestMember = new MemberUserActAddRequest();
    private List<String> attaches = new ArrayList<>();
    private List<String> fileId = new ArrayList<>();
    private TicketAttachAdapter adapter;

    private static final int READ_REQUEST_CODE = 1520;
    private List<TicketingDepartemenModel> departments;
    private String DefaultAnswerBody;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ticket_new_activity);
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

    private void init() {
        List<MaterialAutoCompleteTextView> spinners = new ArrayList() {{
            add(findViewById(R.id.SpinnerService));
            add(findViewById(R.id.SpinnerState));
        }};

        ((TextView) findViewById(R.id.lblTitleActSendTicket)).setTypeface(FontManager.T1_Typeface(this));


        List<EditText> Txts = new ArrayList() {{
            add(findViewById(R.id.txtSubjectActSendTicket));
            add(findViewById(R.id.txtMessageActSendTicket));
            add(findViewById(R.id.txtNameFamilyActSendTicket));
            add(findViewById(R.id.txtPhoneNumberActSendTicket));
            add(findViewById(R.id.txtEmailActSendTicket));
        }};
        List<TextInputLayout> Inputs = new ArrayList() {{
            add(findViewById(R.id.inputSubjectActSendTicket));
            add(findViewById(R.id.inputMessageActSendTicket));
            add(findViewById(R.id.inputNameFamilytActSendTicket));
            add(findViewById(R.id.inputPhoneNumberActSendTicket));
            add(findViewById(R.id.inputEmailtActSendTicket));
        }};
        Btn = findViewById(R.id.btnSubmitActSendTicket);
        Rv = findViewById(R.id.RecyclerAttach);
        layout = findViewById(R.id.mainLayoutActSendTicket);
        findViewById(R.id.btnSubmitActSendTicket).setOnClickListener(v -> ClickSubmit());
        findViewById(R.id.imgBackActSendTicket).setOnClickListener(v -> Clickback());
        findViewById(R.id.RippleAttachActSendTicket).setOnClickListener(v -> ClickAttach());
        for (TextInputLayout ti :
                Inputs) {
            ti.setTypeface(FontManager.T1_Typeface(this));
        }
        for (EditText ti :
                Txts) {
            ti.setTypeface(FontManager.T1_Typeface(this));
        }
        Btn.setTypeface(FontManager.T1_Typeface(this));

        ((TextInputEditText) findViewById(R.id.txtNameFamilyActSendTicket)).setText(Preferences.with(this).ticketVariableInfo().nameFamily());
        ((TextInputEditText) findViewById(R.id.txtPhoneNumberActSendTicket)).setText(Preferences.with(this).ticketVariableInfo().mobile());
        ((TextInputEditText) findViewById(R.id.txtEmailActSendTicket)).setText(Preferences.with(this).ticketVariableInfo().email());
        ((TextInputEditText) findViewById(R.id.txtMessageActSendTicket)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                findViewById(R.id.RippleAttachActSendTicket).setAlpha((float) 0.4);
            }

            @Override
            public void afterTextChanged(Editable s) {
//                findViewById(R.id.RippleAttachActSendTicket).setVisibility(View.INVISIBLE);
            }
        });
        ((TextInputEditText) findViewById(R.id.txtMessageActSendTicket)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    findViewById(R.id.RippleAttachActSendTicket).setAlpha((float) 1);
                } else
                    findViewById(R.id.RippleAttachActSendTicket).setAlpha((float) 0.4);
            }
        });
        Rv.setHasFixedSize(true);
        Rv.setLayoutManager(new GridLayoutManager(this, BaseRecyclerAdapter.getScreenWidth() / NViewUtils.dpToPx(this, 150)));
        adapter = new TicketAttachAdapter(this, attaches);
        Rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        getStateTicket();
        getDepartment();

    }

    public void getDepartment() {

        ServiceExecute.execute(new TicketingDepartemenService(this).getAll(new FilterModel()))
                .subscribe(new NtkObserver<ErrorException<TicketingDepartemenModel>>() {
                    @Override
                    public void onNext(@NonNull ErrorException<TicketingDepartemenModel> model) {
                        if (model.IsSuccess) {
                            departments = model.ListItems;
                            if (departments.size() > 0) {
                                MaterialAutoCompleteTextView spinner = (findViewById(R.id.SpinnerService));
                                List<String> names = new ArrayList<>();
                                for (TicketingDepartemenModel t : model.ListItems)
                                    names.add(t.Title);
                                SpinnerAdapter<TicketingDepartemenModel> adapter_dpartman = new SpinnerAdapter<TicketingDepartemenModel>(NewTicketActivity.this, names);
                                spinner.setOnItemClickListener((parent, view, position, id) -> {
                                    TicketingDepartemenModel selectedModel = departments.get(position);
                                    request.LinkTicketingDepartemenId = selectedModel.Id;
                                    DefaultAnswerBody = selectedModel.DefaultAnswerBody;
                                });
                                spinner.setAdapter(adapter_dpartman);
                                // Do something for lollipop and above versions
                                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                    //if departments is 0
                                    spinner.setText(adapter_dpartman.getItem(0), false);
                                    request.LinkTicketingDepartemenId = departments.get(0).Id;
                                }

                            } else {
                                findViewById(R.id.SpinnerServiceTextInput).setEnabled(false);
                            }
                        } else {
                            switcher.showErrorView(model.ErrorMessage, () -> getDepartment());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        new GenericErrors().throwableException((error, tryAgain) -> Toasty.warning(NewTicketActivity.this, error, Toasty.LENGTH_LONG, true).show(), e, () -> {
                        });
                    }
                });
    }

    public void getStateTicket() {
        SpinnerAdapter<String> adapter_state = new SpinnerAdapter(this, new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.StateTicket))));
        MaterialAutoCompleteTextView spinner = findViewById(R.id.SpinnerState);
        spinner.setAdapter(adapter_state);
        spinner.setOnItemClickListener((parent, view, position, id) -> request.Priority = (position + 1));
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            // Do something for lollipop and above versions
            if (request.Priority == 0) {
                (spinner).setText(adapter_state.getItem(0), false);
                request.Priority = 1;
            }
        }
    }

    public void ClickSubmit() {
        TextInputEditText subject = (findViewById(R.id.txtSubjectActSendTicket));
        TextInputEditText message = (findViewById(R.id.txtMessageActSendTicket));
        TextInputEditText name = (findViewById(R.id.txtNameFamilyActSendTicket));
        TextInputEditText phone = (findViewById(R.id.txtPhoneNumberActSendTicket));
        TextInputEditText email = (findViewById(R.id.txtEmailActSendTicket));
        findViewById(R.id.txtSubjectActSendTicket);
        if (subject.getText().toString().isEmpty()) {
            Toasty.warning(NewTicketActivity.this, R.string.plz_insert_ticket_subject, Toasty.LENGTH_LONG, true).show();
            return;
        }
        if (message.getText().toString().isEmpty()) {
            Toasty.warning(NewTicketActivity.this, R.string.plz_insert_ticket_desc, Toasty.LENGTH_LONG, true).show();
            return;
        }
        if (name.getText().toString().isEmpty()) {
            Toasty.warning(NewTicketActivity.this, R.string.plz_insert_name, Toasty.LENGTH_LONG, true).show();
            return;
        }
        Preferences.with(this).ticketVariableInfo().setNameFamily(name.getText().toString());
        if (phone.getText().toString().isEmpty()) {
            Toasty.warning(NewTicketActivity.this, R.string.plz_insert_num, Toasty.LENGTH_LONG, true).show();
            return;
        }
        if (!phone.getText().toString().startsWith("09")||phone.getText().toString().length()!=11) {
            Toasty.warning(NewTicketActivity.this, R.string.plz_insert_mobile_correct, Toasty.LENGTH_LONG, true).show();
            return;
        }
        Preferences.with(this).ticketVariableInfo().setMobile(phone.getText().toString());
        if (email.getText().toString().isEmpty()) {
            Toasty.warning(NewTicketActivity.this, R.string.plz_insert_email, Toasty.LENGTH_LONG, true).show();
            return;
        }
        if (!Regex.ValidateEmail(email.getText().toString())) {
            Toasty.warning(this, R.string.plz_insert_email_correctly, Toasty.LENGTH_LONG, true).show();
            return;
        }
        if (departments != null && departments.size() > 0)
            if (request.LinkTicketingDepartemenId == null || request.LinkTicketingDepartemenId == 0) {
                Toasty.warning(this, R.string.plz_insert_email_departman, Toasty.LENGTH_LONG, true).show();
                return;
            }
        if (request.Priority == 0) {
            Toasty.warning(this, R.string.plz_insert_parioty, Toasty.LENGTH_LONG, true).show();
            return;
        }
        Preferences.with(this).ticketVariableInfo().setEmail(email.getText().toString());
        if (AppUtill.isNetworkAvailable(this)) {
            //show dialog loading
            switcher.showLoadDialog(this, false);
            request.Email = email.getText().toString();
            request.PhoneNo = phone.getText().toString();
            request.FullName = name.getText().toString();
            request.HtmlBody = message.getText().toString();
            request.Title = subject.getText().toString();

//            String ids = "";
//            for (int i = 0; i < fileId.size(); i++) {
//                if (ids.equals(""))
//                    ids = fileId.get(i);
//                else
//                    ids += "," + fileId.get(i);
//            }
            request.UploadFileGUID = fileId;

            requestMember.FirstName = name.getText().toString();
            requestMember.LastName = name.getText().toString();
            requestMember.PhoneNo = phone.getText().toString();
            requestMember.Email = email.getText().toString();


            findViewById(R.id.btnSubmitActSendTicket).setClickable(false);

            ServiceExecute.execute(new TicketingTaskService(this).add(request))
                    .subscribe(new NtkObserver<ErrorException<TicketingTaskModel>>() {
                        @Override
                        public void onNext(@NonNull ErrorException<TicketingTaskModel> model) {
                            switcher.hideLoadDialog();
                            if (model.IsSuccess) {
                                if (DefaultAnswerBody == null)
                                    DefaultAnswerBody = "";
                                String reply = Html.fromHtml(DefaultAnswerBody.replace("<p>", "")
                                        .replace("</p>", "")) + "\n" + getString(R.string.ticket_number) + model.Item.Id;
                                Toasty.success(NewTicketActivity.this, reply, Toasty.LENGTH_LONG, true).show();
                                setResult(RESULT_OK);
                                finish();
                            } else
                                Toasty.error(NewTicketActivity.this, model.ErrorMessage).show();

                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            switcher.hideLoadDialog();
                            new GenericErrors().throwableException((error, tryAgain) -> Toasty.error(NewTicketActivity.this, error).show(), e, () -> {
                            });
                            findViewById(R.id.btnSubmitActSendTicket).setClickable(true);
                        }
                    });
        } else {
            Snackbar.make(layout, R.string.per_no_net, Snackbar.LENGTH_INDEFINITE).setAction(R.string.success_comment, v -> init()).show();
        }


    }

    public void Clickback() {
        finish();
    }

    public void ClickAttach() {
        new FileManagerService().clickAttach(this, READ_REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri;
            if (resultData != null) {
                uri = resultData.getData();
                if (uri != null) {
                    Btn.setVisibility(View.GONE);
                    attaches.add(FileManagerService.getPath(NewTicketActivity.this, uri));
                    adapter.notifyDataSetChanged();
                    UploadFileToServer(FileManagerService.getPath(NewTicketActivity.this, uri));
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
                            findViewById(R.id.linearAttachment).setVisibility(View.VISIBLE);
                            fileId.add(fileUploadModel.FileKey);
                            Btn.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Btn.setVisibility(View.VISIBLE);
                            attaches.remove(attaches.size() - 1);
                            adapter.notifyDataSetChanged();
                            Snackbar.make(layout, R.string.error_raised, Snackbar.LENGTH_INDEFINITE).setAction(R.string.success_comment, new View.OnClickListener() {
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

    @Subscribe
    public void EventRemove(RemoveAttachEvent event) {
        attaches.remove(event.GetPosition());
        fileId.remove(event.GetPosition());
        adapter.notifyDataSetChanged();
        if (adapter.getItemCount() == 0)
            findViewById(R.id.linearAttachment).setVisibility(View.GONE);
    }
}
