package ntk.android.base.activity.abstraction;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import java9.util.function.BiFunction;
import java9.util.function.Function;
import ntk.android.base.Extras;
import ntk.android.base.R;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.config.ServiceExecute;
import ntk.android.base.dtomodel.application.MainResponseDtoModel;
import ntk.android.base.dtomodel.core.ScoreClickDtoModel;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.ErrorExceptionBase;
import ntk.android.base.entitymodel.base.FilterDataModel;
import ntk.android.base.entitymodel.base.Filters;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.FontManager;
import ntk.android.base.utill.prefrense.Preferences;

public abstract class AbstractionDetailActivity<TEntity, TComment, TOtherInfo> extends BaseActivity {
    ProgressBar Progress;
    LinearLayout Loading;
    protected ImageView ImgHeader;
    public RecyclerView RvTab;
    RecyclerView RvComment;
    public RecyclerView Rv;
    protected RatingBar Rate;
    LinearLayout Page;
    CoordinatorLayout layout;
    public WebView webViewBody;

    protected TEntity model;

    public long Id = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base1_detail_activity);
        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        Progress = findViewById(R.id.progressDetail);

        Loading = findViewById(R.id.rowProgressDetail);
        List<TextView> Lbls = new ArrayList() {{
            add(findViewById(R.id.lblTitleDetail));
            add(findViewById(R.id.lblNameCommandDetail));
            add(findViewById(R.id.lblKeySeenDetail));
            add(findViewById(R.id.lblValueSeenDetail));
            add(findViewById(R.id.lblCommentDetail));
            add(findViewById(R.id.lblProgressDetail));
            add(findViewById(R.id.lblMenuDetail));
            add(findViewById(R.id.lblAllMenuDetail));
        }};

        ImgHeader = findViewById(R.id.imgHeaderDetail);
        RvTab = findViewById(R.id.recyclerTabDetail);
        RvComment = findViewById(R.id.recyclerCommentDetail);
        Rv = findViewById(R.id.recyclerMenuDetail);
        Rate = findViewById(R.id.ratingBarDetail);
        Page = findViewById(R.id.PageDetail);
        layout = findViewById(R.id.mainLayoutDetail);
        webViewBody = findViewById(R.id.WebViewBodyDetail);
        findViewById(R.id.imgBackDetail).setOnClickListener(v -> finish());
        findViewById(R.id.imgCommentDetail).setOnClickListener(v -> ClickCommentAdd());
        findViewById(R.id.imgShareDetail).setOnClickListener(v -> ClickShare());
        findViewById(R.id.imgFavDetail).setOnClickListener(v -> ClickFav());

        for (TextView tv : Lbls) {
            tv.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        }
        //   todo     Progress.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        RvTab.setHasFixedSize(true);
        RvTab.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        webViewBody.getSettings().setJavaScriptEnabled(true);
        webViewBody.getSettings().setBuiltInZoomControls(true);
        Id = getIntent().getExtras().getLong(Extras.EXTRA_FIRST_ARG);
        Loading.setVisibility(View.VISIBLE);

        RvComment.setHasFixedSize(true);
        RvComment.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        Rate.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (!fromUser) return;
            ScoreClickDtoModel request = new ScoreClickDtoModel();
            request.Id = Id;
            request.ScorePercent = ((int) (rating / .5)) * 10;
            setContentRate(request);
        });
        getContent();
    }

    public void afterDetailInit() {
    }

    public abstract Function<ScoreClickDtoModel, Observable<ErrorExceptionBase>> contentRateService();

    public abstract Function<Long, Observable<ErrorException<TEntity>>> getOneContentService();

    public abstract Function<Long, Observable<ErrorException<TComment>>> getCommentListService();

    public abstract Function<Long, Observable<ErrorException<TOtherInfo>>> getOtherInfoListService();

    public abstract Pair<Function<Long, Observable<ErrorExceptionBase>>, Runnable> getFavoriteService();

    public abstract BiFunction<String, String, Observable<ErrorException<TComment>>> addCommentService();

    public abstract RecyclerView.Adapter createCommentAdapter(List<TComment> listItems);

    protected void setContentRate(ScoreClickDtoModel request) {
        if (AppUtill.isNetworkAvailable(this)) {
            ServiceExecute.execute(contentRateService().apply(request))
                    .subscribe(new NtkObserver<ErrorExceptionBase>() {

                        @Override
                        public void onNext(ErrorExceptionBase biographyContentResponse) {
                            Loading.setVisibility(View.GONE);
                            if (biographyContentResponse.IsSuccess) {
                                Toasty.success(AbstractionDetailActivity.this, "نظر شمابا موفقیت ثبت گردید").show();
                            } else {
                                Toasty.warning(AbstractionDetailActivity.this, biographyContentResponse.ErrorMessage).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Loading.setVisibility(View.GONE);
                            Snackbar.make(layout, "خطای سامانه مجددا تلاش کنید", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    init();
                                }
                            }).show();
                        }
                    });
        } else {
            Loading.setVisibility(View.GONE);
            Snackbar.make(layout, "عدم دسترسی به اینترنت", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    init();
                }
            }).show();
        }
    }


    private void getContent() {
        if (AppUtill.isNetworkAvailable(this)) {

            ServiceExecute.execute(getOneContentService().apply(Id))
                    .subscribe(new NtkObserver<ErrorException<TEntity>>() {
                        @Override
                        public void onNext(ErrorException<TEntity> ContentResponse) {
                            model = ContentResponse.Item;
                            bindContentData(ContentResponse);
                            if (Id > 0) {
                                HandelDataContentOtherInfo(Id);
                                HandelDataComment(Id);
                            }
                            Loading.setVisibility(View.GONE);
                            Page.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Loading.setVisibility(View.GONE);
                            Snackbar.make(layout, "خطای سامانه مجددا تلاش کنید", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", v -> init()).show();
                        }

                    });
        } else {
            Loading.setVisibility(View.GONE);
            Snackbar.make(layout, "عدم دسترسی به اینترنت", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", v -> init()).show();
        }

    }


    private void HandelDataComment(long ContentId) {
        if (AppUtill.isNetworkAvailable(this)) {
            ServiceExecute.execute(getCommentListService().apply(ContentId))
                    .subscribe(new NtkObserver<ErrorException<TComment>>() {
                        @Override
                        public void onNext(@NonNull ErrorException<TComment> model) {
                            if (model.IsSuccess && !model.ListItems.isEmpty()) {
                                findViewById(R.id.lblCommentDetail).setVisibility(View.VISIBLE);
                                RecyclerView.Adapter commentAdapter = createCommentAdapter(model.ListItems);
                                RvComment.setAdapter(commentAdapter);
                                commentAdapter.notifyDataSetChanged();
                            } else {
                                findViewById(R.id.lblCommentDetail).setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Snackbar.make(layout, "خطای سامانه مجددا تلاش کنید", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    init();
                                }
                            }).show();
                        }
                    });
        } else {
            findViewById(R.id.lblCommentDetail).setVisibility(View.GONE);
            Snackbar.make(layout, "عدم دسترسی به اینترنت", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    init();
                }
            }).show();
        }
    }


    @NotNull
    protected FilterDataModel otherInfoFilter(long ContentId) {
        FilterDataModel Request = new FilterDataModel();
        Filters f = new Filters();
        f.PropertyName = "LinkContentId";
        f.IntValue1 = ContentId;
        Request.addFilter(f);
        return Request;
    }

    private void HandelDataContentOtherInfo(long ContentId) {
        if (AppUtill.isNetworkAvailable(this)) {
            ServiceExecute.execute(getOtherInfoListService().apply(ContentId))
                    .subscribe(new NtkObserver<ErrorException<TOtherInfo>>() {

                        @Override
                        public void onNext(@NonNull ErrorException<TOtherInfo> ContentOtherInfoResponse) {
                            SetDataOtherinfo(ContentOtherInfoResponse);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Snackbar.make(layout, "خطای سامانه مجددا تلاش کنید", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", v -> init()).show();
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

    public abstract void SetDataOtherinfo(ErrorException<TOtherInfo> model);

    public abstract void bindContentData(ErrorException<TEntity> model);

    public void ClickCommentAdd() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        dialog.setContentView(R.layout.dialog_comment_add);
        TextView Lbl = dialog.findViewById(R.id.lblTitleDialogAddComment);
        Lbl.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));

        EditText[] Txt = new EditText[2];

        Txt[0] = dialog.findViewById(R.id.txtNameDialogAddComment);
        Txt[0].setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));

        Txt[1] = dialog.findViewById(R.id.txtContentDialogAddComment);
        Txt[1].setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));

        Button Btn = dialog.findViewById(R.id.btnSubmitDialogCommentAdd);
        Btn.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));

        Btn.setOnClickListener(v -> {
            if (Txt[0].getText().toString().isEmpty()) {
                Toast.makeText(AbstractionDetailActivity.this, "لطفا مقادیر را وارد نمایید", Toast.LENGTH_SHORT).show();
            } else {
                if (Txt[1].getText().toString().isEmpty()) {
                    Toast.makeText(AbstractionDetailActivity.this, "لطفا مقادیر را وارد نمایید", Toast.LENGTH_SHORT).show();
                } else {
                    if (AppUtill.isNetworkAvailable(this)) {
//                        NewsCommentModel add = new NewsCommentModel();
                        String writer = Txt[0].getText().toString();
                        String comment = Txt[1].getText().toString();
//                        add.LinkContentId = Id;
                        ServiceExecute.execute(addCommentService().apply(writer, comment))
                                .subscribe(new NtkObserver<ErrorException<TComment>>() {
                                    @Override
                                    public void onNext(@NonNull ErrorException<TComment> e) {
                                        if (e.IsSuccess) {
                                            HandelDataComment(Id);
                                            dialog.dismiss();
                                            Toasty.success(AbstractionDetailActivity.this, "نظر شما با موفقیت ثبت شد").show();
                                        } else {
                                            dialog.dismiss();
                                            Toasty.warning(AbstractionDetailActivity.this, e.ErrorMessage).show();
                                        }
                                    }

                                    @Override
                                    public void onError(@NonNull Throwable e) {
                                        Snackbar.make(layout, "خطای سامانه مجددا تلاش کنید", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                init();
                                            }
                                        }).show();
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
            }
        });

        dialog.show();

    }

    public void ClickFav() {
        if (AppUtill.isNetworkAvailable(this)) {
            Pair<Function<Long, Observable<ErrorExceptionBase>>, Runnable> functionFunctionPair = getFavoriteService();
            ServiceExecute.execute(functionFunctionPair.first.apply(Id))
                    .subscribe(new NtkObserver<ErrorExceptionBase>() {

                        @Override
                        public void onNext(ErrorExceptionBase e) {
                            if (e.IsSuccess) {
                                Toasty.success(AbstractionDetailActivity.this, "با موفقیت ثبت شد").show();
                                functionFunctionPair.second.run();
                            } else {
                                Toasty.error(AbstractionDetailActivity.this, e.ErrorMessage, Toast.LENGTH_LONG, true).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Snackbar.make(layout, "خطای سامانه مجددا تلاش کنید", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    init();
                                }
                            }).show();
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


    public void ClickShare() {
        String st = Preferences.with(this).appVariableInfo().configapp();
        MainResponseDtoModel mcr = new Gson().fromJson(st, MainResponseDtoModel.class);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        String message = createShareMassage();
        shareIntent.putExtra(Intent.EXTRA_TEXT, message + "\n\n\n" + this.getString(R.string.app_name) + "\n" + "لینک دانلود:" + "\n" + mcr.AppUrl);
        shareIntent.setType("text/txt");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        this.startActivity(Intent.createChooser(shareIntent, "به اشتراک گزاری با...."));
    }

    protected abstract String createShareMassage();
}
