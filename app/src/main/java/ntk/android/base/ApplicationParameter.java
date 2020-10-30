package ntk.android.base;

/**
 * Created by m.parishani on 29/04/2017.
 */

public class ApplicationParameter {
    String APPLICATION_PACKAGE_NAME;
    String HOST_STRATEGIC ;
    String ENDPOINT_API;
    String DATABASE_NAME;
    int DATABASE_VERSION;
    int APPLICATION_VERSION;

    public ApplicationParameter(String APPLICATION_PACKAGE_NAME, String HOST_STRATEGIC, String ENDPOINT_API, String DATABASE_NAME, int DATABASE_VERSION, int APPLICATION_VERSION) {
        this.APPLICATION_PACKAGE_NAME = APPLICATION_PACKAGE_NAME;
        this.HOST_STRATEGIC = HOST_STRATEGIC;
        this.ENDPOINT_API = ENDPOINT_API;
        this.DATABASE_NAME = DATABASE_NAME;
        this.DATABASE_VERSION = DATABASE_VERSION;
        this.APPLICATION_VERSION = APPLICATION_VERSION;
    }

    public int APPLICATION_VERSION() {
        return APPLICATION_VERSION;
    }

    public String PACKAGE_NAME() {
        return APPLICATION_PACKAGE_NAME;
    }
}
