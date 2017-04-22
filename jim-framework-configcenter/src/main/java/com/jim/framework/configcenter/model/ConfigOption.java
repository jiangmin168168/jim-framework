package com.jim.framework.configcenter.model;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import com.netflix.curator.RetryPolicy;
import com.netflix.curator.retry.ExponentialBackoffRetry;

public class ConfigOption {

    private String nameSpace;
    private String zkUrls;
    private RetryPolicy retryPolicy;
    private boolean useRemote;
    private EventBus enventBus;

    public ConfigOption(String nameSpace,String zkUrls,RetryPolicy retryPolicy,boolean useRemote){
        this.nameSpace= Preconditions.checkNotNull(nameSpace);
        this.zkUrls=Preconditions.checkNotNull(zkUrls);
        this.retryPolicy=Preconditions.checkNotNull(retryPolicy);
        this.useRemote=useRemote;
        this.enventBus=new EventBus();

    }
    public ConfigOption(String nameSpace,String zkUrls){
        this(
                nameSpace,
                zkUrls,
                new ExponentialBackoffRetry(60000, 3),
                DefaultOptions.USE_REMOTE_CONFIG);
    }

    public String getNameSpace() {
        return this.nameSpace;
    }
    public String getPath(){
        return this.nameSpace.replace(".","/");
    }

    public String getZkUrls() {
        return this.zkUrls;
    }

    public RetryPolicy getRetryPolicy() {
        return this.retryPolicy;
    }

    public boolean isUseRemote() {
        return this.useRemote;
    }

    public EventBus getEnventBus() {
        return this.enventBus;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("connectStr", zkUrls).add("namespace", nameSpace).add("retryPolicy", retryPolicy).toString();
    }

}
