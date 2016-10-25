package ec.com.easysoft;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;

import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;


public class DepositoCheque extends CordovaPlugin {


    private CallbackContext callbackContext;
    private boolean showInstructions;
    public static final int TAKE_PICTURE = 1;
    public static final int AUTHENTICATE = 2;
    public static final int AUTHENTICATELIVENESS = 3;

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

        showInstructions = args[0];

        if (action.equals("takePicture")) {
            cordova.setActivityResultCallback(this);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(showInstructions){
                        Context context = cordova.getActivity()
                                .getApplicationContext();
                        Intent intent = new Intent(context, InstructionsActivity.class);
                        //intent.putExtra("configuration", conf);
                        cordova.getActivity().startActivityForResult(intent, DepositoCheque.TAKE_PICTURE);
                    }else{
                        Context context = cordova.getActivity()
                                .getApplicationContext();
                        Intent intent = new Intent(context, MakePhotoActivity.class);
                        //intent.putExtra("configuration", conf);
                        cordova.getActivity().startActivityForResult(intent, DepositoCheque.TAKE_PICTURE);

                    }

                }
            });
            return true;
        }
        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == DepositoCheque.TAKE_PICTURE) {
            switch(resultCode){
                case Activity.RESULT_OK:
                break;
                case Activity.RESULT_CANCEL:
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
            resultado.put("imagen", plantilla);
            resultado.put("noMostrarInstrucciones",notShowAgain);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        callbackContext.success(resultado);
        return;

    }

}
