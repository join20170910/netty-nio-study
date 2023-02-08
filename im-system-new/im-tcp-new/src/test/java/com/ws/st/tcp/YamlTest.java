package com.ws.st.tcp;

import com.ws.st.tcp.model.TestYaml;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.net.URL;

public class YamlTest {

    @Test
    public void demo01(){
        Yaml yaml = new Yaml();
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("config.yml");
        URL resource = this.getClass().getClassLoader().getResource("config.yml");

        TestYaml testYaml = yaml.loadAs(resourceAsStream, TestYaml.class);
        System.out.println(testYaml);

    }
}
