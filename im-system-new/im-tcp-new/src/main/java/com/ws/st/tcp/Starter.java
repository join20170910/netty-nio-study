package com.ws.st.tcp;

import com.ws.st.tcp.server.JohnServer;

public class Starter {
  public static void main(String[] args) {
    //
      JohnServer johnServer = null;
      try {
          johnServer = new JohnServer(50050);
      } catch (InterruptedException e) {
      }
  }
}
