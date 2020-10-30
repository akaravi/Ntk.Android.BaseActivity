package ntk.android.base;

/**
 * Created by m.parishani on 29/04/2017.
 */

public class ApplicationParameter {
    String APPLICATION_PACKAGE_NAME;
    String APPLICATION_ID;

    String VERSION_NAME;
    int VERSION_CODE;
    //    String HOST_STRATEGIC ;
//    String ENDPOINT_API;

    public ApplicationParameter(String APPLICATION_PACKAGE_NAME, String APPLICATION_ID, String VERSION_NAME, int VERSION_CODE) {
        this.APPLICATION_PACKAGE_NAME = APPLICATION_PACKAGE_NAME;
//        this.HOST_STRATEGIC = HOST_STRATEGIC;
//        this.ENDPOINT_API = ENDPOINT_API;
        this.VERSION_NAME = VERSION_NAME;
        this.APPLICATION_ID = APPLICATION_ID;
        this.VERSION_CODE = VERSION_CODE;

//        this.APPLICATION_VERSION = APPLICATION_VERSION;
    }

//    public int APPLICATION_VERSION() {
//        return APPLICATION_VERSION;
//    }

    public String PACKAGE_NAME() {
        return APPLICATION_PACKAGE_NAME;
    }

    public String APPLICATION_ID() {
        return APPLICATION_ID;
    }

    public int VERSION_CODE() {
        return VERSION_CODE;
    }

    public String VERSION_NAME() {
        return VERSION_NAME;
    }
}
