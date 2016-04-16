package kaiqiaole.com.uhf_elf;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.concurrent.Callable;

import kaiqiaole.com.uhf_elf.Common.Common;
import kaiqiaole.com.uhf_elf.Common.CustomPopWindowPlugin;
import kaiqiaole.com.uhf_elf.Service.Service;

public class Login_activity extends Activity {


    Button btnlogin;
    EditText username, pwd;
    CheckBox checkBoxrememberpwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);
        btnlogin = (Button) findViewById(R.id.btnlogin);
        btnlogin.setOnClickListener(onClickListenerlogin);
        username = (EditText) findViewById(R.id.username);
        pwd = (EditText) findViewById(R.id.password);
        checkBoxrememberpwd = (CheckBox) findViewById(R.id.rememberpwd);

        Boolean isrun = getKeyShareVarForBoolean(getApplicationContext(), "IsRun");//判断程序是否运行
        if (!isrun) {
            setKeyShareVar(getApplicationContext(), "username", "null");
            setKeyShareVar(getApplicationContext(), "userpwd", "null");
            setKeyShareVar(getApplicationContext(), "isrememberpwd", true);
            setKeyShareVar(getApplicationContext(), "IsRun", true);
        } else {
            username.setText(getKeyShareVarForString(getApplicationContext(), "username"));
            Boolean ispwd = getKeyShareVarForBoolean(getApplicationContext(), "isrememberpwd");
            checkBoxrememberpwd.setChecked(ispwd);
            if (ispwd) {
                pwd.setText(getKeyShareVarForString(getApplicationContext(), "userpwd"));
            }


        }


    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CustomPopWindowPlugin.CLosePopwindow();


            if (msg.what == 1) {
                setKeyShareVar(getApplicationContext(), "username", username.getText().toString());
                setKeyShareVar(getApplicationContext(), "userpwd", pwd.getText().toString());
                setKeyShareVar(getApplicationContext(), "isrememberpwd", checkBoxrememberpwd.isChecked());

                String json = msg.obj.toString();

                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int r = jsonObject.getInt("type");
                    if (r !=1)
                    {
                        Toast.makeText(Login_activity.this, jsonObject.getString("info"),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                   Common.worker_id = jsonObject.getJSONObject("result").getJSONObject("rows").getJSONObject("worker_info").getString("worker_id");
                    Common.worker_code = jsonObject.getJSONObject("result").getJSONObject("rows").getJSONObject("worker_info").getString("worker_code");
                    Common.worker_name = jsonObject.getJSONObject("result").getJSONObject("rows").getJSONObject("worker_info").getString("worker_name");
                    Toast.makeText(Login_activity.this, "欢迎 " + Common.worker_name,
                            Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(Login_activity.this, LabelBindingActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

            } else
                Toast.makeText(Login_activity.this, "网络错误",
                        Toast.LENGTH_SHORT).show();

        }
    };

    /**
     * 登录
     */
    View.OnClickListener onClickListenerlogin = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (username.getText().toString().equals("") ||
                    pwd.getText().toString().equals("")) {
                Toast.makeText(Login_activity.this, "用户名和密码不能等于空",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            CustomPopWindowPlugin.ShowPopWindow(btnlogin, getLayoutInflater(), "登录中...", "登录中...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Service service = new Service(Common.Login, handler);
                    service.KQ_Login(username.getText().toString(), pwd.getText().toString());

                }
            }).start();

        }
    };


    /**
     * 设置环境参数
     *
     * @param context
     * @param key
     * @param value
     */
    public static void setKeyShareVar(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_APPEND);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }


    public static Boolean getKeyShareVarForBoolean(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_APPEND);
        return sharedPreferences.getBoolean(key, false);
    }

    public static String getKeyShareVarForString(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_APPEND);
        return sharedPreferences.getString(key, "null");
    }

    public static void setKeyShareVar(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_APPEND);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }


    /**
     * 按返回键
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (keyCode == 4) {


            AlertDialog.Builder builder = new AlertDialog.Builder(Login_activity.this);
            builder.setCancelable(false);
            builder.setTitle("提示");
            builder.setMessage("确定退出吗");
            builder.setPositiveButton("取消", null);
            builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();

                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();


            return false;
        }
        return super.onKeyUp(keyCode, event);
    }


}
