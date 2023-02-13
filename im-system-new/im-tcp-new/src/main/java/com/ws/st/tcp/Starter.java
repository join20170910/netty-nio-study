package com.ws.st.tcp;

import com.ws.st.codec.config.BootstrapConfig;
import com.ws.st.tcp.reciver.MessageReceiver;
import com.ws.st.tcp.redis.RedisManager;
import com.ws.st.tcp.register.RegistryZK;
import com.ws.st.tcp.register.ZKit;
import com.ws.st.tcp.server.JohnServer;
import com.ws.st.tcp.server.JohnWebSocketServer;
import com.ws.st.tcp.utils.MqFactory;
import org.I0Itec.zkclient.ZkClient;
import org.yaml.snakeyaml.Yaml;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

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
            MqFactory.init(bootstrapConfig.getWshen().getRedis().getRabbitmq());
            MessageReceiver.init(String.valueOf(bootstrapConfig.getWshen().getBrokerId()));
            registerZK(bootstrapConfig);
        } catch (Exception e) {

            System.exit(500);
        }
    }
    public static void registerZK(BootstrapConfig config) throws UnknownHostException {
        String ipAddress = InetAddress.getLocalHost().getHostAddress();
        ZkClient zkClient = new ZkClient(config.getWshen().getZkConfig().getZkAddr(), config.getWshen().getZkConfig().getZkConnectTimeOut());
        ZKit zKit = new ZKit(zkClient);
        RegistryZK registryZK = new RegistryZK(zKit, ipAddress, config.getWshen());
        new Thread(registryZK).start();

    }
}
