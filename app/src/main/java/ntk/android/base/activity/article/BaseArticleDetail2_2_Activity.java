package ntk.android.base.activity.article;

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
import io.reactivex.annotations.NonNull;
import java9.util.function.Function;
import ntk.android.base.R;
import ntk.android.base.activity.abstraction.AbstractDetailActivity;
import ntk.android.base.activity.common.VideoPlayerActivity;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.config.ServiceExecute;
import ntk.android.base.dtomodel.core.ScoreClickDtoModel;
import ntk.android.base.entitymodel.article.ArticleCategoryModel;
import ntk.android.base.entitymodel.article.ArticleCommentModel;
import ntk.android.base.entitymodel.article.ArticleContentModel;
import ntk.android.base.entitymodel.article.ArticleContentOtherInfoModel;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.ErrorExceptionBase;
import ntk.android.base.entitymodel.base.FilterDataModel;
import ntk.android.base.entitymodel.base.FilterModel;
import ntk.android.base.event.HtmlBodyEvent;
import ntk.android.base.services.article.ArticleCommentService;
import ntk.android.base.services.article.ArticleContentOtherInfoService;
import ntk.android.base.services.article.ArticleContentService;
import ntk.android.base.services.base.CmsApiScoreApi;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.FontManager;

public abstract class BaseArticleDetail2_2_Activity extends AbstractDetailActivity<ArticleContentModel, ArticleCategoryModel, ArticleCommentModel, ArticleContentOtherInfoModel> {
    ProgressBar Progress;
    LinearLayout Loading;
    protected ImageView ImgHeader;
    RecyclerView RvSimilarArticle;
    RecyclerView RvSimilarCategory;
    public RecyclerView RvTab;
    RecyclerView RvComment;


    protected RatingBar Rate;
    LinearLayout Page;
    CoordinatorLayout layout;
    protected int favoriteDrawableId;
    protected int unFavoriteDrawableId;

    public void setContentView() {
        setContentView(R.layout.base2_detail_activity);
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
        setViewListener(findViewById(R.id.RowGalleryDetail), v -> ClickGalley());
        setViewListener(findViewById(R.id.playDetail), v -> onPlayClick());
        setViewListener(findViewById(R.id.calDetail), v -> onCalClick());
        ImgHeader = findViewById(R.id.imgHeaderDetail);
        RvTab = findViewById(R.id.recyclerTabDetail);
        RvComment = findViewById(R.id.recyclerCommentDetail);
        Rate = findViewById(R.id.ratingBarDetail);
        Page = findViewById(R.id.PageDetail);
        layout = findViewById(R.id.mainLayoutDetail);
        RvSimilarArticle = findViewById(R.id.recyclerMenuDetail);
        RvSimilarCategory = findViewById(R.id.recyclerMenuTwoDetail);
        RvTab.setHasFixedSize(true);
        RvTab.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        Loading.setVisibility(View.VISIBLE);

        RvComment.setHasFixedSize(true);
        RvComment.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        RvSimilarArticle.setHasFixedSize(true);
        RvSimilarArticle.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));

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

    protected abstract void onCalClick();

    protected abstract void onPlayClick();

    protected abstract void ClickGalley();

    @Override
    protected void showError(String toString, Runnable onTryingAgain) {
        Snackbar.make(layout, R.string.error_raised, Snackbar.LENGTH_INDEFINITE).setAction(R.string.try_again, new View.OnClickListener() {
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

            ServiceExecute.execute(new ArticleContentService(this).scoreClick(request))
                    .subscribe(new NtkObserver<ErrorExceptionBase>() {

                        @Override
                        public void onNext(ErrorExceptionBase biographyContentResponse) {
                            Loading.setVisibility(View.GONE);
                            if (biographyContentResponse.IsSuccess) {
                                Toasty.success(BaseArticleDetail2_2_Activity.this, R.string.success_comment).show();
                            } else {
                                Toasty.warning(BaseArticleDetail2_2_Activity.this, biographyContentResponse.ErrorMessage).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Loading.setVisibility(View.GONE);
                            Snackbar.make(layout, R.string.error_raised, Snackbar.LENGTH_INDEFINITE).setAction(R.string.try_again, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //todo init();
                                }
                            }).show();
                        }
                    });
        } else {
            Loading.setVisibility(View.GONE);
            Snackbar.make(layout, R.string.per_no_net, Snackbar.LENGTH_INDEFINITE).setAction(R.string.try_again, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //todo init();
                }
            }).show();
        }
    }

    public abstract RecyclerView.Adapter createCommentAdapter(List<ArticleCommentModel> listItems);


    private void HandelDataComment(long ContentId) {
        if (AppUtill.isNetworkAvailable(this)) {
            ServiceExecute.execute(getCommentListService().apply(ContentId))
                    .subscribe(new NtkObserver<ErrorException<ArticleCommentModel>>() {
                        @Override
                        public void onNext(@NonNull ErrorException<ArticleCommentModel> model) {
                            if (model.ListItems.size() == 0) {
                                findViewById(R.id.RowCommentDetail).setVisibility(View.GONE);
                                RvComment.setVisibility(View.GONE);
                            } else {
                                RecyclerView.Adapter adapter = createCommentAdapter(model.ListItems);
                                RvComment.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                findViewById(R.id.RowCommentDetail).setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Snackbar.make(layout, R.string.error_raised, Snackbar.LENGTH_INDEFINITE).setAction(R.string.try_again, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //todo add to  init();
                                }
                            }).show();
                        }
                    });
        } else {
            findViewById(R.id.lblCommentDetail).setVisibility(View.GONE);
            Snackbar.make(layout, R.string.per_no_net, Snackbar.LENGTH_INDEFINITE).setAction(R.string.try_again, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //todo add to  init();
                }
            }).show();
        }
    }


    @NotNull
    protected FilterModel otherInfoFilter(long ContentId) {
        FilterModel Request = new FilterModel();
        FilterDataModel f = new FilterDataModel();
        f.PropertyName = "LinkContentId";
        f.setIntValue(ContentId);
        Request.addFilter(f);
        return Request;
    }


    @Override
    public void bindDataOtherInfo(ErrorException<ArticleContentOtherInfoModel> model) {
        if (model.ListItems == null || model.ListItems.size() == 0) {
            findViewById(R.id.RowTimeDetail).setVisibility(View.GONE);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            p.weight = 3;
            return;
        }
        findViewById(R.id.RowTimeDetail).setVisibility(View.GONE);
        List<ArticleContentOtherInfoModel> Info = new ArrayList<>();
        ArticleContentOtherInfoModel i = new ArticleContentOtherInfoModel();
        i.Title = getString(R.string.article_custom_title);
        i.TypeId = 0;
        i.HtmlBody = this.model.Body;
        Info.add(i);

        for (ArticleContentOtherInfoModel ai : model.ListItems) {
            if (ai.TypeId == null)
                Info.add(ai);
            else
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
        RecyclerView.Adapter adapter = createOtherInfoAdapter(Info);
        RvTab.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    protected abstract RecyclerView.Adapter createOtherInfoAdapter(List<ArticleContentOtherInfoModel> info);


    protected void ClickCommentAdd() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        dialog.setContentView(R.layout.dialog_comment_add);
        TextView Lbl = dialog.findViewById(R.id.lblTitleDialogAddComment);
        Lbl.setTypeface(FontManager.T1_Typeface(this));

        EditText[] Txt = new EditText[2];

        Txt[0] = dialog.findViewById(R.id.txtNameDialogAddComment);
        Txt[0].setTypeface(FontManager.T1_Typeface(this));

        Txt[1] = dialog.findViewById(R.id.txtContentDialogAddComment);
        Txt[1].setTypeface(FontManager.T1_Typeface(this));

        Button Btn = dialog.findViewById(R.id.btnSubmitDialogCommentAdd);
        Btn.setTypeface(FontManager.T1_Typeface(this));

        Btn.setOnClickListener(v -> {
            if (Txt[0].getText().toString().isEmpty()) {
                Toast.makeText(BaseArticleDetail2_2_Activity.this, R.string.per_insert_num, Toast.LENGTH_SHORT).show();
            } else {
                if (Txt[1].getText().toString().isEmpty()) {
                    Toast.makeText(BaseArticleDetail2_2_Activity.this, R.string.per_insert_num, Toast.LENGTH_SHORT).show();
                } else {
                    if (AppUtill.isNetworkAvailable(this)) {
//                        ArticleCommentModel add = new ArticleCommentModel();
                        String writer = Txt[0].getText().toString();
                        String comment = Txt[1].getText().toString();
                        ArticleCommentModel model = new ArticleCommentModel();
                        model.Writer = writer;
                        model.Comment = comment;
                        ServiceExecute.execute(new ArticleCommentService(this).add(model))
                                .subscribe(new NtkObserver<ErrorException<ArticleCommentModel>>() {
                                    @Override
                                    public void onNext(@NonNull ErrorException<ArticleCommentModel> e) {
                                        if (e.IsSuccess) {
                                            HandelDataComment(Id);
                                            dialog.dismiss();
                                            Toasty.success(BaseArticleDetail2_2_Activity.this, R.string.success_comment).show();
                                        } else {
                                            dialog.dismiss();
                                            Toasty.warning(BaseArticleDetail2_2_Activity.this, e.ErrorMessage).show();
                                        }
                                    }

                                    @Override
                                    public void onError(@NonNull Throwable e) {
                                        Snackbar.make(layout, R.string.error_raised, Snackbar.LENGTH_INDEFINITE).setAction(R.string.try_again, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                //todo add
                                            }
                                        }).show();
                                    }
                                });
                    } else {
                        Snackbar.make(layout, R.string.per_no_net, Snackbar.LENGTH_INDEFINITE).setAction(R.string.try_again, new View.OnClickListener() {
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
    public Function<Long, Observable<ErrorException<ArticleContentModel>>> getOneContentService() {
        return new ArticleContentService(this)::getOne;
    }

    @Override
    public Function<Long, Observable<ErrorException<ArticleContentModel>>> getSimilarCategoryService() {
        return (id) -> {
            FilterModel request = new FilterModel();
            return new ArticleContentService(this).getAllWithCategoryUsedInContent(id, request);
        };
    }

    @Override
    public Function<Long, Observable<ErrorException<ArticleContentModel>>> getSimilarContentService() {
        return (id) -> {
            FilterModel Request = new FilterModel();
            FilterDataModel f = new FilterDataModel();
            f.PropertyName = "LinkContentId";
            f.setIntValue(id);
            Request.addFilter(f);
            return new ArticleContentService(this).getAllWithSimilarsId(id, Request);
        };
    }

    @Override
    public Function<Long, Observable<ErrorException<ArticleCommentModel>>> getCommentListService() {
        return linkLongId -> {
            FilterModel Request = new FilterModel();
            FilterDataModel f = new FilterDataModel();
            f.PropertyName = "LinkContentId";
            f.setIntValue(linkLongId);
            Request.addFilter(f);
            return new ArticleCommentService(this).getAll(Request);
        };
    }

    @Override
    public Function<Long, Observable<ErrorException<ArticleContentOtherInfoModel>>> getOtherInfoListService() {
        return linkLongId -> new ArticleContentOtherInfoService(this).getAll(otherInfoFilter(linkLongId));
    }

    @Override
    public Pair<Function<Long, Observable<ErrorExceptionBase>>, Runnable> getFavoriteService() {
        Function<Long, Observable<ErrorExceptionBase>> favorite;
        if (model.Favorited)
            favorite = new ArticleContentService(this)::removeFavorite;
        else
            favorite = new ArticleContentService(this)::addFavorite;
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
    protected void bindContentData(ErrorException<ArticleContentModel> model) {
        getSimilarContent(Id);
        getSimilarCategoryContent(Id);
        if (Id > 0) {
            getContentOtherInfo(Id);
            HandelDataComment(Id);
        }
        if (model.Item.LinkFileMovieIdSrc != null && !model.Item.LinkFileMovieIdSrc.equalsIgnoreCase("")) {
            View videoPlay = findViewById(R.id.videPlayback);
            videoPlay.setVisibility(View.VISIBLE);
            videoPlay.setOnClickListener(v -> VideoPlayerActivity.VIDEO(this, model.Item.LinkFileMovieIdSrc));
        } if (model.Item.LinkFilePodcastIdSrc != null && !model.Item.LinkFilePodcastIdSrc.equalsIgnoreCase("")) {
            View videoPlay = findViewById(R.id.musicPlayback);
            videoPlay.setVisibility(View.VISIBLE);
            videoPlay.setOnClickListener(v -> VideoPlayerActivity.PODCAST(this, model.Item.LinkFilePodcastIdSrc));
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
            ServiceExecute.execute(getSimilarContentService().apply(id))
                    .subscribe(new NtkObserver<ErrorException<ArticleContentModel>>() {
                        @Override
                        public void onNext(@NonNull ErrorException<ArticleContentModel> response) {
                            if (response.ListItems.size() == 0) {
                                findViewById(R.id.RowSimilaryDetail).setVisibility(View.GONE);
                                findViewById(R.id.RowSimilaryDetail).setVisibility(View.GONE);
                            } else {
                                RecyclerView.Adapter adapter = createSimilarContentAdapter(response.ListItems);
                                RvSimilarArticle.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                findViewById(R.id.RowSimilaryDetail).setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            findViewById(R.id.RowSimilaryDetail).setVisibility(View.GONE);
                        }
                    });
        } else {
            Snackbar.make(layout, R.string.per_no_net, Snackbar.LENGTH_INDEFINITE).setAction(R.string.try_again, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                  todo  init();
                }
            }).show();
        }
    }

    protected abstract RecyclerView.Adapter createSimilarContentAdapter(List<ArticleContentModel> listItems);


    private void getSimilarCategoryContent(long id) {
        if (AppUtill.isNetworkAvailable(this)) {
            FilterModel request = new FilterModel();
            ServiceExecute.execute(new ArticleContentService(this).getAllWithCategoryUsedInContent(id, request))
                    .subscribe(new NtkObserver<ErrorException<ArticleContentModel>>() {
                        @Override
                        public void onNext(@NonNull ErrorException<ArticleContentModel> response) {
                            if (response.ListItems.size() == 0) {
                                findViewById(R.id.RowSimilaryCategoryDetail).setVisibility(View.GONE);
                            } else {
                                findViewById(R.id.RowSimilaryCategoryDetail).setVisibility(View.VISIBLE);
                                RecyclerView.Adapter adapter = createSimilarCategoryAdapter(response.ListItems);
                                RvSimilarCategory.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {

                        }
                    });
        } else {
            Snackbar.make(layout, R.string.per_no_net, Snackbar.LENGTH_INDEFINITE).setAction(R.string.try_again, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //todo init();
                }
            }).show();
        }
    }

    protected abstract RecyclerView.Adapter createSimilarCategoryAdapter(List<ArticleContentModel> listItems);

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
