//
//  ViewController2.swift
//  DepositoChequeSwift
//
//  Created by Eduardo Gallegos on 28/10/16.
//  Copyright © 2016 Eduardo Gallegos. All rights reserved.
//

import UIKit

class ViewController2: UIViewController {
    
    var image: UIImage!
    var plugin: DepositoCheque!
    var strBase64: String!
    var reverso: Bool = false
    
    @IBOutlet weak var ImageView: UIImageView!
    @IBAction func donePicture(_ sender: UIButton) {
        let imageCroppedData:NSData = UIImagePNGRepresentation(image)! as NSData
        strBase64 = imageCroppedData.base64EncodedString(options: [])
        //self.dismiss(animated: true, completion: nil)
        //self.modalTransitionStyle = .crossDissolve
        let transition: CATransition = CATransition()
        transition.duration = 0.5
        transition.timingFunction = CAMediaTimingFunction(name: kCAMediaTimingFunctionEaseInEaseOut)
        transition.type = kCATransitionReveal
        transition.subtype = kCATransitionFade
        self.view.window!.layer.add(transition, forKey: nil)
        self.view.window!.rootViewController?.dismiss(animated: false, completion: nil)
        self.plugin.responseFromPlugin(code: "0000", message: "Capturada Correctamente", imageBase64: strBase64)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //let value = UIInterfaceOrientation.portrait.rawValue
        //UIDevice.current.setValue(value, forKey: "orientation")
        ImageView.image = image
        // Do any additional setup after loading the view, typically from a nib.
        
        
        
    }
    
    override var supportedInterfaceOrientations: UIInterfaceOrientationMask {
        return .landscape
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "backToTakePicture" {
            let viewController:ViewController = segue.destination as! ViewController
            viewController.plugin = self.plugin
            viewController.reverso = self.reverso
        }
        
    }
    
    
    
}
