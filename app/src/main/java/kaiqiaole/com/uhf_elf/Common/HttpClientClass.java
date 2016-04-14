package kaiqiaole.com.uhf_elf.Common;




import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * 封装http请求
 * 对apache的http请求的二次封装
 * @author liuzeren
 *
 */
public final class HttpClientClass {
	
	private static final String TAG = HttpClientClass.class.getSimpleName();
	
	/**
	 * get请求
	 */
	public final static int REQ_METHOD_GET = 0;
	/**
	 * post请求
	 */
	public final static int REQ_METHOD_POST = 1;
	
	private HttpClient m_httpClient = null;
	private HttpGet m_httpGet = null;
	private HttpPost m_httpPost = null;
	private HttpResponse m_httpResp = null;

		
	public HttpClientClass(HttpClient httpClient) {
		m_httpClient = httpClient;
	}
		
	public boolean openRequest(String url, int nReqMethod) {
		closeRequest();

		if (nReqMethod == REQ_METHOD_GET) {
			m_httpGet = new HttpGet(url);
			if (SuyApplication.getApplication()
					.getSuyClient().getSuyUserInfo().m_loginResult!=null) {
				m_httpGet.addHeader("token", SuyApplication.getApplication()
						.getSuyClient().getSuyUserInfo().m_loginResult.m_strSKey);
			}
			m_httpGet.addHeader("deviceID",SuyApplication.getApplication()
			.getUuid());
			m_httpGet.addHeader("deviceType","02");



			if (AppConfig.appConfig !=null) {
				m_httpGet.addHeader("appCode", AppConfig.appConfig.getAppcode());
				m_httpGet.addHeader("appVer", AppConfig.appConfig.getAppver());
			}
			else
			{
				m_httpGet.addHeader("appCode", "000000");
				m_httpGet.addHeader("appVer", SuyApplication.getApplication().AppVerName());
			}




		} else if (nReqMethod == REQ_METHOD_POST) {
			m_httpPost = new HttpPost(url);
			if (SuyApplication.getApplication()
					.getSuyClient().getSuyUserInfo().m_loginResult!=null)
				m_httpPost.addHeader("token",SuyApplication.getApplication()
						.getSuyClient().getSuyUserInfo().m_loginResult.m_strSKey);
			m_httpPost.addHeader("deviceID",SuyApplication.getApplication()
					.getUuid());
			m_httpPost.addHeader("deviceType","02");
			if (AppConfig.appConfig !=null) {
				m_httpPost.addHeader("appCode", AppConfig.appConfig.getAppcode());
				m_httpPost.addHeader("appVer", AppConfig.appConfig.getAppver());
			}
			else
			{
				m_httpPost.addHeader("appCode", "000000");
				m_httpPost.addHeader("appVer", SuyApplication.getApplication().AppVerName());
			}
		} else {
			return false;
		}
		
		return true;
	}


	/**
	 * server调用
	 * @param url
	 * @param nReqMethod
	 * @return
	 */
	public boolean openRequestForServer(String url, int nReqMethod, String token) {
		closeRequest();

		if (nReqMethod == REQ_METHOD_GET) {
			m_httpGet = new HttpGet(url);
			m_httpGet.addHeader("token", token);
			m_httpGet.addHeader("deviceID",SuyApplication.getApplication()
					.getUuid());
			m_httpGet.addHeader("deviceType","02");
			m_httpGet.addHeader("appCode", "000000");
			m_httpGet.addHeader("appVer", SuyApplication.getApplication().AppVerName());

		} else if (nReqMethod == REQ_METHOD_POST) {
			m_httpPost = new HttpPost(url);
			m_httpPost.addHeader("token", token);
			m_httpPost.addHeader("deviceID",SuyApplication.getApplication()
					.getUuid());
			m_httpPost.addHeader("deviceType","02");
			m_httpPost.addHeader("appCode", "000000");
			m_httpPost.addHeader("appVer", SuyApplication.getApplication().AppVerName());

		} else {
			return false;
		}

		return true;
	}



	public void addHeader(String name, String value) {
		if (m_httpGet != null) {
			m_httpGet.addHeader(name, value);
		} else if (m_httpPost != null) {
			m_httpPost.addHeader(name, value);
		} 
	}
	
	public void setEntity(HttpEntity entity) {
		if (m_httpPost != null) {
			m_httpPost.setEntity(entity);
		}
	}
	
	public Boolean sendRequest() {
		if (null == m_httpClient)
			return false;
		
		try {
			if (m_httpGet != null) {

				m_httpResp = m_httpClient.execute(m_httpGet);

				return true;
			} else if (m_httpPost != null) {
				m_httpResp = m_httpClient.execute(m_httpPost);
				return true;
			}
		} catch (ClientProtocolException e) {

			return false;
		} catch (IOException e) {
//
			return false;

		}
		return false;
	}
	
	public int getRespCode() {
		if (m_httpResp != null)
			return m_httpResp.getStatusLine().getStatusCode();
		else
			return 0;
	}
	
	public Header[] getRespHeader() {
		if (m_httpResp != null)
			return m_httpResp.getAllHeaders();
		else
			return null;
	}
	
	public List<Cookie> getCookies() {
		if (m_httpClient != null)
			return ((DefaultHttpClient)m_httpClient).getCookieStore().getCookies();
		else
			return null;
	}


	public HttpResponse getHttpResponse()
	{
		try
		{


			return m_httpResp;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 读取服务器返回数据
	 * @return
	 */
	public byte[] getRespBodyData() {
		try {
			if (m_httpResp != null) {
				InputStream is = m_httpResp.getEntity().getContent();
				byte[] bytData = InputStreamToByte(is);
				is.close();
				return bytData;
			}
		} catch (IllegalStateException e) {
			Logger.e(TAG, e);
		} catch (IOException e) {
			Logger.e(TAG, e);
		}
		
		return null;
	}

	public InputStream getRespBodyDataInputStream() {
		try {
			if (m_httpResp != null) {
				InputStream is = m_httpResp.getEntity().getContent();

				return is;
			}
		} catch (IllegalStateException e) {
			Logger.e(TAG, e);
		} catch (IOException e) {
			Logger.e(TAG, e);
		}

		return null;
	}


	
	public void closeRequest() {
		if (m_httpGet != null)
			m_httpGet.abort();
		
		if (m_httpPost != null)
			m_httpPost.abort();
		
		m_httpResp = null;
		m_httpGet = null;
		m_httpPost = null;
	}
	
	public HttpClient getHttpClient() {
		return m_httpClient;
	}
	
	private byte[] InputStreamToByte(InputStream is) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int ch;
		byte[] buf = new byte[1024 * 4];
		byte data[] = null;
		
		try {
			while ((ch = is.read(buf)) != -1) {
				out.write(buf, 0, ch);
			}
			data = out.toByteArray();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return data;
	}
}