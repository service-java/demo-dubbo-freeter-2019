package com.freeter.zookeeper;


import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;

import java.io.InputStream;
import java.util.Properties;
/**
 * 
 * 注册中心启动类
 * @author xuchen
 * @email 171998110@qq.com
 * @date 2018-10-17 22:11:22
 */
public class ZookeeperStartup {
    public static void main(String[] args) throws Exception {
        QuorumPeerConfig config = new QuorumPeerConfig();
        InputStream is = ZookeeperStartup.class.getResourceAsStream("/zookeeper.properties");
        Properties p = new Properties();
        p.load(is);
        config.parseProperties(p);
        ServerConfig serverconfig = new ServerConfig();
        serverconfig.readFrom(config);
        new ZooKeeperServerMain().runFromConfig(serverconfig);
    }
}
