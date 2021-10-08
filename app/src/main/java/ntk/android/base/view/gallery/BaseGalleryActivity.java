package ntk.android.base.view.gallery;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

import ntk.android.base.Extras;
import ntk.android.base.R;

 abstract class BaseGalleryActivity extends AppCompatActivity {
    protected Toolbar mToolbar;
    protected ArrayList<String> imageURLs;
    private String title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResourceLayoutId());

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        // get values
        imageURLs = getIntent().getStringArrayListExtra(Extras.EXTRA_SECOND_ARG);
//        toolbarColorResId = getIntent().getIntExtra(Constants.IntentPassingParams.TOOLBAR_COLOR_ID, -1);
        title = getIntent().getStringExtra(Extras.Extra_THIRD_ARG);
//        toolbarTitleColor = (ZColor) getIntent().getSerializableExtra(Constants.IntentPassingParams.TOOLBAR_TITLE_COLOR);

//        if (getSupportActionBar() == null) {
//            setSupportActionBar(mToolbar);
//            mToolbar.setVisibility(View.VISIBLE);
//            if (toolbarTitleColor == ZColor.BLACK) {
//                mToolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.black));
//                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black);
//            } else {
//                mToolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white));
//                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
//            }
//            mToolbar.setBackgroundColor(getResources().getColor(toolbarColorResId));
        if (title != null) {
            ((TextView) findViewById(R.id.txtToolbarTitle)).setText(title);
        }

    afterInflation();

}


    protected abstract int getResourceLayoutId();

    protected abstract void afterInflation();
}