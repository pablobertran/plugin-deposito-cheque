//
//  ViewController.swift
//  DepositoChequeSwift
//
//  Created by Eduardo Gallegos on 28/10/16.
//  Copyright © 2016 Eduardo Gallegos. All rights reserved.
//

import UIKit
import AVFoundation
//import SnapKit

class InstructionsViewController: UIViewController {
    
    var plugin: DepositoCheque!
    var reverso: Bool = false
    
    @IBOutlet weak var checkbox: UIButton!
    @IBAction func checkboxTouched(_ sender: UIButton) {
        if(sender.currentImage?.isEqual(UIImage(named:"checked.png")))!{
            sender.setImage(UIImage(named:"unchecked.png"), for: .normal)
        }else{
            sender.setImage(UIImage(named:"checked.png"), for: .normal)
        }
    }
    
    @IBAction func Continuar(_ sender: UIButton) {
        
        if(checkbox.currentImage?.isEqual(UIImage(named:"checked.png")))!{
            plugin.notShowInstructions = true
        }
        
        let storyboard = UIStoryboard(name: "DepositoCheque", bundle: nil)
        
        let controller = storyboard.instantiateViewController(withIdentifier: "Preview") as! ViewController
        controller.reverso = reverso
        controller.plugin = plugin
        self.present(controller, animated: true, completion: nil)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
    }
    
    
    override func viewDidAppear(_ animated: Bool) {
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        
    }
    
    override var supportedInterfaceOrientations: UIInterfaceOrientationMask {
        return .landscape
    }
    
    override var prefersStatusBarHidden: Bool {
        return true
    }
    
    
    
}

