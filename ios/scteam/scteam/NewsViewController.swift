  //
//  NewsViewController.swift
//  
//
//  Created by PEPDEVILS  on 03/02/2017.
//  Copyright © 2017 PEPDEVILS . All rights reserved.
//

import Foundation
import UIKit      
import youtube_ios_player_helper
import ImageSlideshow
import SDWebImage

class NewsViewController : UIViewController, UIScrollViewDelegate, UITextViewDelegate, YTPlayerViewDelegate, UIWebViewDelegate {
    var newsTitle = ""
    var newsText = ""
    var image : UIImage!
    var newsDate = ""
    var newsVideo = ""
    var newsGallery = ""
    var url : [String] = []
    var slideShow : ImageSlideshow!
    @IBAction func go_back(_ sender: Any) {
        scrollView.contentOffset.y = 0
        self.dismiss(animated: true, completion: nil)
    }
    var posTitle : CGRect!
    @IBOutlet weak var textView: UITextView!
    @IBOutlet weak var lblTitle: UILabel!
    @IBOutlet weak var imageNews: UIImageView!
    @IBOutlet weak var scrollView: UIScrollView!
    @IBOutlet weak var imageNews_image: UIImageView!
    @IBOutlet weak var lbl_date: UILabel!
    @IBOutlet weak var ViewImage: UIView!
    @IBOutlet weak var bt_goback: UIButton!
    var activityIndicator: UIActivityIndicatorView!
    var video : YTPlayerView!
    var imageView : UIWebView!
    var lblvideo : UILabel!
    var lblgallery : UILabel!
    var lblvideoLine : UILabel!
    var lblgalleryLine : UILabel!
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        
        scrollView.contentInset = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: 0)
    }
  
    override func viewDidLoad() {
        lblTitle.text = newsTitle
        lblTitle.text = lblTitle.text!.uppercased()
        lblTitle.adjustsFontSizeToFitWidth = true
        lbl_date.layer.masksToBounds = true
        lbl_date.layer.cornerRadius = 6
        lbl_date.text = newsDate
        textView.delegate = self
        textView.isScrollEnabled = false
        scrollView.delegate = self
        
        self.automaticallyAdjustsScrollViewInsets = true
        imageNews.image = image
        newsText = newsText.replacingOccurrences(of: "\\\\n", with: "\n")
        newsText = newsText.replacingOccurrences(of: "\n", with: "<div>")
        newsText = newsText.replacingOccurrences(of: "<strong>", with: "<b>")
        newsText = newsText.replacingOccurrences(of: "</strong>", with: "</b>")
        while newsText.contains("<blockquote") {
            if let startRange = newsText.range(of: "<blockquote"), let endRange = newsText.range(of: "</blockquote>"), startRange.upperBound <= endRange.lowerBound {
                let substring = newsText[startRange.upperBound..<endRange.lowerBound]
                url.append("<blockquote\(substring)</blockquote>")
                newsText = newsText.replacingOccurrences(of: "<blockquote\(substring)</blockquote>", with: "")
            } else {
                print("invalid input")
            }
        }
        while newsText.contains("[") {
            if let startRange = newsText.range(of: "["), let endRange = newsText.range(of: "]"), startRange.upperBound <= endRange.lowerBound {
                let substring = newsText[startRange.upperBound..<endRange.lowerBound]
                newsText = newsText.replacingOccurrences(of: "[\(substring)]", with: "")
            } else {
                print("invalid input")
            }
        }
        
        if newsText.contains("Partidas às") {
            if let startRange = newsText.range(of: "width=\""), let endRange = newsText.range(of: "\" />"), startRange.upperBound <= endRange.lowerBound {
                let substring = newsText[startRange.upperBound..<endRange.lowerBound]
                newsText = newsText.replacingOccurrences(of: "width=\"\(substring)\" />", with: "width=\"\(UIScreen.main.bounds.width - 48) height=\"\((UIScreen.main.bounds.width - 48) * (9/11))\" />")
            } else {
                print("invalid input")
            }
            
            if let startRange = newsText.range(of: "src=\""), let endRange = newsText.range(of: "\" alt="), startRange.upperBound <= endRange.lowerBound {
                let substring = newsText[startRange.upperBound..<endRange.lowerBound]
                var array = substring.components(separatedBy: "/")
                
                if array.last!.contains("INF-TUB") {
                    array.removeLast()
                    array.append("INF-TUB.jpg")
                }
                let substringCorrect = array.joined(separator: "/")
                newsText = newsText.replacingOccurrences(of: "src=\"\(substring)\" alt=", with: "src=\"\(substringCorrect)\" alt=")
            } else {
                print("invalid input")
            }
        }
        textView.attributedText = htmlEncodedAttributedString(string: newsText)
    }
    
    func htmlEncodedAttributedString(string : String) -> NSMutableAttributedString {
        
        let paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.lineSpacing = 5
        paragraphStyle.alignment = NSTextAlignment.justified
        var StringKL = string
        var substring = ""
        var ArrayString : [String] = []
        while StringKL.contains("<b>") {
            if let startRange = StringKL.range(of: "<b>"), let endRange = StringKL.range(of: "</b>"), startRange.upperBound <= endRange.lowerBound {
                substring = StringKL[startRange.upperBound..<endRange.lowerBound]
                ArrayString.append(substring)
            }
            // replacing attributes
            StringKL = StringKL.replacingCharacters(in: StringKL.range(of: "<b>")!, with: "")
            StringKL = StringKL.replacingCharacters(in: StringKL.range(of: "</b>")!, with: "")
        }
        if StringKL.contains("<tbody>") {
            StringKL = StringKL.replacingCharacters(in: StringKL.range(of: "<tbody>")!, with: "")
            StringKL = StringKL.replacingCharacters(in: StringKL.range(of: "</tbody>")!, with: "")
        }
        if StringKL.contains("<tr>") {
            StringKL = StringKL.replacingCharacters(in: StringKL.range(of: "<tr>")!, with: "")
            StringKL = StringKL.replacingCharacters(in: StringKL.range(of: "</tr>")!, with: "")
        }
        
        if StringKL.contains("<table") {
            
            if let startRange = StringKL.range(of: "<table"), let endRange = StringKL.range(of: "\">"), startRange.upperBound <= endRange.lowerBound {
                let substringTable = StringKL[startRange.upperBound..<endRange.lowerBound]
                StringKL = StringKL.replacingCharacters(in: StringKL.range(of: "<table\(substringTable)\">")!, with: "")
            }
            
            
            StringKL = StringKL.replacingCharacters(in: StringKL.range(of: "</table>")!, with: "")
        }
        let attrStr = try! NSMutableAttributedString(data: StringKL.data(using: String.Encoding.unicode, allowLossyConversion: true)!, options: [ NSDocumentTypeDocumentAttribute: NSHTMLTextDocumentType], documentAttributes: nil)
        attrStr.addAttribute(NSParagraphStyleAttributeName, value: paragraphStyle, range: NSMakeRange(0, attrStr.length))
        attrStr.addAttribute(NSFontAttributeName, value: UIFont(name: "Roboto-Regular", size: 14.0)!, range: NSMakeRange(0, attrStr.length))
        for i in 0..<ArrayString.count {
            if (attrStr.mutableString).contains(ArrayString[i]) {
                let substringKL = ((attrStr.mutableString) as NSString).range(of: ArrayString[i])
                attrStr.addAttribute(NSFontAttributeName, value: UIFont(name: "Roboto-Bold", size: 14.0)!, range: NSRange(location: substringKL.location, length: substringKL.length))
            }
        }
        return attrStr
    }
    
    var pos : CGFloat = 0
    override func viewDidAppear(_ animated: Bool) {
        if !url.isEmpty {
            
            for i in 0..<url.count
            {
                if i == 0
                {
                    pos = textView.frame.origin.y + textView.frame.size.height + 15
                }
                else
                {
                    pos = pos + (UIScreen.main.bounds.width + 8) * (9/15) + 15
                }
                imageView = UIWebView(frame: CGRect(x: -8, y: pos, width: UIScreen.main.bounds.width + 8, height: (UIScreen.main.bounds.width + 8) * (9/15)))
                imageView.allowsInlineMediaPlayback = true
                imageView.loadHTMLString("<html><head><script type=\"text/javascript\" src=\"https://platform.twitter.com/widgets.js\"></script></head><body>\(url[i])</body></html>", baseURL: Bundle.main.executableURL)
                imageView.scrollView.isScrollEnabled = false
                
                scrollView.addSubview(imageView)
                scrollView.contentSize.height = pos + imageView.frame.size.height + 15
            }
            
            let viewLoad = UIView(frame: self.view.bounds)
            viewLoad.backgroundColor = UIColor.gray.withAlphaComponent(0.3)
            self.view.isUserInteractionEnabled = false
            let Loader = UIActivityIndicatorView(activityIndicatorStyle: .whiteLarge)
            Loader.hidesWhenStopped = true
            Loader.center = viewLoad.center
            Loader.startAnimating()
            viewLoad.addSubview(Loader)
            self.view.addSubview(viewLoad)
            UIView.animate(withDuration: 5, animations: { _ in
                viewLoad.backgroundColor = UIColor.gray.withAlphaComponent(0.6)
            }, completion: { finished in
                Loader.stopAnimating()
                viewLoad.removeFromSuperview()
                self.view.isUserInteractionEnabled = true
            })
            
            if newsGallery != "NoGallery"{
                createGallery()
            } else{
                scrollView.contentSize.height = pos + imageView.frame.size.height + 15
                if newsVideo != "NoVideo"{
                    createVideo()
                }
            }
        } else {
            if newsGallery != "NoGallery"{
                createGallery()
            } else{
                scrollView.contentSize.height = textView.frame.origin.y + textView.frame.size.height + 15
                if newsVideo != "NoVideo"{
                    createVideo()
                }
            }
        }
    }
    
    func playerViewDidBecomeReady(_ playerView: YTPlayerView) {
        activityIndicator.stopAnimating()
    }
    
    func createVideo() {
        var videoID = newsVideo.components(separatedBy: "v=")[1]
        videoID = videoID.components(separatedBy: "\"][")[0]
        if !url.isEmpty {
            video = YTPlayerView(frame: CGRect(x: 8, y: pos + imageView.frame.size.height + 15, width: UIScreen.main.bounds.width - 16, height: (UIScreen.main.bounds.width - 16) * (9/16)))
            if newsGallery != "NoGallery"{
                video = YTPlayerView(frame: CGRect(x: 8, y: slideShow.frame.origin.y + slideShow.frame.size.height + 15, width: UIScreen.main.bounds.width - 16, height: (UIScreen.main.bounds.width - 16) * (9/16)))
            }
        } else {
            if newsGallery != "NoGallery"{
                lblvideo = UILabel(frame: CGRect(x: 16, y: slideShow.frame.origin.y + slideShow.frame.size.height + 15, width: UIScreen.main.bounds.width - 16, height: 15))
                lblvideo.text = "VIDEO"
                lblvideo.font = UIFont(name: "Roboto-Bold", size: 17)
                lblvideo.textColor = UIColor(red: 116/255, green: 0/255, blue: 0/255, alpha: 1)
                lblvideoLine = UILabel(frame: CGRect(x: 16, y: lblvideo.frame.origin.y + lblvideo.frame.size.height + 5, width: UIScreen.main.bounds.width - 32, height: 2))
                lblvideoLine.backgroundColor = UIColor(red: 116/255, green: 0/255, blue: 0/255, alpha: 1)
                video = YTPlayerView(frame: CGRect(x: 8, y: lblvideoLine.frame.origin.y + lblvideoLine.frame.size.height + 15, width: UIScreen.main.bounds.width - 16, height: (UIScreen.main.bounds.width - 16) * (9/16)))
            } else {
                lblvideo = UILabel(frame: CGRect(x: 16, y: textView.frame.origin.y + textView.frame.size.height + 15, width: UIScreen.main.bounds.width - 16, height: 15))
                lblvideo.text = "VIDEO"
                lblvideo.font = UIFont(name: "Roboto-Bold", size: 17)
                lblvideo.textColor = UIColor(red: 116/255, green: 0/255, blue: 0/255, alpha: 1)
                lblvideoLine = UILabel(frame: CGRect(x: 16, y: lblvideo.frame.origin.y + lblvideo.frame.size.height + 5, width: UIScreen.main.bounds.width - 32, height: 2))
                lblvideoLine.backgroundColor = UIColor(red: 116/255, green: 0/255, blue: 0/255, alpha: 1)
                video = YTPlayerView(frame: CGRect(x: 8, y: lblvideoLine.frame.origin.y + lblvideoLine.frame.size.height + 15, width: UIScreen.main.bounds.width - 16, height: (UIScreen.main.bounds.width - 16) * (9/16)))
            }
        }
        activityIndicator = UIActivityIndicatorView(activityIndicatorStyle: .whiteLarge)
        activityIndicator.center = video.center
        activityIndicator.hidesWhenStopped = true
        activityIndicator.color = UIColor(red: 200/255, green: 0/255, blue: 0/255, alpha: 1)
        activityIndicator.startAnimating()
        video.delegate = self
        video.load(withVideoId: videoID)
        scrollView.addSubview(lblvideo)
        scrollView.addSubview(lblvideoLine)
        scrollView.addSubview(video)
        scrollView.addSubview(activityIndicator)
        scrollView.contentSize.height = video.frame.origin.y + video.frame.size.height + 15
    }
    
    func createGallery() {
        while newsGallery.contains("http:") {
            newsGallery = newsGallery.replacingOccurrences(of: "http:", with: "https:")
        }
        var ArrayImagesURL = newsGallery.components(separatedBy: ",")
        if !url.isEmpty {
            slideShow = ImageSlideshow(frame: CGRect(x: 8, y: pos + imageView.frame.size.height + 15, width: UIScreen.main.bounds.width - 16, height: (UIScreen.main.bounds.width - 16) * (9/16)))
        } else {
            lblgallery = UILabel(frame: CGRect(x: 16, y: textView.frame.origin.y + textView.frame.size.height + 15, width: UIScreen.main.bounds.width - 16, height: 15))
            lblgallery.text = "GALERIA"
            lblgallery.font = UIFont(name: "Roboto-Bold", size: 17)
            lblgallery.textColor = UIColor(red: 116/255, green: 0/255, blue: 0/255, alpha: 1)
            lblgalleryLine = UILabel(frame: CGRect(x: 16, y: lblgallery.frame.origin.y + lblgallery.frame.size.height + 5, width: UIScreen.main.bounds.width - 32, height: 2))
            lblgalleryLine.backgroundColor = UIColor(red: 116/255, green: 0/255, blue: 0/255, alpha: 1)
            slideShow = ImageSlideshow(frame: CGRect(x: 8, y: lblgalleryLine.frame.origin.y + lblgalleryLine.frame.size.height + 15, width: UIScreen.main.bounds.width - 16, height: (UIScreen.main.bounds.width - 16) * (9/16)))
        }
        
        var ArrayImages : [InputSource] = []
        self.scrollView.addSubview(self.lblgallery)
        self.scrollView.addSubview(self.lblgalleryLine)
        self.scrollView.addSubview(self.slideShow)
        for i in 0..<ArrayImagesURL.count {
            ArrayImages.append(SDWebImageSource(url: URL(string: ArrayImagesURL[i].addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed)!)!, placeholder: UIImage(named: "Banner-logoTeam-Adeptos-")!))
        }
        self.slideShow.preload = .fixed(offset: 2)
        self.slideShow.setImageInputs(ArrayImages)
        self.slideShow.pageControlPosition = PageControlPosition.insideScrollView
        self.slideShow.contentScaleMode = UIViewContentMode.scaleAspectFill
        scrollView.contentSize.height = slideShow.frame.origin.y + slideShow.frame.size.height + 15
        
        if newsVideo != "NoVideo"{
            createVideo()
        }
    }
    
    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        
        let offsetY = scrollView.contentOffset.y
        let height = UIScreen.main.bounds.width * (9/16)
        if offsetY < 0
        {
            scrollView.scrollIndicatorInsets.top = -offsetY
            
            imageNews.frame.size.height = height + -offsetY
            imageNews_image.frame.size.height = height + -offsetY
            self.lblTitle.frame.origin.y = (height-lblTitle.frame.size.height-10) + -offsetY
            posTitle = self.lblTitle.frame
        }
            else {
            ViewImage.frame.size.height = height
            imageNews.frame.size.height = height
            imageNews_image.frame.size.height = height
            
            lblTitle.frame.origin.y = height - lblTitle.frame.size.height - 10
            posTitle = self.lblTitle.frame
        }
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
