package ntk.android.base.activity.hyper;

import android.os.Bundle;

import androidx.annotation.Nullable;

import es.dmoral.toasty.Toasty;
import io.reactivex.annotations.NonNull;
import ntk.android.base.Extras;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.config.ServiceExecute;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.hypershop.HyperShopContentModel;
import ntk.android.base.services.hypershop.HyperShopContentService;

public abstract class BaseHyperShopContentDetail_1_Activity extends BaseActivity {
    protected HyperShopContentModel model;
    String Id;

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        Id = getIntent().getExtras().getString(Extras.EXTRA_FIRST_ARG);
        onCreated();
        super.onCreate(savedInstanceState);
        callGetOne();
    }

    protected abstract void onCreated();

    public void callGetOne() {
        switcher.showProgressView();
        ServiceExecute.execute(new HyperShopContentService(this).getOneMicroService(Id))
                .subscribe(new NtkObserver<ErrorException<HyperShopContentModel>>() {
                    @Override
                    public void onNext(@NonNull ErrorException<HyperShopContentModel> response) {
                        if (response.IsSuccess) {
                            switcher.showContentView();
                            model = response.Item;
                            if (model.Unit == null)
                                model.Unit = "";
                            bindData();
                        } else
                            switcher.showErrorView();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toasty.error(BaseHyperShopContentDetail_1_Activity.this, e.toString()).show();
                    }
                });
    }

    protected abstract void bindData();
}

