package pt.pepdevils.scteam.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.uncopt.android.widget.text.justify.JustifiedTextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import pt.pepdevils.scteam.Helper;
import pt.pepdevils.scteam.LinkMovementMethodExt;
import pt.pepdevils.scteam.R;
import pt.pepdevils.scteam.activities.InitialActivity;
import pt.pepdevils.scteam.custom.PepeDialogBuilder;
import pt.pepdevils.scteam.util.News;
import pt.pepdevils.scteam.video.Config;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NewsFragment extends Fragment {

    private View v;
    private News mNoticia;
    private ImageView img_news;
    private NestedScrollView scroll;
    private TextView tx_title_after;
    private TextView tx_date;
    private JustifiedTextView tx_descr;
    private FrameLayout fragment_container;
    private Subscription mSubscription;
    private Observable<Spanned> obs;
    private CollapsingToolbarLayout collapsingtoolbarlayout;
    private AppBarLayout appbar;
    private LinearLayout ll_scroll_container;
    private boolean isTweeted = false;
    private double density;
    private static ImageLoader imageLoader = ImageLoader.getInstance();
    private static DisplayImageOptions defaultOptions;
    private static final String NOT_KEY = "noticia_chave";

    protected String newsTitle;
    protected String newsDate;
    protected String newsContent;
    protected String newsImage;
    protected TextView tx_galeria;
    protected SliderLayout sl;
    protected static int width_video;
    protected static int height_video;

    public static YouTubePlayer activePlayer = null;
    public static String videoID;
    public static YouTubePlayerSupportFragment youTubeView;
    public static boolean isFullScrenn = false;

    //      http://stackoverflow.com/questions/9931993/passing-an-object-from-an-activity-to-a-fragment
    public NewsFragment() {
        // Required empty public constructor
    }

    public static NewsFragment newInstance(News noticia, ImageLoader imageLoader_, DisplayImageOptions defaultOptions_) {
        imageLoader = imageLoader_;
        defaultOptions = defaultOptions_;
        NewsFragment fragment = new NewsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(NOT_KEY, noticia);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.postponeEnterTransition(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_news, container, false);
        UI(v);
        mNoticia = (News) getArguments().getSerializable(NOT_KEY);
        newsTitle = mNoticia.getPostTitle();
        newsDate = mNoticia.getPostDate();
        newsImage = mNoticia.getImage();
        collapsingtoolbarlayout.setTitle("");
        tx_title_after.setText(newsTitle);
        tx_date.setText(newsDate);
        IFAreImageInHTMLNews(mNoticia);
        IFAreTwitterInNews(mNoticia);
        newsContent = mNoticia.getPostContent();
        newsContent = newsContent.replaceAll("\\[.*?\\]", "");
        tx_descr.setText(newsContent);
        IfAreVideoShow(mNoticia, v);
        IfAreGalerieShow(mNoticia, v);
        if (!isTweeted) {
            cookingHTML(newsContent);
        }
        imageLoader.displayImage(newsImage, img_news, defaultOptions);
        LinearLayout ll_scroll_container = (LinearLayout) v.findViewById(R.id.ll_scroll_container);
        ll_scroll_container.setLayoutTransition(null);
        appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (verticalOffset == 0) {
                collapsingtoolbarlayout.setTitle("");
            } else {
                collapsingtoolbarlayout.setTitle(getResources().getString(R.string.str_bt_noticias));
            }
        });
        scroll.setSmoothScrollingEnabled(true);
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        int count = ll_scroll_container.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = ll_scroll_container.getChildAt(i);
            Object aux = v.getTag();
            if (aux != null) {
                if (aux.toString() != null) {
                    if (aux.toString().contains("webtweeter_")) {
                        WebView wv = (WebView) v;
                        wv.onPause();
                    }
                }
            }
        }
    }

    @Override
    public void onStop() {
        if (mSubscription != null) {
            if (mSubscription.isUnsubscribed()) {
                mSubscription.unsubscribe();
            }
        }
        super.onStop();
    }

    private void UI(View v) {
        scroll = (NestedScrollView) v.findViewById(R.id.scroll);
        tx_date = (TextView) v.findViewById(R.id.tx_date);
        tx_title_after = (TextView) v.findViewById(R.id.tx_title_after);
        tx_descr = (JustifiedTextView) v.findViewById(R.id.tx_descr);
        collapsingtoolbarlayout = (CollapsingToolbarLayout) v.findViewById(R.id.collapsingtoolbarlayout);
        appbar = (AppBarLayout) v.findViewById(R.id.appbar);
        img_news = (ImageView) v.findViewById(R.id.img_news);
        ll_scroll_container = (LinearLayout) v.findViewById(R.id.ll_scroll_container);
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what == 100) {
                Object[] spans = (Object[])msg.obj;
                for (Object span : spans) {
                    if (span instanceof URLSpan) {
                       String url = ((URLSpan) span).getURL();
                        if (!url.startsWith("http://") && !url.startsWith("https://"))
                            url = "http://" + url;

                        Intent browserIntent = new Intent( Intent.ACTION_VIEW, Uri.parse(url));
                        ((AppCompatActivity)v.getContext()).startActivity(browserIntent);
                    }
                }
            }
        }
    };

    private void cookingHTML(String html_string) {
        String aux = html_string.replaceAll("\\n", "<div>");
        obs = Observable.defer(() -> Observable.create(new Observable.OnSubscribe<Spanned>() {
            @Override
            public void call(Subscriber<? super Spanned> subscriber) {

                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        subscriber.onNext((Html.fromHtml(aux, Html.FROM_HTML_MODE_COMPACT)));
                    } else {
                        subscriber.onNext((Html.fromHtml(aux)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }));

        mSubscription = new Subscriber<Spanned>() {
            @Override
            public void onNext(Spanned s) {
                tx_descr.setMovementMethod(LinkMovementMethodExt.getInstance(handler, URLSpan.class));
                tx_descr.setText(s);
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }
        };

        obs.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((Observer<? super Spanned>) mSubscription);

    }

    private int[] ScreenWidthMakeHeight(Context c, double ratio) {
        int[] all_dim = new int[2];
        DisplayMetrics dm = new DisplayMetrics();
        ((AppCompatActivity) c).getWindowManager().getDefaultDisplay().getMetrics(dm);
        density = dm.density;
        int width = (dm.widthPixels);
        if (ratio <= 0) {
            ratio = 0.5625;
        }
        int height = (int) (width * ratio);
        all_dim[0] = width;
        all_dim[1] = height;
        return all_dim;
    }

    private void IfAreVideoShow(News mNoticia, View v) {
        if (mNoticia.getVideo() != null) {
            Log.e("Temos video", "IfAreVideoShow: ");

            int vhmargin = getActivity().getResources().getDimensionPixelSize(R.dimen.video_hor_margin);

            Uri uri = Uri.parse(mNoticia.getVideo());
            videoID = uri.getQueryParameter("v");
            videoID = (videoID.split("\"]"))[0];
            int[] dims = ScreenWidthMakeHeight(getActivity(), 0);
            width_video = dims[0] - (int) (15 * density);//margem
            height_video = dims[1];
            width_video = (int) (width_video / density);
            height_video = (int) (height_video / density);
            youTubeView = new YouTubePlayerSupportFragment();
            youTubeView.onPictureInPictureModeChanged(true);
            youTubeView.initialize(Config.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
                    activePlayer = youTubePlayer;
                    ((ViewGroup) ((ViewGroup) ((ViewGroup) ((ViewGroup) ((ViewGroup) ((ViewGroup) ((ViewGroup) ((ViewGroup) youTubeView.getView()).getChildAt(0)).getChildAt(0))
                            .getChildAt(4)).getChildAt(0)).getChildAt(0)).getChildAt(3)).getChildAt(0)).getChildAt(1).setVisibility(View.GONE);
                    List<View> vvv = Helper.findEspecifiqueViewRecursively((ViewGroup) youTubeView.getView());
                    vvv.get(0).setVisibility(View.GONE);
                    vvv.get(1).setVisibility(View.GONE);
                    if (!wasRestored) {
                        youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                        youTubePlayer.loadVideo(videoID);
                        youTubePlayer.pause();

                    }
                    youTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                        @Override
                        public void onFullscreen(boolean isFull) {
                            isFullScrenn = isFull;
                            if (!isFull) {
                                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                            }
                        }
                    });
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
                    if (result.isUserRecoverableError()) {
                        result.getErrorDialog(getActivity(), 1).show();
                    } else {
                        Toast.makeText(getActivity(), "YouTubePlayer.onInitializationFailure(): " + result.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            });
            TextView tx_video = new TextView(getActivity());
            tx_video.setText("VIDEO");
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llp.gravity = Gravity.CENTER;

            float d = getActivity().getResources().getDisplayMetrics().scaledDensity;
            int margin = (int) (2 * d);
            int padding = (int) (4 * d);
            llp.setMargins(margin, margin, margin, margin);
            tx_video.setPadding(padding, padding, padding, padding);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tx_video.setTextColor(getResources().getColor(R.color.colorPrimary, getActivity().getTheme()));
            } else {
                tx_video.setTextColor(getResources().getColor(R.color.colorPrimary));
            }
            int margini = getActivity().getResources().getDimensionPixelSize(R.dimen.fab_margin);


            //textview
            tx_video.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimensionPixelSize(R.dimen.ts_med_regular));
            llp.setMargins(margini, 0, margini,0);
            tx_video.setLayoutParams(llp);
            tx_video.setGravity(Gravity.LEFT);
            ll_scroll_container.addView(tx_video, 3);

            //insert line view
            View line_v =  new View(getActivity());
            LinearLayout.LayoutParams llp_v = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getActivity().getResources().getDimensionPixelSize(R.dimen.line_height));
            llp_v.setMargins(margini,0,margini,10);
            line_v.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
            line_v.setLayoutParams(llp_v);
            ll_scroll_container.addView(line_v, 4);

            fragment_container = new FrameLayout(getActivity());

            int idezingo = 0b11000000111001;
            fragment_container.setId(idezingo);
            ll_scroll_container.addView(fragment_container, 5);
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.add(idezingo, youTubeView);
            ft.addToBackStack("Video");
            ft.commit();
            LinearLayout.LayoutParams llmp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            //llmp.setMargins(0, 0, 0, (int) (5 * d));
            llmp.setMargins(vhmargin, 0, vhmargin, (int) (5 * d));
            fragment_container.setLayoutParams(llmp);
        }
    }

    private void IfAreGalerieShow(News mNoticia, View v) {
        if (mNoticia.getGaleria() != null) {
            List<String> Galeria = mNoticia.getGaleria();
            LinearLayout relaout = (LinearLayout) v.findViewById(R.id.relaout);
            tx_galeria = (TextView) v.findViewById(R.id.tx_galeria);
            View line_view = v.findViewById(R.id.line_view);
            relaout.setVisibility(View.VISIBLE);
            tx_galeria.setVisibility(View.VISIBLE);
            line_view.setVisibility(View.VISIBLE);
            sl = (SliderLayout) v.findViewById(R.id.slider);
            for (int i = 0; i < Galeria.size(); i++) {
                DefaultSliderView textSliderView = new DefaultSliderView(getActivity());
                String image_url = Galeria.get(i);
                // initialize a SliderLayout
                textSliderView
                        .image(image_url)
                        .setScaleType(BaseSliderView.ScaleType.CenterCrop);
                //add your extra information
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle()
                        .putString("extra", Galeria.get(i));
                sl.addSlider(textSliderView);
            }
            sl.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            sl.setDuration(6000);
            sl.startAutoCycle();
            int[] dims = ScreenWidthMakeHeight(getActivity(), 0);
            sl.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dims[1]));
            if (Galeria.size() == 1) {
                sl.stopAutoCycle();
                sl.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);
                sl.getPagerIndicator().setEnabled(true);
                sl.setPresetTransformer(SliderLayout.Transformer.Fade);
            }
            relaout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            sl.bringToFront();
        }//fim if
    }//fim func

    private void IFAreImageInHTMLNews(News mNoticia) {
        String content = mNoticia.getPostContent();
        if (content.contains("<img class")) {
            Document doc = Jsoup.parse(content);
            Elements imgs = doc.getElementsByTag("img");
            if (imgs.size() > 0) {
                for (Element elment : imgs) {
                    String image_url = elment.absUrl("src");
                    //insert ImageView ??
                    InsertImageView(image_url);
                    //retirar da noticia
                    //retirar da view
                    RemoveImgTagFromNews(doc, elment.tagName());
                }
            }
        }
    }

    private void RemoveImgTagFromNews(Document doc, String tag_name) {
        for (Element element : doc.select(tag_name)) {
            element.remove();
        }
        String content = doc.toString();
        mNoticia.setPostContent(content);
        cookingHTML(content);
    }

    private void InsertImageView(String image_url) {
        ImageView imgV = new ImageView(getActivity());
        image_url = OriginalSrcImage(image_url);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(5, 5, 5, 5);
        imgV.setLayoutParams(lp);
        imgV.setAdjustViewBounds(true);
        imageLoader.displayImage(image_url, imgV, defaultOptions);

        int count = ll_scroll_container.getChildCount();


        ll_scroll_container.addView(imgV, /*8*/ count -1 );
    }

    public String RemoveLastCharIf(String str, char chari) {
        if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == chari) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    private String OriginalSrcImage(String image_url) {
        String res = "";
        String[] aux = image_url.split("/");
        if (aux.length > 0) {
            String img_name = aux[7];
            String[] array = img_name.split("x");
            String sss = array[0];
            //https://regex101.com/r/dV4fE6/1
            sss = sss.replaceAll("[^a-zA-Z-]", "");
            sss = RemoveLastCharIf(sss, '-');
            String original_name = sss;
            for (int i = 0; i < aux.length; i++) {
                if (i < 7) {
                    res += aux[i] + "/";
                } else {
                    res += original_name + ".jpg";
                }
            }
            //Toast.makeText(getActivity(), "res" + res, Toast.LENGTH_SHORT).show();
        }
        return res;
    }

    private void IFAreTwitterInNews(News mNoticia) {
        String content = mNoticia.getPostContent();
        if (content.contains("twitter")) {
            try {
                Document doc = Jsoup.parse(content);
                // Elements all = doc.getAllElements();          FOR DEBUG PROPOSES
                Elements all_tweetS = doc.getElementsByClass("twitter-video");
                if (all_tweetS.size() > 0) {
                    for (int i = 0; i < all_tweetS.size(); i++) {
                        String tweet_video = all_tweetS.get(i).toString();
                        WebView wb = addWebViewTwitter(tweet_video);
                        wb.setTag("webtweeter_" + i);
                        ll_scroll_container.addView(wb);
                    }
                    RetirarTweetFromMainContent("twitter-video");
                    isTweeted = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void RetirarTweetFromMainContent(String tweet_video) {
        //remoção do tweet
        String content = mNoticia.getPostContent();
        Document doc = Jsoup.parse(content);
        for (Element element : doc.select("blockquote." + tweet_video)) {
            element.remove();
        }
        for (Element element : doc.select("a")) {
            if (element.toString().contains(tweet_video)) {
                element.remove();
            }
        }
        content = doc.toString();
        //colocar de novo na noticia json
        mNoticia.setPostContent(content);
        //colocar de novo na view
        content = content.replaceAll("\\[.*?\\]", "");
        tx_descr.setText(content);
        cookingHTML(content);
    }

    private WebView addWebViewTwitter(String twitterContent) {
        WebView webView = new WebView(getActivity());
        if (twitterContent != null && twitterContent.length() > 0) {
            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                    WebView newWebView = new WebView(getContext());
                    view.addView(newWebView);
                    WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                    transport.setWebView(newWebView);
                    resultMsg.sendToTarget();
                    return true;
                }
            });
            webView.setWebViewClient(new WebViewClient());
            webView.getSettings().setAppCacheEnabled(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setPluginState(WebSettings.PluginState.ON);
            webView.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
            twitterContent =
                    "<html>" +
                            "<head>" +
                            "<script type=\"text/javascript\" src=\"https://platform.twitter.com/widgets.js\"></script>" +
                            "</head>" +
                            "<body>" +
                            twitterContent +
                            "</body></html>";

            webView.loadDataWithBaseURL("https://twitter.com", twitterContent, "text/html", "UTF-8", null);
        }
        return webView;
    }
}
