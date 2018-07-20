package pt.pepdevils.scteam;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.List;

import pt.pepdevils.scteam.util.News;

/**
 * Created by PepDevils on 20/02/2017.
 */

public class Helper {

    /**
     * TimerDelayRemoveDialog
     *
     *
     * @param a Aactividade
     * @param time O tempo
     * @param d Dialog
     */
    public static void TimerDelayRemoveDialog(Activity a, long time, final Dialog d) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                d.dismiss();
                CloseApp(a);
            }
        }, time);
    }

    public static void TimerDelayRemoveDialogNoClose(long time, final Dialog d) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                d.dismiss();
            }
        }, time);
    }

    public static void CloseApp(Activity a) {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        // i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        a.startActivity(i);
    }

    public static ColorFilter getColorFilter(int color) {
        ColorMatrixColorFilter colorFilter;
        int red = (color & 0xFF0000) / 0xFFFF;
        int green = (color & 0xFF00) / 0xFF;
        int blue = color & 0xFF;

        float[] matrix = {0, 0, 0, 0, red
                , 0, 0, 0, 0, green
                , 0, 0, 0, 0, blue
                , 0, 0, 0, 1, 0};

        colorFilter = new ColorMatrixColorFilter(matrix);

        return colorFilter;
    }

    public static void HideStatusBar(AppCompatActivity a) {

        //hide status bar
/*        View decorView = a.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
       // int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);*/

        //change color of statusbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = a.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }

        //hide action bar
            /*ActionBar actionBar = a.getWindow().getSupportActionBar();
            actionBar.hide();*/

        a.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

    }

    public static void ShowStatusBar(AppCompatActivity a) {

        //show status bar
/*        View decorView = a.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);*/

        //change color of statusbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = a.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(a.getResources().getColor(R.color.colorPrimaryDark, null));
        }

        //show action bar
/*        ActionBar actionBar = a.getSupportActionBar();
        actionBar.show();*/

        a.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                      /*  | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);*/

    }

    public static void MakeActivitiesTransitions(Activity a) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition transition = new Explode();
            Transition transition_ = new Fade();
            a.getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            a.getWindow().setEnterTransition(transition);
            a.getWindow().setExitTransition(transition);
            a.getWindow().setSharedElementEnterTransition(transition_);
            a.getWindow().setSharedElementExitTransition(transition_);
        }
    }

    public static String colorDecToHex(int p_red, int p_green, int p_blue) {
        String red = Integer.toHexString(p_red);
        String green = Integer.toHexString(p_green);
        String blue = Integer.toHexString(p_blue);

        if (red.length() == 1) {
            red = "0" + red;
        }
        if (green.length() == 1) {
            green = "0" + green;
        }
        if (blue.length() == 1) {
            blue = "0" + blue;
        }

        String colorHex = "#" + red + green + blue;
        return colorHex;
    }

    /**
     * Get all the views which matches the given Tag recursively
     *
     * @param root parent view. for e.g. Layouts
     * @param tag  tag to look for
     * @return List of views
     */
    public static List<View> findViewWithTagRecursively(ViewGroup root, Object tag) {
        List<View> allViews = new ArrayList<View>();

        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View childView = root.getChildAt(i);

            if (childView instanceof ViewGroup) {
                allViews.addAll(findViewWithTagRecursively((ViewGroup) childView, tag));
            } else {
                final Object tagView = childView.getTag();
                if (tagView != null && tagView.equals(tag))
                    allViews.add(childView);
            }
        }

        return allViews;
    }

    public static List<View> findEspecifiqueViewRecursively(ViewGroup root) {
        List<View> allViews = new ArrayList<>();
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View childView = root.getChildAt(i);
            if (childView instanceof ViewGroup) {
                allViews.addAll(findEspecifiqueViewRecursively((ViewGroup) childView));
            } else if (childView instanceof ProgressBar) {
                allViews.add(childView);
            }
        }
        return allViews;
    }

    public static void saveObjectInSharedPref(Context c, News not, String tag, int total) {
        try {
            SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(c);
            SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
            Gson gson = new Gson();
            String json = gson.toJson(not);
            prefsEditor.putInt("total", total);
            prefsEditor.putString(tag, json);
            prefsEditor.apply();
        } catch (Exception e) {
            Log.d("CacheDatePepeError:", "saveObjectInFileCache: " + e.getMessage());
        }
    }

    public static ArrayList<News> getObjectInSharedPref(Context c, String tag) {
        ArrayList<News> notic = new ArrayList<>();
        try {
            SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(c);
            Gson gson = new Gson();
            int total = appSharedPrefs.getInt("total", 0);
            if (total > 0) {
                for (int i = 0; i < total; i++) {
                    String json = appSharedPrefs.getString(tag + i, "");
                    News n = gson.fromJson(json, News.class);
                    notic.add(n);
                }
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return notic;
    }

    public static void WarningInternetSnack(Activity a) {
        ConnectivityManager conectivtyManager = (ConnectivityManager) a.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null && conectivtyManager.getActiveNetworkInfo().isAvailable() && conectivtyManager.getActiveNetworkInfo().isConnected()) {
        } else {
            Snackbar.make(a.findViewById(android.R.id.content), "Ligue a Internet", Snackbar.LENGTH_LONG)
                    .setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // http://stackoverflow.com/questions/2318310/how-can-i-call-wi-fi-settings-screen-from-my-application-using-android
                            a.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                            a.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }
                    })
                    .setActionTextColor(Color.RED)
                    .show();
        }
    }

}
