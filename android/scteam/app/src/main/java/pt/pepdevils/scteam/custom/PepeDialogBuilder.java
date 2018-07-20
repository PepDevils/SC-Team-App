package pt.pepdevils.scteam.custom;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import pt.pepdevils.scteam.Helper;
import pt.pepdevils.scteam.R;

/**
 * Created by PepDevils on 20/02/2017.
 */

public class PepeDialogBuilder extends Dialog implements DialogInterface {

    private LinearLayout mLinearLayoutView;
    private RelativeLayout mRelativeLayoutView;
    private LinearLayout mLinearLayoutMsgView;
    private LinearLayout mLinearLayoutTopView;
    private FrameLayout mFrameLayoutCustomView;
    private View mDialogView;
    private View mDivider;
    private TextView mTitle;
    private TextView mMessage;
    private ImageView mIcon;
    private Button mButton1;
    private Button mButton2;
    private boolean isCancelable = true;
    private static PepeDialogBuilder instance;

    //CONSTRUTOR
    private PepeDialogBuilder(Context context, int theme) {
        super(context, theme);
        init(context);
    }

    public static PepeDialogBuilder getInstance(Context context) {
       instance = new PepeDialogBuilder(context, com.gitonway.lee.niftymodaldialogeffects.lib.R.style.dialog_untran);
        return instance;
    }

    //INICIALIZADOR DA UI
    private void init(Context context) {
        mDialogView = View.inflate(context, R.layout.dialog_layout, null);
        mLinearLayoutView = (LinearLayout) mDialogView.findViewById(R.id.parentPanel);
        mRelativeLayoutView = (RelativeLayout) mDialogView.findViewById(R.id.main);
        mLinearLayoutTopView = (LinearLayout) mDialogView.findViewById(R.id.topPanel);
        mLinearLayoutMsgView = (LinearLayout) mDialogView.findViewById(R.id.contentPanel);
        mFrameLayoutCustomView = (FrameLayout) mDialogView.findViewById(R.id.customPanel);
        mTitle = (TextView) mDialogView.findViewById(R.id.alertTitle);
        mMessage = (TextView) mDialogView.findViewById(R.id.message);
        mIcon = (ImageView) mDialogView.findViewById(R.id.icon);
        mDivider = mDialogView.findViewById(R.id.titleDivider);
        mButton1 = (Button) mDialogView.findViewById(R.id.button1);
        mButton2 = (Button) mDialogView.findViewById(R.id.button2);
        setContentView(mDialogView);

        this.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                mLinearLayoutView.setVisibility(View.VISIBLE);
            }
        });
        mRelativeLayoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCancelable) dismiss();
            }
        });
    }

    public PepeDialogBuilder withDividerColor(String colorString) {
        mDivider.setBackgroundColor(Color.parseColor(colorString));
        return this;
    }

    public PepeDialogBuilder withTitle(CharSequence title) {
        toggleView(mLinearLayoutTopView, title);
        mTitle.setText(title);
        return this;
    }

    public PepeDialogBuilder withTitleColor(String colorString) {
        mTitle.setTextColor(Color.parseColor(colorString));
        return this;
    }

    public PepeDialogBuilder withMessage(CharSequence msg) {
        toggleView(mLinearLayoutMsgView, msg);
        mMessage.setText(msg);
        return this;
    }

    public PepeDialogBuilder withMessageCenter(boolean centered) {
        if (centered) {
            mMessage.setGravity(Gravity.CENTER);
        }
        return this;
    }

    public PepeDialogBuilder withMessageColor(String colorString) {
        mMessage.setTextColor(Color.parseColor(colorString));
        return this;
    }

    public PepeDialogBuilder withDialogColor(String colorString) {
        mLinearLayoutView.getBackground().setColorFilter(Helper.getColorFilter(Color.parseColor(colorString)));
        return this;
    }

    public PepeDialogBuilder withIcon(Drawable icon) {
        mIcon.setImageDrawable(icon);
        return this;
    }

    public PepeDialogBuilder withButton1Text(CharSequence text) {
        mButton1.setVisibility(View.VISIBLE);
        mButton1.setText(text);
        return this;
    }

    public PepeDialogBuilder withButton2Text(CharSequence text) {
        mButton2.setVisibility(View.VISIBLE);
        mButton2.setText(text);
        return this;
    }

    public PepeDialogBuilder setButton1Click(View.OnClickListener click) {
        mButton1.setOnClickListener(click);
        return this;
    }

    public PepeDialogBuilder setButton2Click(View.OnClickListener click) {
        mButton2.setOnClickListener(click);
        return this;
    }

    public PepeDialogBuilder isCancelableOnTouchOutside(boolean cancelable)     {
        this.isCancelable = cancelable;
        this.setCanceledOnTouchOutside(cancelable);
        return this;
    }

    private void toggleView(View view, Object obj) {
        if (obj == null) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mButton1.setVisibility(View.GONE);
        mButton2.setVisibility(View.GONE);
    }

}
