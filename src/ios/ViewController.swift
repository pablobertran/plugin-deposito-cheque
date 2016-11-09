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

class ViewController: UIViewController {
    
    @IBOutlet var cameraContainerView: UIView!
    @IBOutlet weak var guiaImageVIew: UIImageView!
    
    var session: AVCaptureSession!
    var capturePreviewView: UIView!
    var capturePreviewLayer: AVCaptureVideoPreviewLayer!
    var captureQueue: OperationQueue!
    var plugin: DepositoCheque!
    
    
    @IBAction func capturePicture(_ sender: UIButton) {
        takePicture();
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Initialise the capture queue
        captureQueue = OperationQueue()
        
        NotificationCenter.default.addObserver(self, selector: #selector(orientationChanged(sender:)), name: NSNotification.Name.UIDeviceOrientationDidChange, object: nil)
        
    }
    
    func orientationChanged(sender: Notification){
        updateOrientation()
    }
    
    //    override var shouldAutorotate: Bool {
    //        return false
    //    }
    
    override func viewDidAppear(_ animated: Bool) {
        //let value = UIInterfaceOrientation.landscapeLeft.rawValue
        //UIDevice.current.setValue(value, forKey: "orientation")
        enableCapture()
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        
        captureQueue.cancelAllOperations()
        capturePreviewLayer.removeFromSuperlayer()
        for input in session.inputs {
            session.removeInput(input as! AVCaptureInput)
        }
        for output in session.outputs {
            session.removeOutput(output as! AVCaptureOutput)
        }
        session.stopRunning()
        session = nil
    }
    
    override var supportedInterfaceOrientations: UIInterfaceOrientationMask {
        return .landscape
    }
    
    override var prefersStatusBarHidden: Bool {
        return true
    }
    
    // MARK: - Helpers
    func enableCapture() {
        if (session != nil) { return }
        
        let operation = captureOperation()
        operation.completionBlock = {
            self.operationCompleted()
        }
        operation.queuePriority = .veryHigh
        captureQueue.addOperation(operation)
    }
    
    func captureOperation() -> BlockOperation {
        let operation = BlockOperation {
            self.session = AVCaptureSession()
            self.session.sessionPreset = AVCaptureSessionPresetPhoto
            let device = AVCaptureDevice.defaultDevice(withMediaType: AVMediaTypeVideo)
            
            let input: AVCaptureDeviceInput?
            do {
                input = try AVCaptureDeviceInput(device: device)
            } catch {
                input = nil
            }
            
            if (input == nil) { return }
            
            self.session.addInput(input)
            
            // Turn on point autofocus for middle of view
            do {
                try device?.lockForConfiguration()
            } catch {
                return
            }
            
            if (device?.isFocusModeSupported(.autoFocus))! {
                device?.focusPointOfInterest = CGPoint(x: 0.5, y: 0.5)
                device?.focusMode = .continuousAutoFocus
            }
            
            device?.unlockForConfiguration()
            
            self.capturePreviewLayer = AVCaptureVideoPreviewLayer(session: self.session)
            self.capturePreviewLayer.frame = self.cameraContainerView.bounds
            //self.capturePreviewLayer.frame = CGRect(x: 0, y: 0, width: 100, height: 100);
            self.capturePreviewLayer.videoGravity = AVLayerVideoGravityResizeAspectFill
            
            // Still Image Output
            let stillOutput = AVCaptureStillImageOutput()
            stillOutput.outputSettings = [AVVideoCodecKey: AVVideoCodecJPEG]
            self.session.addOutput(stillOutput)
        }
        
        return operation
    }
    
    func operationCompleted() {
        DispatchQueue.main.async {
            if (self.session == nil) { return }
            guard let device = self.currentDevice() else { return }
            
            self.capturePreviewView = UIView(frame: CGRect.zero)
            self.cameraContainerView.addSubview(self.capturePreviewView)
            //            self.capturePreviewView.snp_makeConstraints(closure: { (make) in
            //                make.edges.equalToSuperview()
            //            })
            self.capturePreviewView.layer.addSublayer(self.capturePreviewLayer)
            self.session.startRunning()
            if (device.hasFlash) {
                //self.updateFlashlightState()
                //self.flashButton.hidden = false
            }
            if (UIImagePickerController.isCameraDeviceAvailable(.front) && UIImagePickerController.isCameraDeviceAvailable(.rear)){
                //self.cameraButton.hidden = false
            }
        }
    }
    
    func currentDevice() -> AVCaptureDevice? {
        if (session == nil) { return nil }
        guard let inputDevice = session.inputs.first as? AVCaptureDeviceInput else { return nil }
        return inputDevice.device
    }
    
    override func viewWillLayoutSubviews() {
        updateOrientation()
    }
    
    
    func updateOrientation(){
        let orientation: UIInterfaceOrientation = UIApplication.shared.statusBarOrientation
        
        if(session != nil){
            
            switch (orientation) {
            case .portrait:
                capturePreviewLayer?.connection.videoOrientation = .portrait
            case .landscapeRight:
                capturePreviewLayer?.connection.videoOrientation = .landscapeRight
            case .landscapeLeft:
                capturePreviewLayer?.connection.videoOrientation = .landscapeLeft
            default:
                capturePreviewLayer?.connection.videoOrientation = .portrait
            }
        }
        
    }
    
    func takePicture() {
        //if (!cameraButton.enabled) { return }
        
        let output = session.outputs.last as! AVCaptureStillImageOutput
        guard let videoConnection = output.connections.last as? AVCaptureConnection else {
            return
        }
        //let imageDataSampleBuffer: CMSampleBuffer!
        //let error: NSError!
        
        //let orient: UIImageOrientation = self.currentImageOrientation();
        
        
        output.captureStillImageAsynchronously(from: videoConnection,completionHandler: {
            (imageDataSampleBuffer, error) in
            //self.cameraButton.enabled = true
            
            if (imageDataSampleBuffer == nil || error != nil) { return }
            
            let imageData = AVCaptureStillImageOutput.jpegStillImageNSDataRepresentation(imageDataSampleBuffer)
            
            let outputRec: CGRect = self.capturePreviewLayer.metadataOutputRectOfInterest(for: self.capturePreviewLayer.bounds)
            let cgImageR: CGImage = UIImage(data: imageData!)!.cgImage!
            
            let ratio : CGFloat = (CGFloat)(cgImageR.width) / self.cameraContainerView.frame.width
            //let offsetX = self.guiaImageVIew.convert(self.guiaImageVIew.frame, from:self.cameraContainerView).origin.x * ratio;
            
            let offsetX = (self.guiaImageVIew.frame.origin.x - self.cameraContainerView.frame.origin.x) * ratio;
            let offsetY = (self.guiaImageVIew.frame.origin.y - self.cameraContainerView.frame.origin.y) * ratio;
            
            let widthToCrop = (Int)((self.cameraContainerView.frame.size.width - self.guiaImageVIew.frame.size.width) * ratio);
            let heightToCrop = (Int)((self.cameraContainerView.frame.size.height - self.guiaImageVIew.frame.size.height) * ratio);
            
            
            let cropRect: CGRect = CGRect(x: (Int(outputRec.origin.x * CGFloat(cgImageR.width) + offsetX)), y:(Int(outputRec.origin.y * CGFloat(cgImageR.height) + offsetY)), width:(Int(outputRec.size.width*CGFloat(cgImageR.width)) - widthToCrop), height: (Int(outputRec.size.height*CGFloat(cgImageR.height))) - heightToCrop)
            
            let cgImageResult: CGImage = cgImageR.cropping(to: cropRect)!;
            let image = UIImage(cgImage: cgImageResult, scale: 1.0, orientation: self.currentImageOrientation())
            
            //self.handleImage(image: image)
            let storyBoard : UIStoryboard = UIStoryboard(name: "DepositoCheque", bundle:nil)
            let controller = storyBoard.instantiateViewController(withIdentifier: "ViewControllerTwo") as! ViewController2
            controller.image = image
            controller.plugin = self.plugin
            
            self.present(controller, animated: true, completion: nil)
            
        })
        
        //cameraButton.enabled = false
    }
    
    //    func handleImage(image: UIImage) {
    //        //let navController: UINavigationController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "ImageViewerStoryboardIdentifier") as! UINavigationController
    //
    //        let viewController: ImageViewerViewController = navController.viewControllers.first as! ImageViewerViewController
    //        viewController.image = image
    //        viewController.navigationItem.rightBarButtonItem = UIBarButtonItem(barButtonSystemItem: .Done, target: self, action: #selector(doneBarButtonWasTouched(_:)))
    //
    //        self.present(navController, animated: true, completion: nil)
    //    }
    
    func currentImageOrientation() -> UIImageOrientation {
        //let deviceOrientation = UIDevice.current.orientation
        
        let deviceOrientation: UIInterfaceOrientation = UIApplication.shared.statusBarOrientation
        
        let imageOrientation: UIImageOrientation
        
        let input = session.inputs.first as! AVCaptureDeviceInput
        if (input.device.position == .back) {
            switch (deviceOrientation) {
            case .landscapeLeft:
                imageOrientation = .down
                break
                
            case .landscapeRight:
                imageOrientation = .up
                break
                
            case .portraitUpsideDown:
                imageOrientation = .left
                break
                
            default:
                imageOrientation = .right
                break
            }
        } else {
            switch (deviceOrientation) {
            case .landscapeLeft:
                imageOrientation = .downMirrored
                break
                
            case .landscapeRight:
                imageOrientation = .upMirrored
                break
                
            case .portraitUpsideDown:
                imageOrientation = .rightMirrored
                break
                
            default:
                imageOrientation = .leftMirrored
                break
            }
        }
        
        return imageOrientation
    }
    
    
}

