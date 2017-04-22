package com.jim.framework.configcenter.utils;

import com.jim.framework.configcenter.model.DefaultOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;

public class ZKUtil {

    private final static Logger logger= LoggerFactory.getLogger(ZKUtil.class);

    private static InputStream getInputStreamFromClassPath() {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(DefaultOptions.CONFIG_NAME);
    }

    public static synchronized  String getZkurl(){
        InputStream inputStream= getInputStreamFromClassPath();

        if(inputStream!=null) {
            Properties properties=new Properties();
            
            try {
                properties.load(inputStream);
                String env = System.getProperty("evn");
                System.out.print("env:"+env);
                HashSet<String> envs = new HashSet<String>();
                envs.add("dev");
                envs.add("test");
                envs.add("sim");
                envs.add("online");
                
                if(!envs.contains(env)) {
                	env ="test";
                }
                String zkurlKey=env+"."+ DefaultOptions.CONFIG_ZK_URLS_KEY;
                Object value=properties.get(zkurlKey);
                if(value!=null){
                    logger.info("zkurl:{}",value);
                    return String.valueOf(value);
                }
            } catch (IOException e) {
                logger.error("load config center zk url error", e);
                throw new RuntimeException("load config error");
            }
        }

        return null;
    }


}
