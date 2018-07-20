//
//  TutorialView.swift
//  
//
//  Created by PEPDEVILS  on 16/03/2017.
//  Copyright © 2017 PEPDEVILS . All rights reserved.
//

import UIKit
import ImageIO
import Gifu


class TutorialView : UIViewController {
    
    @IBOutlet weak var ImageViewGIF: GIFImageView!
    @IBOutlet weak var bt_ok_outlet: UIButton!
    @IBOutlet weak var bt_remover_outlet: UIButton!
    @IBAction func bt_ok(_ sender: Any) {
        self.view.removeFromSuperview()
    }
    @IBAction func bt_remover(_ sender: Any) {
        let defaults = UserDefaults.standard
        defaults.set("false", forKey: "Tutorial")
        defaults.synchronize()
        self.view.removeFromSuperview()
    }
    
    override func viewDidLoad() {
        ImageViewGIF.animate(withGIFNamed: "SCTeam_APP_Refresh_1")
        bt_ok_outlet.layer.cornerRadius = 6
        bt_remover_outlet.layer.cornerRadius = 6
    }
}

extension UIImageView: GIFAnimatable {
    private struct AssociatedKeys {
        static var AnimatorKey = "gifu.animator.key"
    }
    
    override open func display(_ layer: CALayer) {
        updateImageIfNeeded()
    }
    
    public var animator: Animator? {
        get {
            guard let animator = objc_getAssociatedObject(self, &AssociatedKeys.AnimatorKey) as? Animator else {
                let animator = Animator(withDelegate: self)
                self.animator = animator
                return animator
            }
            
            return animator
        }
        
        set {
            objc_setAssociatedObject(self, &AssociatedKeys.AnimatorKey, newValue as Animator?, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
        }
    }
}
