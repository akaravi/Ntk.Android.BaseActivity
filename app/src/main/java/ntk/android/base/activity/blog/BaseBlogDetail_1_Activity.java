package ntk.android.base.activity.blog;

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
import ntk.android.base.entitymodel.blog.BlogCategoryModel;
import ntk.android.base.entitymodel.blog.BlogCommentModel;
import ntk.android.base.entitymodel.blog.BlogContentModel;
import ntk.android.base.entitymodel.blog.BlogContentOtherInfoModel;
import ntk.android.base.services.blog.BlogCommentService;
import ntk.android.base.services.blog.BlogContentOtherInfoService;
import ntk.android.base.services.blog.BlogContentService;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.FontManager;

public abstract class BaseBlogDetail_1_Activity extends
        AbstractDetailActivity<BlogContentModel, BlogCategoryModel, BlogCommentModel, BlogContentOtherInfoModel> {
    ProgressBar Progress;
    LinearLayout Loading;
    protected ImageView ImgHeader;
    public RecyclerView RvTab;
    RecyclerView RvComment;
    public RecyclerView Rv;
    protected RatingBar Rate;
    LinearLayout Page;
    CoordinatorLayout layout;
    protected int favoriteDrawableId;
    protected int unFavoriteDrawableId;

    public void setContentView() {
        setContentView(R.layout.base1_detail_activity);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initChild();
    }

    protected abstract void initChild();

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        Progress = findViewById(R.id.progressDetail);

        Loading = findViewById(R.id.rowProgressDetail);
        setTypeFace(new ArrayList() {{
            add(findViewById(R.id.lblTitleDetail));
            add(findViewById(R.id.lblNameCommandDetail));
            add(findViewById(R.id.lblKeySeenDetail));
            add(findViewById(R.id.lblValueSeenDetail));
            add(findViewById(R.id.lblCommentDetail));
            add(findViewById(R.id.lblProgressDetail));
            add(findViewById(R.id.lblMenuDetail));
            add(findViewById(R.id.lblAllMenuDetail));
        }});

        ImgHeader = findViewById(R.id.imgHeaderDetail);
        RvTab = findViewById(R.id.recyclerTabDetail);
        RvComment = findViewById(R.id.recyclerCommentDetail);
        Rv = findViewById(R.id.recyclerMenuDetail);
        Rate = findViewById(R.id.ratingBarDetail);
        Page = findViewById(R.id.PageDetail);
        layout = findViewById(R.id.mainLayoutDetail);

        RvTab.setHasFixedSize(true);
        RvTab.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
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


    protected void setContentRate(ScoreClickDtoModel request) {
        if (AppUtill.isNetworkAvailable(this)) {

            new BlogContentService(this).scoreClick(request).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new NtkObserver<ErrorExceptionBase>() {

                        @Override
                        public void onNext(ErrorExceptionBase biographyContentResponse) {
                            Loading.setVisibility(View.GONE);
                            if (biographyContentResponse.IsSuccess) {
                                Toasty.success(BaseBlogDetail_1_Activity.this, "نظر شمابا موفقیت ثبت گردید").show();
                            } else {
                                Toasty.warning(BaseBlogDetail_1_Activity.this, biographyContentResponse.ErrorMessage).show();
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

    public abstract RecyclerView.Adapter createCommentAdapter(List<BlogCommentModel> listItems);


    private void HandelDataComment(long ContentId) {
        if (AppUtill.isNetworkAvailable(this)) {
            getCommentListService().apply(ContentId).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NtkObserver<ErrorException<BlogCommentModel>>() {
                        @Override
                        public void onNext(@NonNull ErrorException<BlogCommentModel> model) {
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
    public void bindDataOtherInfo(ErrorException<BlogContentOtherInfoModel> model) {
        if (model.ListItems == null || model.ListItems.size() == 0) {
            findViewById(R.id.RowTimeDetail).setVisibility(View.GONE);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            p.weight = 3;
            return;
        }
        List<BlogContentOtherInfoModel> Info = new ArrayList<>();

        for (BlogContentOtherInfoModel ai : model.ListItems) {
            switch (ai.TypeId) {
                case 21:
                    ((TextView) findViewById(R.id.lblAllMenuDetail)).setText(ai.Title);
                    ai.HtmlBody = ai.HtmlBody.replace("<p>", "");
                    ai.HtmlBody = ai.HtmlBody.replace("</p>", "");
                    ((TextView) findViewById(R.id.lblMenuDetail)).setText(Html.fromHtml(ai.HtmlBody));
                    break;
                //todo add later
//                case 22:
//                    Lbls.get(9).setText(ai.Title);
//                    ai.HtmlBody = ai.HtmlBody.replace("<p>", "");
//                    ai.HtmlBody = ai.HtmlBody.replace("</p>", "");
//                    Lbls.get(8).setText(Html.fromHtml(ai.HtmlBody));
//                    break;
//                case 23:
//                    Lbls.get(11).setText(ai.Title);
//                    ai.HtmlBody = ai.HtmlBody.replace("<p>", "");
//                    ai.HtmlBody = ai.HtmlBody.replace("</p>", "");
//                    Lbls.get(10).setText(Html.fromHtml(ai.HtmlBody));
//                    break;
                default:
                    Info.add(ai);
                    break;
            }
        }
        RecyclerView.Adapter adapter = createOtherInfoAdapter(Info);
        RvTab.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    protected abstract RecyclerView.Adapter createOtherInfoAdapter(List<BlogContentOtherInfoModel> info);


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
                Toast.makeText(BaseBlogDetail_1_Activity.this, "لطفا مقادیر را وارد نمایید", Toast.LENGTH_SHORT).show();
            } else {
                if (Txt[1].getText().toString().isEmpty()) {
                    Toast.makeText(BaseBlogDetail_1_Activity.this, "لطفا مقادیر را وارد نمایید", Toast.LENGTH_SHORT).show();
                } else {
                    if (AppUtill.isNetworkAvailable(this)) {
//                        BlogCommentModel add = new BlogCommentModel();
                        String writer = Txt[0].getText().toString();
                        String comment = Txt[1].getText().toString();
                        BlogCommentModel model = new BlogCommentModel();
                        model.Writer = writer;
                        model.Comment = comment;
                        new BlogCommentService(this).add(model).
                                subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new NtkObserver<ErrorException<BlogCommentModel>>() {
                                    @Override
                                    public void onNext(@NonNull ErrorException<BlogCommentModel> e) {
                                        if (e.IsSuccess) {
                                            HandelDataComment(Id);
                                            dialog.dismiss();
                                            Toasty.success(BaseBlogDetail_1_Activity.this, "نظر شما با موفقیت ثبت شد").show();
                                        } else {
                                            dialog.dismiss();
                                            Toasty.warning(BaseBlogDetail_1_Activity.this, e.ErrorMessage).show();
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
    public Function<Long, Observable<ErrorException<BlogContentModel>>> getOneContentService() {
        return new BlogContentService(this)::getOne;
    }

    @Override
    public Function<Long, Observable<ErrorException<BlogCommentModel>>> getCommentListService() {
        return linkLongId -> {
            FilterDataModel Request = new FilterDataModel();
            Filters f = new Filters();
            f.PropertyName = "LinkContentId";
            f.IntValue1 = linkLongId;
            Request.addFilter(f);
            return new BlogCommentService(this).getAll(Request);
        };
    }

    @Override
    public Function<Long, Observable<ErrorException<BlogContentOtherInfoModel>>> getOtherInfoListService() {
        return linkLongId -> {
            return new BlogContentOtherInfoService(this).getAll(otherInfoFilter(linkLongId));
        };
    }

    @Override
    public Pair<Function<Long, Observable<ErrorExceptionBase>>, Runnable> getFavoriteService() {
        Function<Long, Observable<ErrorExceptionBase>> favorite;
        if (model.Favorited)
            favorite = new BlogContentService(this)::removeFavorite;
        else
            favorite = new BlogContentService(this)::addFavorite;
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
    protected void bindContentData(ErrorException<BlogContentModel> model) {
        if (Id > 0) {
            getContentOtherInfo(Id);
            HandelDataComment(Id);
        }
        Loading.setVisibility(View.GONE);
        Page.setVisibility(View.VISIBLE);
        ImageLoader.getInstance().displayImage(model.Item.LinkMainImageIdSrc, ImgHeader);
        ((TextView) findViewById(R.id.lblTitleDetail)).setText(model.Item.Title);
        ((TextView) findViewById(R.id.lblNameCommandDetail)).setText(model.Item.Title);
        ((TextView) findViewById(R.id.lblValueSeenDetail)).setText(String.valueOf(model.Item.ViewCount));
        if (model.Item.Favorited) {
            ((ImageView) findViewById(R.id.imgHeartDetail)).setImageResource(favoriteDrawableId);
        } else
            ((ImageView) findViewById(R.id.imgHeartDetail)).setImageResource(unFavoriteDrawableId);

        double rating = 0.0;
        int sumClick = model.Item.ViewCount;
        if (model.Item.ViewCount == 0) sumClick = 1;
        if (model.Item.ScoreSumPercent / sumClick > 0 && model.Item.ScoreSumPercent / sumClick <= 10) {
            rating = 0.5;
        } else if (model.Item.ScoreSumPercent / sumClick > 10 && model.Item.ScoreSumPercent / sumClick <= 20) {
            rating = 1.0;
        } else if (model.Item.ScoreSumPercent / sumClick > 20 && model.Item.ScoreSumPercent / sumClick <= 30) {
            rating = 1.5;
        } else if (model.Item.ScoreSumPercent / sumClick > 30 && model.Item.ScoreSumPercent / sumClick <= 40) {
            rating = 2.0;
        } else if (model.Item.ScoreSumPercent / sumClick > 40 && model.Item.ScoreSumPercent / sumClick <= 50) {
            rating = 2.5;
        } else if (model.Item.ScoreSumPercent / sumClick > 50 && model.Item.ScoreSumPercent / sumClick <= 60) {
            rating = 3.0;
        } else if (model.Item.ScoreSumPercent / sumClick > 60 && model.Item.ScoreSumPercent / sumClick <= 70) {
            rating = 3.5;
        } else if (model.Item.ScoreSumPercent / sumClick > 70 && model.Item.ScoreSumPercent / sumClick <= 80) {
            rating = 4.0;
        } else if (model.Item.ScoreSumPercent / sumClick > 80 && model.Item.ScoreSumPercent / sumClick <= 90) {
            rating = 4.5;
        } else if (model.Item.ScoreSumPercent / sumClick > 90) {
            rating = 5.0;
        }
        Rate.setRating((float) rating);
        if (model.Item.Body != null)
            webViewBody.loadData("<html dir=\"rtl\" lang=\"\"><body>" + model.Item.Body + "</body></html>", "text/html; charset=utf-8", "UTF-8");
        if (model.Item.Favorited) {
            ((ImageView) findViewById(R.id.imgHeartDetail)).setImageResource(favoriteDrawableId);
        }
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