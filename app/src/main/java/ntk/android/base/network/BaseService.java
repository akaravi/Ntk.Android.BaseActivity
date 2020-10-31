package ntk.android.base.network;

import android.content.Context;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import ntk.android.base.config.ConfigRestHeader;
import ntk.base.config.RetrofitManager;
import ntk.base.entityModel.base.ErrorException;
import ntk.base.entityModel.base.ErrorExceptionBase;
import ntk.base.entityModel.base.FilterModel;
import ntk.base.services.base.ICmsApiServerBase;

class BaseService<TEntity, TKey> {
    String baseUrl = "https://apicms.ir/api/v1/";
    String controlerUrl;
    Context context;
    Map<String, String> headers;

    public BaseService(Context context, String controlerUrl) {
        this.controlerUrl = controlerUrl;
        headers = new ConfigRestHeader().GetHeaders(context);
    }

    ICmsApiServerBase ICmsApiServerBase() {
        return new RetrofitManager(context).getRetrofitUnCached().create(ICmsApiServerBase.class);


    }

    public Observable<ErrorException<TEntity>> getAll(FilterModel request) {
        return ICmsApiServerBase().getAll(baseUrl + controlerUrl + "/getAll", headers, request);

    }

    public Observable<ErrorException<TEntity>> getViewModel() {
        return ICmsApiServerBase().getViewModel(baseUrl + controlerUrl + "/getViewModel", headers);

    }

    public Observable<ErrorExceptionBase> Exist(FilterModel request) {
        return ICmsApiServerBase().Exist(baseUrl + controlerUrl + "/Exist", headers, request);

    }

    public Observable<ErrorExceptionBase> Count(FilterModel request) {
        return ICmsApiServerBase().Count(baseUrl + controlerUrl + "/Count", headers, request);

    }

    public Observable<ErrorExceptionBase> ExportFile(FilterModel request) {
        return ICmsApiServerBase().ExportFile(baseUrl + controlerUrl + "/ExportFile", headers, request);

    }

    public Observable<ErrorExceptionBase> Add(TEntity request) {
        return ICmsApiServerBase().Add(baseUrl + controlerUrl + "/", headers, request);

    }

    public Observable<ErrorExceptionBase> Edit(TEntity request) {
        return ICmsApiServerBase().Edit(baseUrl + controlerUrl + "/1", headers, request);

    }

    public Observable<ErrorExceptionBase> Delete(TKey id) {
        return ICmsApiServerBase().Delete(baseUrl + controlerUrl + "/" + id, headers);

    }

    public Observable<ErrorExceptionBase> Delete(List<TKey> request) {
        return ICmsApiServerBase().Delete(baseUrl + controlerUrl + "/DeleteList", headers, request);

    }


}
