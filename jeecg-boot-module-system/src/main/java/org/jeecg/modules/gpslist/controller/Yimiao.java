package org.jeecg.modules.gpslist.controller;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import com.alibaba.fastjson.JSONObject;
import net.sf.json.JSONArray;

/**
 * Http请求
 * @author mszhou
 *
 */
public class Yimiao {
    private static final int TIMEOUT = 45000;
    public static final String ENCODING = "UTF-8";

    /**
     * 创建HTTP连接
     *
     * @param url
     *            地址
     * @param method
     *            方法
     * @param headerParameters
     *            头信息
     * @param body
     *            请求内容
     * @return
     * @throws Exception
     */
    private static HttpURLConnection createConnection(String url,
                                                      String method, Map<String, String> headerParameters, String body,String postData)
            throws Exception {
        //System.out.println("请求url==> "+url);//打印链接
        URL Url = new URL(url);
        trustAllHttpsCertificates();
        HttpURLConnection httpConnection = (HttpURLConnection) Url
                .openConnection();
        // 设置请求时间
        httpConnection.setConnectTimeout(TIMEOUT);
        // 设置 header
        //System.out.println("headerParameters==> "+headerParameters);//打印cok
        if (headerParameters != null) {
            Iterator<String> iteratorHeader = headerParameters.keySet()
                    .iterator();
            while (iteratorHeader.hasNext()) {
                String key = iteratorHeader.next();
                //System.out.println("key==> "+key+"   value==>"+headerParameters.get(key));
                httpConnection.setRequestProperty(key,
                        headerParameters.get(key));
            }
        }
        httpConnection.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded;charset=" + ENCODING);

        // 设置请求方法
        httpConnection.setRequestMethod(method);
        httpConnection.setDoOutput(true);
        httpConnection.setDoInput(true);
        // 写query数据流
        //System.out.println("请求body==> "+body);//打印链接
        //System.out.println("请求postData==> "+postData);//打印链接
        if (!(body == null || body.trim().equals(""))) {
            OutputStream writer = httpConnection.getOutputStream();
            try {
                writer.write(body.getBytes(ENCODING));
            } finally {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
            }
        }else if(!(postData == null || postData.trim().equals(""))){
            DataOutputStream writer = new DataOutputStream(httpConnection.getOutputStream());
            try {
                System.out.println("进入这里==> ");
                writer.write(postData.getBytes(ENCODING));
            }catch (Exception e){
                System.out.println("出错啦==> "+e);
            }finally {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
            }
        }


        //System.out.println(html);


        // 请求结果
        /*int responseCode = httpConnection.getResponseCode();
        if (responseCode != 1000) {
            throw new Exception(responseCode
                    + ":"
                    + inputStream2String(httpConnection.getErrorStream(),
                    ENCODING));
        }*/

        return httpConnection;
    }

    /**
     * POST请求
     * @param address 请求地址
     * @param headerParameters 参数
     * @param body
     * @return
     * @throws Exception
     */
    public static String post(String address,
                              Map<String, String> headerParameters, String body,Map<String, String> headers,String postData) throws Exception {

        return proxyHttpRequest(address, "POST", headers,
                getRequestBody(headerParameters),postData);
    }

    /**
     * GET请求
     * @param address
     * @param headerParameters
     * @param body
     * @return
     * @throws Exception
     */
    public static String get(String address,
                             Map<String, String> headerParameters,Map<String, String> headers, String body,String postData) throws Exception {

        return proxyHttpRequest(address + "?"
                + getRequestBody(headerParameters), "GET", headers, null,postData);
    }

    /**
     * 读取网络文件
     * @param address
     * @param headerParameters
     * @param
     * @param file
     * @return
     * @throws Exception
     */
    public static String getFile(String address,
                                 Map<String, String> headerParameters, File file,String postData) throws Exception {
        String result = "fail";

        HttpURLConnection httpConnection = null;
        try {
            httpConnection = createConnection(address, "POST", null,
                    getRequestBody(headerParameters), postData);
            result = readInputStream(httpConnection.getInputStream(), file);

        } catch (Exception e) {
            throw e;
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }

        }

        return result;
    }

    public static byte[] getFileByte(String address,
                                     Map<String, String> headerParameters,String postData) throws Exception {
        byte[] result = null;

        HttpURLConnection httpConnection = null;
        try {
            httpConnection = createConnection(address, "POST", null,
                    getRequestBody(headerParameters),postData);
            result = readInputStreamToByte(httpConnection.getInputStream());

        } catch (Exception e) {
            throw e;
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }

        }

        return result;
    }

    /**
     * 读取文件流
     * @param in
     * @return
     * @throws Exception
     */
    public static String readInputStream(InputStream in, File file)
            throws Exception {
        FileOutputStream out = null;
        ByteArrayOutputStream output = null;

        try {
            output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                output.write(buffer, 0, len);
            }

            out = new FileOutputStream(file);
            out.write(output.toByteArray());

        } catch (Exception e) {
            throw e;
        } finally {
            if (output != null) {
                output.close();
            }
            if (out != null) {
                out.close();
            }
        }
        return "success";
    }

    public static byte[] readInputStreamToByte(InputStream in) throws Exception {
        FileOutputStream out = null;
        ByteArrayOutputStream output = null;
        byte[] byteFile = null;

        try {
            output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                output.write(buffer, 0, len);
            }
            byteFile = output.toByteArray();
        } catch (Exception e) {
            throw e;
        } finally {
            if (output != null) {
                output.close();
            }
            if (out != null) {
                out.close();
            }
        }

        return byteFile;
    }

    /**
     * HTTP请求
     *
     * @param address
     *            地址
     * @param method
     *            方法
     * @param headerParameters
     *            头信息
     * @param body
     *            请求内容
     * @return
     * @throws Exception
     */
    public static String proxyHttpRequest(String address, String method,
                                          Map<String, String> headerParameters, String body,String postData) throws Exception {
        String result = null;
        HttpURLConnection httpConnection = null;

        try {
            httpConnection = createConnection(address, method,
                    headerParameters, body ,postData);

            String encoding = "UTF-8";
            if (httpConnection.getContentType() != null
                    && httpConnection.getContentType().indexOf("charset=") >= 0) {
                encoding = httpConnection.getContentType()
                        .substring(
                                httpConnection.getContentType().indexOf(
                                        "charset=") + 8);
            }
            result = inputStream2String(httpConnection.getInputStream(),
                    encoding);
            // logger.info("HTTPproxy response: {},{}", address,
            // result.toString());

        } catch (Exception e) {
            // logger.info("HTTPproxy error: {}", e.getMessage());
            throw e;
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }
        return result;
    }

    /**
     * 将参数化为 body
     * @param params
     * @return
     */
    public static String getRequestBody(Map<String, String> params) {
        return getRequestBody(params, true);
    }

    /**
     * 将参数化为 body
     * @param params
     * @return
     */
    public static String getRequestBody(Map<String, String> params,
                                        boolean urlEncode) {
        StringBuilder body = new StringBuilder();
        if (params == null) {
            return "";
        }

        Iterator<String> iteratorHeader = params.keySet().iterator();
        while (iteratorHeader.hasNext()) {
            String key = iteratorHeader.next();
            String value = params.get(key);

            if (urlEncode) {
                try {
                    body.append(key + "=" + URLEncoder.encode(value, ENCODING)
                            + "&");
                } catch (UnsupportedEncodingException e) {
                    // e.printStackTrace();
                }
            } else {
                body.append(key + "=" + value + "&");
            }
        }

        if (body.length() == 0) {
            return "";
        }
        return body.substring(0, body.length() - 1);
    }

    /**
     * 读取inputStream 到 string
     * @param input
     * @param encoding
     * @return
     * @throws IOException
     */
    private static String inputStream2String(InputStream input, String encoding)
            throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input,
                encoding));
        StringBuilder result = new StringBuilder();
        String temp = null;
        while ((temp = reader.readLine()) != null) {
            result.append(temp);
        }

        return result.toString();

    }


    /**
     * 设置 https 请求
     * @throws Exception
     */
    private static void trustAllHttpsCertificates() throws Exception {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String str, SSLSession session) {
                return true;
            }
        });
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
        javax.net.ssl.TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
                .getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc
                .getSocketFactory());
    }


    //设置 https 请求证书
    static class miTM implements javax.net.ssl.TrustManager,javax.net.ssl.X509TrustManager {

        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public void checkServerTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }

        public void checkClientTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }


    }

    //====================================================================
    //============================= 测试调用   ============================
    //====================================================================
    public static void main(String[] args) throws Exception {
        for(int i=1;i<10000;i++){
            System.out.println("启动自动获取疫苗程序第 "+i+" 次!");
            check1();
            Thread.sleep(5000);
        }

    }
    public static void  check1() throws Exception {
        Map<String, String> headers = headers = new HashMap<>();
        headers.put("method", "POST");
        headers.put("authority", "xgsz.szcdc.net");
        headers.put("scheme", "https");
        headers.put("path", "/crmobile/outpatient/nearby");
        headers.put("content-length", "110");
        headers.put("sec-ch-ua", "Google Chrome 89");
        headers.put("selfappid", "wxde83a258df5775a1");
        headers.put("sec-ch-ua-mobile", "?1");
        headers.put("user-agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36");
        headers.put("content-type", "application/x-www-form-urlencoded");
        headers.put("accept", "application/json, text/plain, */*");
        headers.put("appid", "app569d18f5");
        headers.put("token", "-t-qG6HflI4_k_1sB8erl2lvy214mFyysUO0qVHJPzJIf-7k-px3zhYefOeXCaqC2Mr");
        headers.put("reservationtoken", "8f1c1d535f3d43ed807ea543cde7759e");
        headers.put("origin", "https://xgsz.szcdc.net");
        headers.put("sec-fetch-site", "same-origin");
        headers.put("sec-fetch-mode", "cors");
        headers.put("sec-fetch-dest", "empty");
        headers.put("referer", "https://xgsz.szcdc.net/crmobile/?appId=app569d18f5&token=-t-qG6HflI4_k_1sB8erl2lvy214mFyysUO0qVHJPzJIf-7k-px3zhYefOeXCaqC2Mr&cardNo=56F6C06085F3CDF91A6067AE0231B13D1AF5A11F51FAB347DC0E1964B6FCC18E&reservationToken=8f1c1d535f3d43ed807ea543cde7759e&vaccineCode=5601&selfAppId=wxde83a258df5775a1");
        headers.put("accept-language", "zh-CN,zh;q=0.9,en;q=0.8");
        String address="https://xgsz.szcdc.net/crmobile/outpatient/nearby";
        //请求参数
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageNum", "1");
        params.put("numPerPage", "10");
        params.put("areaCode", "440303");
        params.put("bactCode", "5601");
        params.put("outpName", "");
        params.put("outpMapLongitude", "");
        params.put("outpMapLatitude", "");
        params.put("corpCode", "80");
        String res = post(address, params, null,headers,null);
        //System.out.println("输出结果res==>"+res);//打印返回参数
        JSONObject result = JSONObject.parseObject(res);//转JSON
        JSONObject num =JSONObject.parseObject(String.valueOf(result.get("data")));
        JSONArray jsonArray = JSONArray.fromObject(num.get("list"));
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsoni = JSONObject.parseObject(String.valueOf(jsonArray.get(i)));
            System.out.println("疫苗查询结果: "+jsoni.get("outpName")+" : "+jsoni.get("status")+" : "+jsoni.get("corpName"));
            if("1".equals(jsoni.get("status"))&"科兴中维".equals(jsoni.get("corpName"))){
                address="https://xgsz.szcdc.net/crmobile/reservationStock/timeNumber";
                params = new HashMap<String, String>();
                params.put("depaId", String.valueOf(jsoni.get("depaId")));
                params.put("date", "2021-06-03");
                params.put("vaccCode", "5601");
                String res2 = get(address,params,headers,null,null);
                JSONObject result2 = JSONObject.parseObject(res2);//转JSON
                //System.out.println("接种时间为1:"+result2);
                String checkD =String.valueOf(result2.get("data"));
                if("".equals(checkD)||"null".equals(checkD)){
                    System.out.println("剩余针剂数量: "+result2.get("msg"));
                    continue;
                }
                //System.out.println("接种时间为1:"+checkD);
                JSONArray ym = JSONArray.fromObject(String.valueOf(checkD));
                //System.out.println("接种时间为3:"+ym);
                for (int j = 0; j < ym.size(); j++) {
                    JSONObject tym = JSONObject.parseObject(String.valueOf(ym.get(j)));
                    System.out.println("接种疫苗时间:"+tym.get("ouatBeginTime")+" ~ "+tym.get("ouatEndTime")+" ;还有 "+tym.get("restSurplus")+" 针");//打印返回参数
                    SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
                    String bet="2021-06-03 "+tym.get("ouatBeginTime")+":00";
                    String end="2021-06-03 15:10:00";
                    String tot="2021-06-03 18:50:00";
                    Date dateb=sdf.parse(bet);
                    Date datee=sdf.parse(end);
                    Date datet=sdf.parse(tot);
                    if(Integer.parseInt(tym.get("restSurplus").toString())>=1&&!dateb.before(datee)&&dateb.before(datet)){
                    //if(Integer.parseInt(tym.get("restSurplus").toString())>=1&&!dateb.before(datee)){
                        System.out.println("启动提交程序!");
                        headers.put("method", "POST");
                        headers.put("authority", "xgsz.szcdc.net");
                        headers.put("scheme", "https");
                        headers.put("path", "/crmobile/reservation/saveAppointment");
                        headers.put("accept", "application/json, text/plain, */*");
                        //headers.put("accept-encoding", "gzip, deflate, br");
                        headers.put("accept-language", "zh-CN,zh;q=0.9");
                        headers.put("appid", "app569d18f5");
                        headers.put("content-length", "149");
                        headers.put("content-type", "application/json;charset=UTF-8");
                        headers.put("origin", "https://xgsz.szcdc.net");
                        headers.put("referer", "https://xgsz.szcdc.net/crmobile/?appId=app569d18f5&token=-t-qG6HflI4_k_1sB8erl2lvy214mFyysUO0qVHJPzJIf-7k-px3zhYefOeXCaqC2Mr&cardNo=56F6C06085F3CDF91A6067AE0231B13D1AF5A11F51FAB347DC0E1964B6FCC18E&reservationToken=8f1c1d535f3d43ed807ea543cde7759e&vaccineCode=5601&selfAppId=wxde83a258df5775a1");
                        headers.put("sec-ch-ua", "Google Chrome 89");
                        headers.put("sec-ch-ua-mobile", "?0");
                        headers.put("sec-fetch-dest", "empty");
                        headers.put("sec-fetch-mode", "cors");
                        headers.put("sec-fetch-site", "same-origin");
                        headers.put("selfappid", "wxde83a258df5775a1");
                        headers.put("token", "-t-qG6HflI4_k_1sB8erl2lvy214mFyysUO0qVHJPzJIf-7k-px3zhYefOeXCaqC2Mr");
                        headers.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36");

                        address="https://xgsz.szcdc.net/crmobile/reservation/saveAppointment";
                        JSONObject tj =new JSONObject(new LinkedHashMap());
                        tj.put("reusId", "607158701251784704");
                        tj.put("depaId", String.valueOf(tym.get("depaId")));
                        tj.put("corpCode", "80");
                        tj.put("date", "2021-06-03");
                        tj.put("ouatId", String.valueOf(tym.get("ouatId")));
                        tj.put("vaccCodes", "5601");

                        //String jsonParam = "{\"reusId\":\"605353917695258624\",\"depaId\":\"55111F9F-22E5-AC7F-3B92-BCCE8749C61B\",\"corpCode\":\"36\",\"date\":\"2021-06-01\",\"ouatId\":\"673\",\"vaccCodes\":\"5601\"}";
                        //String ret  = post(address, null, null,headers,tj.toString());
                        String ret  = post2(address, headers, tj,"UTF-8");
                        JSONObject result3 = JSONObject.parseObject(ret);//转JSON
                        System.out.println("最终结果:"+result3);
                        if("此疫苗已被预约完".equals(result3.get("msg"))){

                            break;
                        }
                    }
                }

            }
        }
    }
    public static String  check2(){


        return null;
    }
    public static String  check3(){

        return null;
    }



    public static String post2(String url, Map<String, String> headers, JSONObject postData,String charset) throws Exception {
        return net(url, headers, postData, "POST",charset);
    }

    private static String net(String url, Map<String, String> headers,
                                           JSONObject postData, String method,String charset) throws Exception {


        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);
        conn.setRequestMethod(method);

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            conn.setRequestProperty(entry.getKey(), entry.getValue());
        }

        if (method.equalsIgnoreCase("POST")) {
            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeChars(postData.toString());
            os.flush();
        } else {
            conn.connect();
        }

        Object cookie = conn.getHeaderFields().get("Set-Cookie");
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));

        StringBuilder html = new StringBuilder();
        String line = br.readLine();
        System.out.println("最终结果5:"+line);
        System.out.println("最终结果6:"+postData);
        while (line != null) {
            html.append(line);
            line = br.readLine();
        }
        Map<String, Object> result = new HashMap<>(2);
        result.put("cookie", cookie);
        result.put("html", html.toString());
        return html.toString();
    }


}