package kaiqiaole.com.uhf_elf.Common;



import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
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
    List<NameValuePair> pairList;
    /**
     * 初始化http
     * @return
     */
    public HttpClientClass initHttp()
    {
        try {
            pairList = new ArrayList<NameValuePair>();

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


            return null;
        }


    }



    public UrlEncodedFormEntity getPostBodyData()
    {
        try {
            UrlEncodedFormEntity urlEncodedFormEntity;
            urlEncodedFormEntity = new UrlEncodedFormEntity(pairList, HTTP.UTF_8);
            return urlEncodedFormEntity;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }


    public void addBodyData(String key,String value)
    {
        BasicNameValuePair basicNameValuePair;
        basicNameValuePair = new BasicNameValuePair(key,
                value);
        pairList.add(basicNameValuePair);
    }






}
