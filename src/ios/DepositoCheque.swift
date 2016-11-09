import Foundation

@objc(DepositoCheque) class DepositoCheque : CDVPlugin {
    
    var _command : CDVInvokedUrlCommand!
    var notShowInstructions: Bool = false
    
    func takePicture(_ command: CDVInvokedUrlCommand) {
        //    var pluginResult = CDVPluginResult(
        //      status: CDVCommandStatus_ERROR
        //    )
        
        _command = command
        
        notShowInstructions = command.arguments[0] as? Bool ?? false
        let side: String = command.arguments[1] as? String ?? ""
        
        
        var reverso = false
        if(side == "reverso"){
            reverso = true
        }
        
        
        
        if(notShowInstructions){
            let storyboard = UIStoryboard(name: "DepositoCheque", bundle: nil)
            
            let controller = storyboard.instantiateViewController(withIdentifier: "Preview") as! ViewController
            controller.reverso = reverso
            controller.plugin = self
            self.viewController.present(controller, animated: true, completion: nil)
        }else{
            let storyboard = UIStoryboard(name: "DepositoCheque", bundle: nil)
            let controller = storyboard.instantiateViewController(withIdentifier: "Instructions") as! InstructionsViewController
            controller.reverso = reverso
            controller.plugin = self
            self.viewController.present(controller, animated: true, completion: nil)
        }
        
        
    }
    
    func responseFromPlugin(code : String, message: String, imageBase64:String){
        
        
        let jsonObj:[String: Any] = [
            "codigoRetorno":code,
            "mensaje": message,
            "imagen": imageBase64,
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
    
    func responseFromPlugin(code : String, message: String){
        let jsonObj:[String: Any] = [
            "codigoRetorno":code,
            "mensaje": message,
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
    
    func close(){
        self.responseFromPlugin(code: "9998", message: "Captura Cancelada")
    }
    
    
    
}
