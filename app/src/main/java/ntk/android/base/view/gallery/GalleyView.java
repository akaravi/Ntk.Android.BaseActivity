package ntk.android.base.view.gallery;

import android.widget.RelativeLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import ntk.android.base.Extras;
import ntk.android.base.R;

public class GalleyView extends BaseGalleryActivity {

    CustomViewPager mViewPager;
    ViewPagerAdapter adapter;
    RecyclerView imagesHorizontalList;
    LinearLayoutManager mLayoutManager;
    HorizontalListAdapters hAdapter;
    private int currentPos;


    @Override
    protected int getResourceLayoutId() {
        return R.layout.activity_gallery;
    }

    @Override
    protected void afterInflation() {
        // init layouts
        mViewPager = (CustomViewPager) findViewById(R.id.pager);
        imagesHorizontalList = (RecyclerView) findViewById(R.id.imagesHorizontalList);

        // get intent data
        currentPos = getIntent().getIntExtra(Extras.EXTRA_FIRST_ARG, 0);
//        bgColor = (ZColor) getIntent().getSerializableExtra(Constants.IntentPassingParams.BG_COLOR);
//
//        if (bgColor == ZColor.WHITE) {
//            mainLayout.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
//        }

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        // pager adapter
        adapter = new ViewPagerAdapter(this, imageURLs, mToolbar, imagesHorizontalList);
        mViewPager.setAdapter(adapter);
        // horizontal list adaapter
        hAdapter = new HorizontalListAdapters(this, imageURLs, new OnImgClick() {
            @Override
            public void onClick(int pos) {
                mViewPager.setCurrentItem(pos, true);
            }
        });
        imagesHorizontalList.setLayoutManager(mLayoutManager);
        imagesHorizontalList.setAdapter(hAdapter);
        hAdapter.notifyDataSetChanged();

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                imagesHorizontalList.smoothScrollToPosition(position);
                hAdapter.setSelectedItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        hAdapter.setSelectedItem(currentPos);
        mViewPager.setCurrentItem(currentPos);
    }


}
