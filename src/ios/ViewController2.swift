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
    
    @IBOutlet weak var ImageView: UIImageView!
    @IBAction func donePicture(_ sender: UIButton) {
        let imageCroppedData:NSData = UIImagePNGRepresentation(image)! as NSData
        strBase64 = imageCroppedData.base64EncodedString(options: [])
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
    
    
    
}
