package kaiqiaole.com.uhf_elf;

import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import scan.ScanActivity;

public class LabelBindingActivity extends AppCompatActivity {

    Button btnscan;
    EditText barcode;
    Button btnreturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label_binding);
        barcode = (EditText) findViewById(R.id.barcode);
        btnscan = (Button) findViewById(R.id.btnscan);
        btnscan.setOnClickListener(onClickListenersacan);
        barcode.setOnKeyListener(onKeyListenerbarcode);
        btnreturn = (Button) findViewById(R.id.btnreturn);
        btnreturn.setOnClickListener(onClickListenerreturn);
    }


    View.OnKeyListener onKeyListenerbarcode = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            if (event.getKeyCode() == 66)
            {

            }
            return false;
        }
    };


    View.OnClickListener onClickListenerreturn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    /**
     * 扫描
     */
    View.OnClickListener onClickListenersacan = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(LabelBindingActivity.this,ScanActivity.class);
            startActivityForResult(intent,ScanActivity.SCANRESULTREQUEST);
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ScanActivity.SCANRESULTREQUEST == requestCode)//扫描
        {
            switch (resultCode) {
                case 1://结果
                    barcode.setText(data.getExtras().getString("code"));
                    barcode.setSelection(barcode.getText().toString().length());
                    break;
                case 0://没有结果
                    break;
            }
        }
    }
}
