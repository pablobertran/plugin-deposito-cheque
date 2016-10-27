package ec.com.easysoft.bancamovil.capturacheque;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.CameraInfo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MakePhotoActivity extends Activity {

    private static Camera mCamera;

    private static CameraPreview mPreview;
    private SensorManager mSensorManager;
    private Sensor mOrientation;
    private int orientationDegrees = 0;
    private int currentDisplayRotation;
    private int mDeviceOrientation;
    private int defaultCameraId;
    private int baseRectangle;
    private byte[] byteArrayImage;

    private int[] offSet;


    private OrientationEventListener mOrientationEventListener;

    static final int MEDIA_TYPE_IMAGE = 1;

    static Context context;

    /**
     * setting last image taken
     **/
    static ImageView myImage;
    static ImageButton imageButton;
    static ImageButton flashButton;
    //static GuideView guide;
    static RelativeLayout overlay;
    static RelativeLayout background;
    static File imgFile = null;
    int duration = Toast.LENGTH_LONG;
    static private String TAG = "Make Photo Activity";

    static double ASPECTO_RATIO_RECT = 156.0 / 74.0; // Medidas del Cheque Ancho/Alto(mm)
    static double ASPECT_RATIO = 16.0 / 9.0; //Aspecto Relación Preview Cámara
    static double PERCENTAGE_ADDED = 0.025; // Original 0.1

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_make_photo);
        setContentView(getApplication().getResources().getIdentifier("activity_make_photo", "layout", getApplication().getPackageName()));

        final Boolean front;
        Intent intent = getIntent();
        front = intent.getBooleanExtra("front",true);



        if (mCamera == null) {
            mCamera = Camera.open();
        }
        context = getApplicationContext();
        //mCamera = getCameraInstance();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        // Create our Preview view and set it as the content of our activity.

        //guide = (GuideView) findViewById(R.id.guide);
        //myImage = (ImageView) findViewById(R.id.lastPic);
        final String packageName = getApplication().getPackageName();
        myImage = (ImageView) findViewById(getApplication().getResources().getIdentifier("lastPic", "id", packageName));
        //imageButton = (ImageButton) findViewById(R.id.imageButton1);
        imageButton = (ImageButton) findViewById(getApplication().getResources().getIdentifier("imageButton1", "id", packageName));
        //flashButton = (ImageButton) findViewById(R.id.imageButton2);
        flashButton = (ImageButton) findViewById(getApplication().getResources().getIdentifier("imageButton2", "id", packageName));
        //overlay = (RelativeLayout) findViewById(R.id.overlay);
        overlay = (RelativeLayout) findViewById(getApplication().getResources().getIdentifier("overlay", "id", packageName));
        //background = (RelativeLayout) findViewById(R.id.speedBackgound);
        background = (RelativeLayout) findViewById(getApplication().getResources().getIdentifier("camera_preview2", "id", packageName));

        final ImageView guideImage = new ImageView(this);


        //final FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        final FrameLayout preview = (FrameLayout) findViewById(getApplication().getResources().getIdentifier("camera_preview", "id", packageName));
        mPreview = new CameraPreview(context, mCamera, ASPECT_RATIO);


        //mPreview.setCamera(mCamera);

        if (!mPreview.haveFlashMode()) {
            flashButton.setVisibility(View.GONE);
        }

        //final
        final DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Log.v(TAG, "Width:" + String.valueOf(metrics.widthPixels));
        Log.v(TAG, "Height:" + String.valueOf(metrics.heightPixels));


        ViewTreeObserver vto = overlay.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                overlay.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                //int width  = layout.getMeasuredWidth();
                int width = overlay.getMeasuredWidth();
                int height = overlay.getMeasuredHeight();

                int widthAvailableForPreview = metrics.widthPixels - overlay.getWidth();
                int[] sizePreview = getOptimalCameraPreviewSize(widthAvailableForPreview, metrics.heightPixels);
                preview.getLayoutParams().width = sizePreview[0];
                preview.getLayoutParams().height = sizePreview[1];
                mPreview.getHolder().setFixedSize(sizePreview[0], sizePreview[1]);
                preview.addView(mPreview);
                //mCamera.startPreview();

                //RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) sizePreview.first,(int) sizePreview.second);

                //preview.setLayoutParams(layoutParams);


                //RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(metrics.widthPixels, metrics.heightPixels / 4);
                //preview.setLayoutParams(layoutParams);

                ViewTreeObserver vto = preview.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {

                        //mPreview.change();
                        //mCamera.startPreview();
                        preview.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        Log.v(TAG, "Preview1");

                        int[] location = new int[2];
                        preview.getLocationInWindow(location);
                        int[] location2 = new int[2];
                        background.getLocationInWindow(location2);

                        offSet = new int[2];
                        int[] bh = getOptimalRectangleSize(preview.getWidth(), preview.getHeight());
                        baseRectangle = bh[0];
                        int h = (int) (baseRectangle / ASPECTO_RATIO_RECT);
                        getOffsetToCenter(offSet, baseRectangle, h);
                        //guide.changeRectangle(location[0] + offSet[0], location[1] + offSet[1], baseRectangle, h);

                        /*Rectangle x: location[0]+offSet[0]  y: location[1]+offSet[1]  width: baseRectangle height: h*/
                        //guideImage.setImageResource(R.drawable.guia);
                        if(front) {
                            guideImage.setImageResource(getApplication().getResources().getIdentifier("guia", "drawable", packageName));
                        }else{
                            guideImage.setImageResource(getApplication().getResources().getIdentifier("guia_reverso", "drawable", packageName));
                        }
                        guideImage.setX(location[0] + offSet[0]);
                        guideImage.setY(location[1] + offSet[1] - location2[1]);
                        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(baseRectangle, h);
                        guideImage.setLayoutParams(parms);
                        //guideImage.setMaxWidth(baseRectangle);
                        //guideImage.setMaxHeight(h);

                        background.addView(guideImage);

                        ViewTreeObserver vto3 = guideImage.getViewTreeObserver();
                        vto3.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                            @Override
                            public void onGlobalLayout() {

                                //mPreview.change();
                                //mCamera.startPreview();
                                guideImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            }
                        });


                        //mPreview.change();
                    }
                });


            }
        });


        //setContentView(mPreview);

        // Find the total number of cameras available
        int numberOfCameras = Camera.getNumberOfCameras();

        // Find the ID of the default camera
        CameraInfo cameraInfo = new CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                defaultCameraId = i;
            }
        }


        mOrientationEventListener = new OrientationEventListener(this,
                SensorManager.SENSOR_DELAY_NORMAL) {

            @Override
            public void onOrientationChanged(int orientation) {
                //Log.v(TAG, "Orientation changed to " + orientation);
                setCameraOrientation(1, mCamera);

            }
        };

        if (mOrientationEventListener.canDetectOrientation() == true) {
            Log.v(TAG, "Can detect orientation");
            mOrientationEventListener.enable();
        } else {
            Log.v(TAG, "Cannot detect orientation");
            mOrientationEventListener.disable();
        }

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto(view);
            }
        });

        flashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPreview.changeFlashMode();
            }
        });

        /** if last image is clicked it will open in an image viewer app **/
        myImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                if (imgFile == null) {
                    CharSequence text = "This is only an icon, take a photo";
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } else {
                    intent.setDataAndType(Uri.parse("file://" + imgFile.getAbsolutePath()), "image");
                    startActivity(intent);
                }
                return false;
            }
        });
    }

    private void getOffsetToCenter(int[] offSet, int b, int h) {
        offSet[0] = (int) ((mPreview.getWidth() - b) / 2);
        offSet[1] = (int) ((mPreview.getHeight() - h) / 2);
    }

    /*@Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // Get the preview size
        int previewWidth = mPreview.getMeasuredWidth(),
                previewHeight = mPreview.getMeasuredHeight();

        // Set the height of the overlay so that it makes the preview a square
        RelativeLayout.LayoutParams overlayParams = (RelativeLayout.LayoutParams) overlay.getLayoutParams();
        overlayParams.height = previewWidth - previewHeight;
        overlay.setLayoutParams(overlayParams);
    }*/

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    public void takePhoto(View v) {
        mCamera.takePicture(null, null, mPicture);
    }

    private PictureCallback mPicture = new PictureCallback()

    {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            byte[] data2 = getCropImageFromData(data, orientationDegrees);


            if (pictureFile == null) {
                Log.d("ERROR", "Error creating media file, check storage permissions:");
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data2);
                fos.close();
                openPreview(imgFile);

            } catch (FileNotFoundException e) {
                Log.d("ERROR", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("ERROR", "Error accessing file: " + e.getMessage());
            }

            //setImage();

            mCamera.startPreview();
        }
    };

    private byte[] getCropImageFromData(byte[] data, int degree) {
        Bitmap cropImg = null;
        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
        double ratio = (double) bmp.getWidth() / (double) mPreview.getWidth();

        if (degree != 0) {
            Matrix m = new Matrix();
            m.postRotate(degree);
            cropImg = Bitmap.createBitmap(bmp, (int) (offSet[0] * ratio), (int) (offSet[1] * ratio), (int) (baseRectangle * ratio), (int) ((baseRectangle / ASPECTO_RATIO_RECT) * ratio), m, true);
        } else {
            cropImg = Bitmap.createBitmap(bmp, (int) (offSet[0] * ratio), (int) (offSet[1] * ratio), (int) (baseRectangle * ratio), (int) ((baseRectangle / ASPECTO_RATIO_RECT) * ratio));
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        cropImg.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byteArrayImage = stream.toByteArray();
        // Convert to Base64
        //String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return byteArrayImage;
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {

        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (!isSDPresent) {
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, "card not mounted", duration);
            toast.show();

            Log.d("ERROR", "Card not mounted");
        }
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getPath() + "/cameraAppFiles/");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {

                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
            imgFile = mediaFile;
        } else {
            return null;
        }

        return mediaFile;
    }

    public void setImage() {
        if (imgFile != null) {
            if (imgFile.exists()) {
                Bitmap myBitmap = decodeSampleImage(imgFile, 100, 100); // prevents memory out of memory exception
                //myImage.setImageBitmap(myBitmap);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getPath(), options);
                //Bitmap cropImg = Bitmap.createBitmap(bitmap,0,0,(int)(1000*bitmap.getWidth()/mPreview.getWidth()), (int) (1000/ASPECTO_RATIO_RECT*bitmap.getHeight()/mPreview.getHeight()));
                /*double ratio = (double) bitmap.getWidth()/(double) mPreview.getWidth();
                Bitmap cropImg = Bitmap.createBitmap(bitmap, (int) (offSet[0]*ratio), (int) (offSet[1]*ratio),(int)(baseRectangle*ratio), (int) ((baseRectangle/ASPECTO_RATIO_RECT)*ratio));*/
                //Bitmap mask = BitmapFactory.decodeResource(getResources(),R.drawable.mask);
                //float ratio = mask.getWidth()/mask.getHeight();

                //int height = (int) (bitmap.getWidth()/ratio);

                //Bitmap mask2 = Bitmap.createScaledBitmap(mask,bitmap.getWidth(),height,true);
                //myImage.setImageBitmap(cropImg);
                myImage.setImageBitmap(bitmap);
                openPreview(imgFile);

            }
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        mOrientationEventListener.disable();
        if (mCamera != null) {
            mPreview.setCamera(null);
            releaseCamera();
            mCamera = null;
        }
        // release the camera immediately on pause event
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*if (mCamera == null) {
            mCamera = getCameraInstance();

            context = getApplicationContext();
            mPreview = new CameraPreview(this);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.addView(mPreview);
        }*/

        // Open the default i.e. the first rear facing camera.
        if (mCamera == null) {
            mCamera = Camera.open();
        }

        //cameraCurrentlyLocked = defaultCameraId;
        mPreview.setCamera(mCamera);
        mCamera.startPreview();

        mOrientationEventListener.enable();
    }

    private void releaseCamera() {
        if (mCamera != null) {
            //mPreview.getHolder().removeCallback(mPreview);
            mCamera.release();
            mCamera = null;

        }
    }

    public static Bitmap decodeSampleImage(File f, int width, int height) {
        try {
            System.gc(); // First of all free some memory 	        // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);            // The new size we want to scale to 	                            final int requiredWidth = width;
            final int requiredHeight = height;            // Find the scale value (as a power of 2)
            final int requiredWidth = width;
            int sampleScaleSize = 1;
            while (o.outWidth / sampleScaleSize / 2 >= requiredWidth && o.outHeight / sampleScaleSize / 2 >= requiredHeight)
                sampleScaleSize *= 2;

            // Decode with inSampleSize

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = sampleScaleSize;

            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (Exception e) {
            //  Log.d(TAG, e.getMessage()); // We don't want the application to just throw an exception
        }

        return null;
    }

    private int[] getOptimalCameraPreviewSize(int w, int h) {

        int newWidth = (int) (ASPECT_RATIO * h);
        int size[] = new int[2];
        //Pair size = new Pair(w, h);
        size[0] = w;
        size[1] = h;
        if (newWidth < w) {
            //size = new Pair(newWidth, h);
            size[0] = newWidth;
            size[1] = h;
        } else {
            int newHeight = (int) (w / ASPECT_RATIO);
            size[0] = w;
            size[1] = newHeight;
            //size = new Pair(w, newHeight);
        }
        return size;
    }

    private int[] getOptimalRectangleSize(int w, int h) {
        int nHeight = (int) (h * (1 - PERCENTAGE_ADDED));
        int nWidth = (int) (w * (1 - PERCENTAGE_ADDED));
        int newWidth = (int) ((ASPECTO_RATIO_RECT * nHeight));
        int[] size = new int[2];
        //Pair size = new Pair(nWidth, nHeight);
        size[0] = nWidth;
        size[1] = nHeight;
        if (newWidth < nWidth) {
            //size = new Pair(newWidth, nHeight);
            size[0] = newWidth;
            size[1] = nHeight;
        } else {
            int newHeight = (int) ((nWidth / ASPECTO_RATIO_RECT));
            //size = new Pair(nWidth, newHeight);
            size[0] = nWidth;
            size[1] = newHeight;
        }
        return size;
    }


    private void setCameraOrientation(int camId, Camera camera) {
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(camId, info);

        //Log.v(TAG, String.valueOf(guide.getX())+"Left"+String.valueOf(guide.getLeft()));

        currentDisplayRotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (currentDisplayRotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        result = (info.orientation + degrees) % 360;
        result = (360 - result) % 360;  //compensate for mirror effect

        if (camera != null) {
            if (Build.VERSION.SDK_INT < 14)
                camera.stopPreview();
            camera.setDisplayOrientation(result);
            orientationDegrees = result;
            if (Build.VERSION.SDK_INT < 14)
                camera.startPreview();
        } else {
            Log.d(TAG, "The camera is null");
        }
    }

    /*public static Bitmap makeMaskImage(Bitmap original, Bitmap mask)
    {
        //Bitmap original = BitmapFactory.decodeResource(getResources(), mContent);
        //Bitmap mask = BitmapFactory.decodeResource(getResources(),R.drawable.mask);
        Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCanvas.drawBitmap(original, 0, 0, null);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        int positionY = (int) ((result.getHeight() - mask.getHeight())*0.5);
        mCanvas.drawBitmap(mask, 0, positionY, paint);
        paint.setXfermode(null);
        return result;


        *//*Bitmap bitmap = Bitmap.createBitmap(original.getWidth(), original.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        canvas.drawBitmap(original, 0, 0, paint);
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        float radius = (float)(mask.getWidth() *.35);
        float x = (float) ((original.getWidth()*.5) + (radius * .5));
        float y = (float)  ((original.getHeight()*.5) + (radius * .5));
        canvas.drawCircle(x, y, radius, paint);
        //canvas.drawBitmap(mask, 0, 0, paint);
        return bitmap;*//*
    }*/

    private void openPreview(File photo) {
        Intent intent = new Intent(this, PreviewPhotoActivity.class);
        intent.putExtra("pathImage", photo.getPath());
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Boolean deleted;
        if (requestCode == 1) {
            switch (resultCode) {
                case Activity.RESULT_CANCELED:
                    deleted = imgFile.delete();
                    if (deleted) {
                        Toast toast1 = Toast.makeText(context, "Image Deleted", duration);
                        toast1.show();
                    } else {
                        Toast toast1 = Toast.makeText(context, "Image Not Deleted", duration);
                        toast1.show();
                    }
                    Toast toast = Toast.makeText(context, "Activity Cancelled by User", duration);
                    toast.show();
                    break;
                case Activity.RESULT_OK:
                    String message = data.getStringExtra("action");


                    if (message.equals("another_photo")) {
                        deleted = imgFile.delete();
                        if (deleted) {
                            Toast toast1 = Toast.makeText(context, "Image Deleted", duration);
                            toast1.show();
                        } else {
                            Toast toast1 = Toast.makeText(context, "Image Not Deleted", duration);
                            toast1.show();
                        }

                    } else if (message.equals("use_photo")) {
                        //String encodedImage = returnPhotoInBase64(imgFile);
                        /*deleted = imgFile.delete();
                        if (deleted) {
                            Toast toast1 = Toast.makeText(context, "Image Deleted", duration);
                            toast1.show();
                        } else {
                            Toast toast1 = Toast.makeText(context, "Image Not Deleted", duration);
                            toast1.show();
                        }*/

                        Intent data2 = new Intent();
                        data2.putExtra("path", imgFile.getPath());
                        setResult(RESULT_OK, data2);
                        finish();

                    }
                    break;
            }

        }
    }

    private String returnPhotoInBase64(File imgFile) {

        String encoded = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getPath(), options);
        return encoded;

        /*byte[] byteArray = null;
        try
        {
            InputStream inputStream = new FileInputStream(imgFile);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024*8];
            int bytesRead =0;

            while ((bytesRead = inputStream.read(b)) != -1)
            {
                bos.write(b, 0, bytesRead);
            }

            byteArray = bos.toByteArray();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return byteArray;*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mOrientationEventListener.disable();
    }
}
