package pt.pepdevils.scteam.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionInflater;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import pt.pepdevils.scteam.R;
import pt.pepdevils.scteam.activities.InitialActivity;
import pt.pepdevils.scteam.fragments.NewsFragment;
import pt.pepdevils.scteam.util.News;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by PepDevils on 31/01/2017.
 */

public class ViewPagerNewsAdapter extends PagerAdapter {

    private Context mContext;
    private AppCompatActivity a;
    private ArrayList<News> mList;
    private News noticia = new News();
    private ImageView imageView;

    // public static Subscription sub_in;
    // public static ProgressBar progressBar;

    private static ImageLoader imageLoader = ImageLoader.getInstance();
    private static DisplayImageOptions defaultOptions;

    public ViewPagerNewsAdapter(AppCompatActivity a, Context mContext, ArrayList<News> mList) {
        this.mContext = mContext;
        this.a = a;
        this.mList = mList;
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        // int width = displaymetrics.widthPixels;

        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(mContext)
                .build();
        defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        imageLoader.init(config);
    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int pos) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.pager_item, container, false);
        noticia = mList.get(pos);
        imageView = (ImageView) itemView.findViewById(R.id.img_pager_item);
        imageLoader.displayImage(noticia.getImage(), imageView, defaultOptions);
        TextView tx_title = (TextView) itemView.findViewById(R.id.title_pager_item);
        TextView tx_date = (TextView) itemView.findViewById(R.id.date_pager_item);
        tx_title.setText(noticia.getPostTitle());
        tx_date.setText(noticia.getPostDate());

        itemView.setOnClickListener(v -> {
            InitialActivity.pepeProgressDialog.show();
            Observable
                    .just(true)
                    .delay(2000, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Boolean>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            InitialActivity.pepeProgressDialog.dismiss();
                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            Observable
                                    .just(OpenFragmentAtPos(pos))
                                    .delay(2000, TimeUnit.MILLISECONDS)
                                    .subscribeOn(Schedulers.io())
                                    //.observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Subscriber<Boolean>() {
                                        @Override
                                        public void onCompleted() {}

                                        @Override
                                        public void onError(Throwable e) {
                                            InitialActivity.pepeProgressDialog.dismiss();
                                        }

                                        @Override
                                        public void onNext(Boolean aBoolean) {
                                            InitialActivity.pepeProgressDialog.dismiss();
                                        }
                                    });
                        }
                    });

        });
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((FrameLayout) object);
    }

    private boolean OpenFragmentAtPos(int pos) {
        NewsFragment fragment = NewsFragment.newInstance(mList.get(pos), imageLoader, defaultOptions);
        FragmentTransaction transaction = a.getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack("transaction");
        transaction.addSharedElement(imageView, "image_news");
        transaction.add(android.R.id.content, fragment);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fragment.setEnterTransition(TransitionInflater.from(a).inflateTransition(android.R.transition.fade));
            fragment.setExitTransition(TransitionInflater.from(a).inflateTransition(android.R.transition.fade));
        } else {
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        }
        transaction.addToBackStack("NEWS").commit();
        return true;
    }
}
