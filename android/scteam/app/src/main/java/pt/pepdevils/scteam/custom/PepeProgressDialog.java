package pt.pepdevils.scteam.custom;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import pt.pepdevils.scteam.R;

/**
 * Created by PepDevils on 14/02/2017.
 */

public class PepeProgressDialog extends Dialog implements DialogInterface {

    private Context mContext;
    private View mDialogView;
    private static PepeProgressDialog instance;


    public PepeProgressDialog(Context context, int theme) {
        super(context, theme);
        init(context);
        this.mContext = context;
    }

    public static PepeProgressDialog getInstance(Context context) {
        instance = new PepeProgressDialog(context, R.style.MyDialogTheme);  //Theme.AppCompat.Light
        instance.setCanceledOnTouchOutside(false);
        return instance;
    }

    private void init(Context context) {
        mDialogView = View.inflate(context, R.layout.progress_dialog_layout,null);
        this.setCanceledOnTouchOutside(false);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(mDialogView);
    }

    @Override
    public void onBackPressed() {}

}
