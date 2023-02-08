package com.ws.st.tcp;

import com.ws.st.tcp.server.JohnServer;
import com.ws.st.tcp.server.JohnWebSocketServer;
import org.yaml.snakeyaml.Yaml;

public class Starter {
  public static void main(String[] args) {

      Yaml yaml = new Yaml();

      try {
          JohnServer johnServer = new JohnServer(50050);
          JohnWebSocketServer johnWebSocketServer = new JohnWebSocketServer(56789);

      } catch (InterruptedException e) {
      }
  }
}
