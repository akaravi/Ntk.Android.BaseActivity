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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.annotations.NonNull;
import ntk.android.base.Extras;
import ntk.android.base.R;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.adapter.TicketAnswerAdapter;
import ntk.android.base.adapter.TicketAttachAdapter;
import ntk.android.base.config.GenericErrors;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.config.ServiceExecute;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterDataModel;
import ntk.android.base.entitymodel.base.Filters;
import ntk.android.base.entitymodel.file.FileUploadModel;
import ntk.android.base.entitymodel.ticketing.TicketingAnswerModel;
import ntk.android.base.event.RemoveAttachEvent;
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
        if (CheckPermission()) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, READ_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(TicketAnswerActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 220);
        }
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
                    attaches.add(getPath(TicketAnswerActivity.this, uri));
                    AdAtach.notifyDataSetChanged();
                    UploadFileToServer(getPath(TicketAnswerActivity.this, uri));
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