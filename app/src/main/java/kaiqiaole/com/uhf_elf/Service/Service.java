package kaiqiaole.com.uhf_elf.Service;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import kaiqiaole.com.uhf_elf.Common.AjaxHttpPlugin;
import kaiqiaole.com.uhf_elf.Common.Common;
import kaiqiaole.com.uhf_elf.Common.HttpClientClass;

/**
 * Created by Stereo on 16/4/16.
 */
public class Service {


    Handler handler;
    String url;

    public Service(String Method, Handler handler) {
        this.handler = handler;
        url = getUrl(Method);

    }

    public String getUrl(String Method) {
        return String.format("%1$s%2$s", Common.WebUrl, Method);
    }


    public void KQ_Login(String username, String password) {
        HttpClientClass httpClientClass=null;
        try {
            AjaxHttpPlugin ajaxHttpPlugin = new AjaxHttpPlugin();
            httpClientClass = ajaxHttpPlugin.initHttp();
            httpClientClass.openRequest(url, HttpClientClass.REQ_METHOD_POST);
            httpClientClass.addBodyData("worker_code", username);
            httpClientClass.addBodyData("password", password);
            httpClientClass.setEntity(httpClientClass.getPostBodyData());
            Boolean result = httpClientClass.sendRequest();
            if (!result) {
                handler.sendEmptyMessage(-1);

                return;
            }
            byte[] buffer =  httpClientClass.getRespBodyData();
            String json = new String(buffer,"UTF-8");
            Log.i("返回数据",json);
            Message message = handler.obtainMessage();
            message.what=1;
            message.obj =json;
            handler.sendMessage(message);

        } catch (Exception e) {
            e.printStackTrace();
            handler.sendEmptyMessage(-1);
        } finally {
            if (httpClientClass !=null) {
                httpClientClass.closeRequest();
                httpClientClass = null;
            }
        }

    }


    public void KQ_scanBoxTags(String BoxId) {
        HttpClientClass httpClientClass=null;
        try {
            AjaxHttpPlugin ajaxHttpPlugin = new AjaxHttpPlugin();
            httpClientClass = ajaxHttpPlugin.initHttp();
            httpClientClass.openRequest(url, HttpClientClass.REQ_METHOD_POST);
            httpClientClass.addBodyData("box_code", BoxId);
            httpClientClass.setEntity(httpClientClass.getPostBodyData());
            Boolean result = httpClientClass.sendRequest();
            if (!result) {
                handler.sendEmptyMessage(-1);

                return;
            }
            byte[] buffer =  httpClientClass.getRespBodyData();
            String json = new String(buffer,"UTF-8");
            Log.i("返回数据",json);
            Message message = handler.obtainMessage();
            message.what=1;
            message.obj =json;
            handler.sendMessage(message);

        } catch (Exception e) {
            e.printStackTrace();
            handler.sendEmptyMessage(-1);
        } finally {
            if (httpClientClass !=null) {
                httpClientClass.closeRequest();

            }
        }

    }


}
