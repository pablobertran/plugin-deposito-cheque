package ec.com.easysoft;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.io.ByteArrayOutputStream;

import ec.com.easysoft.bancamovil.capturacheque.InstructionsActivity;
import ec.com.easysoft.bancamovil.capturacheque.MakePhotoActivity;
import ec.com.easysoft.bancamovil.capturacheque.PreviewPhotoActivity;


public class DepositoCheque extends CordovaPlugin {


    private CallbackContext callbackContext;
    private boolean showInstructions;
    public static final int TAKE_PICTURE = 1;
    public static final int INSTRUCTIONS = 2;

    JSONArray argumentos = null;

    /**
     * Constructor.
     */
    public DepositoCheque() {
    }

    /**
     * Sets the context of the Command. This can then be used to do things like
     * get file paths associated with the Activity.
     *
     * @param cordova The context of the main Activity.
     * @param webView The CordovaWebView Cordova is running in.
     */

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    public boolean execute(final String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {

        this.callbackContext = callbackContext;
        this.argumentos = args;

        showInstructions = args.getBoolean(0);
        showInstructions = true;

        if (action.equals("takePicture")) {
            cordova.setActivityResultCallback(this);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                        Context context = cordova.getActivity()
                                .getApplicationContext();
                        if(!showInstructions) {
                            Intent intent = new Intent(context, MakePhotoActivity.class);
                            cordova.getActivity().startActivityForResult(intent, DepositoCheque.TAKE_PICTURE);
                        }else{
                            Intent intent = new Intent(context, InstructionsActivity.class);
                            cordova.getActivity().startActivityForResult(intent, DepositoCheque.INSTRUCTIONS);
                        }

                }
            });
            return true;
        }
        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == DepositoCheque.TAKE_PICTURE) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    String path = data.getStringExtra("path");
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bmp = BitmapFactory.decodeFile(path,options);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                    byte[] b = baos.toByteArray();
                    String image = Base64.encodeToString(b, Base64.DEFAULT);
                    sendResultToApp("0000", "Capturada Correctamente", image, showInstructions);
                    break;
                case Activity.RESULT_CANCELED:
                    sendResultToApp("9998", "Capturada Cancelada", showInstructions);
                    break;
                default:
                    sendResultToApp("9999", "Error", showInstructions);
                    break;

            }
        }else if(requestCode == DepositoCheque.INSTRUCTIONS){
            switch (resultCode){
                case Activity.RESULT_OK:
                    showInstructions = data.getBooleanExtra("showInstructions",true);
                    cordova.setActivityResultCallback(this);
                    cordova.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Context context = cordova.getActivity()
                                    .getApplicationContext();
                                Intent intent = new Intent(context, MakePhotoActivity.class);
                                cordova.getActivity().startActivityForResult(intent, DepositoCheque.TAKE_PICTURE);

                        }
                    });
                    break;
                case Activity.RESULT_CANCELED:
                    sendResultToApp("9998", "Capturada Cancelada", showInstructions);
                    break;
                default:
                    sendResultToApp("9999", "Error", showInstructions);
                    break;
            }
        }
    }

    public void sendResultToApp(String code, String message, boolean notShowAgain) {
        JSONObject resultado = new JSONObject();
        try {
            resultado.put("codigoRetorno", code);
            resultado.put("mensaje", message);
            resultado.put("noMostrarInstrucciones",notShowAgain);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        callbackContext.success(resultado);
        return;
    }

    public void sendResultToApp(String code, String message, String imagenb64, boolean notShowAgain) {

        JSONObject resultado = new JSONObject();
        try {
            resultado.put("codigoRetorno", code);
            resultado.put("mensaje", message);
            resultado.put("imagen", imagenb64);
            resultado.put("noMostrarInstrucciones",notShowAgain);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        callbackContext.success(resultado);
        return;

    }

}
