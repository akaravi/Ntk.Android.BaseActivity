package ntk.android.base.utill;

public class imageCompressor {
    public static String stringInsert(String bag, int index, String marble) {
        String bagBegin = bag.substring(0, index);
        String bagEnd = bag.substring(index);
        return bagBegin + marble + bagEnd;
    }

    public static String convertSizeThumbnailImage(String imageUrl, int width, int height) {
        if (imageUrl == null)
            return "";
        if (imageUrl.toLowerCase().indexOf(".png") > 0 || imageUrl.toLowerCase().indexOf(".jpg") > 0 || imageUrl.toLowerCase().indexOf(".jpeg") > 0) {
            String retOut = stringInsert(imageUrl, imageUrl.lastIndexOf("."), "." + width + "x" + height);
            retOut = retOut.replace("/images/", "/thumbnails/");
            return retOut;
        }

        return imageUrl;
    }
}
