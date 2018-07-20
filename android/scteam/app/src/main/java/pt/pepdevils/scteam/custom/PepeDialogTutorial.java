package pt.pepdevils.scteam.custom;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import pt.pepdevils.scteam.R;

/**
 * Created by PepDevils on 16/03/2017.
 */

public class PepeDialogTutorial extends Dialog implements DialogInterface {

    private Activity mContext;
    private View mDialogView;
    private Button mButton1;
    private Button mButton2;
    private static PepeDialogTutorial instance;

    public PepeDialogTutorial(@NonNull Activity context, int theme) {
        super(context, theme);
        init(context);
        this.mContext = context;
    }


    public static PepeDialogTutorial getInstance(Activity context) {
        if(instance != null){
            instance.dismiss();
            instance = null;
            instance = new PepeDialogTutorial(context, R.style.MyDialogTheme);  //Theme.AppCompat.Light

        }else{
            instance = new PepeDialogTutorial(context, R.style.MyDialogTheme);  //Theme.AppCompat.Light

        }

        return instance;
    }

    private void init(Activity context) {
        mDialogView = View.inflate(context, R.layout.tutorial_dialog_layout, null);
        this.setCanceledOnTouchOutside(true);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        mButton1 = (Button) mDialogView.findViewById(R.id.button1);
        mButton2 = (Button) mDialogView.findViewById(R.id.button2);


        setContentView(mDialogView);

    }

    public PepeDialogTutorial setButton1Click(View.OnClickListener click) {
        mButton1.setOnClickListener(click);
        return this;
    }

    public PepeDialogTutorial setButton2Click(View.OnClickListener click) {
        mButton2.setOnClickListener(click);
        return this;
    }



}
