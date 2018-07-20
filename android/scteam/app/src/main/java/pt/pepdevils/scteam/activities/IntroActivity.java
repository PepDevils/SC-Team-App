package pt.pepdevils.scteam.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import pt.pepdevils.scteam.Helper;
import pt.pepdevils.scteam.R;

public class IntroActivity extends AppCompatActivity {

    private ImageView img_logo_braga;
    public static IntroActivity iact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        Helper.HideStatusBar(this);
        setContentView(R.layout.fragment_splash_screen);
        iact = this;
        img_logo_braga = (ImageView) findViewById(R.id.img_logo_braga);
    }

    @Override
    protected void onResume() {
        super.onResume();
        View v1 = findViewById(R.id.framelayout);
        View v2 = findViewById(R.id.img_logo_braga);
        List<View> listv = new ArrayList<>();
        listv.add(v1);
        listv.add(v2);
        for (View v : listv) {
            OnClick(v);
        }
        AnimateViewCycle(img_logo_braga, 1000);
    }

    private void OnClick(View v) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view != null) {
                    BeginActivity();
                }
            }
        });
    }

    private void BeginActivity() {
        Intent i = new Intent(this, InitialActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void AnimateViewCycle(View v, int cycle) {
        v.animate()
                .setDuration(cycle)
                .alpha(1f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        v
                                .animate()
                                .setDuration(cycle)
                                .alpha(0.5f)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        v
                                                .animate()
                                                .setDuration(cycle)
                                                .alpha(1f)
                                                .setListener(new AnimatorListenerAdapter() {
                                                    @Override
                                                    public void onAnimationEnd(Animator animation) {
                                                        super.onAnimationEnd(animation);
                                                        v
                                                                .animate()
                                                                .setDuration(cycle)
                                                                .alpha(0.5f)
                                                                .setListener(new AnimatorListenerAdapter() {
                                                                    @Override
                                                                    public void onAnimationEnd(Animator animation) {
                                                                        super.onAnimationEnd(animation);
                                                                        v
                                                                                .animate()
                                                                                .setDuration(cycle)
                                                                                .alpha(1f)
                                                                                .setListener(new AnimatorListenerAdapter() {
                                                                                    @Override
                                                                                    public void onAnimationEnd(Animator animation) {
                                                                                        super.onAnimationEnd(animation);
                                                                                        BeginActivity();
                                                                                    }
                                                                                });
                                                                    }
                                                                });

                                                    }
                                                });

                                    }
                                });

                    }
                });
    }
}
