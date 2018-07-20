//
//  LaunchViewController.swift
//  
//
//  Created by PEPDEVILS  on 09/02/2017.
//  Copyright © 2017 PEPDEVILS . All rights reserved.
//

import Foundation
import UIKit
import SCLAlertView

class LaunchViewController : UIViewController {
    
    @IBOutlet weak var lbl_background: UILabel!
    @IBOutlet weak var scbraga_logo: UIImageView!
    
    func gradient(frame:CGRect) -> CAGradientLayer {
        let layer = CAGradientLayer()
        layer.frame = frame
        layer.startPoint = CGPoint(x: 0.5, y: 0)
        layer.endPoint = CGPoint(x: 0.5, y: 1)
        layer.colors = [UIColor(red: 180/255,green: 0/255,blue: 0/255,alpha: 1).cgColor, UIColor(red: 90/255,green: 0/255,blue: 0/255,alpha: 1).cgColor]
        return layer
    }
    
    var ArrayImage : [String] = []
    var ArrayText : [String] = []
    var ArrayTitle : [String] = []
    var ArrayDate : [String] = []
    var srcTV = ""
    var srcTitle = ""
    var srcDate = ""
    var srcText = ""
    var Internet : Bool = false
    var ArrayVideo : [String] = []
    var ArrayImages : [String] = []
    
    @IBOutlet weak var scbragatvWidthConstraint: NSLayoutConstraint!
    @IBOutlet weak var scbragatvHeightConstraint: NSLayoutConstraint!
    override func viewDidLoad() {
        let value = UIInterfaceOrientation.portrait.rawValue
        UIDevice.current.setValue(value, forKey: "orientation")
        
        if UIScreen.main.bounds.width > 500 {
            scbragatvWidthConstraint.constant = 300
            scbragatvHeightConstraint.constant = 300
        } else if UIScreen.main.bounds.width > 320 && UIScreen.main.bounds.width < 500 {
            scbragatvWidthConstraint.constant = 200
            scbragatvHeightConstraint.constant = 200
        }
        let url = NSURL(string: "https://xxxxxx.xx/xxx/mobile_api.php?function=debug".addingPercentEncoding(withAllowedCharacters: NSCharacterSet.urlQueryAllowed)!)
        let request = NSMutableURLRequest(url : url! as URL,cachePolicy: .reloadIgnoringLocalAndRemoteCacheData, timeoutInterval: 10.0 * 1000)
        let session = URLSession.shared
        request.httpMethod = "GET"
        let task = session.dataTask(with: request as URLRequest, completionHandler: {data, response, error -> Void in
            
            DispatchQueue.main.async(execute: {
                if data == nil
                {
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
                    self.Internet = true
                    let alertViewIcon = UIImage(named: "LogoMarca-SCTeamTV") //Replace the IconImage text with the image name
                    alertView.showError("Aviso", subTitle: "Verifique a sua conexão à internet", circleIconImage: alertViewIcon)
                }
                else
                {
                    if let json = try? JSONSerialization.jsonObject(with: data!, options: []) as! [String: AnyObject]{
                        self.srcTV = json["url_tv"] as! String
                        let jsonAux = json["news"] as! NSArray
                        for i in 0..<jsonAux.count
                        {
                            let jsonAuxAux = jsonAux[i] as! [String:AnyObject]
                            
                            if let image = jsonAuxAux["image"] as? String {
                                self.ArrayImage.append(image)
                            } else {
                                self.ArrayImage.append("https://xxxxxxxxx.xx/wp-content/uploads/2016/11/scteam-logo.png")
                            }
                            if let text = jsonAuxAux["post_content"] as? String {
                                self.ArrayText.append(text)
                            }
                            if let date = jsonAuxAux["post_date"] as? String {
                                self.ArrayDate.append(date)
                            }
                            if let title = jsonAuxAux["post_title"] as? String {
                                self.ArrayTitle.append(title)
                            }
                            
                            if let video = jsonAuxAux["video"] as? String {
                                if video == "null"{
                                    self.ArrayVideo.append("NoVideo")
                                } else {
                                    self.ArrayVideo.append(video)
                                }
                            }
                            
                            if let gallery = jsonAuxAux["galeria"] as? NSArray {
                                if gallery.count == 0 {
                                    self.ArrayImages.append("NoGallery")
                                } else {
                                    let string = gallery.componentsJoined(by: ",")
                                    self.ArrayImages.append(string)
                                }
                            } else {
                                self.ArrayImages.append("NoGallery")
                            }
                        }
                        
                    } else {
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
                        alertView.showError("Aviso", subTitle: "Verifique a sua conexão à internet", closeButtonTitle: "Ok", circleIconImage: alertViewIcon)
                        self.Internet = true
                    }
                }
            })
        })
        task.resume()
        self.animateLogo()
    }
    var i : Int = 0
    func animateLogo() {
        UIView.animate(withDuration: 1, animations: {
            self.scbraga_logo.alpha = 1
        }, completion: { finished in
            UIView.animate(withDuration: 1, animations: {
                self.scbraga_logo.alpha = 0.5
            }, completion: { finished in
                UIView.animate(withDuration: 1, animations: {
                    self.scbraga_logo.alpha = 1
                }, completion: { finished in
                    UIView.animate(withDuration: 1, animations: {
                        self.scbraga_logo.alpha = 0.5
                    }, completion: { finished in
                        UIView.animate(withDuration: 1, animations: {
                            self.scbraga_logo.alpha = 1
                        }, completion: { finished in
                            if (self.ArrayDate.isEmpty || self.ArrayImage.isEmpty || self.ArrayTitle.isEmpty || self.ArrayText.isEmpty || self.srcTV == "" || self.ArrayVideo.isEmpty || self.ArrayImages.isEmpty){
                                if self.i < 3 {
                                    self.animateLogo()
                                    self.i += 1
                                } else {
                                    if self.Internet {
                                        
                                    } else {
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
                                        alertView.showError("Aviso", subTitle: "Verifique a sua conexão à internet e volte a iniciar a aplicação.", closeButtonTitle: "Ok", circleIconImage: alertViewIcon)
                                    }
                                }
                            } else {
                                let vc = self.storyboard?.instantiateViewController(withIdentifier: "SCTeamInitial") as! InitialViewController
                                vc.ArrayDate = self.ArrayDate
                                vc.ArrayImage = self.ArrayImage
                                vc.ArrayTitle = self.ArrayTitle
                                vc.ArrayText = self.ArrayText
                                vc.srcTV = self.srcTV
                                vc.ArrayVideo = self.ArrayVideo
                                vc.ArrayGallery = self.ArrayImages
                                self.present(vc, animated: true, completion: nil)
                            }
                        })
                    })
                })
            })
        })
    }
    override func viewDidAppear(_ animated: Bool) {
        lbl_background.layer.insertSublayer(gradient(frame: lbl_background.frame), at: 0)
    }
    override var shouldAutorotate: Bool {
        return false
    }
    
    override var supportedInterfaceOrientations: UIInterfaceOrientationMask {
        return UIInterfaceOrientationMask.portrait
    }
    
    override var prefersStatusBarHidden: Bool {
        return true
    }
}
