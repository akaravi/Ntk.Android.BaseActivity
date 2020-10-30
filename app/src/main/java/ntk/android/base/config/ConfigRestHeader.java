package ntk.android.base.config;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

import ntk.android.base.BuildConfig;
import ntk.android.base.NTKBASEApplication;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.EasyPreference;

public class ConfigRestHeader {

    @SuppressLint("HardwareIds")
    public Map<String, String> GetHeaders(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Map<String, String> headers = new HashMap<>();
        headers.put("Token", "");
        headers.put("LocationLong", "0");
        headers.put("LocationLat", "0");
        headers.put("DeviceId", Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        headers.put("DeviceBrand", AppUtill.GetDeviceName());
        headers.put("Country", "IR");
        headers.put("Language", "FA");
        headers.put("SimCard", manager.getSimOperatorName());
        headers.put("PackageName", NTKBASEApplication.get().getApplicationParameter().PACKAGE_NAME());
        headers.put("AppBuildVer", String.valueOf(NTKBASEApplication.get().getApplicationParameter().VERSION_CODE()));//String.valueOf(BuildConfig.VERSION_CODE));
        headers.put("AppSourceVer", NTKBASEApplication.get().getApplicationParameter().VERSION_NAME());
        String NotId= EasyPreference.with(context).getString("NotificationId", "null");

        if (NotId != null && !NotId.isEmpty() && !NotId.toLowerCase().equals("null")) {
            headers.put("NotificationId", NotId);
            FirebaseMessaging.getInstance().subscribeToTopic(NTKBASEApplication.get().getApplicationParameter().PACKAGE_NAME());
        }
        return headers;
    }
}
