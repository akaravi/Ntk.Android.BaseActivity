package ntk.android.base.activity.biography;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Html;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
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
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import java9.util.function.Function;
import ntk.android.base.R;
import ntk.android.base.activity.abstraction.AbstractDetailActivity;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.dtomodel.core.ScoreClickDtoModel;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.ErrorExceptionBase;
import ntk.android.base.entitymodel.base.FilterDataModel;
import ntk.android.base.entitymodel.base.Filters;
import ntk.android.base.entitymodel.biography.BiographyCategoryModel;
import ntk.android.base.entitymodel.biography.BiographyCommentModel;
import ntk.android.base.entitymodel.biography.BiographyContentModel;
import ntk.android.base.entitymodel.biography.BiographyContentOtherInfoModel;
import ntk.android.base.event.HtmlBodyEvent;
import ntk.android.base.services.base.CmsApiScoreApi;
import ntk.android.base.services.biography.BiographyCommentService;
import ntk.android.base.services.biography.BiographyContentOtherInfoService;
import ntk.android.base.services.biography.BiographyContentService;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.FontManager;

public abstract class BaseBiographyDetail_1_Activity extends AbstractDetailActivity<BiographyContentModel, BiographyCategoryModel, BiographyCommentModel, BiographyContentOtherInfoModel> {
    ProgressBar Progress;
    LinearLayout Loading;
    protected ImageView ImgHeader;
    RecyclerView RvSimilarBiography;
    RecyclerView RvSimilarCategory;
    public RecyclerView RvTab;
    RecyclerView RvComment;


    public RecyclerView Rv;
    protected RatingBar Rate;
    LinearLayout Page;
    CoordinatorLayout layout;
    protected int favoriteDrawableId = R.drawable.ic_fav_full;
    protected int unFavoriteDrawableId = R.drawable.ic_fav;

    public void setContentView() {
        setContentView(R.layout.base2_detail_activity);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initChild();
    }

    protected void initChild(){};

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        Progress = findViewById(R.id.progressDetail);

        Loading = findViewById(R.id.rowProgressDetail);
        setTypeFace(new ArrayList() {{
            add(findViewById(R.id.lblTitleDetail));
            add(findViewById(R.id.lblNameCommandDetail));
            add(findViewById(R.id.lblKeySeenDetail));
            add(findViewById(R.id.lblValueSeenDetail));
            add(findViewById(R.id.lblPhotoExtraDetail));
            add(findViewById(R.id.lblCalDetail));
            add(findViewById(R.id.lblTimerOne));
            add(findViewById(R.id.lblTimerTwo));
            add(findViewById(R.id.lblTimerThree));
            add(findViewById(R.id.lblTimerFour));
            add(findViewById(R.id.lblTimerFive));
            add(findViewById(R.id.lblTimerSix));
            add(findViewById(R.id.lblMenuDetail));
            add(findViewById(R.id.lblMenuTwoDetail));
            add(findViewById(R.id.lblCommentDetail));
            add(findViewById(R.id.lblProgressDetail));
        }});

        ImgHeader = findViewById(R.id.imgHeaderDetail);
        RvTab = findViewById(R.id.recyclerTabDetail);
        RvComment = findViewById(R.id.recyclerCommentDetail);
        Rv = findViewById(R.id.recyclerMenuDetail);
        Rate = findViewById(R.id.ratingBarDetail);
        Page = findViewById(R.id.PageDetail);
        layout = findViewById(R.id.mainLayoutDetail);
        RvSimilarBiography = findViewById(R.id.recyclerMenuDetail);
        RvSimilarCategory = findViewById(R.id.recyclerMenuTwoDetail);
        RvTab.setHasFixedSize(true);
        RvTab.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        Loading.setVisibility(View.VISIBLE);

        RvComment.setHasFixedSize(true);
        RvComment.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        RvSimilarBiography.setHasFixedSize(true);
        RvSimilarBiography.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));

        RvSimilarCategory.setHasFixedSize(true);
        RvSimilarCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));

        Rate.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (!fromUser) return;
            ScoreClickDtoModel request = new ScoreClickDtoModel();
            request.Id = Id;
            request.ScorePercent = ((int) (rating / .5)) * 10;
            setContentRate(request);
        });
    }


    @Override
    protected void showError(String toString) {
        Snackbar.make(layout, "خطای سامانه مجددا تلاش کنید", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContent();
            }
        }).show();
    }

    @Subscribe
    public void EventHtmlBody(HtmlBodyEvent event) {
        webViewBody.loadData("<html dir=\"rtl\" lang=\"\"><body>" + event.GetMessage() + "</body></html>", "text/html; charset=utf-8", "UTF-8");
    }

    protected void setContentRate(ScoreClickDtoModel request) {
        if (AppUtill.isNetworkAvailable(this)) {

            new BiographyContentService(this).scoreClick(request).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new NtkObserver<ErrorExceptionBase>() {

                        @Override
                        public void onNext(ErrorExceptionBase biographyContentResponse) {
                            Loading.setVisibility(View.GONE);
                            if (biographyContentResponse.IsSuccess) {
                                Toasty.success(BaseBiographyDetail_1_Activity.this, "نظر شمابا موفقیت ثبت گردید").show();
                            } else {
                                Toasty.warning(BaseBiographyDetail_1_Activity.this, biographyContentResponse.ErrorMessage).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Loading.setVisibility(View.GONE);
                            Snackbar.make(layout, "خطای سامانه مجددا تلاش کنید", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //todo init();
                                }
                            }).show();
                        }
                    });
        } else {
            Loading.setVisibility(View.GONE);
            Snackbar.make(layout, "عدم دسترسی به اینترنت", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //todo init();
                }
            }).show();
        }
    }

    public abstract RecyclerView.Adapter createCommentAdapter(List<BiographyCommentModel> listItems);


    private void HandelDataComment(long ContentId) {
        if (AppUtill.isNetworkAvailable(this)) {
            getCommentListService().apply(ContentId).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NtkObserver<ErrorException<BiographyCommentModel>>() {
                        @Override
                        public void onNext(@NonNull ErrorException<BiographyCommentModel> model) {
                            if (model.ListItems.size() == 0) {
                                findViewById(R.id.RowCommentDetail).setVisibility(View.GONE);
                                RvComment.setVisibility(View.GONE);
                            } else {
                                RvComment.setVisibility(View.VISIBLE);
                                RecyclerView.Adapter adapter = createCommentAdapter(model.ListItems);
                                RvComment.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                findViewById(R.id.RowCommentDetail).setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Snackbar.make(layout, "خطای سامانه مجددا تلاش کنید", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //todo add to  init();
                                }
                            }).show();
                        }
                    });
        } else {
            findViewById(R.id.lblCommentDetail).setVisibility(View.GONE);
            Snackbar.make(layout, "عدم دسترسی به اینترنت", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //todo add to  init();
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


    @Override
    public void bindDataOtherInfo(ErrorException<BiographyContentOtherInfoModel> model) {
        List<BiographyContentOtherInfoModel> Info = new ArrayList<>();
        if (this.model.Description != null) {
            BiographyContentOtherInfoModel i = new BiographyContentOtherInfoModel();
            i.Title = "توضیحات";
            i.TypeId = 0;
            i.HtmlBody = this.model.Description;
            Info.add(i);
        }
        if (this.model.Body != null) {
            BiographyContentOtherInfoModel i1 = new BiographyContentOtherInfoModel();
            i1.Title = "بیوگرافی";
            i1.TypeId = 0;
            i1.HtmlBody = this.model.Body;
            Info.add(i1);
        }

        for (BiographyContentOtherInfoModel ai : model.ListItems) {
            switch (ai.TypeId) {
                case 21:
                    ((TextView) findViewById(R.id.lblTimerTwo)).setText(ai.Title);
                    ai.HtmlBody = ai.HtmlBody.replace("<p>", "");
                    ai.HtmlBody = ai.HtmlBody.replace("</p>", "");
                    ((TextView) findViewById(R.id.lblTimerOne)).setText(Html.fromHtml(ai.HtmlBody));
                    findViewById(R.id.RowTimeDetail).setVisibility(View.VISIBLE);
                    break;
                case 22:
                    ((TextView) findViewById(R.id.lblTimerFour)).setText(ai.Title);
                    ai.HtmlBody = ai.HtmlBody.replace("<p>", "");
                    ai.HtmlBody = ai.HtmlBody.replace("</p>", "");
                    ((TextView) findViewById(R.id.lblTimerThree)).setText(Html.fromHtml(ai.HtmlBody));
                    findViewById(R.id.RowTimeDetail).setVisibility(View.VISIBLE);
                    break;
                case 23:
                    ((TextView) findViewById(R.id.lblTimerSix)).setText(ai.Title);
                    ai.HtmlBody = ai.HtmlBody.replace("<p>", "");
                    ai.HtmlBody = ai.HtmlBody.replace("</p>", "");
                    ((TextView) findViewById(R.id.lblTimerFive)).setText(Html.fromHtml(ai.HtmlBody));
                    findViewById(R.id.RowTimeDetail).setVisibility(View.VISIBLE);
                    break;
                default:
                    Info.add(ai);
                    break;
            }
        }
        if (this.model.Source != null) {
            BiographyContentOtherInfoModel i2 = new BiographyContentOtherInfoModel();
            i2.Title = "منبع";
            i2.TypeId = 0;
            i2.HtmlBody = this.model.Source;
            Info.add(i2);
        }
        RecyclerView.Adapter adapter = createOtherInfoAdapter(Info);
        RvTab.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    protected abstract RecyclerView.Adapter createOtherInfoAdapter(List<BiographyContentOtherInfoModel> info);


    protected void ClickCommentAdd() {
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
                Toast.makeText(BaseBiographyDetail_1_Activity.this, "لطفا مقادیر را وارد نمایید", Toast.LENGTH_SHORT).show();
            } else {
                if (Txt[1].getText().toString().isEmpty()) {
                    Toast.makeText(BaseBiographyDetail_1_Activity.this, "لطفا مقادیر را وارد نمایید", Toast.LENGTH_SHORT).show();
                } else {
                    if (AppUtill.isNetworkAvailable(this)) {
//                        BiographyCommentModel add = new BiographyCommentModel();
                        String writer = Txt[0].getText().toString();
                        String comment = Txt[1].getText().toString();
                        BiographyCommentModel model = new BiographyCommentModel();
                        model.Writer = writer;
                        model.Comment = comment;
                        new BiographyCommentService(this).add(model).
                                subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new NtkObserver<ErrorException<BiographyCommentModel>>() {
                                    @Override
                                    public void onNext(@NonNull ErrorException<BiographyCommentModel> e) {
                                        if (e.IsSuccess) {
                                            HandelDataComment(Id);
                                            dialog.dismiss();
                                            Toasty.success(BaseBiographyDetail_1_Activity.this, "نظر شما با موفقیت ثبت شد").show();
                                        } else {
                                            dialog.dismiss();
                                            Toasty.warning(BaseBiographyDetail_1_Activity.this, e.ErrorMessage).show();
                                        }
                                    }

                                    @Override
                                    public void onError(@NonNull Throwable e) {
                                        Snackbar.make(layout, "خطای سامانه مجددا تلاش کنید", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                //todo add
                                            }
                                        }).show();
                                    }
                                });
                    } else {
                        Snackbar.make(layout, "عدم دسترسی به اینترنت", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //todo add instead of init();
                            }
                        }).show();
                    }
                }
            }
        });

        dialog.show();

    }

    @Override
    public Function<Long, Observable<ErrorException<BiographyContentModel>>> getOneContentService() {
        return new BiographyContentService(this)::getOne;
    }

    @Override
    public Function<Long, Observable<ErrorException<BiographyContentModel>>> getSimilarCategoryService() {
        return (id) -> {
            FilterDataModel request = new FilterDataModel();
            return new BiographyContentService(this).getAllWithCategoryUsedInContent(id, request);
        };
    }

    @Override
    public Function<Long, Observable<ErrorException<BiographyContentModel>>> getSimilarContentService() {
        return (id) -> {
            FilterDataModel Request = new FilterDataModel();
            Filters f = new Filters();
            f.PropertyName = "LinkContentId";
            f.IntValue1 = id;
            Request.addFilter(f);
            return new BiographyContentService(this).getAllWithSimilarsId(id, Request);
        };
    }

    @Override
    public Function<Long, Observable<ErrorException<BiographyCommentModel>>> getCommentListService() {
        return linkLongId -> {
            FilterDataModel Request = new FilterDataModel();
            Filters f = new Filters();
            f.PropertyName = "LinkContentId";
            f.IntValue1 = linkLongId;
            Request.addFilter(f);
            return new BiographyCommentService(this).getAll(Request);
        };
    }

    @Override
    public Function<Long, Observable<ErrorException<BiographyContentOtherInfoModel>>> getOtherInfoListService() {
        return linkLongId -> new BiographyContentOtherInfoService(this).getAll(otherInfoFilter(linkLongId));
    }

    @Override
    public Pair<Function<Long, Observable<ErrorExceptionBase>>, Runnable> getFavoriteService() {
        Function<Long, Observable<ErrorExceptionBase>> favorite;
        if (model.Favorited)
            favorite = new BiographyContentService(this)::removeFavorite;
        else
            favorite = new BiographyContentService(this)::addFavorite;
        return new Pair<>(favorite, () -> {
            model.Favorited = !model.Favorited;
            if (model.Favorited) {

                ((ImageView) findViewById(ntk.android.base.R.id.imgHeartDetail)).setImageResource(favoriteDrawableId);
            } else {

                ((ImageView) findViewById(ntk.android.base.R.id.imgHeartDetail)).setImageResource(unFavoriteDrawableId);
            }
        });
    }


    @Override
    protected void bindContentData(ErrorException<BiographyContentModel> model) {
        getSimilarContent(Id);
        getSimilarCategoryContent(Id);
        if (Id > 0) {
            getContentOtherInfo(Id);
            HandelDataComment(Id);
        }
        Loading.setVisibility(View.GONE);
        Page.setVisibility(View.VISIBLE);
        double rating = CmsApiScoreApi.CONVERT_TO_RATE(model.Item.ViewCount, model.Item.ScoreSumPercent);
        Rate.setRating((float) rating);
        ImageLoader.getInstance().displayImage(model.Item.LinkMainImageIdSrc, ImgHeader);
        ((TextView) findViewById(R.id.lblTitleDetail)).setText(model.Item.Title);
        ((TextView) findViewById(R.id.lblNameCommandDetail)).setText(model.Item.Title);
        ((TextView) findViewById(R.id.lblValueSeenDetail)).setText(String.valueOf(model.Item.ViewCount));
        if (model.Item.Favorited) {
            ((ImageView) findViewById(R.id.imgHeartDetail)).setImageResource(favoriteDrawableId);
        }
    }

    private void getSimilarContent(long id) {
        if (AppUtill.isNetworkAvailable(this)) {
            getSimilarContentService().apply(id).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new NtkObserver<ErrorException<BiographyContentModel>>() {
                        @Override
                        public void onNext(@NonNull ErrorException<BiographyContentModel> response) {
                            if (response.ListItems.size() == 0) {
                                findViewById(R.id.RowSimilaryDetail).setVisibility(View.GONE);
                                RvSimilarBiography.setVisibility(View.GONE);
                            } else {
                                RecyclerView.Adapter adapter = createSimilarContentAdapter(response.ListItems);
                                RvSimilarBiography.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                findViewById(R.id.RowSimilaryDetail).setVisibility(View.VISIBLE);
                                RvSimilarBiography.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Snackbar.make(layout, "خطای سامانه مجددا تلاش کنید", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // todo init();
                                }
                            }).show();
                        }
                    });
        } else {
            Snackbar.make(layout, "عدم دسترسی به اینترنت", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                  todo  init();
                }
            }).show();
        }
    }

    protected abstract RecyclerView.Adapter createSimilarContentAdapter(List<BiographyContentModel> listItems);


    private void getSimilarCategoryContent(long id) {
        if (AppUtill.isNetworkAvailable(this)) {
            getSimilarCategoryService().apply(id).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new NtkObserver<ErrorException<BiographyContentModel>>() {
                        @Override
                        public void onNext(@NonNull ErrorException<BiographyContentModel> response) {
                            if (response.ListItems.size() == 0) {
                                findViewById(R.id.RowSimilaryCategoryDetail).setVisibility(View.GONE);
                                RvSimilarCategory.setVisibility(View.GONE);
                            } else {
                                RvSimilarCategory.setVisibility(View.VISIBLE);
                                findViewById(R.id.RowSimilaryCategoryDetail).setVisibility(View.VISIBLE);
                                RecyclerView.Adapter adapter = createSimilarCategoryAdapter(response.ListItems);
                                RvSimilarCategory.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Snackbar.make(layout, "خطای سامانه مجددا تلاش کنید", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //todo init();
                                }
                            }).show();
                        }
                    });
        } else {
            Snackbar.make(layout, "عدم دسترسی به اینترنت", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //todo init();
                }
            }).show();
        }
    }

    protected abstract RecyclerView.Adapter createSimilarCategoryAdapter(List<BiographyContentModel> listItems);

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


    @Override
    protected String createShareMassage() {
        String message = model.Title + "\n" + model.Description + "\n";
        if (model.Body != null) {
            message = message + Html.fromHtml(model.Body
                    .replace("<p>", "")
                    .replace("</p>", ""));
        }
        return message;
    }
}
