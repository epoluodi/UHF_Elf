package kaiqiaole.com.uhf_elf;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    Thread.sleep(2000);
                    handler.sendEmptyMessage(0);
                }
                catch (Exception e)
                {e.printStackTrace();}
            }
        }).start();

    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Intent intent = new Intent(SplashActivity.this,Login_activity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        }
    };

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode==4)
            return false;
        return super.onKeyUp(keyCode, event);
    }
}
