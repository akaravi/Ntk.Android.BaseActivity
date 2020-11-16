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
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import ntk.android.base.R;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.adapter.TicketAttachAdapter;
import ntk.android.base.adapter.SpinnerAdapter;
import ntk.android.base.api.member.model.MemberUserActAddRequest;
import ntk.android.base.config.NtkObserver;
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
import ntk.android.base.utill.EasyPreference;
import ntk.android.base.utill.FontManager;
import ntk.android.base.utill.Regex;

public class NewTicketActivity extends BaseActivity {
    List<Spinner> spinners;
    List<TextView> Lbls;
    List<EditText> Txts;
    List<TextInputLayout> Inputs;
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
        spinners = new ArrayList() {{
            add(findViewById(R.id.SpinnerService));
            add(findViewById(R.id.SpinnerState));
        }};

        Lbls = new ArrayList() {{
            add(findViewById(R.id.lblTitleActSendTicket));
            add(findViewById(R.id.lblImportantActSendTicket));
            add(findViewById(R.id.lblServiceActSendTicket));
        }};
        Txts = new ArrayList() {{
            add(findViewById(R.id.txtSubjectActSendTicket));
            add(findViewById(R.id.txtMessageActSendTicket));
            add(findViewById(R.id.txtNameFamilyActSendTicket));
            add(findViewById(R.id.txtPhoneNumberActSendTicket));
            add(findViewById(R.id.txtEmailActSendTicket));
        }};
        Inputs = new ArrayList() {{
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
        Lbls.get(0).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Lbls.get(1).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Lbls.get(2).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));

        Inputs.get(0).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Inputs.get(1).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Inputs.get(2).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Inputs.get(3).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Inputs.get(4).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));

        Txts.get(0).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Txts.get(1).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Txts.get(2).setText(EasyPreference.with(this).getString("NameFamily", ""));
        Txts.get(3).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Txts.get(3).setText(EasyPreference.with(this).getString("PhoneNumber", ""));
        Txts.get(4).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Txts.get(4).setText(EasyPreference.with(this).getString("Email", ""));

        Btn.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));

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
        new TicketingDepartemenService(this).getAll(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())

                .subscribe(new NtkObserver<ErrorException<TicketingDepartemenModel>>() {
                    @Override
                    public void onNext(@NonNull ErrorException<TicketingDepartemenModel> model) {
                        List<String> list = new ArrayList<>();
                        for (TicketingDepartemenModel td : model.ListItems) {
                            list.add(td.Title);
                            SpinnerAdapter<String> adapter_dpartman = new SpinnerAdapter<>(NewTicketActivity.this, R.layout.spinner_item, list);
                            spinners.get(0).setAdapter(adapter_dpartman);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toasty.warning(NewTicketActivity.this, "خطای سامانه", Toasty.LENGTH_LONG, true).show();

                    }
                });
    }

    public void ClickSubmit() {

        if (Txts.get(0).getText().toString().isEmpty()) {
            YoYo.with(Techniques.Tada).duration(700).playOn(Txts.get(0));
            Toasty.warning(NewTicketActivity.this, "موضوع درخواست خود را وارد کنید", Toasty.LENGTH_LONG, true).show();
            return;
        }
        if (Txts.get(1).getText().toString().isEmpty()) {
            YoYo.with(Techniques.Tada).duration(700).playOn(Txts.get(1));
            Toasty.warning(NewTicketActivity.this, "متن درخواست خود را وارد کنید", Toasty.LENGTH_LONG, true).show();
            return;
        }
        if (Txts.get(2).getText().toString().isEmpty()) {
            YoYo.with(Techniques.Tada).duration(700).playOn(Txts.get(2));
            Toasty.warning(NewTicketActivity.this, "نام و نام خانوادگی را وارد کنید", Toasty.LENGTH_LONG, true).show();
            return;
        }
        EasyPreference.with(this).addString("NameFamily", Txts.get(2).getText().toString());
        if (Txts.get(3).getText().toString().isEmpty()) {
            YoYo.with(Techniques.Tada).duration(700).playOn(Txts.get(3));
            Toasty.warning(NewTicketActivity.this, "شماره تلفن همراه را وارد کنید", Toasty.LENGTH_LONG, true).show();
            return;
        }
        if (!Txts.get(3).getText().toString().startsWith("09")) {
            YoYo.with(Techniques.Tada).duration(700).playOn(Txts.get(3));
            Toasty.warning(NewTicketActivity.this, "شماره تلفن همراه را به صورت صحیح وارد کنید", Toasty.LENGTH_LONG, true).show();
            return;
        }
        EasyPreference.with(this).addString("PhoneNumber", Txts.get(3).getText().toString());
        if (Txts.get(4).getText().toString().isEmpty()) {
            YoYo.with(Techniques.Tada).duration(700).playOn(Txts.get(4));
            Toasty.warning(NewTicketActivity.this, "پست الکترونیک را وارد کنید", Toasty.LENGTH_LONG, true).show();
            return;
        }
        if (!Regex.ValidateEmail(Txts.get(4).getText().toString())) {
            Toasty.warning(this, "آدرس پست الکترونیکی صحیح نمیباشد", Toasty.LENGTH_LONG, true).show();
            return;
        }
        EasyPreference.with(this).addString("Email", Txts.get(4).getText().toString());
        if (AppUtill.isNetworkAvailable(this)) {
            //show dialog loading
            switcher.showLoadDialog(this, false);
            request.Email = Txts.get(4).getText().toString();
            request.PhoneNo = Txts.get(3).getText().toString();
            request.FullName = Txts.get(2).getText().toString();
            request.HtmlBody = Txts.get(1).getText().toString();
            request.Title = Txts.get(0).getText().toString();

            String ids = "";
            for (int i = 0; i < fileId.size(); i++) {
                if (ids.equals(""))
                    ids = fileId.get(i);
                else
                    ids += "," + fileId.get(i);
            }
            request.LinkFileIds = ids;

            requestMember.FirstName = Txts.get(2).getText().toString();
            requestMember.LastName = Txts.get(2).getText().toString();
            requestMember.PhoneNo = Txts.get(3).getText().toString();
            requestMember.Email = Txts.get(4).getText().toString();


            findViewById(R.id.btnSubmitActSendTicket).setClickable(false);

            new TicketingTaskService(this).add(request).
                    observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new NtkObserver<ErrorException<TicketingTaskModel>>() {
                        @Override
                        public void onNext(@NonNull ErrorException<TicketingTaskModel> model) {
                            switcher.hideLoadDialog();
                            Toasty.success(NewTicketActivity.this, "با موفقیت ثبت شد", Toasty.LENGTH_LONG, true).show();
                            finish();
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            switcher.hideLoadDialog();
                            Snackbar.make(layout, "خطای سامانه مجددا تلاش کنید", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", v -> init()).show();
                            findViewById(R.id.btnSubmitActSendTicket).setClickable(true);
                        }
                    });
        } else {

            Snackbar.make(layout, "عدم دسترسی به اینترنت", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    init();
                }
            }).show();
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
            new FileUploaderService(this).uploadFile(url).
                    observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
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
