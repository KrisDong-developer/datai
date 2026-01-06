package com.datai;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

/**
 * 启动程序
 * 
 * @author datai
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class DataIApplication {
    public static void main(String[] args) throws UnknownHostException {
         System.setProperty("spring.devtools.restart.enabled", "false");
        ConfigurableApplicationContext application = SpringApplication.run(DataIApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  集成系统启动成功   ლ(´ڡ`ლ)ﾞ  \n");
        Environment env = application.getEnvironment();
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port");
        System.out.println("\n----------------------------------------------------------\n" +
                " Application datai is running! Access URLs:\n" +
                " Local:        http://localhost:" + port + "/\n" +
                " External:     http://" + ip + ":" + port + "/\n" +
                " Swagger文档:  http://" + ip + ":" + port + "/swagger-ui/index.html\n" +
                " Knife4j文档:  http://" + ip + ":" + port + "/doc.html" + "" + "\n" +
                "----------------------------------------------------------");
    }
}
