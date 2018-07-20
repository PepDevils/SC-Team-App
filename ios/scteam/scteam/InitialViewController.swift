//
//  InitialViewController.swift
//  
//
//  Created by PEPDEVILS  on 25/01/2017.
//  Copyright © 2017 PEPDEVILS . All rights reserved.
//

import Foundation
import UIKit
import SDWebImage

class InitialViewController : UIViewController, UICollectionViewDelegate, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout {
    @IBOutlet weak var PageControl: UIPageControl!
    @IBOutlet weak var CollectionView: UICollectionView!
    @IBOutlet weak var bt_scbragatv: UIButton!
    var ArrayImages : [UIImage] = []
    @IBAction func bt_scbTV(_ sender: Any) {
        let myVC = storyboard?.instantiateViewController(withIdentifier: "SCTeamTV") as! ViewController
        myVC.src = srcTV
        print(srcTV)
        self.present(myVC, animated: true, completion: nil)
    }
    
    @IBOutlet weak var bt_scbragatvHeightConstraint: NSLayoutConstraint!
    @IBOutlet weak var bt_scbragatvWidthConstraint: NSLayoutConstraint!
        
    var ArrayImage : [String] = []
    var ArrayText : [String] = []
    var ArrayTitle : [String] = []
    var ArrayDate : [String] = []
    var ArrayVideo : [String] = []
    var ArrayGallery : [String] = []
    var srcTV = ""
    var srcTitle = ""
    var srcDate = ""
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        let cell = collectionView.cellForItem(at: indexPath) as! CollectionViewCell
        let myVC = storyboard?.instantiateViewController(withIdentifier: "News") as! NewsViewController
        myVC.newsTitle = cell.BottomLabel.text!
        myVC.newsDate = cell.lbl_date.text!
        myVC.image = cell.ImageView.image
        myVC.newsText = ArrayText[indexPath.row]
        myVC.newsVideo = ArrayVideo[indexPath.row]
        myVC.newsGallery = ArrayGallery[indexPath.row]
        self.present(myVC, animated: true, completion: nil)
    }

    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return CollectionView.frame.size
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "cell", for: indexPath) as! CollectionViewCell
        cell.ImageView.setShowActivityIndicator(true)
        cell.isUserInteractionEnabled = false
        if ArrayImage[indexPath.row].contains("http:") {
            ArrayImage[indexPath.row] = ArrayImage[indexPath.row].replacingOccurrences(of: "http:", with: "https:")
        }
        let url = URL(string: ArrayImage[indexPath.row].addingPercentEncoding(withAllowedCharacters: NSCharacterSet.urlQueryAllowed)!)
        cell.ImageView.sd_setImage(with: url, placeholderImage: UIImage(named: "Banner-logoSCTeam-Adeptos-"), options: .highPriority) { image, error, cacheType, imageURL in
            cell.isUserInteractionEnabled = true
        }
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
        dateFormatter.locale = Locale(identifier: "pt_PT")
        let date = dateFormatter.date(from: ArrayDate[indexPath.row])
        
        dateFormatter.dateFormat = "MMM dd | yyyy"
        let goodDate = dateFormatter.string(from: date!)
        cell.BottomLabel.text = ArrayTitle[indexPath.row]
        cell.lbl_date.text = goodDate.uppercased()
    
        if cell.lbl_background.layer.sublayers?.count != nil
        {
            cell.lbl_background.layer.sublayers?.removeAll()
        }

        cell.lbl_background.bounds.size.width = UIScreen.main.bounds.size.width
        cell.lbl_background.layer.insertSublayer(gradientBackground(frame: cell.lbl_background.bounds), at: 0)
        cell.lbl_background.backgroundColor = UIColor.clear
        
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return 7
    }
    
    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        PageControl.currentPage = Int(self.CollectionView.contentOffset.x) / Int(self.CollectionView.frame.size.width)
    }
    
    func gradientBackground(frame:CGRect) -> CAGradientLayer {
        let layer = CAGradientLayer()
        layer.frame = frame
        layer.startPoint = CGPoint(x: 0.5, y: 0)
        layer.endPoint = CGPoint(x: 0.5, y: 1)
        layer.colors = [UIColor.clear.cgColor, UIColor.black.withAlphaComponent(0.8).cgColor]
        return layer
    }
    
    override func viewDidLoad() {
        
        let value = UIInterfaceOrientation.portrait.rawValue
        UIDevice.current.setValue(value, forKey: "orientation")
        self.CollectionView.delegate = self
        self.CollectionView.dataSource = self
        self.CollectionView.isPagingEnabled = true
        if UIScreen.main.bounds.width > 500 {
            bt_scbragatvHeightConstraint.constant = 330
            bt_scbragatvWidthConstraint.constant = 330
        }
        else if UIScreen.main.bounds.width > 320 && UIScreen.main.bounds.width < 500 {
            bt_scbragatvHeightConstraint.constant = 270
            bt_scbragatvWidthConstraint.constant = 270
        }
        
        bt_scbragatv.layer.shadowColor = UIColor.black.cgColor
        bt_scbragatv.layer.shadowOpacity = 1
        bt_scbragatv.layer.shadowOffset = CGSize.zero
        bt_scbragatv.layer.shadowRadius = 20
    }
    
    override var shouldAutorotate: Bool {
        return true
    }
    
    override var supportedInterfaceOrientations: UIInterfaceOrientationMask {
        return UIInterfaceOrientationMask.portrait
    }
    
    override var prefersStatusBarHidden: Bool {
        return true
    }
}

extension String {
    init(htmlEncodedString: String) {
        self.init()
        
        guard let encodedData = htmlEncodedString.data(using: .unicode, allowLossyConversion: true) else {
            self = htmlEncodedString
            return
        }
        
        let attributedOptions : [String: Any] = [
            NSDocumentTypeDocumentAttribute: NSHTMLTextDocumentType]
        do {
            let attributedString = try NSAttributedString(data: encodedData, options: attributedOptions, documentAttributes: nil)
            self = attributedString.string
        } catch {
            self = htmlEncodedString
        }
    }
}
