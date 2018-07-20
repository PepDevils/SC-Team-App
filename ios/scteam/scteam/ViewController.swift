//
//  ViewController.swift
//  
//
//  Created by PEPDEVILS  on 25/01/2017.
//  Copyright © 2017 PEPDEVILS . All rights reserved.
//

import UIKit
import AVKit
import AVFoundation
import WebKit
import ImageIO
import SCLAlertView
import MediaPlayer

class ViewController: UIViewController, UIWebViewDelegate, UIPopoverControllerDelegate, UIPopoverPresentationControllerDelegate {

    @IBOutlet weak var wv: UIWebView!
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!
    @IBOutlet weak var scrollView: UIScrollView!
    @IBOutlet weak var lbl_top: UILabel!
    @IBOutlet weak var image_logo: UIImageView!
    @IBOutlet weak var lbl_topHeightConstraint: NSLayoutConstraint!
    @IBOutlet weak var logo_heightConstraint: NSLayoutConstraint!
    @IBOutlet weak var logo_widthConstrain: NSLayoutConstraint!
    @IBOutlet weak var image_logo_center_Constraint: NSLayoutConstraint!
    @IBOutlet weak var bt_gobackHeightConstraint: NSLayoutConstraint!
    @IBOutlet weak var bt_gobackWidthConstraint: NSLayoutConstraint!
    @IBOutlet weak var bt_gobackTopConstraint: NSLayoutConstraint!
    @IBOutlet weak var scrollViewTopConstraint: NSLayoutConstraint!
    
    var refreshControl : UIRefreshControl!
    var src = ""
    var token = ""
    let popView = TutorialView(nibName: "TutorialView", bundle: nil)
    override func viewDidLoad() {
        super.viewDidLoad()
        let defaults = UserDefaults.standard
        token = defaults.string(forKey: "Tutorial")!
        if token == "true" {
            openPopView()
        }
        
        activityIndicator.startAnimating()
        wv.isUserInteractionEnabled = false
        wv.delegate = self
        refreshControl = UIRefreshControl()
        //let dict : NSDictionary = [NSForegroundColorAttributeName : UIColor.white]
        //refreshControl.attributedTitle = NSAttributedString(string: "SC Team", attributes: dict as? [String : Any])//NSAttributedString(string: "SC Team")
        refreshControl.tintColor = UIColor.red
        refreshControl.addTarget(self, action: #selector(refreshView), for: .valueChanged)
        scrollView.insertSubview(refreshControl, at: 0)
        self.loadYoutube(videoID: URL(string: self.src)!)
        constraintCenterY = NSLayoutConstraint(item: image_logo, attribute: .centerY, relatedBy: .equal, toItem: lbl_top, attribute: .centerY, multiplier: 1, constant: lbl_topHeightConstraint.constant/2 + 10)
        constraintTrailing = NSLayoutConstraint(item: image_logo, attribute: .trailing, relatedBy: .equal, toItem: self.view, attribute: .trailingMargin, multiplier: 1, constant: 0)
        constraintCenterX = NSLayoutConstraint(item: image_logo, attribute: .centerX, relatedBy: .equal, toItem: self.view, attribute: .centerX, multiplier: 1, constant: 0)
        let value = UIInterfaceOrientation.portrait.rawValue//.landscapeRight.rawValue
        UIDevice.current.setValue(value, forKey: "orientation")
//        image_logo.translatesAutoresizingMaskIntoConstraints = false
//        lbl_topHeightConstraint.constant = 50
//        logo_widthConstrain.constant = 40
//        logo_heightConstraint.constant = 40
//        bt_gobackHeightConstraint.constant = 25
//        bt_gobackWidthConstraint.constant = 25
//        bt_gobackTopConstraint.constant = 10
//        scrollViewTopConstraint.constant = 0
        image_logo.layer.shadowColor = UIColor.black.cgColor
        image_logo.layer.shadowOpacity = 1
        image_logo.layer.shadowOffset = CGSize.zero
        image_logo.layer.shadowRadius = 20
//        if image_logo_center_Constraint != nil {
//            image_logo_center_Constraint.isActive = false
//        }
//        NSLayoutConstraint.deactivate([constraintCenterX])
        NSLayoutConstraint.activate([constraintCenterY])
    }
    
    func openPopView() {
        view.endEditing(true)
        popView.modalPresentationStyle = UIModalPresentationStyle.popover
        popView.preferredContentSize = CGSize(width: self.view.frame.width-50, height: self.view.frame.height-50)
        popView.view.frame.size.width = self.view.frame.width - 50
        popView.view.frame.size.height = self.view.frame.height - 60
        popView.view.frame.origin.x = self.view.frame.width/2 - popView.view.frame.width/2
        popView.view.frame.origin.y = 30
        popView.view.layer.borderColor = UIColor(red: 200/255, green: 0/255, blue: 0/255, alpha: 1).cgColor
        popView.view.layer.borderWidth = 2
        popView.view.layer.cornerRadius = 10
        let popoverPresentationController = popView.popoverPresentationController
        popoverPresentationController?.sourceView = self.view
        popoverPresentationController?.sourceRect = CGRect(x: self.view.bounds.midX, y: self.view.bounds.midY,width: 0,height: 0)
        popoverPresentationController?.permittedArrowDirections = UIPopoverArrowDirection()
        popoverPresentationController?.delegate = self
        self.view.addSubview(popView.view)
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        popView.view.removeFromSuperview()
    }
    
    override func willRotate(to toInterfaceOrientation: UIInterfaceOrientation, duration: TimeInterval) {
        popView.dismiss(animated: true, completion: nil)
    }
    
    func refreshView(refresh : UIRefreshControl) {
        let deadlineTime = DispatchTime.now() + .seconds(5)
        DispatchQueue.main.asyncAfter(deadline: deadlineTime) {
            self.activityIndicator.startAnimating()
            self.wv.isUserInteractionEnabled = false
            self.wv.stopLoading()
            URLCache.shared.removeAllCachedResponses()
            URLCache.shared.diskCapacity = 0
            URLCache.shared.memoryCapacity = 0
            self.loadYoutube(videoID: URL(string: self.src)!)
            refresh.endRefreshing()
        }
    }
    
    override func viewDidAppear(_ animated: Bool) {
        scrollView.contentSize.height = UIScreen.main.bounds.height
        //imageView.frame = refreshControl.bounds
        //imageView.frame.size.width = 200
        //imageView.center.x = self.view.center.x
        print(scrollView.contentSize.height)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    func webViewDidFinishLoad(_ webView: UIWebView) {
        if !wv.isLoading {
            activityIndicator.stopAnimating()
            wv.isUserInteractionEnabled = true
            let html = wv.stringByEvaluatingJavaScript(from: "document.body.innerHTML")!
            print(wv.stringByEvaluatingJavaScript(from: "document.body.innerHTML")!)
            if !html.contains("\" style=\"display: none;\"><div class=\"PlayerNoSignal__container___3GJmO\" data-reactid=\".3\"><h3 data-reactid=\".3.0\">There seems to be a problem with the video.</h3></div>") {
                let appearance = SCLAlertView.SCLAppearance(
                    kCircleTopPosition: 0,
                    kCircleIconHeight: 55,
                    kTitleFont: UIFont(name: "Roboto-Regular", size: 20)!,
                    kTextFont: UIFont(name: "Roboto-Regular", size: 14)!,
                    kButtonFont: UIFont(name: "Roboto-Bold", size: 14)!,
                    showCloseButton: true,
                    showCircularIcon: true,
                    contentViewCornerRadius: 10)
                let alertView = SCLAlertView(appearance: appearance)
                let alertViewIcon = UIImage(named: "LogoMarca-SCTeamTV") //Replace the IconImage text with the image name
                //alertView.showCustom("Conexão internet", subTitle: "Verifique a sua conexão à internet", color: UIColor.red, icon: alertViewIcon!)
                alertView.showError("Aviso", subTitle: "Ocorreu um erro, por favor tente mais tarde.", closeButtonTitle: "OK", circleIconImage: alertViewIcon)
            }
        }
    }
    
    func webView(_ webView: UIWebView, didFailLoadWithError error: Error) {
        let appearance = SCLAlertView.SCLAppearance(
            kCircleTopPosition: 0,
            kCircleIconHeight: 55,
            kTitleFont: UIFont(name: "Roboto-Regular", size: 20)!,
            kTextFont: UIFont(name: "Roboto-Regular", size: 14)!,
            kButtonFont: UIFont(name: "Roboto-Bold", size: 14)!,
            showCloseButton: false,
            showCircularIcon: true,
            contentViewCornerRadius: 10)
        let alertView = SCLAlertView(appearance: appearance)
        alertView.addButton("Ok", action: { // create button on alert
            exit(0) // action on click
        })
        let alertViewIcon = UIImage(named: "LogoMarca-SCTeamTV") //Replace the IconImage text with the image name
        //alertView.showCustom("Conexão internet", subTitle: "Verifique a sua conexão à internet", color: UIColor.red, icon: alertViewIcon!)
        alertView.showError("Aviso", subTitle: "Verifique a sua conexão à internet", circleIconImage: alertViewIcon)
    }
    
    func loadYoutube(videoID:URL) {
        print(videoID)
        let request = URLRequest(url: videoID)
        wv.loadRequest(request)
        //wv.loadHTMLString(embededHTML, baseURL: Bundle.main.bundleURL)
        wv.scrollView.isScrollEnabled = false
    }
    
    @IBAction func goBack(_ sender: Any) {
        wv.stopLoading()
        wv.removeFromSuperview()
        print(URLCache.shared.memoryCapacity)
        URLCache.shared.removeAllCachedResponses()
        
        URLCache.shared.diskCapacity = 0
        print(URLCache.shared.memoryCapacity)
        URLCache.shared.memoryCapacity = 0
        wv.delegate = nil
        self.dismiss(animated: true, completion: nil)
    }
    
    var constraintCenterY : NSLayoutConstraint!
    var constraintTrailing : NSLayoutConstraint!
    var constraintCenterX : NSLayoutConstraint!
    
    override func viewWillTransition(to size: CGSize, with coordinator: UIViewControllerTransitionCoordinator) {
        image_logo.translatesAutoresizingMaskIntoConstraints = false
        if UIDevice.current.orientation.isLandscape {
            lbl_topHeightConstraint.constant = 50
            logo_widthConstrain.constant = 40
            logo_heightConstraint.constant = 40
            bt_gobackHeightConstraint.constant = 25
            bt_gobackWidthConstraint.constant = 25
            bt_gobackTopConstraint.constant = 10
            scrollViewTopConstraint.constant = 0
            if image_logo_center_Constraint != nil {
                image_logo_center_Constraint.isActive = false
            }
            constraintCenterY.constant = 0
            NSLayoutConstraint.deactivate([constraintCenterX])
            NSLayoutConstraint.activate([constraintTrailing])
        } else {
            lbl_topHeightConstraint.constant = 70
            logo_widthConstrain.constant = 120
            logo_heightConstraint.constant = 120
            bt_gobackHeightConstraint.constant = 30
            bt_gobackWidthConstraint.constant = 30
            bt_gobackTopConstraint.constant = 20
            scrollViewTopConstraint.constant = 50
            image_logo.layer.shadowColor = UIColor.black.cgColor
            image_logo.layer.shadowOpacity = 1
            image_logo.layer.shadowOffset = CGSize.zero
            image_logo.layer.shadowRadius = 20
            constraintCenterY.constant = lbl_topHeightConstraint.constant/2 + 10
            NSLayoutConstraint.deactivate([constraintTrailing])
            NSLayoutConstraint.activate([constraintCenterX])
        }
        super.viewWillTransition(to: size, with: coordinator)
        coordinator.animate(alongsideTransition: nil, completion: { _ in
            self.scrollView.contentSize.height = size.height
        })
    }
    
    override var prefersStatusBarHidden: Bool {
        return true
    }
    
}
