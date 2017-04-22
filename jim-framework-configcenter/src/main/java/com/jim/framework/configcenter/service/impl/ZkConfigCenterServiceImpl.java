package com.jim.framework.configcenter.service.impl;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.eventbus.Subscribe;
import com.jim.framework.configcenter.event.DataChangeEvent;
import com.jim.framework.configcenter.model.ConfigOption;
import com.jim.framework.configcenter.service.ConfigCenterService;
import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.framework.api.GetChildrenBuilder;
import com.netflix.curator.framework.api.GetDataBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ZkConfigCenterServiceImpl extends AbstractConfigCenterService implements ConfigCenterService {

    private final static Logger logger = LoggerFactory.getLogger(ZkConfigCenterServiceImpl.class);

    private ConfigOption configOption = null;
    private CuratorFramework client;

    private Map<String, Object> config = null;

    private static final Object initLockObj = new Object();
    private AtomicBoolean initFlag = new AtomicBoolean(false);

    public ZkConfigCenterServiceImpl(ConfigOption configOption, Map<String, Object> config) {
        this.configOption = Preconditions.checkNotNull(configOption);
        this.config = Preconditions.checkNotNull(config);
        Preconditions.checkNotNull(configOption.getNameSpace());
        Preconditions.checkNotNull(configOption.getZkUrls());
        this.init();
    }

    @Override
    public Map<String, Object> getConfig() {
        return this.config;
    }

    public void init() {
        if (this.initFlag.get()){
            return;
        }
        synchronized (initLockObj) {
            if (this.initFlag.get()){
                return;
            }
            this.startClient();
            this.loadConfig();
            this.wathPath(null);
            this.initFlag.getAndSet(true);
        }
    }

    private void startClient() {
        if (this.client == null) {
            try {
                this.client = CuratorFrameworkFactory.builder()
                        .connectString(configOption.getZkUrls())
                        .namespace(configOption.getNameSpace())
                        .retryPolicy(configOption.getRetryPolicy())
                        .connectionTimeoutMs(20000)
                        .build();
                this.client.start();
                logger.info("zkclient start " + configOption.getNameSpace());

            } catch (Throwable e) {
                if(null!=this.client){
                    this.client.close();
                }
                logger.error("can not connet {} namespace {} error is:", new Object[]{configOption.getZkUrls(), configOption.getNameSpace(), e});
                throw new RuntimeException("CuratorFrameworkFactory start error",e);
            }

        }
    }


    @Override
    public Object getObjectValue(String key) {
        if (this.config == null || this.config.size() < 1) {
            this.init();
        }
        key = checkKeyPrefix(key);
        if (this.config != null) {
            return this.config.get(key);
        }
        return null;
    }

    private void wathPath(String nodePath) {

        try {
            this.watchPathDataChange(nodePath);
            GetChildrenBuilder childrenBuilder = client.getChildren();

            try {
                List<String> children = null;
                if (StringUtils.isEmpty(nodePath)) {
                    children = childrenBuilder.watched().forPath(null);
                } else {
                    children = childrenBuilder.watched().forPath(nodePath);
                }

                this.watchChildDataChange(children, nodePath);
            } catch (Exception e) {
                throw Throwables.propagate(e);
            }

        } catch (Exception e) {
            logger.error("load zk config error namespace={}", configOption.getNameSpace());
            logger.error("load config error", e);
            throw new RuntimeException("load zk error");
        }
    }

    private void watchChildDataChange(List<String> children, String parentPath) throws Exception {
        if (children != null) {

            for (String child : children) {
                String path;
                if (StringUtils.isNotEmpty(parentPath)) {
                    path = parentPath + "/" + child;
                } else {
                    path = "/" + child;
                }

                wathPath(path);
            }
        }
    }

    private void loadConfig() {
        this.loadConfig(null);
    }

    private void watchPathDataChange(String path) throws Exception {

        this.client.getData().usingWatcher(getPathWatcher()).forPath(path);

    }

    private Watcher getPathWatcher() {
        return new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event != null) {

                    logger.debug(event.toString());
                    logger.info("thread id={}    even={}  path={}", new Object[]{Thread.currentThread().getId(), event.getType().name(), event.getPath()});

                    try {
                        boolean isDelete = false;
                        if (event.getState() == Event.KeeperState.SyncConnected) {

                            String path = event.getPath();
                            if (path == null || path.equals("/")) return;
                            switch (event.getType()) {
                                case NodeDeleted:
                                    postRemovePath(event.getPath());
                                    isDelete = true;
                                    break;
                                case NodeDataChanged:
                                    postDataChangeEvent(event);

                                    break;
                                default:
                                    break;
                            }

                            if (!isDelete) {
                                watchPathDataChange(event.getPath());
                            }
                        }

                    } catch (Exception e) {
                        logger.info("zk data changed error:",e);
                    }
                }
            }
        };
    }

    private void postDataChangeEvent(WatchedEvent event) throws Exception {
        byte[] data = client.getData().forPath(event.getPath());
        String value = new String(data, Charsets.UTF_8);
        String key = event.getPath().replace("/", ".");
        postDataChangeKeyValue(key, value);
    }

    private void postDataChangeKeyValue(String key, String value) {
        this.config.put(key, value);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(key, value);
        DataChangeEvent dataChangeEvent = new DataChangeEvent(map);

        configOption.getEnventBus().post(dataChangeEvent);

    }

    private void postRemovePath(String path) {
        if (StringUtils.isEmpty(path)) return;
        String key = path.replace("/", ".");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(key, null);
        DataChangeEvent dataChangeEvent = new DataChangeEvent(map, DataChangeEvent.ChangeType.DELETE);
        configOption.getEnventBus().post(dataChangeEvent);
    }

    private void loadConfig(String nodePath) {

        try {
            if (StringUtils.isNotEmpty(nodePath)) {
                loadData(nodePath);
            }
            GetChildrenBuilder childrenBuilder = client.getChildren();

            try {
                List<String> children = null;
                if (StringUtils.isEmpty(nodePath)) {
                    children = childrenBuilder.watched().forPath(null);
                } else {
                    children = childrenBuilder.watched().forPath(nodePath);
                }

                loadChildsConfig(children, nodePath);
            } catch (Exception e) {
                throw Throwables.propagate(e);
            }

        } catch (Exception e) {
            logger.error("load zk config error namespace={}", configOption.getNameSpace());
            logger.error("load config error", e);
            this.client.close();
            throw new RuntimeException("load zk error");
        }
    }

    private void loadChildsConfig(List<String> children, String parentPath) throws Exception {
        if (children != null) {

            for (String child : children) {
                String path;
                if (StringUtils.isNotEmpty(parentPath)) {
                    path = parentPath + "/" + child;
                } else {
                    path = "/" + child;
                }

                this.loadConfig(path);
            }
        }
    }

    private void loadData(String path) throws Exception {
        GetDataBuilder getDataBuilder = client.getData();
        String key = getKeyFromNodePath(path);
        byte[] data = getDataBuilder.forPath(path);
        String value = new String(data, Charsets.UTF_8);
        this.config.put(key, value);
    }

    private String getKeyFromNodePath(String nodePath) {
        if (StringUtils.isNotEmpty(nodePath)) {
            return nodePath.replace("/", ".");
        }
        return nodePath;
    }


    @Subscribe
    @Override
    public void notify(DataChangeEvent event) {
        //to do something
    }

    @Override
    public void colse() {
        if(null!=this.client) {
            this.client.close();
        }
    }
}
