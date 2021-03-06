package com.tuma.util;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import net.sf.json.JSONObject;

/**
 * 微信公众接口工具类
*    
* 项目名称：WeiChatService   
* 类名称：WeiXinUtil   
* 类描述：   
* 创建人：zhouling
* 创建时间：Nov 15, 2013 11:29:07 AM    
* 修改备注：   
* @version    
*
 */
public class WeiXinUtil {
	
	private static LogUtil log=new LogUtil(WeiXinUtil.class);
	
	// 获取access_token的接口地址（GET） 限200（次/天）   
	public final static String access_token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET"; 
	// 菜单创建（POST） 限100（次/天）   
	public static String menu_create_url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";  
	//企业微信token
	public final static String qy_token_url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=ID&corpsecret=SECRECT"; 
	
	
	/**
	 * 发起https请求
	 * @param requestUrl 请求地址
	 * @param requestMethod 请求方式（Get或者post）
	 * @param outputStr 提交数据
	 * @return
	 */
	public static JSONObject httpsRequest(String requestUrl,String requestMethod,String outputStr){
		JSONObject jsonObject=null;
		StringBuffer buffer=new StringBuffer();
		try{
			//创建SSLcontext管理器对像，使用我们指定的信任管理器初始化
			TrustManager[] tm={new MyX509TrustManager()};
			SSLContext sslContext=SSLContext.getInstance("SSL","SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			SSLSocketFactory ssf=sslContext.getSocketFactory();
			
			URL url= new URL(requestUrl);
			HttpsURLConnection httpsUrlConn=(HttpsURLConnection)url.openConnection();
		    httpsUrlConn.setSSLSocketFactory(ssf);
		    httpsUrlConn.setDoInput(true);
		    httpsUrlConn.setDoOutput(true);
		    httpsUrlConn.setUseCaches(false);
		    //设置请求方式（GET/POST）
		    httpsUrlConn.setRequestMethod(requestMethod);
		    if("GET".equalsIgnoreCase(requestMethod)){
		    	httpsUrlConn.connect();
		    }
		    
		    //当有数据需要提交时
		    if(outputStr!=null){
		    	OutputStream outputStream=httpsUrlConn.getOutputStream();
		    	//防止中文乱码
		    	outputStream.write(outputStr.getBytes("UTF-8"));
		    	outputStream.close();
		    	outputStream=null;
		    }
		    
		    //将返回的输入流转换成字符串
		    InputStream inputStream=httpsUrlConn.getInputStream();
		    InputStreamReader inputStreamReader=new InputStreamReader(inputStream,"UTF-8");
		    BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
		    
		    String str=null;
		    while((str=bufferedReader.readLine())!=null){
		    	buffer.append(str);
		    }
		    
		    bufferedReader.close();
		    inputStreamReader.close();
		    
		    inputStream.close();
		    inputStream=null;
		    httpsUrlConn.disconnect();
		    jsonObject=JSONObject.fromObject(buffer.toString());
		    
		}   
		catch (ConnectException ce) {
			// TODO: handle exception
		} 	
		catch (Exception e) {
			// TODO: handle exception
			log.error("https request error:{}", e);
		}
		
		return jsonObject;
	}
	
	public static AccessToken getAccessToken(String appid,String appsecret){
		AccessToken accessToken=null;
		String requestUrl=access_token_url.replace("APPID", appid).replace("APPSECRET", appsecret);
		JSONObject jsonObject=httpsRequest(requestUrl, "GET", null);
		//如果请求成功
		if(jsonObject!=null){
			try{
				accessToken=new AccessToken();
				accessToken.setToken(jsonObject.getString("access_token"));
				accessToken.setExpiresIn(Integer.valueOf(jsonObject.getString("expires_in")));
			}catch (Exception e) {
				// TODO: handle exception
				// TODO: handle exception
				accessToken=null;
				 // 获取token失败   
	            //log.error("获取token失败 errcode:{"+jsonObject.getInt("errcode")+"} errmsg:{"+jsonObject.getString("errmsg")+"}"); 

			}
		}
		return accessToken;
	}
	
	//获取企业微信token
	public static AccessToken getQYToken(String corpid,String secret){
		AccessToken accessToken=null;
		String requestUrl=qy_token_url.replace("ID", corpid).replace("SECRECT", secret);
		JSONObject jsonObject=httpsRequest(requestUrl, "GET", null);
		//如果请求成功
		if(jsonObject!=null){
			try{
				accessToken=new AccessToken();
				accessToken.setToken(jsonObject.getString("access_token"));
				accessToken.setExpiresIn(Integer.valueOf(jsonObject.getString("expires_in")));
			}catch (Exception e) {
				// TODO: handle exception
				// TODO: handle exception
				accessToken=null;
				 // 获取token失败   
	            //log.error("获取token失败 errcode:{"+jsonObject.getInt("errcode")+"} errmsg:{"+jsonObject.getString("errmsg")+"}"); 

			}
		}
		return accessToken;
	}
	
	
    
	

}
