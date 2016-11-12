//
//  ViewController2.swift
//  DepositoChequeSwift
//
//  Created by Eduardo Gallegos on 28/10/16.
//  Copyright © 2016 Eduardo Gallegos. All rights reserved.
//

import UIKit

class ViewController2: UIViewController, CAAnimationDelegate {
    
    var image: UIImage!
    var plugin: DepositoCheque!
    var viewController1 : ViewController!
    var strBase64: String!
    var reverso: Bool = false
    
    @IBOutlet weak var ImageView: UIImageView!
    @IBAction func donePicture(_ sender: UIButton) {
        let imageCroppedData:NSData = UIImagePNGRepresentation(image)! as NSData
        strBase64 = imageCroppedData.base64EncodedString(options: [])
        //self.dismiss(animated: true, completion: nil)
        //self.modalTransitionStyle = .crossDissolve
        ImageView.isHidden = true
        self.view.isHidden = true
        let transition: CATransition = CATransition()
        transition.duration = 0.1
        transition.timingFunction = CAMediaTimingFunction(name: kCAMediaTimingFunctionEaseInEaseOut)
        transition.type = kCATransitionReveal
        transition.subtype = kCATransitionFade
        transition.fillMode = kCAFillModeForwards
        transition.delegate = self
        self.view.window!.backgroundColor = UIColor.white
        self.view.window!.layer.add(transition, forKey: nil)
        self.plugin.viewController.view.backgroundColor = UIColor.white
        self.viewController1.view.backgroundColor = UIColor.white
        self.viewController1.view.isHidden = true
        self.view.window!.rootViewController?.view.backgroundColor = UIColor.white
        self.plugin.responseFromPlugin(code: "0000", message: "Capturada Correctamente", imageBase64: strBase64)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //let value = UIInterfaceOrientation.portrait.rawValue
        //UIDevice.current.setValue(value, forKey: "orientation")
        ImageView.image = image
        // Do any additional setup after loading the view, typically from a nib.
        
        
        
    }
    
    func animationDidStop(_ anim: CAAnimation, finished flag: Bool) {
        
        let transition1: CATransition = CATransition()
        transition1.duration = 1
        transition1.timingFunction = CAMediaTimingFunction(name: kCAMediaTimingFunctionEaseInEaseOut)
        transition1.type = kCATransitionReveal
        transition1.subtype = kCATransitionFade
        transition1.fillMode = kCAFillModeRemoved
        self.view.window!.layer.add(transition1, forKey: nil)
        self.view.window!.rootViewController?.dismiss(animated: false, completion: nil)
        
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
