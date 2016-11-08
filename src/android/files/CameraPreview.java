package ec.com.easysoft.bancamovil.capturacheque;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

/** A basic Camera preview class */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private final String TAG = "Preview";
    //private SurfaceView mSurfaceView;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Camera.Size mPreviewSize;
    private List<Camera.Size> mSupportedPreviewSizes;
    private List<Camera.Size> mSupportedPictureSizes;
    private double aspect_ratio;

    public boolean pictureSizeSupported; // Variable to set if picturseSize support the same aspect ratio as previewSize
    //RelativeLayout buttons;
    //private GlobalVar  gv = new GlobalVar();

    public CameraPreview(Context context, Camera camera, double aspectRatio) {
        super(context);
        setCamera(camera);

        aspect_ratio = aspectRatio;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        /*buttons = new RelativeLayout(context);
        RelativeLayout.LayoutParams b1 = (RelativeLayout.LayoutParams) new LayoutParams(
        RelativeLayout.LayoutParams.WRAP_CONTENT,
        RelativeLayout.LayoutParams.WRAP_CONTENT);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) new LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT,
                RelativeLayout.LayoutParams.FILL_PARENT);
        buttons.setLayoutParams(params);
        Button button = new Button(context);
        button.setText("Take Picture");
        button.setLayoutParams(new LayoutParams(10, 100));

        buttons.addView(button);
        b1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        b1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        button.setLayoutParams(b1);*/
//And you want to set some properties of the view
        //addView(buttons);

        //mSurfaceView = new SurfaceView(context);
        //addView(mSurfaceView);
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void setCamera(Camera camera){
        mCamera = camera;
        if(mCamera != null){
            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
            mSupportedPictureSizes = mCamera.getParameters().getSupportedPictureSizes();
            requestLayout();
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            setWillNotDraw(true);
            if(mCamera != null) {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            }

        } catch (IOException e) {
            Log.d("ERROR", "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //	Paint temp = gv.getPaint();
        //	temp.setTextSize(150);
        //  canvas.drawText(Integer.toString(gv.getSpeed()), 50, 100, gv.getPaint());
        Log.w(this.getClass().getName(), "On Draw Called");
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
        if(mCamera != null){
            mCamera.stopPreview();
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.



        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);

            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            if(parameters.getFocusMode() != null) {
                if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                }
            }
            if (parameters.getFlashMode() != null) {
                if (parameters.getSupportedFlashModes().contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                }
            }
            //parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            Camera.Size mPictureSize = getOptimalPictureSize(mSupportedPictureSizes);
            parameters.setPictureSize(mPictureSize.width, mPictureSize.height);
            requestLayout();

            mCamera.setParameters(parameters);
            //setCameraDisplayOrientation((Activity)mContext,0,mCamera);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d("ERROR", "Error starting camera preview: " + e.getMessage());
        }
    }

    /*public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }*/

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // We purposely disregard child measurements because act as a
        // wrapper to a SurfaceView that centers the camera preview instead
        // of stretching it.
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    private Camera.Size getOptimalPictureSize(List<Camera.Size> sizes){
        //Camera.Size optimalSize = sizes.get(sizes.size()-1);
        Camera.Size optimalSize = sizes.get(0);
        pictureSizeSupported = false;
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if(ratio == aspect_ratio){
                pictureSizeSupported = true;
                optimalSize = size;
                break;
            }
        }
        return optimalSize;
    }

    public void changeFlashMode(){
        try {
            mCamera.stopPreview();
            Camera.Parameters parameters = mCamera.getParameters();
            if (parameters.getFlashMode() != null) {
                if (parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_AUTO)) {
                    if (parameters.getSupportedFlashModes().contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    }
                } else {
                    if (parameters.getSupportedFlashModes().contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                    }
                }
            }
            mCamera.setParameters(parameters);
            mCamera.startPreview();
        }catch (Exception e){
            Log.d("ERROR", "Error change FlashMode camera preview: " + e.getMessage());
        }
    }

    public Boolean haveFlashMode(){
        Camera.Parameters parameters = mCamera.getParameters();
        if(parameters.getFlashMode() != null){
            return true;
        }else{
            return false;
        }
    }
}