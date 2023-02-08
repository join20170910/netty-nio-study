package com.ws.st.tcp;

import com.ws.st.codec.config.BootstrapConfig;
import com.ws.st.tcp.server.JohnServer;
import com.ws.st.tcp.server.JohnWebSocketServer;
import org.yaml.snakeyaml.Yaml;

import java.net.URL;

public class Starter {
  public static void main(String[] args) {
      URL resource = Starter.class.getClassLoader().getResource("config.yml");

      Yaml yaml = new Yaml();

      BootstrapConfig bootstrapConfig = yaml.loadAs(Starter.class.getClassLoader().getResourceAsStream("config.yml"), BootstrapConfig.class);
      try {
          JohnServer johnServer = new JohnServer(bootstrapConfig.getWshen());
         // JohnWebSocketServer johnWebSocketServer = new JohnWebSocketServer(tcpConfig);

      } catch (InterruptedException e) {
      }
  }
}
