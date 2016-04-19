package kaiqiaole.com.uhf_elf;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaiqiaole.com.uhf_elf.Common.Common;
import kaiqiaole.com.uhf_elf.Common.CustomPopWindowPlugin;
import kaiqiaole.com.uhf_elf.Service.Service;
import rfid.ivrjacku1.IvrJackAdapter;
import rfid.ivrjacku1.IvrJackService;
import rfid.ivrjacku1.IvrJackStatus;


public class BindingActivity extends Activity {
    Button btnreturn;

    TextView txttitle;
    String json;
    JSONArray jsonArray;
    ListView list;
    Myadpter myadpter;
    List<Map<String, String>> mapLIst;
    String BoxID;
    Button btnsf,btnft;
    ImageView uhfstate;
    IvrJackService ivrJackService;
    String labtype;
    String labcode;
    Boolean IsScan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding);
        btnreturn = (Button) findViewById(R.id.btnreturn);
        btnreturn.setOnClickListener(onClickListenerreturn);
        txttitle = (TextView) findViewById(R.id.title);
        BoxID = getIntent().getStringExtra("BoxID");
        txttitle.setText(String.format("%1$s", BoxID));
        json = getIntent().getStringExtra("json");
        mapLIst = new ArrayList<>();
        refreshlistdata(json);

        btnsf = (Button)findViewById(R.id.btnsf);
        btnsf.setOnClickListener(onClickListenersf);
        btnft = (Button)findViewById(R.id.btnft);
        btnft.setOnClickListener(onClickListenerft);
        uhfstate = (ImageView)findViewById(R.id.uhfstate);


        list = (ListView) findViewById(R.id.listview);
        list.setOnItemLongClickListener(onItemLongClickListener);
        myadpter = new Myadpter(this);
        list.setAdapter(myadpter);
        ivrJackService = new IvrJackService();

        IsScan = false;
    }



    View.OnClickListener onClickListenersf =new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            labtype = "0";
            IsScan =(IsScan)?false:true;

            if (IsScan)
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ivrJackService.readEPC(true);
                    }
                }).start();

            else
               ivrJackService.readEPC(false);

        }
    };

    View.OnClickListener onClickListenerft =new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            labtype = "1";
            IsScan =(IsScan)?false:true;

            if (IsScan)
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ivrJackService.readEPC(true);
                    }
                }).start();

            else
                ivrJackService.readEPC(false);
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        ivrJackService.open(this,ivrJackAdapter);


    }

    @Override
    protected void onPause() {
        super.onPause();
        ivrJackService.close();
    }

    void refreshlistdata(String json) {
        mapLIst.clear();
        try {
            JSONObject jsonObject = new JSONObject(json);
            jsonArray = jsonObject.getJSONArray("tag_list");


            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                Map<String, String> map = new HashMap<>();
                map.put("code", jsonObject1.getString("label_code"));

                map.put("type", jsonObject1.getString("label_type"));
                map.put("id", jsonObject1.getString("label_id"));
                mapLIst.add(map);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CustomPopWindowPlugin.CLosePopwindow();


                if (msg.what == 10 || msg.what ==11) {

                    String json = msg.obj.toString();

                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        int r = jsonObject.getInt("type");
                        if (r != 1) {
                            Toast.makeText(BindingActivity.this, jsonObject.getJSONObject("result").getString("info"),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        CustomPopWindowPlugin.ShowPopWindow(list, getLayoutInflater(), "处理中...", "处理中...");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Service service = new Service(Common.scanBoxTags, handler);
                                service.KQ_scanBoxTags(BoxID);

                            }
                        }).start();
                        return;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else if (msg.what == 9) {

                    String json = msg.obj.toString();

                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        int r = jsonObject.getInt("type");
                        if (r != 1) {
                            Toast.makeText(BindingActivity.this, jsonObject.getJSONObject("result").getString("info"),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }


                        String arry = jsonObject.getJSONObject("result").getJSONObject("rows").toString();
                        refreshlistdata(arry);
                        myadpter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (msg.what == 12) {

                    String json = msg.obj.toString();

                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        int r = jsonObject.getInt("type");
                        if (r != 1) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(BindingActivity.this);
                            builder.setTitle("提示");
                            builder.setMessage(jsonObject.getJSONObject("result").getString("info"));
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    CustomPopWindowPlugin.ShowPopWindow(list, getLayoutInflater(), "处理中...", "处理中...");
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Service service = new Service(Common.bindTag, handler);
                                            service.KQ_bindTag(labcode,labtype,BoxID);

                                        }
                                    }).start();
                                }
                            });
                            builder.setNegativeButton("取消", null);
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();



                            return;
                        }

                        CustomPopWindowPlugin.ShowPopWindow(list, getLayoutInflater(), "处理中...", "处理中...");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Service service = new Service(Common.bindTag, handler);
                                service.KQ_bindTag(labcode,labtype,BoxID);



                            }
                        }).start();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (msg.what ==2)
                {
                    Toast.makeText(BindingActivity.this, "扫描到多个标签，请重新扫描",
                            Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(BindingActivity.this, "网络错误",
                            Toast.LENGTH_SHORT).show();


        }
    };




    void BindingTag(final String labtype, final String labcode)
    {
        CustomPopWindowPlugin.ShowPopWindow(list, getLayoutInflater(), "处理中...", "处理中...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Service service = new Service(Common.checkTag, handler);
                service.KQ_CheckTag(labcode,labtype);



            }
        }).start();
    }



    AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            final Map<String, String> map = mapLIst.get(i);

            AlertDialog.Builder builder = new AlertDialog.Builder(BindingActivity.this);
            builder.setTitle("提示");
            builder.setMessage("确定解绑标签吗？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    CustomPopWindowPlugin.ShowPopWindow(list, getLayoutInflater(), "处理中...", "处理中...");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Service service = new Service(Common.unbindTag, handler);
                            service.KQ_unbindTag(map.get("code"), map.get("type"), BoxID);

                        }
                    }).start();
                }
            });
            builder.setNegativeButton("取消", null);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();


            return false;
        }
    };

    View.OnClickListener onClickListenerreturn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };


    class Myadpter extends BaseAdapter {

        TextView lab_ID, Lab_type;
        LayoutInflater layoutInflater;

        public Myadpter(Context context) {
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mapLIst.size();
        }

        @Override
        public Object getItem(int i) {
            return mapLIst.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view1 = layoutInflater.inflate(R.layout.list_binding, null);
            lab_ID = (TextView) view1.findViewById(R.id.label_id);
            Lab_type = (TextView) view1.findViewById(R.id.label_type);
            Map<String, String> map = mapLIst.get(i);

            lab_ID.setText(map.get("code"));
            if (map.get("type").equals("1"))
                Lab_type.setText("封条标签");
            else
                Lab_type.setText("身份标签");
            return view1;
        }
    }


    IvrJackAdapter ivrJackAdapter = new IvrJackAdapter() {
        @Override
        public void onConnect(String s) {
            btnsf.setEnabled(true);
            btnft.setEnabled(true);
            uhfstate.setBackground(getResources().getDrawable(R.mipmap.uhf_state_green));
            Toast.makeText(BindingActivity.this,"UHF设备已经连接",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDisconnect() {
            btnsf.setEnabled(false);
            btnft.setEnabled(false);
            uhfstate.setBackground(getResources().getDrawable(R.mipmap.uhf_state_red));
            Toast.makeText(BindingActivity.this,"UHF设备已经断开",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChange(IvrJackStatus ivrJackStatus) {

        }

        @Override
        public void onInventory(String s) {
            ivrJackService.readEPC(false);
            IsScan=false;
            Log.i("标签",s);
            String[] sEPC = s.split(";");
            if (sEPC.length>1)
            {
                handler.sendEmptyMessage(2);
                return;
            }
            labcode = "3000"+s;
            BindingTag(labtype,labcode);
        }
    };



}
