package com.ws.st.tcp;

import com.ws.st.codec.config.BootstrapConfig;
import com.ws.st.tcp.redis.RedisManager;
import com.ws.st.tcp.server.JohnServer;
import com.ws.st.tcp.server.JohnWebSocketServer;
import org.yaml.snakeyaml.Yaml;

import java.net.URL;

public class Starter {
  public static void main(String[] args) {
    start("config.yml");
  }

    private static void start(String path) {
        URL resource = Starter.class.getClassLoader().getResource(path);
        Yaml yaml = new Yaml();
        BootstrapConfig bootstrapConfig = yaml.loadAs(Starter.class.getClassLoader().getResourceAsStream(path), BootstrapConfig.class);
        try {
            new JohnServer(bootstrapConfig.getWshen()).start();
            new JohnWebSocketServer(bootstrapConfig.getWshen()).start();
            RedisManager.init(bootstrapConfig);
        } catch (InterruptedException e) {

            System.exit(500);
        }
    }
}
