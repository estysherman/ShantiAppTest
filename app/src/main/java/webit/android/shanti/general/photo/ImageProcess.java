package webit.android.shanti.general.photo;

import android.graphics.Bitmap;

public class ImageProcess {

    public static final int USER_BORDER = -13454262;
    public static final int RED = -65522;

    public static Bitmap cropImageVer2(Bitmap img, Bitmap templateImage) {

//        img = Bitmap.createScaledBitmap(img, 10, 10, true);
//        templateImage = Bitmap.createScaledBitmap(img, 10, 10, true);
        img = Bitmap.createScaledBitmap(img, templateImage.getWidth(), templateImage.getHeight(), true);

        Bitmap finalBm = Bitmap.createBitmap(templateImage.getWidth(), templateImage.getHeight(), Bitmap.Config.ARGB_8888);
        int px;
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                px = templateImage.getPixel(x, y);
                //Log.d("pixel", Integer.toString(px));
                if (px != RED) {
                    finalBm.setPixel(x, y, px);
                } else {
                    px = img.getPixel(x, y);
                    finalBm.setPixel(x, y, px);
                }
            }
        }

        // Get rid of images that we finished with to save memory.

        if (templateImage != null && !templateImage.isRecycled()) {
            templateImage.recycle();
            templateImage = null;
        }


//        if (img != null && !img.isRecycled()) {
//            img.recycle();
//            img = null;
//        }
//
        return finalBm;
    }



}
