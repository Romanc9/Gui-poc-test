import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpTemp {
    //HTTP方法里面给了三个形参，分别是字符串类型的请求URL、请求的类型(GET/POST)、请求体
    public static String HTTP(String requestUrl, String requestMethod, String outputStr) {
        //先定义一个buffer字符串缓冲区
        StringBuilder buffer = null;
        try {
            //new一下url，将HTTP方法中的requestUrl重新赋值
            URL url = new URL(requestUrl);
            //建立一个HTTP连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/4.76");
            //http正文内，因此需要设为true
            conn.setDoOutput(true);
            conn.setDoInput(true);
            //设置请求的方法，这样的话无论是GET型请求还是POST型请求我们都适用
            conn.setRequestMethod(requestMethod);
            //连接请求
            conn.connect();
            //往服务器端写内容 也就是发起http请求需要带的参数
            if (null != outputStr) {
                //接收请求的body数据流
                OutputStream os = conn.getOutputStream();
                os.write(outputStr.getBytes(StandardCharsets.UTF_8));
                os.close();
            }

            //读取服务器端返回的内容
            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
            //读取一下字节流
            BufferedReader br = new BufferedReader(isr);
            //其实这里可以与13行写一起 ，即 StringBuilder buffer = new StringBuilder();
            buffer = new StringBuilder();
            //创建一个字符类型，迎来接收返回内容
            String line = null;
            while ((line = br.readLine()) != null) {
                //逐行将返回内容加载到buffer字符串缓冲区
                buffer.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //防止buffer不为空，但是为空了似乎也并不会影响输出
        assert buffer != null;
        //输出打印一下，要想打印Stringbuilder类型的内容，必须toString才行
        System.out.println(buffer.toString());
        //因为这是一个有返回类型的HTTP方法，所以也需要return一个String类型的值，这里返回buffer.toString()即可
        return buffer.toString();
    }
    public static void main(String[] args) {
        //HTTP("http://localhost:9000/minio/bootstrap/v1/verify1","GET",null);
        HTTP("https://localhost:9000/minio/bootstrap/v1/verify","POST","");
    }
}