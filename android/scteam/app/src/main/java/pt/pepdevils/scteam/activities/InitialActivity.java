package pt.pepdevils.scteam.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import pt.pepdevils.scteam.Helper;
import pt.pepdevils.scteam.custom.PepeDialogBuilder;
import pt.pepdevils.scteam.custom.PepeProgressDialog;
import pt.pepdevils.scteam.fragments.NewsFragment;
import pt.pepdevils.scteam.retrofit.API_BRAGA;
import pt.pepdevils.scteam.R;
import pt.pepdevils.scteam.retrofit.ServiceFactory;
import pt.pepdevils.scteam.adapters.ViewPagerNewsAdapter;
import pt.pepdevils.scteam.util.News;
import pt.pepdevils.scteam.util.Tudo;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

/**
 * Created by PepDevils on 29/01/17.
 */
public class InitialActivity extends AppCompatActivity {

    private ViewPager mPager;
    private ViewPagerNewsAdapter mPagerAdapter;
    private LinearLayout pager_indicator;
    private FrameLayout frame_banner;
    public static PepeProgressDialog pepeProgressDialog;
    private ImageView[] dots;
    private int dotsCount;
    private Subscription subscr;
    private float scale_dp;
    private String video_url;
    private ArrayList<News> noticias = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("entrei aqi! on create ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
        if (IntroActivity.iact != null) {
            IntroActivity.iact.finish();
        }
        UI();
        ArrayList<News> array = Helper.getObjectInSharedPref(getApplicationContext(), "newsCache_");
        noticias = (noticias.size() != 0 ? noticias : array);
        if (noticias == null || noticias.size() == 0) {
            System.out.println("noticias null ou 0");
            GET_DATA_FROM_API();
        } else {
            System.out.println("ELSE---noticias null ou 0");
            setReference();
        }
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        postponeEnterTransition();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        List<Fragment> frags = getSupportFragmentManager().getFragments();
        if (frags != null) {
            for (int i = 0; i < frags.size(); i++) {
                Fragment frag = frags.get(i);
                if (frag != null && frag instanceof NewsFragment) {
                    //NewsFragment.myOnKeyDownFrag(keyCode);
                }
            }
        } else {
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        GET_DATA_FROM_API();
    }


    @Override
    protected void onPause() {
        if (subscr.isUnsubscribed()) {
            subscr.unsubscribe();
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        List<Fragment> all_frags = getSupportFragmentManager().getFragments();
        if (all_frags != null) {
            if (all_frags.size() == 0) {
                super.onBackPressed();
            } else {
                Fragment frag = getSupportFragmentManager().findFragmentById(android.R.id.content);

                if (frag != null) {
                    if (frag instanceof NewsFragment) {
                        if (NewsFragment.isFullScrenn) {
                            NewsFragment.activePlayer.setFullscreen(false);
                        } else {
                            getSupportFragmentManager().popBackStack("NEWS", POP_BACK_STACK_INCLUSIVE);
                        }
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    }
                } else {
                    super.onBackPressed();
                }
            }
        } else {
            super.onBackPressed();
        }
    }

    private ImageView img_shadow;
    private ImageView img;

    private void UI() {
        scale_dp = this.getResources().getDisplayMetrics().density;
        pepeProgressDialog = PepeProgressDialog.getInstance(InitialActivity.this);
        img_shadow = (ImageView) findViewById(R.id.img_shadow);
        img = (ImageView) findViewById(R.id.img);

        frame_banner = (FrameLayout) findViewById(R.id.frame_banner);
        frame_banner.setOnClickListener(view -> {
            Intent i = new Intent(InitialActivity.this, TVActivity.class);
            i.putExtra("video", video_url);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, findViewById(R.id.img), "btv_img");
            ActivityCompat.startActivity(this, i, options.toBundle());

        });

        frame_banner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.animate().alpha(0.3f).setDuration(200).start();
                        break;
                    case MotionEvent.ACTION_UP:
                        v.animate().alpha(1f).setDuration(200).start();
                    default:
                        v.setAlpha(1f);
                }
                return false;
            }
        });
    }


    private void GET_DATA_FROM_API() {
        API_BRAGA api_braga_ = (ServiceFactory.createRetrofitService(API_BRAGA.SERVICE_ENDPOINT));

        subscr = api_braga_.getAll("debug")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Tudo>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("!!!!!!!!!!!-Erro", "onError: " + e.getMessage().toString());
                        if (noticias.size() == 0) {
                            AlertForExitorAlert("Erro");
                        } else {
                            AlertForExitorAlert("Alert");
                        }
                    }

                    @Override
                    public void onNext(Tudo tudo) {
                        try {
                            video_url = tudo.getUrlTv();
                            //save video url in shared prefs
                            if (video_url != null) {
                                if (!video_url.equalsIgnoreCase("")) {
                                    SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    SharedPreferences.Editor edit = appSharedPrefs.edit();
                                    edit.putString("VIDEO_URL", video_url);
                                    edit.apply();
                                }
                            }

                            List<News> news = tudo.getNews();
                            if (news.size() > 0) {
                                noticias.clear();
                                for (int i = 0; i < news.size(); i++) {
                                    News newnew = news.get(i);
                                    String data = newnew.getPostDate();
                                    String title = newnew.getPostTitle();
                                    String content = newnew.getPostContent();
                                    String image = newnew.getImage();
                                    String video = null;
                                    String aux = newnew.getVideo();
                                    if (!aux.equalsIgnoreCase("null")) {
                                        video = newnew.getVideo();
                                    }
                                    List<String> galeria = null;
                                    if (newnew.getGaleria().size() > 0) {
                                        galeria = newnew.getGaleria();
                                    }
                                    System.out.println("titulo : " + title + " content " + content + "  imag:" + image );
                                    noticias.add(new News(FormateDate(data), title, content, image, video, galeria));
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //save in cache
                        int count = noticias.size();
                        for (int i = 0; i < count; i++) {
                            News not = noticias.get(i);
                            Helper.saveObjectInSharedPref(getApplicationContext(), not, "newsCache_" + i, count);
                        }
                        setReference();
                    }//fim onNext NEXT
                });
    }

    private void AlertForExitorAlert(String alert) {

        String TitleColor = getResources().getString(0 + R.color.colorPrimary);
        String DividerColor = getResources().getString(0 + R.color.colorPrimary);
        String MessageColor = getResources().getString(0 + R.color.colorPrimary);
        String DialogColor_ = getResources().getString(0 + R.color.colorAccent);


        if (alert.equalsIgnoreCase("Alert")) {
            PepeDialogBuilder dialogBuilder = PepeDialogBuilder.getInstance(InitialActivity.this);
            dialogBuilder
                    .withTitle("  Verifique a sua conexão ")
                    .withMessage("A aplicação não está actualizada \n podem acontecer erros.")
                    .withTitleColor(TitleColor)
                    .withMessageCenter(true)
                    .withDividerColor(DividerColor)
                    .withMessageColor(MessageColor)
                    .withDialogColor(DialogColor_)
                    .withIcon(getResources().getDrawable(R.mipmap.ic_launcher))
                    .isCancelableOnTouchOutside(false)
                    .withButton1Text("OK")
                    .setButton1Click(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            dialogBuilder.dismiss();
                        }
                    })
                    .show();
        } else {
            PepeDialogBuilder dialogBuilder = PepeDialogBuilder.getInstance(InitialActivity.this);
            dialogBuilder
                    .withTitle("  Verifique a sua conexão")
                    .withMessage("A aplicação vai fechar!...")
                    .withTitleColor(TitleColor)
                    .withMessageCenter(true)
                    .withDividerColor(DividerColor)
                    .withMessageColor(MessageColor)
                    .withDialogColor(DialogColor_)
                    .withIcon(getResources().getDrawable(R.mipmap.ic_launcher))
                    .isCancelableOnTouchOutside(false)
                    .withButton1Text("OK")
                    .setButton1Click(v -> {
                        Helper.CloseApp(InitialActivity.this);
                        dialogBuilder.dismiss();
                    })
                    .show();

            Helper.TimerDelayRemoveDialog(InitialActivity.this, 5000, dialogBuilder);

        }


    }

    private void setReference() {
        //          http://www.androprogrammer.com/2015/06/view-pager-with-circular-indicator.html
        //      http://stackoverflow.com/questions/14171755/custom-scrollbar-android
        //      https://medium.com/@BashaChris/the-android-viewpager-has-become-a-fairly-popular-component-among-android-apps-its-simple-6bca403b16d4#.u3xuj664q
        //      https://medium.com/tangoagency/rxjava-meets-android-data-binding-4ca5e1144107#.z2wst9d3n
        // float view
        // https://www.youtube.com/watch?v=kjGPE_XLmwg

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        pager_indicator = (LinearLayout) findViewById(R.id.viewPagerCountDots);
        //set pager height
        ViewTreeObserver viewTreeObserver = mPager.getViewTreeObserver();
        viewTreeObserver
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        layoutParams.bottomMargin = (int) (12 * scale_dp);
                        int viewPagerWidth = mPager.getWidth();
                        float viewPagerHeight = (float) (viewPagerWidth * 0.5625);
                        layoutParams.width = viewPagerWidth;
                        layoutParams.height = (int) viewPagerHeight;
                        mPager.setLayoutParams(layoutParams);
                        mPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });

        // mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        //mPager.setPageTransformer(true, new DepthPageTransformer());
        mPagerAdapter = new ViewPagerNewsAdapter(this, this, noticias);
        mPager.setAdapter(mPagerAdapter);
        CleanDotsIf();
        mPager.setCurrentItem(0);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < dotsCount; i++) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        dots[i].setImageDrawable(getResources().getDrawable(R.drawable.un_selected_dot, null));
                    } else {
                        dots[i].setImageDrawable(getResources().getDrawable(R.drawable.un_selected_dot));
                    }
                }
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selected_dot, null));
                    } else {
                        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selected_dot));
                    }
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        setUiPageViewController();
    }

    private void CleanDotsIf() {
        if (pager_indicator.getChildCount() != 0) {
            for (int i = 0; i < dotsCount; i++) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    dots[i].setImageDrawable(getResources().getDrawable(R.drawable.un_selected_dot, null));
                } else {
                    dots[i].setImageDrawable(getResources().getDrawable(R.drawable.un_selected_dot));
                }
            }
            dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selected_dot));
        }
    }

    private void setUiPageViewController() {
        if (pager_indicator.getChildCount() == 0) {
            dotsCount = mPagerAdapter.getCount();
            dots = new ImageView[dotsCount];
            for (int i = 0; i < dotsCount; i++) {
                dots[i] = new ImageView(this);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    dots[i].setImageDrawable(getResources().getDrawable(R.drawable.un_selected_dot, null));
                } else {
                    dots[i].setImageDrawable(getResources().getDrawable(R.drawable.un_selected_dot));
                }
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(2, 0, 2, 0);
                pager_indicator.addView(dots[i], params);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                dots[mPager.getCurrentItem()].setImageDrawable(getResources().getDrawable(R.drawable.selected_dot, null));
            } else {
                dots[mPager.getCurrentItem()].setImageDrawable(getResources().getDrawable(R.drawable.selected_dot));
            }
        }
    }

    private String FormateDate(String dirtyDate) {
        String cleanDate = "";
        String[] aux = dirtyDate.split(" ");
        String[] aux_ = aux[0].split("-");
        String ano = aux_[0];
        int mes = Integer.valueOf(aux_[1]);
        String dia = aux_[2];
        String mes_escrito;
        switch (mes) {
            case 1:
                mes_escrito = "JAN";
                break;
            case 2:
                mes_escrito = "FEV";
                break;
            case 3:
                mes_escrito = "MAR";
                break;
            case 4:
                mes_escrito = "ABR";
                break;
            case 5:
                mes_escrito = "MAI";
                break;
            case 6:
                mes_escrito = "JUN";
                break;
            case 7:
                mes_escrito = "JUL";
                break;
            case 8:
                mes_escrito = "AGO";
                break;
            case 9:
                mes_escrito = "SET";
                break;
            case 10:
                mes_escrito = "OUT";
                break;
            case 11:
                mes_escrito = "NOV";
                break;
            case 12:
                mes_escrito = "DEZ";
                break;
            default:
                mes_escrito = "";
                break;
        }
        cleanDate = mes_escrito + " " + dia + " | " + ano;
        return cleanDate;
    }

}
