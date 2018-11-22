package capstone.abang.com.Register;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import capstone.abang.com.R;

public class OwnerSplashActivity extends AppCompatActivity {
    private ImageView imageView;
    private TextView textView1;
    private TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_splash);
        imageView = findViewById(R.id.imgViewLogo);
        textView1 = findViewById(R.id.text1);
        textView2 = findViewById(R.id.text2);

        Animation myAnimation = AnimationUtils.loadAnimation(this, R.anim.mytransition);
        imageView.startAnimation(myAnimation);
        textView1.startAnimation(myAnimation);
        textView2.startAnimation(myAnimation);

        final Intent intent = new Intent(getApplicationContext(), CarOwnerRegistration.class);
        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(2500);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    startActivity(intent);
                    finish();
                }
            }
        };
        timer.start();

    }
}
