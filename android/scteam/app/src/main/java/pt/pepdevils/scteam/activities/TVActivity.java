package pt.pepdevils.scteam.activities;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.net.http.SslError;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import pt.pepdevils.scteam.Helper;
import pt.pepdevils.scteam.R;
import pt.pepdevils.scteam.custom.PepeDialogBuilder;
import pt.pepdevils.scteam.custom.PepeDialogTutorial;

public class TVActivity extends AppCompatActivity {

    private WebView webView;
    private FrameLayout customViewContainer;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private View mCustomView;
    private myWebChromeClient mWebChromeClient;
    private myWebViewClient mWebViewClient;
    private SwipeRefreshLayout swipeContainer;
    private View ll_header;
    private ImageView img;
    private ProgressBar pr_bar_rot;
    private float dens;
    private String video_url;
    private NestedScrollView scrollNested;
    private LinearLayout lili;
    private boolean isBigSized;
    private boolean isOpenFirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv);

        if (getIntent().getExtras() != null) {
            video_url = getIntent().getExtras().getString("video");
        }

        UI();
        STARTVIDEOFROMWEBVIEW();
        REFRESHVIEW();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
        AnimateAndChangeLayout();
        ChangeBackIfHeight();
        Helper.WarningInternetSnack(this);
        TutorialForRefres();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (inCustomView()) {
            hideCustomView();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        webView.saveState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        AnimateAndChangeLayout();
        TutorialForRefres();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (inCustomView()) {
                hideCustomView();
                return true;
            }
            if ((mCustomView == null) && webView.canGoBack()) {
                webView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {

        String TitleColor = getResources().getString(0 + R.color.colorPrimary);
        String DividerColor = getResources().getString(0 + R.color.colorPrimary);
        String MessageColor = getResources().getString(0 + R.color.colorPrimary);
        String DialogColor_ = getResources().getString(0 + R.color.colorAccent);


        PepeDialogBuilder pepeDialogBuilder = PepeDialogBuilder.getInstance(TVActivity.this);

        pepeDialogBuilder
                .withTitle("Aviso")
                .withMessage("Deseja sair da TV ?")
                .withMessageCenter(true)
                .withTitleColor(TitleColor)
                .withDividerColor(DividerColor)
                .withMessageColor(MessageColor)
                .withDialogColor(DialogColor_)
                .withIcon(getResources().getDrawable(R.mipmap.ic_launcher))
                .isCancelableOnTouchOutside(true)
                .withButton1Text("Sim")
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pepeDialogBuilder.dismiss();
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                })
                .withButton2Text("Não")
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        pepeDialogBuilder.dismiss();
                    }
                })
                .show();
    }

    private void UI() {
        webView = (WebView) findViewById(R.id.webView);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        customViewContainer = (FrameLayout) findViewById(R.id.customViewContainer);
        scrollNested = (NestedScrollView) findViewById(R.id.scrollNested);
        lili = (LinearLayout) findViewById(R.id.lili);
        ll_header = findViewById(R.id.ll_header);
        img = (ImageView) findViewById(R.id.img);
        img.bringToFront();
        pr_bar_rot = (ProgressBar) findViewById(R.id.pr_bar_rot);
    }

    private void STARTVIDEOFROMWEBVIEW() {
        mWebViewClient = new myWebViewClient();
        mWebChromeClient = new myWebChromeClient();
        webView.setWebViewClient(mWebViewClient);
        webView.setWebChromeClient(mWebChromeClient);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSaveFormData(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.setScrollContainer(false);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        // disable scroll on touch
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });
        //load do video, novo ou antigo guardado na memoria
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (video_url != null) {
            if (video_url.equalsIgnoreCase("")) {
                video_url = appSharedPrefs.getString("VIDEO_URL", "https://mycujoo.tv/embed/260?vid=14363");
                Helper.WarningInternetSnack(this);
                webView.loadUrl(video_url);
            } else {
                webView.loadUrl(video_url);
            }
        } else {
            video_url = appSharedPrefs.getString("VIDEO_URL", "https://mycujoo.tv/embed/260?vid=14363");
            Helper.WarningInternetSnack(this);
            webView.loadUrl(video_url);
        }
    }

    private void REFRESHVIEW() {
        //      https://developer.android.com/reference/android/support/v4/widget/SwipeRefreshLayout.html
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            swipeContainer.setColorSchemeColors(getResources().getColor(R.color.colorPrimary, null), getResources().getColor(android.R.color.white, null));
        } else {
            swipeContainer.setColorSchemeColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(android.R.color.white));

        }
        swipeContainer.setProgressBackgroundColorSchemeResource(android.R.color.transparent);


        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.setVisibility(View.GONE);
                pr_bar_rot.setVisibility(View.VISIBLE);
                STARTVIDEOFROMWEBVIEW();
                Helper.WarningInternetSnack(TVActivity.this);
                swipeContainer.setRefreshing(false);
            }
        });

    }



    private void TutorialForRefres() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        isOpenFirstTime = appSharedPrefs.getBoolean("isOpenFirstTime", true);
        //open first time?
        if (isOpenFirstTime) {
            PepeDialogTutorial pepDT = PepeDialogTutorial.getInstance(this);
            pepDT.setButton1Click(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pepDT.dismiss();
                }
            });
            pepDT.setButton2Click(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                    prefsEditor.putBoolean("isOpenFirstTime", false);
                    prefsEditor.apply();
                    pepDT.dismiss();
                }
            });
            pepDT.show();
        }
    }

    private void ChangeBackIfHeight() {
        LinearLayout lili = (LinearLayout) findViewById(R.id.lili);
        NestedScrollView scrollNested = (NestedScrollView) findViewById(R.id.scrollNested);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int h = (int) (dm.heightPixels / dm.scaledDensity);

        int alt_dra = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            alt_dra = getResources().getDrawable(R.drawable.backbtv, null).getIntrinsicHeight();
        } else {
            alt_dra = getResources().getDrawable(R.drawable.backbtv).getIntrinsicHeight();
        }

        if (h <= alt_dra) {
            isBigSized = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                lili.setBackground(getResources().getDrawable(R.drawable.croped_back, null));
            } else {
                lili.setBackground(getResources().getDrawable(R.drawable.croped_back));

            }
        } else {
            isBigSized = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                lili.setBackgroundColor(getResources().getColor(android.R.color.darker_gray, null));
                scrollNested.setBackgroundColor(getResources().getColor(android.R.color.darker_gray, null));
            } else {
                lili.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                scrollNested.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            }
        }


    }

    private void AnimateAndChangeLayout() {
        int orientation = getResources().getConfiguration().orientation;
        dens = getResources().getDisplayMetrics().density;
        int h_port = (int) (100 * dens);
        int h_land = (int) (48 * dens);
        int margin_top_port = (int) (10 * dens);
        int margin_land = (int) (6.5 * dens);
        int margin_web_top_land = (int) (110 * dens);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams ll_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);

        if (orientation == 1) {
            //port
            if (!customViewContainer.isShown()) {
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                params.setMargins(0, margin_top_port, 0, 0);
                img.setLayoutParams(params);

                img.getLayoutParams().height = h_port;
                img.getLayoutParams().width = h_port;

                ll_params.setMargins(0, margin_web_top_land, 0, 0);
                webView.setLayoutParams(ll_params);
                customViewContainer.setLayoutParams(ll_params);

                int width_screen = getResources().getDisplayMetrics().widthPixels;
                int aux = (int) (width_screen * 0.5625);
                webView.getLayoutParams().height = aux;
            }
        } else {
            //land
            if (!customViewContainer.isShown()) {
                params.addRule(RelativeLayout.ALIGN_BOTTOM | RelativeLayout.ALIGN_PARENT_RIGHT);
                params.setMargins(0, margin_land, margin_land, 0);
                img.setLayoutParams(params);

                img.getLayoutParams().height = h_land;
                img.getLayoutParams().width = h_land;

                ll_params.setMargins(0, (int) (10 * dens), 0, 0);
                webView.setLayoutParams(ll_params);
                customViewContainer.setLayoutParams(ll_params);

                int width_screen = getResources().getDisplayMetrics().widthPixels;
                int aux = (int) (width_screen * 0.5625);
                webView.getLayoutParams().height = aux;
            }

        }

    }

    public boolean inCustomView() {
        return (mCustomView != null);
    }

    public void hideCustomView() {
        mWebChromeClient.onHideCustomView();
    }

    class myWebChromeClient extends WebChromeClient {

        private View mVideoProgressView;

        @Override
        public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
            onShowCustomView(view, callback);    //To change body of overridden methods use File | Settings | File Templates.
        }

        @Override
        public View getVideoLoadingProgressView() {
            if (mVideoProgressView == null) {
                LayoutInflater inflater = LayoutInflater.from(TVActivity.this);
                mVideoProgressView = inflater.inflate(R.layout.video_progress, null);
            }
            return mVideoProgressView;
        }

        @Override
        public void onHideCustomView() {
            super.onHideCustomView();
            if (mCustomView == null)
                return;

            Helper.ShowStatusBar(TVActivity.this);

            webView.setVisibility(View.VISIBLE);
            ll_header.setVisibility(View.VISIBLE);
            img.setVisibility(View.VISIBLE);

            if (isBigSized) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    lili.setBackgroundColor(getResources().getColor(android.R.color.darker_gray, null));
                } else {
                    lili.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                }

            } else {
                lili.setBackground(getResources().getDrawable(R.drawable.croped_back));
            }


            // Hide the custom view.
            customViewContainer.setVisibility(View.GONE);
            mCustomView.setVisibility(View.GONE);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            // Remove the custom view from its container.
            customViewContainer.removeView(mCustomView);
            customViewContainer.setLayoutParams(params);
            customViewCallback.onCustomViewHidden();
            AnimateAndChangeLayout();
            mCustomView = null;

            FrameLayout.LayoutParams params_f = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            lili.setLayoutParams(params_f);
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {

            // if a view already exists then immediately terminate the new one
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            mCustomView = view;

            webView.setVisibility(View.GONE);
            img.setVisibility(View.GONE);
            ll_header.setVisibility(View.GONE);

            customViewContainer.setVisibility(View.VISIBLE);
            customViewContainer.addView(view);
            customViewCallback = callback;

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.setMargins(0, 0, 0, 0);
            customViewContainer.setLayoutParams(params);

            FrameLayout.LayoutParams params_f = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            params_f.gravity = Gravity.CENTER;
            lili.setBackgroundColor(getResources().getColor(android.R.color.black));
            lili.setLayoutParams(params_f);

            customViewContainer.setLayoutParams(params);

            Helper.HideStatusBar(TVActivity.this);
        }
    }

    class myWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            pr_bar_rot.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            //webView.setVisibility(View.INVISIBLE);
        }

    }
}
