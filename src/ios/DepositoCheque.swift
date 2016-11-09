import Foundation

@objc(DepositoCheque) class DepositoCheque : CDVPlugin {
    
    var _command : CDVInvokedUrlCommand!
    
    func takePicture(_ command: CDVInvokedUrlCommand) {
        //    var pluginResult = CDVPluginResult(
        //      status: CDVCommandStatus_ERROR
        //    )
        
        //let msg = command.arguments[0] as? String ?? ""
        _command = command
        
        let storyboard = UIStoryboard(name: "DepositoCheque", bundle: nil)
        //let controller = storyboard.withIdentifier: "someViewController") as! UIViewController
        let controller = storyboard.instantiateViewController(withIdentifier: "Preview") as! ViewController
        controller.plugin = self
        self.viewController.present(controller, animated: true, completion: nil)
        
        //    if msg.characters.count > 0 {
        //      /* UIAlertController is iOS 8 or newer only. */
        //      let toastController: UIAlertController =
        //        UIAlertController(
        //          title: "",
        //          message: msg,
        //          preferredStyle: .alert
        //        )
        //
        //      self.viewController?.present(
        //        toastController,
        //        animated: true,
        //        completion: nil
        //      )
        //
        //      let duration = Double(NSEC_PER_SEC) * 3.0
        //
        //      DispatchQueue.main.asyncAfter(
        //        deadline: DispatchTime.now() + Double(Int64(duration)) / Double(NSEC_PER_SEC),
        //        execute: {
        //          toastController.dismiss(
        //            animated: true,
        //            completion: nil
        //          )
        //        }
        //      )
        //
        //      pluginResult = CDVPluginResult(
        //        status: CDVCommandStatus_OK,
        //        messageAs: msg
        //      )
        //    }
        
        //    self.commandDelegate!.send(
        //      pluginResult, 
        //      callbackId: command.callbackId
        //    )
    }
    
    func responseFromPlugin(code : String, message: String, imageBase64:String, notShowInstructions: Bool){
        
        
        let jsonObj:[String: Any] = [
            "codigoRetorno":code,
            "mensaje": message,
            "imagenb": imageBase64,
            "noMostrarInstrucciones": notShowInstructions
        ]
        
        let pluginResult = CDVPluginResult(
            status: CDVCommandStatus_OK,
            messageAs: jsonObj
        )
        self.commandDelegate!.send(
            pluginResult,
            callbackId: _command.callbackId
        )
        self.viewController.dismiss(animated: true, completion: nil);
    }
    
}
