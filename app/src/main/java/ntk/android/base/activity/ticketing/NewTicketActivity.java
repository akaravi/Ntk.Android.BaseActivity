package ntk.android.base.activity.ticketing;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import ntk.android.base.adapter.SpinnerAdapter;
import ntk.android.base.adapter.TicketAttachAdapter;
import ntk.android.base.api.member.model.MemberUserActAddRequest;
import ntk.android.base.config.GenericErrors;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.config.ServiceExecute;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterDataModel;
import ntk.android.base.entitymodel.file.FileUploadModel;
import ntk.android.base.entitymodel.ticketing.TicketingDepartemenModel;
import ntk.android.base.entitymodel.ticketing.TicketingTaskModel;
import ntk.android.base.event.RemoveAttachEvent;
import ntk.android.base.services.file.FileUploaderService;
import ntk.android.base.services.ticketing.TicketingDepartemenService;
import ntk.android.base.services.ticketing.TicketingTaskService;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.FontManager;
import ntk.android.base.utill.Regex;
import ntk.android.base.utill.prefrense.Preferences;

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

        ((TextView) findViewById(R.id.lblTitleActSendTicket)).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));


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
            ti.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        }
        for (EditText ti :
                Txts) {
            ti.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        }
        Btn.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));

        ((TextInputEditText) findViewById(R.id.txtNameFamilyActSendTicket)).setText(Preferences.with(this).ticketVariableInfo().nameFamily());
        ((TextInputEditText) findViewById(R.id.txtPhoneNumberActSendTicket)).setText(Preferences.with(this).ticketVariableInfo().mobile());
        ((TextInputEditText) findViewById(R.id.txtEmailActSendTicket)).setText(Preferences.with(this).ticketVariableInfo().email());


        Rv.setHasFixedSize(true);
        Rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        adapter = new TicketAttachAdapter(this, attaches);
        Rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        SpinnerAdapter adapter_state = new SpinnerAdapter(this, R.layout.spinner_item, new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.StateTicket))));
        spinners.get(1).setAdapter(adapter_state);
        spinners.get(1).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                request.Priority = (position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (request.Priority == 0) {
            spinners.get(1).setSelection(0);
        }
        FilterDataModel request = new FilterDataModel();
        ServiceExecute.execute(new TicketingDepartemenService(this).getAll(request))
                .subscribe(new NtkObserver<ErrorException<TicketingDepartemenModel>>() {
                    @Override
                    public void onNext(@NonNull ErrorException<TicketingDepartemenModel> model) {
                        if (model.IsSuccess) {
                            List<String> list = new ArrayList<>();
                            for (TicketingDepartemenModel td : model.ListItems) {
                                list.add(td.Title);
                                SpinnerAdapter<String> adapter_dpartman = new SpinnerAdapter<>(NewTicketActivity.this, R.layout.spinner_item, list);
                                spinners.get(0).setAdapter(adapter_dpartman);
                            }
                        } else {
                            switcher.showErrorView();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        new GenericErrors().throwableException((error, tryAgain) -> Toasty.warning(NewTicketActivity.this, error, Toasty.LENGTH_LONG, true).show(), e, () -> {
                        });
                    }
                });
    }

    public void ClickSubmit() {
        TextInputEditText subject = (findViewById(R.id.txtSubjectActSendTicket));
        TextInputEditText message = (findViewById(R.id.txtMessageActSendTicket));
        TextInputEditText name = (findViewById(R.id.txtNameFamilyActSendTicket));
        TextInputEditText phone = (findViewById(R.id.txtPhoneNumberActSendTicket));
        TextInputEditText email = (findViewById(R.id.txtEmailActSendTicket));
        findViewById(R.id.txtSubjectActSendTicket);
        if (subject.getText().toString().isEmpty()) {
            Toasty.warning(NewTicketActivity.this, "موضوع درخواست خود را وارد کنید", Toasty.LENGTH_LONG, true).show();
            return;
        }
        if (message.getText().toString().isEmpty()) {
            Toasty.warning(NewTicketActivity.this, "متن درخواست خود را وارد کنید", Toasty.LENGTH_LONG, true).show();
            return;
        }
        if (name.getText().toString().isEmpty()) {
            Toasty.warning(NewTicketActivity.this, "نام و نام خانوادگی را وارد کنید", Toasty.LENGTH_LONG, true).show();
            return;
        }
        Preferences.with(this).ticketVariableInfo().setNameFamily(name.getText().toString());
        if (phone.getText().toString().isEmpty()) {
            Toasty.warning(NewTicketActivity.this, "شماره تلفن همراه را وارد کنید", Toasty.LENGTH_LONG, true).show();
            return;
        }
        if (!phone.getText().toString().startsWith("09")) {
            Toasty.warning(NewTicketActivity.this, "شماره تلفن همراه را به صورت صحیح وارد کنید", Toasty.LENGTH_LONG, true).show();
            return;
        }
        Preferences.with(this).ticketVariableInfo().setMobile(phone.getText().toString());
        if (email.getText().toString().isEmpty()) {
            Toasty.warning(NewTicketActivity.this, "پست الکترونیک را وارد کنید", Toasty.LENGTH_LONG, true).show();
            return;
        }
        if (!Regex.ValidateEmail(email.getText().toString())) {
            Toasty.warning(this, "آدرس پست الکترونیکی صحیح نمیباشد", Toasty.LENGTH_LONG, true).show();
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
                                Toasty.success(NewTicketActivity.this, "با موفقیت ثبت شد", Toasty.LENGTH_LONG, true).show();
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
            Snackbar.make(layout, "عدم دسترسی به اینترنت", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", v -> init()).show();
        }


    }

    public void Clickback() {
        finish();
    }

    public void ClickAttach() {
        if (CheckPermission()) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, READ_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(NewTicketActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 220);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri;
            if (resultData != null) {
                uri = resultData.getData();
                if (uri != null) {
                    Btn.setVisibility(View.GONE);
                    attaches.add(getPath(NewTicketActivity.this, uri));
                    adapter.notifyDataSetChanged();
                    UploadFileToServer(getPath(NewTicketActivity.this, uri));
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private boolean CheckPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            return checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
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
                        public void onError(Throwable e) {
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

    @Subscribe
    public void EventRemove(RemoveAttachEvent event) {
        attaches.remove(event.GetPosition());
        fileId.remove(event.GetPosition());
        adapter.notifyDataSetChanged();
    }
}
