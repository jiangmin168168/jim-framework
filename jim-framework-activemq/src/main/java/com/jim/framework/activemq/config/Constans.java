package com.jim.framework.activemq.config;

/**
 * Created by jiangmin on 2018/1/5.
 */
public class Constans {

    public static final String QUEUE_NAME="jim.queue.product";

    public static final String LISTENER_A_CONTAINER_QUEUE_NAME="jmsListenerAContainerQueue";

    public static final String LISTENER_B_CONTAINER_QUEUE_NAME="jmsListenerBContainerQueue";

    //private static final String BROKER_URL="failover:(tcp://172.31.10.247:61616,tcp://172.31.10.247:61617,tcp://172.31.10.247:61618,tcp://172.31.10.247:61619)";


//    public static final String PRODUCER_BROKER_URL="failover:(tcp://192.168.10.222:61616,tcp://192.168.10.222:61617,tcp://192.168.10.222:61618,tcp://192.168.10.222:61619)?randomize=true";
//
//    public static final String CONSUMER_BROKER_URL="failover:(tcp://192.168.10.222:61616,tcp://192.168.10.222:61617,tcp://192.168.10.222:61618,tcp://192.168.10.222:61619)?randomize=true";
//
//    public static final String CONSUMER_A_BROKER_URL="failover:(tcp://192.168.10.222:61616,tcp://192.168.10.222:61617)?randomize=true";
//
//    public static final String CONSUMER_B_BROKER_URL="failover:(tcp://192.168.10.222:61618,tcp://192.168.10.222:61619)?randomize=true";

//
//    public static final String PRODUCER_BROKER_URL="failover:(tcp://10.1.0.8:61616,tcp://10.1.0.8:61617,tcp://10.1.0.9:61616,tcp://10.1.0.9:61617)?randomize=true";
//
//    public static final String CONSUMER_BROKER_URL="failover:(tcp://10.1.0.8:61616,tcp://10.1.0.8:61617,tcp://10.1.0.9:61616,tcp://10.1.0.9:61617)?randomize=true";
//
//    public static final String CONSUMER_A_BROKER_URL="failover:(tcp://10.1.0.8:61616,tcp://10.1.0.8:61617)?randomize=true";
//
//    public static final String CONSUMER_B_BROKER_URL="failover:(tcp://10.1.0.9:61616,tcp://10.1.0.9:61617)?randomize=true";



    //public static final String PRODUCER_BROKER_URL="failover:(tcp://10.1.0.8:61616,tcp://10.1.0.8:61617;tcp://10.1.0.9:61616,tcp://10.1.0.9:61617)?randomize=true&useExponentialBackOff=true&maxReconnectDelay=10000&maxReconnectAttempts=50&initialReconnectDelay=10000&reconnectDelay=5000";

    //public static final String PRODUCER_BROKER_URL="failover:(tcp://192.168.10.222:61616,tcp://192.168.10.222:61617;tcp://192.168.10.222:61618,tcp://192.168.10.222:61619)?randomize=true&useExponentialBackOff=true&maxReconnectDelay=10000&maxReconnectAttempts=5&initialReconnectDelay=1000&reconnectDelay=5000&timeout=2000";

    public static final String PRODUCER_BROKER_URL="failover:(tcp://127.0.0.1:61616,tcp://127.0.0.1:61617;tcp://127.0.0.1:61618,tcp://127.0.0.1:61619)?randomize=true&useExponentialBackOff=true&maxReconnectDelay=10000&maxReconnectAttempts=5&initialReconnectDelay=1000&reconnectDelay=5000";

    //public static final String PRODUCER_BROKER_URL="failover:(tcp://127.0.0.1:61616)?randomize=true&useExponentialBackOff=true&maxReconnectDelay=10000&maxReconnectAttempts=5&initialReconnectDelay=1000&reconnectDelay=5000";


    //public static final String PRODUCER_BROKER_URL="failover:(tcp://10.1.0.8:61616,tcp://10.1.0.8:61617)?randomize=true&useExponentialBackOff=true&maxReconnectDelay=10000&maxReconnectAttempts=5&initialReconnectDelay=1000&reconnectDelay=5000";


    public static final String CONSUMER_BROKER_URL="failover:(tcp://10.1.0.8:61616,tcp://10.1.0.9:61616)?randomize=true";

    public static final String CONSUMER_A_BROKER_URL="failover:(tcp://10.1.0.8:61616,tcp://10.1.0.8:61617)?randomize=true";

    public static final String CONSUMER_B_BROKER_URL="failover:(tcp://10.1.0.9:61616,tcp://10.1.0.9:61617)?randomize=true";

    public static final String CONSUMER_BROKER_CLUSTER_URL="failover:(tcp://10.1.0.8:61616);failover:(tcp://10.1.0.9:61616)";

    public static final int MAX_CONNETIONS_COUNT_PRODUCER=10;

}
