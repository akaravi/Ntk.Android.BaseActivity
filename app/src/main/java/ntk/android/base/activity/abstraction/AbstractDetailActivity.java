package ntk.android.base.activity.abstraction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import java9.util.function.Function;
import ntk.android.base.Extras;
import ntk.android.base.R;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.dtomodel.application.MainResponseDtoModel;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.ErrorExceptionBase;
import ntk.android.base.entitymodel.news.NewsContentModel;
import ntk.android.base.services.news.NewsContentService;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.FontManager;
import ntk.android.base.utill.prefrense.Preferences;

public abstract class AbstractDetailActivity<TEntity, TCategory, TComment, TOtherInfo> extends BaseActivity {
    protected TEntity model;
    public long Id = -1;
    public WebView webViewBody;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        initAbstractView();
    }

    private void initAbstractView() {
        setViewListerner(findViewById(R.id.imgBackDetail), (v -> finish()));
        setViewListerner(findViewById(R.id.imgCommentDetail), (v -> ClickCommentAdd()));
        setViewListerner(findViewById(R.id.imgShareDetail), (v -> ClickShare()));
        setViewListerner(findViewById(R.id.imgFavDetail), (v -> ClickFav()));
        webViewBody = findViewById(R.id.WebViewBodyDetail);
        webViewBody.getSettings().setJavaScriptEnabled(true);
        webViewBody.getSettings().setBuiltInZoomControls(true);
        Id = getIntent().getExtras().getLong(Extras.EXTRA_FIRST_ARG);
        getContent();
    }

    public final void setTypeFace(List<TextView> Lbls) {
        for (TextView tv : Lbls) {
            tv.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        }
    }

    private void setViewListerner(View viewById, View.OnClickListener o) {
        if (viewById != null)
            viewById.setOnClickListener(o);
    }

    protected final void getContent() {
        if (AppUtill.isNetworkAvailable(this)) {
            Observable<ErrorException<NewsContentModel>> one = new NewsContentService(this).getOne(Id);
            getOneContentService().apply(Id).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new NtkObserver<ErrorException<TEntity>>() {
                        @Override
                        public void onNext(ErrorException<TEntity> ContentResponse) {
                            if (ContentResponse.IsSuccess) {
                                model = ContentResponse.Item;
                                bindContentData(ContentResponse);
                            } else
                                showError(ContentResponse.ErrorMessage);
                        }

                        @Override
                        public void onError(Throwable e) {
                            showError(e.toString());
                        }

                    });
        } else {
            showError("عدم دسترسی به اینترنت");
        }

    }

    protected final void getContentOtherInfo(long ContentId) {
        if (AppUtill.isNetworkAvailable(this)) {
            getOtherInfoListService().apply(ContentId).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new NtkObserver<ErrorException<TOtherInfo>>() {

                        @Override
                        public void onNext(@NonNull ErrorException<TOtherInfo> ContentOtherInfoResponse) {
                            bindDataOtherInfo(ContentOtherInfoResponse);
                        }

                        @Override
                        public void onError(Throwable e) {
                         showError(e.toString());                        }
                    });
        } else {
            showError("عدم دسترسی به اینترنت");
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

    public void ClickFav() {
        if (AppUtill.isNetworkAvailable(this)) {
            Pair<Function<Long, Observable<ErrorExceptionBase>>, Runnable> functionFunctionPair = getFavoriteService();
            functionFunctionPair.first.apply(Id).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NtkObserver<ErrorExceptionBase>() {

                        @Override
                        public void onNext(ErrorExceptionBase e) {
                            if (e.IsSuccess) {
                                Toasty.success(AbstractDetailActivity.this, "با موفقیت ثبت شد").show();
                                functionFunctionPair.second.run();
                            } else {
                                Toasty.error(AbstractDetailActivity.this, e.ErrorMessage, Toast.LENGTH_LONG, true).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            showError(e.toString());
                        }
                    });
        } else {
            showError("عدم دسترسی به اینترنت");
        }
    }

    protected abstract String createShareMassage();

    protected abstract void bindContentData(ErrorException<TEntity> contentResponse);

    public abstract void bindDataOtherInfo(ErrorException<TOtherInfo> contentOtherInfoResponse);

    protected abstract void showError(String toString);

    protected abstract void setContentView();

    protected abstract void ClickCommentAdd();

    public abstract Function<Long, Observable<ErrorException<TEntity>>> getOneContentService();

    public abstract Function<Long, Observable<ErrorException<TComment>>> getCommentListService();

    public abstract Function<Long, Observable<ErrorException<TOtherInfo>>> getOtherInfoListService();

    public abstract Pair<Function<Long, Observable<ErrorExceptionBase>>, Runnable> getFavoriteService();


}
