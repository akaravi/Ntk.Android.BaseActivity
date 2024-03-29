package ntk.android.base.activity.abstraction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import java9.util.function.Function;
import ntk.android.base.Extras;
import ntk.android.base.R;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.appclass.UpdateClass;
import ntk.android.base.config.ErrorExceptionObserver;
import ntk.android.base.config.GenericErrors;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.config.ServiceExecute;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.ErrorExceptionBase;
import ntk.android.base.utill.AppUtil;
import ntk.android.base.utill.FontManager;
import ntk.android.base.utill.prefrense.Preferences;

public abstract class AbstractDetailActivity<TEntity, TCategory, TComment, TOtherInfo> extends BaseActivity {
    protected TEntity model;
    public long Id = -1;
    public WebView webViewBody;
    public UpdateClass updateInfo ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateInfo = Preferences.with(this).appVariableInfo().updateInfo();
        setContentView();
        initAbstractView();
    }

    private void initAbstractView() {

        setViewListener(findViewById(R.id.imgBackDetail), (v -> finish()));
        setViewListener(findViewById(R.id.imgCommentDetail), (v -> ClickCommentAdd()));
        if(!updateInfo.allowDirectShareApp)
        {
            findViewById(R.id.imgShareDetail).setVisibility(View.GONE);
        }
        setViewListener(findViewById(R.id.imgShareDetail), (v -> ClickShare()));
        setViewListener(findViewById(R.id.imgFavDetail), (v -> ClickFav()));
        webViewBody = findViewById(R.id.WebViewBodyDetail);
        if(webViewBody!=null) {
            webViewBody.getSettings().setJavaScriptEnabled(true);
            webViewBody.getSettings().setBuiltInZoomControls(true);
        }
        Id = getIntent().getExtras().getLong(Extras.EXTRA_FIRST_ARG);
        getContent();
    }

    public final void setTypeFace(List<TextView> Lbls) {
        for (TextView tv : Lbls) {
            tv.setTypeface(FontManager.T1_Typeface(this ));
        }
    }

    protected void setViewListener(View viewById, View.OnClickListener o) {
        if (viewById != null)
            viewById.setOnClickListener(o);
    }

    protected final void getContent() {
        if (AppUtil.isNetworkAvailable(this)) {

            ServiceExecute.execute(getOneContentService().apply(Id))
                    .subscribe(new ErrorExceptionObserver<TEntity>(this::showError) {

                        @Override
                        protected void SuccessResponse(ErrorException<TEntity> ContentResponse) {
                            model = ContentResponse.Item;
                            bindContentData(ContentResponse);
                        }

                        @Override
                        protected Runnable tryAgainMethod() {
                            return AbstractDetailActivity.this::getContent;
                        }

                    });
        } else {
            new GenericErrors().netError(this::showError, this::getContent);
        }

    }

    protected final void getContentOtherInfo(long ContentId) {
        if (AppUtil.isNetworkAvailable(this)) {
            ServiceExecute.execute(getOtherInfoListService().apply(ContentId))
                    .subscribe(new ErrorExceptionObserver<TOtherInfo>(this::showError) {

                        @Override
                        protected void SuccessResponse(ErrorException<TOtherInfo> tOtherInfoErrorException) {
                            bindDataOtherInfo(tOtherInfoErrorException);
                        }

                        @Override
                        protected Runnable tryAgainMethod() {
                            return () -> getContentOtherInfo(ContentId);
                        }

                    });
        } else {
            new GenericErrors().netError(this::showError, () -> getContentOtherInfo(ContentId));
        }
    }


    public void ClickShare() {

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        String message = createShareMassage();
        shareIntent.putExtra(Intent.EXTRA_TEXT, message + "\n\n\n" + this.getString(R.string.app_name) + "\n" + getString(R.string.per_download_link) + "\n" + getUrlShare());
        shareIntent.setType("text/txt");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        this.startActivity(Intent.createChooser(shareIntent, getString(R.string.per_share_to)));
    }

    protected abstract String getUrlShare();

    public void ClickFav() {
        if (AppUtil.isNetworkAvailable(this)) {
            Pair<Function<Long, Observable<ErrorExceptionBase>>, Runnable> functionFunctionPair = getFavoriteService();
            ServiceExecute.execute(functionFunctionPair.first.apply(Id))
                    .subscribe(new NtkObserver<ErrorExceptionBase>() {

                        @Override
                        public void onNext(ErrorExceptionBase e) {
                            if (e.IsSuccess) {
                                Toasty.success(AbstractDetailActivity.this, R.string.per_success).show();
                                functionFunctionPair.second.run();
                            } else {
                                Toasty.error(AbstractDetailActivity.this, e.ErrorMessage, Toast.LENGTH_LONG, true).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            new GenericErrors().throwableException(AbstractDetailActivity.this::showErrorDialog, e, () -> ClickFav());
                        }
                    });
        } else {
            new GenericErrors().netError(this::showErrorDialog, this::ClickFav);
        }
    }

    protected abstract String createShareMassage();

    protected abstract void bindContentData(ErrorException<TEntity> contentResponse);

    public abstract void bindDataOtherInfo(ErrorException<TOtherInfo> contentOtherInfoResponse);

    protected abstract void showError(String toString, Runnable onTryingAgain);

    protected abstract void showErrorDialog(String toString, Runnable onTryingAgain);

    protected abstract void setContentView();

    protected abstract void ClickCommentAdd();

    public abstract Function<Long, Observable<ErrorException<TEntity>>> getOneContentService();

    public abstract Function<Long, Observable<ErrorException<TComment>>> getCommentListService();

    public abstract Function<Long, Observable<ErrorException<TOtherInfo>>> getOtherInfoListService();

    public abstract Pair<Function<Long, Observable<ErrorExceptionBase>>, Runnable> getFavoriteService();

    public abstract Function<Long,
            Observable<ErrorException<TEntity>>> getSimilarCategoryService();

    public abstract Function<Long,
            Observable<ErrorException<TEntity>>> getSimilarContentService();


}
