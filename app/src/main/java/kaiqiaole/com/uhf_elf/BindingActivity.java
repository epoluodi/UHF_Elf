package kaiqiaole.com.uhf_elf;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BindingActivity extends Activity {
    Button btnreturn;

    TextView txttitle;
    String json;
    JSONArray jsonArray;
    ListView list;
    Myadpter myadpter;
    List<Map<String,String>> mapLIst;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding);
        btnreturn = (Button) findViewById(R.id.btnreturn);
        btnreturn.setOnClickListener(onClickListenerreturn);
        txttitle  =(TextView)findViewById(R.id.title);
        txttitle.setText(String.format("窍号:%1$s",getIntent().getStringExtra("BoxID")));
        json = getIntent().getStringExtra("json");
        mapLIst = new ArrayList<>();
        try {
            JSONObject jsonObject=new JSONObject(json);
            jsonArray = jsonObject.getJSONArray("tag_list");


            for (int i=0;i<jsonArray.length();i++)
            {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                Map<String,String> map =new HashMap<>();
                map.put("code",jsonObject1.getString("label_code"));

                map.put("type",jsonObject1.getString("label_type"));
                map.put("id",jsonObject1.getString("label_id"));
                mapLIst.add(map);
            }

        }
        catch (Exception e)
        {e.printStackTrace();}

        list = (ListView)findViewById(R.id.listview);
        myadpter = new Myadpter(this);
        list.setAdapter(myadpter);



    }




    View.OnClickListener onClickListenerreturn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };






    class Myadpter extends BaseAdapter
    {

        TextView lab_ID,Lab_type;
        LayoutInflater layoutInflater;
        public Myadpter(Context context)
        {
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
            View view1=layoutInflater.inflate(R.layout.list_binding,null);
            lab_ID = (TextView)view1.findViewById(R.id.label_id);
            Lab_type = (TextView)view1.findViewById(R.id.label_type);
            Map<String,String>map =mapLIst.get(i);

            lab_ID.setText(map.get("code"));
            if (map.get("type").equals("1"))
                Lab_type.setText("封条标签");
            else
                Lab_type.setText("身份标签");
            return view1;
        }
    }





}
