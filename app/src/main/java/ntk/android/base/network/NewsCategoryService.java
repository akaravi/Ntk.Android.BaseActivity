package ntk.android.base.network;

import android.content.Context;

import ntk.base.api.news.entity.NewsCategory;

public class NewsCategoryService extends BaseService<NewsCategory, Long> {
    public NewsCategoryService(Context context) {
        super(context, "NewsCategory");
    }

}
