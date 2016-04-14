package kaiqiaole.com.uhf_elf.Common;



import org.apache.http.conn.ssl.SSLSocketFactory;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Ajax Http 调用 类
 * @author YXG
 */
public class AjaxHttpPlugin {
    public static String Tag = "AjaxHttpPlugin";

    private static final String USER_AGENT = "Mozilla/4.0 "
            + "(compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR "
            + "1.1.4322; .NET CLR 2.0.50727; InfoPath.2; Alexa Toolbar)";
    /**
     * http请求对象
     */
    private HttpClient m_httpClient;

    /**
     * 初始化http
     * @return
     */
    public HttpClientClass initHttp()
    {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore
                    .getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            // 设置一些基本参数
            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
            HttpProtocolParams.setUseExpectContinue(params, false);
            HttpProtocolParams.setUserAgent(params, USER_AGENT);
            // 超时设置
            // ConnManagerParams.setTimeout(params, 1000);
            HttpConnectionParams.setConnectionTimeout(params, 10000);// 连接超时(单位：毫秒)
            // HttpConnectionParams.setSoTimeout(params, 30*1000); //
            // 读取超时(单位：毫秒)

            SchemeRegistry schReg = new SchemeRegistry();
            schReg.register(new Scheme("http", PlainSocketFactory
                    .getSocketFactory(), 80));
            schReg.register(new Scheme("https", sf, 443));

            // 设置我们的HttpClient支持HTTP和HTTPS两种模式
            // SchemeRegistry schReg = new SchemeRegistry();
            // schReg.register(new Scheme("http",
            // PlainSocketFactory.getSocketFactory(), 80));
            // schReg.register(new Scheme("https",
            // SSLSocketFactory.getSocketFactory(), 443));

            // 使用线程安全的连接管理来创建HttpClient
            ClientConnectionManager connectionMgr = new ThreadSafeClientConnManager(
                    params, schReg);
            m_httpClient = new DefaultHttpClient(connectionMgr, params);
            HttpClientClass suyHttpClient = new HttpClientClass(m_httpClient);
           
            return suyHttpClient;
        }
        catch (Exception e)
        {

            Logger.e(Tag, e.fillInStackTrace().toString());
            return null;
        }


    }


    /**
     * 组合url字符串 get请求
     * @param jsonArray
     * @return
     */
    public String getUrlString(JSONArray jsonArray)
    {
        String url;
        JSONObject jsonObject;

        try
        {
            url = GlobalConfig.globalConfig.getAppUrl();
            url += "?";
            jsonObject = jsonArray.getJSONObject(0);
            Iterator<String> stringIterator = jsonObject.keys();
            String params="",key;
            while (stringIterator.hasNext())
            {
                key = stringIterator.next();
                params += String.format("%1$s=%2$s&",key
                ,jsonObject.getString(key));

            }

            url += params.substring(0,params.length()-1);


            return url;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * post 请求
     * @param jsonArray
     * @return
     */
    public UrlEncodedFormEntity postUrlString(JSONArray jsonArray)
    {

        JSONObject jsonObject;
        UrlEncodedFormEntity urlEncodedFormEntity;
        BasicNameValuePair basicNameValuePair;
        List<NameValuePair> pairList;
        try
        {

            jsonObject = jsonArray.getJSONObject(0);
            Iterator<String> stringIterator = jsonObject.keys();
            String key;
            pairList = new ArrayList<NameValuePair>();
            while (stringIterator.hasNext())
            {
                key = stringIterator.next();
                basicNameValuePair = new BasicNameValuePair(key,
                        jsonObject.getString(key));
                pairList.add(basicNameValuePair);
            }


            urlEncodedFormEntity = new UrlEncodedFormEntity(pairList, HTTP.UTF_8);
            return urlEncodedFormEntity;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }





}
