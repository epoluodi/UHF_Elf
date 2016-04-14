package kaiqiaole.com.uhf_elf;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class BindingActivity extends AppCompatActivity {
    Button btnreturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding);
        btnreturn = (Button) findViewById(R.id.btnreturn);
        btnreturn.setOnClickListener(onClickListenerreturn);

    }


    View.OnClickListener onClickListenerreturn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

}
