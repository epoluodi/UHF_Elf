package kaiqiaole.com.uhf_elf;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.LabeledIntent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import kaiqiaole.com.uhf_elf.Common.Common;
import kaiqiaole.com.uhf_elf.Common.CustomPopWindowPlugin;
import kaiqiaole.com.uhf_elf.Service.Service;
import scan.ScanActivity;

public class LabelBindingActivity extends Activity {

    Button btnscan;
    EditText barcode;
    Button btnreturn;
    TextView txttitle;

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
        txttitle = (TextView)findViewById(R.id.txttitle);
        txttitle.setText(Common.worker_name + "(" + Common.worker_id + ")");
    }


    View.OnKeyListener onKeyListenerbarcode = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            barcode.setText("NJ01TYQ1099");
            if (event.getKeyCode() == 66 && event.getAction() == KeyEvent.ACTION_UP)
            {
                if (barcode.getText().toString().equals(""))
                {
                    Toast.makeText(LabelBindingActivity.this, "请输入窍号!!",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }


                ScanBox(barcode.getText().toString());
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
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        }
    };


    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CustomPopWindowPlugin.CLosePopwindow();
            String arry="";
            if (msg.what == 1) {

                String json = msg.obj.toString();

                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int r = jsonObject.getInt("type");
                    if (r !=1)
                    {
                        Toast.makeText(LabelBindingActivity.this, jsonObject.getString("info"),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }


                    arry = jsonObject.getJSONObject("result").getJSONObject("rows").toString();

                } catch (Exception e) {
                    e.printStackTrace();
                }




                Intent intent = new Intent(LabelBindingActivity.this, BindingActivity.class);
                intent.putExtra("json",arry);
                intent.putExtra("BoxID",barcode.getText().toString());
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

            } else
                Toast.makeText(LabelBindingActivity.this, "网络错误",
                        Toast.LENGTH_SHORT).show();
        }
    };


    void ScanBox(final String BoxId)
    {
        CustomPopWindowPlugin.ShowPopWindow(btnscan, getLayoutInflater(), "处理中...", "处理中...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Service service = new Service(Common.scanBoxTags, handler);
                service.KQ_scanBoxTags(BoxId);

            }
        }).start();
    }


    @Override
    protected void onResume() {
        super.onResume();
        barcode.setText("");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Common.worker_name = "";
        Common.worker_code = "";
        Common.worker_id = "";
    }

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
