package webit.android.shanti.general.photo.getimage;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by user on 27/07/2015.
 */
public class CameraLauncher {


    public CameraLauncherArguments arguments;

    final int PICK_IMAGE = 10;
    final int PICK_IMAGE_KIT_KAT = 40;
    final int CAPTURE_IMAGE = 20;
    final int CROP_FROM_CAMERA = 30;
    final int ACTION_TAKE_VIDEO = 5;
    Uri mImageCaptureUri = Uri.EMPTY;
    Uri mCroppedUri = Uri.EMPTY;
    String imgPath = "";

    public boolean checked = false;
    private Activity activity;
    private Fragment fragment;
    public CallbackContext callbackContext;
    public Context mContext;


    public CameraLauncher(Fragment fragment) {
        this.fragment = fragment;
        this.activity = fragment.getActivity();
    }

    public CameraLauncher(Activity activity) {
        this.activity = activity;
    }

    public void execute(CameraLauncherArguments args, CallbackContext callbackContext, Context context) {
        this.mContext = context;
        if (args.eSourceType == null)
            callbackContext.error("SourceType not defined");
        this.callbackContext = callbackContext;
        this.arguments = args;
        this.checked = true;
        //this.arguments.allowEdit = false;
        if (this.arguments.eSourceType == CameraLauncherArguments.ESourceType.CAMERA)
            getImageFromCamera();
        else if (this.arguments.eSourceType == CameraLauncherArguments.ESourceType.GALLERY)
            getImageFromGallery();
    }


    public void getImageFromCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (this.arguments.allowEdit) {
            mImageCaptureUri = Uri.fromFile(new File(Environment
                    .getExternalStorageDirectory(), "tmp_avatar_"
                    + String.valueOf(System.currentTimeMillis()) + ".png"));
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        }
        if (fragment != null)
            fragment.startActivityForResult(cameraIntent, CAPTURE_IMAGE);
        else
            activity.startActivityForResult(cameraIntent, CAPTURE_IMAGE);
    }

    public void getImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");

        if (Build.VERSION.SDK_INT < 19) {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (fragment != null)
            fragment.startActivityForResult(Intent.createChooser(intent, ""),
                    PICK_IMAGE);
        else
            activity.startActivityForResult(Intent.createChooser(intent, ""),
                    PICK_IMAGE);
    }


    public Bitmap decodeFile(String path) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, o);
            final int REQUIRED_SIZE = 70;

            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE
                    && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(path, o2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;

    }

    public String getAbsolutePath(Uri uri) {

        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = activity
                .managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();

            return cursor.getString(column_index);
        } else
            return null;
    }

    private String getTempDirectoryPath() {
        File cache = null;

        // SD Card Mounted
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cache = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/Android/data/" + activity.getPackageName() + "/cache/");
        }
        // Use internal storage
        else {
            cache = activity.getCacheDir();
        }

        // Create the cache directory if it doesn't exist
        cache.mkdirs();
        return cache.getAbsolutePath();
    }

    private void doCrop() {

        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            getRightAngleImage(mImageCaptureUri.getPath());
            cropIntent.setDataAndType(mImageCaptureUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            //Environment.getExternalStorageDirectory() + "/" + DocumentsContract.getDocumentId(mImageCaptureUri).split(":")[1];
            // indicate output X and Y
            if (arguments.targetWidth > 0) {
                cropIntent.putExtra("outputX", arguments.targetWidth);
            }
            if (arguments.targetHeight > 0) {
                cropIntent.putExtra("outputY", arguments.targetHeight);
            }
            if (arguments.targetHeight > 0 && arguments.targetWidth > 0) {
                int big, little = 0;
                if (arguments.targetHeight > arguments.targetWidth) {
                    big = arguments.targetHeight;
                    little = arguments.targetWidth;
                } else {
                    big = arguments.targetWidth;
                    little = arguments.targetHeight;
                }
                cropIntent.putExtra("aspectX", big);
                cropIntent.putExtra("aspectY", little);
            }
            // create new file handle to get full resolution crop
            //Uri mCroppedUri = Uri.fromFile(new File(getTempDirectoryPath(), System.currentTimeMillis() + ".jpg"));
            //cropIntent.putExtra("output", mCroppedUri);

            // start the activity - we handle returning in onActivityResult

            mCroppedUri = Uri.fromFile(new File(getTempDirectoryPath(), System.currentTimeMillis() + ".png"));

            cropIntent.putExtra("output", mCroppedUri);
            cropIntent.putExtra("scale", true);
            cropIntent.putExtra("scaleUpIfNeeded", true);
            //cropIntent.putExtra("return-data", true);

            if (this.activity != null) {
                if (fragment != null)
                    this.fragment.startActivityForResult(cropIntent, CROP_FROM_CAMERA);
                else this.activity.startActivityForResult(cropIntent, CROP_FROM_CAMERA);
            }
        } catch (ActivityNotFoundException anfe) {
        }
    }


    private void getRealUri() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {


            File file = new File(Environment.getExternalStorageDirectory(), "KitKatOSTemp" + String.valueOf(System.currentTimeMillis()) + ".png");
            InputStream is = null;
            try {
                is = activity.getContentResolver()
                        .openInputStream(mImageCaptureUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                this.callbackContext.error(e.getMessage());
            }

            try {
                OutputStream output = new FileOutputStream(file);
                try {
                    try {
                        byte[] buffer = new byte[4 * 1024]; // or other buffer size
                        int read;

                        while ((read = is.read(buffer)) != -1) {
                            output.write(buffer, 0, read);
                        }
                        output.flush();
                    } finally {
                        output.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace(); // handle exception, define IOException and others
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //Uri.parse(new File("/sdcard/cats.jpg").toString())
            mImageCaptureUri = Uri.fromFile(file);
        }
    }

    int rotate = 0;

    public void onActivityResult(int requestCode, int resultCode, Intent data) throws IOException {
        int rotate = 0;
        // Create an ExifHelper to save the exif data that is lost during compression
        ExifHelper exif = new ExifHelper();
        String sourcePath;
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAPTURE_IMAGE) {
                if (this.arguments != null && this.arguments.allowEdit)
                    doCrop();
                else {
                    useImage((Bitmap) data.getExtras().get("data"));
                }
            }
            if (requestCode == PICK_IMAGE || requestCode == PICK_IMAGE_KIT_KAT) {
                mImageCaptureUri = data.getData();
                getRealUri();
                if (this.arguments.allowEdit)
                    doCrop();
                else {
                    String absolutePath = getAbsolutePath(mImageCaptureUri);
                    if (absolutePath == null) {
                        InputStream is = null;
                        try {
                            is = activity.getContentResolver()
                                    .openInputStream(mImageCaptureUri);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            this.callbackContext.error(e.getMessage());
                        }
//                        Bitmap bitmap = BitmapFactory.decodeStream(is);
//                        useImage(bitmap);
                        try {
                            Bitmap bitmap = BitmapFactory.decodeStream(is);
                            useImage(bitmap);
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            this.callbackContext.error(e.getMessage());
                        }
                    } else {
                        useImage(decodeFile(absolutePath));
                    }

                    File f = new File(mImageCaptureUri.getPath());

                    if (f.exists())
                        f.delete();
                }
            }
            if (requestCode == CROP_FROM_CAMERA) {

                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), mCroppedUri);

                } catch (IOException e) {
                    e.printStackTrace();
                    this.callbackContext.error(e.getMessage());
                }
                useImage(bitmap);

                File f = new File(mCroppedUri.getPath());
                if (f.exists())
                    f.delete();


//                Bundle extras = data.getExtras();
//
//                if (extras != null) {
//                    Bitmap img = extras.getParcelable("data");
//                    useImage(img);
//                    File f = new File(mImageCaptureUri.getPath());
//
//                    if (f.exists())
//                        f.delete();
//                }
            }
            if (requestCode == ACTION_TAKE_VIDEO) {
                handleCameraVideo(data);
            }

        }
    }

    private void useImage(Bitmap bitmap) {
        if (bitmap != null) {
            callbackContext.success(bitmap);
            ////////bitmap

        } else callbackContext.error("Bitmap is null");
        File f = new File(mImageCaptureUri.getPath());
        if (f.exists())
            f.delete();
    }

    public void handleCameraVideo(Intent intent) {
        Uri mVideoUri;
        mVideoUri = intent.getData();
        //getImage.useVideo(mVideoUri);
        this.callbackContext.success(mVideoUri);
    }


    private Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) {

        // Detect rotation
        int rotation = getRotation(context, selectedImage);
        if (rotation != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
            img.recycle();
            return rotatedImg;
        } else {
            return img;
        }
    }

    private int getRotation(Context context, Uri selectedImage) {
        int rotation = 0;
        ContentResolver content = context.getContentResolver();
        Cursor mediaCursor = content.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{"orientation", "date_added"}, null, null, "date_added desc");

        if (mediaCursor != null && mediaCursor.getCount() != 0) {
            while (mediaCursor.moveToNext()) {
                rotation = mediaCursor.getInt(0);
                break;
            }
        }
        mediaCursor.close();
        return rotation;
    }


    private String getRightAngleImage(String photoPath) {

        try {
            ExifInterface ei = new ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int degree = 0;

            switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    degree = 0;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                case ExifInterface.ORIENTATION_UNDEFINED:
                    degree = 0;
                    break;
                default:
                    degree = 90;
            }

            return rotateImage(degree, photoPath);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return photoPath;
    }

    private String rotateImage(int degree, String imagePath) {

        if (degree <= 0) {
            return imagePath;
        }
        try {
            Bitmap b = BitmapFactory.decodeFile(imagePath);


//            Matrix matrix = new Matrix();
//            if (b.getWidth() > b.getHeight()) {
//                matrix.setRotate(degree);
//                b = Bitmap.createBitmap(b, 0, 0, b.getWidth()-(b.getWidth()-b.getHeight()), b.getHeight(), matrix, true);
//            }

            FileOutputStream fOut = new FileOutputStream(imagePath);
            String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
            String imageType = imageName.substring(imageName.lastIndexOf(".") + 1);

            FileOutputStream out = new FileOutputStream(imagePath);
            if (imageType.equalsIgnoreCase("png")) {
                //take time
                b.compress(Bitmap.CompressFormat.PNG, 100, out);
            } else if (imageType.equalsIgnoreCase("jpeg") || imageType.equalsIgnoreCase("jpg")) {
                b.compress(Bitmap.CompressFormat.JPEG, 100, out);
            }
            fOut.flush();
            fOut.close();

            b.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagePath;
    }

}
