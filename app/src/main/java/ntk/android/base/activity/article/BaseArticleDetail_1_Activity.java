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
import ntk.android.base.services.article.ArticleCommentService;
import ntk.android.base.services.article.ArticleContentOtherInfoService;
import ntk.android.base.services.article.ArticleContentService;
import ntk.android.base.services.base.CmsApiScoreApi;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.FontManager;

public abstract class BaseArticleDetail_1_Activity extends
        AbstractDetailActivity<ArticleContentModel, ArticleCategoryModel, ArticleCommentModel, ArticleContentOtherInfoModel> {
    ProgressBar Progress;
    LinearLayout Loading;
    protected ImageView ImgHeader;
    public RecyclerView RvTab;
    RecyclerView RvComment;
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

    protected void initChild() {
        favoriteDrawableId = R.drawable.ic_fav_full;
        unFavoriteDrawableId = R.drawable.ic_fav;
    }

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
    protected void showError(String toString, Runnable onTryingAgain) {
        Snackbar.make(layout, R.string.error_raised, Snackbar.LENGTH_INDEFINITE).setAction(R.string.try_again, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContent();
            }
        }).show();
    }

    @Override
    protected void showErrorDialog(String toString, Runnable onTryingAgain) {

    }

    protected void setContentRate(ScoreClickDtoModel request) {
        if (AppUtill.isNetworkAvailable(this)) {

            ServiceExecute.execute(new ArticleContentService(this).scoreClick(request))
                    .subscribe(new NtkObserver<ErrorExceptionBase>() {

                        @Override
                        public void onNext(ErrorExceptionBase biographyContentResponse) {
                            Loading.setVisibility(View.GONE);
                            if (biographyContentResponse.IsSuccess) {
                                Toasty.success(BaseArticleDetail_1_Activity.this, R.string.success_comment).show();
                            } else {
                                Toasty.warning(BaseArticleDetail_1_Activity.this, biographyContentResponse.ErrorMessage).show();
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
                            if (model.IsSuccess && !model.ListItems.isEmpty()) {
                                findViewById(R.id.lblCommentDetail).setVisibility(View.VISIBLE);
                                RecyclerView.Adapter commentAdapter = createCommentAdapter(model.ListItems);
                                RvComment.setAdapter(commentAdapter);
                                commentAdapter.notifyDataSetChanged();
                            } else {
                                findViewById(R.id.lblCommentDetail).setVisibility(View.GONE);
                                RvComment.setVisibility(View.GONE);
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


    @NonNull
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
        List<ArticleContentOtherInfoModel> Info = new ArrayList<>();

        for (ArticleContentOtherInfoModel ai : model.ListItems) {
            if (ai.TypeId == null)
                Info.add(ai);
            else
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
                Toast.makeText(BaseArticleDetail_1_Activity.this, R.string.per_insert_num, Toast.LENGTH_SHORT).show();
            } else {
                if (Txt[1].getText().toString().isEmpty()) {
                    Toast.makeText(BaseArticleDetail_1_Activity.this, R.string.per_insert_num, Toast.LENGTH_SHORT).show();
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
                                            Toasty.success(BaseArticleDetail_1_Activity.this, R.string.success_comment).show();
                                        } else {
                                            dialog.dismiss();
                                            Toasty.warning(BaseArticleDetail_1_Activity.this, e.ErrorMessage).show();
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
    public Function<Long, Observable<ErrorException<ArticleContentModel>>> getSimilarCategoryService() {
        throw new RuntimeException("getSimilarCategoryService not implement in  Article base activity");
    }

    @Override
    public Function<Long, Observable<ErrorException<ArticleContentModel>>> getSimilarContentService() {
        throw new RuntimeException("getSimilarContentService not implement in  Article base activity");
    }

    @Override
    public Function<Long, Observable<ErrorException<ArticleContentOtherInfoModel>>> getOtherInfoListService() {
        return linkLongId -> {
            return new ArticleContentOtherInfoService(this).getAll(otherInfoFilter(linkLongId));
        };
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

                ((ImageView) findViewById(R.id.imgHeartDetail)).setImageResource(favoriteDrawableId);
            } else {

                ((ImageView) findViewById(R.id.imgHeartDetail)).setImageResource(unFavoriteDrawableId);
            }
        });
    }


    @Override
    protected void bindContentData(ErrorException<ArticleContentModel> model) {
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
        ImageLoader.getInstance().displayImage(model.Item.LinkMainImageIdSrc, ImgHeader);
        ((TextView) findViewById(R.id.lblTitleDetail)).setText(model.Item.Title);
        ((TextView) findViewById(R.id.lblNameCommandDetail)).setText(model.Item.Title);
        ((TextView) findViewById(R.id.lblValueSeenDetail)).setText(String.valueOf(model.Item.ViewCount));
        if (model.Item.Favorited) {
            ((ImageView) findViewById(R.id.imgHeartDetail)).setImageResource(favoriteDrawableId);
        } else
            ((ImageView) findViewById(R.id.imgHeartDetail)).setImageResource(unFavoriteDrawableId);

        double rating = CmsApiScoreApi.CONVERT_TO_RATE(model.Item.ViewCount, model.Item.ScoreSumPercent);
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
