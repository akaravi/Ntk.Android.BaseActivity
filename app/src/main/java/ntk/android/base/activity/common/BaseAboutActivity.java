package ntk.android.base.activity.common;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import ntk.android.base.NTKApplication;
import ntk.android.base.R;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.appclass.AboutUsClass;
import ntk.android.base.utill.prefrense.Preferences;
import ntk.android.base.view.NViewUtils;
import ntk.android.base.view.aboutus.AboutPage;
import ntk.android.base.view.aboutus.Element;

public abstract class BaseAboutActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        AboutUsClass about = Preferences.with(this).appVariableInfo().aboutUs();
        if (about != null) {
            View aboutPage = new AboutPage(this)
                    .isRTL(true)
                    .setImage(NViewUtils.getResIdFromAttribute(this, R.attr.ntk_base_logo))
                    .setDescription(about.AboutUsDescription)
                    .addGroup(getString(R.string.address) + " : ")
                    .addItem(new Element().setTitle(about.AboutUsAddress))
                    .addGroup(getString(R.string.contact_us))
                    .addPhone(about.AboutUsTel, getString(R.string.call_number) + ": ")
                    .addPhone(about.AboutUsFax, getString(R.string.fax_numebr) + " : ")
                    .addEmail(about.AboutUsEmail, getString(R.string.email) + " : ")
//                    .addPhone(about., about.Item.TitleMobileNo)
//                    .addPhone(about.Item.OfficeNo, about.Item.TitleOfficeNo)
//                    .addPhone(about.Item.Tel1, about.Item.TitleTel1)
//                                    .addInstagram(about.Instagram, about.Item.TitleInstagram)
//                                    .addWebsite(about.Item.WebUrl, about.Item.TitleWebUrl)
//                                    .addTelegram(about.Item.Telegram, about.Item.TitleTelegram)
                    .addItem(new Element().setTitle(getString(R.string.current_version)).setInnerElement(new Element().setTitle(getString(R.string.version_number) + getVersionName() + "   " + "( " + getVersionCode() + " Build Code )")))
                    .create();
            setContentView(aboutPage);
        }
    }

    public String getVersionName() {
        return NTKApplication.get().getApplicationParameter().VERSION_NAME();
    }

    public String getVersionCode() {
        return NTKApplication.get().getApplicationParameter().VERSION_NAME();
    }
}
