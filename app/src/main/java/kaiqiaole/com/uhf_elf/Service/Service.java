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


    /**
     * 登录
     * @param username
     * @param password
     */
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

    /**
     * 扫描窍号
     * @param BoxId
     */
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
            message.what=9;
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


    /**
     * 解绑
     * @param Labcode
     * @param labtype
     * @param boxid
     */
    public void KQ_unbindTag(String Labcode,String labtype,String boxid) {
        HttpClientClass httpClientClass=null;
        try {
            AjaxHttpPlugin ajaxHttpPlugin = new AjaxHttpPlugin();
            httpClientClass = ajaxHttpPlugin.initHttp();
            httpClientClass.openRequest(url, HttpClientClass.REQ_METHOD_POST);
            httpClientClass.addBodyData("box_code", boxid);
            httpClientClass.addBodyData("label_type", labtype);
            httpClientClass.addBodyData("label_code", Labcode);
            httpClientClass.addBodyData("worker_id", Common.worker_id);
            httpClientClass.addBodyData("worker_code", Common.worker_code);
            httpClientClass.addBodyData("worker_name", Common.worker_name);
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
            message.what=10;
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


    /**
     * 绑定
     * @param Labcode
     * @param labtype
     * @param boxid
     */
    public void KQ_bindTag(String Labcode,String labtype,String boxid) {
        HttpClientClass httpClientClass=null;
        try {
            AjaxHttpPlugin ajaxHttpPlugin = new AjaxHttpPlugin();
            httpClientClass = ajaxHttpPlugin.initHttp();
            httpClientClass.openRequest(url, HttpClientClass.REQ_METHOD_POST);
            httpClientClass.addBodyData("box_code", boxid);
            httpClientClass.addBodyData("label_type", labtype);
            httpClientClass.addBodyData("label_code", Labcode);
            httpClientClass.addBodyData("worker_id", Common.worker_id);
            httpClientClass.addBodyData("worker_code", Common.worker_code);
            httpClientClass.addBodyData("worker_name", Common.worker_name);
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
            message.what=11;
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


    /**
     * 检查标签
     * @param Labcode
     * @param labtype
     */
    public void KQ_CheckTag(String Labcode,String labtype) {
        HttpClientClass httpClientClass=null;
        try {
            AjaxHttpPlugin ajaxHttpPlugin = new AjaxHttpPlugin();
            httpClientClass = ajaxHttpPlugin.initHttp();
            httpClientClass.openRequest(url, HttpClientClass.REQ_METHOD_POST);

            httpClientClass.addBodyData("label_type", labtype);
            httpClientClass.addBodyData("label_code", Labcode);

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
            message.what=12;
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
