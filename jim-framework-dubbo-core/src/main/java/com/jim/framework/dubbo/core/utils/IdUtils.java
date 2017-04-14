package com.jim.framework.dubbo.core.utils;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/*
* 获取随机数字工具类
* 作者：姜敏
* 版本：V1.0
* 创建日期：2017/4/13
* 修改日期:2017/4/13
*/
public class IdUtils {
    public static final int TOTAL_BITS_LENGTH = 63;

    public static final int TIME_BITS_LENGTH = 41;

    public static final int NODE_BITS_LENGTH = 10;

    private static final int COUNT_BITS_LENGTH = 12;

    private static final long TIME_BITS_MASK = (1L << TIME_BITS_LENGTH) - 1L;

    private static final int TIME_BITS_SHIFT_SIZE = TOTAL_BITS_LENGTH - TIME_BITS_LENGTH;

    private static final int NODE_BITS_MASK = (1 << NODE_BITS_LENGTH) - 1;

    private static final int MAX_COUNTER = 1 << COUNT_BITS_LENGTH;

    private int nodeId;

    private AtomicInteger counter;

    private long lastMillisecond;

    private static IdUtils instance = new IdUtils();

    private IdUtils() {
        this.nodeId = new Random().nextInt(1023) + 1;
        this.counter = new AtomicInteger(0);
    }

    public static long get() {
        long id = 0;
        //正常获取
        try {
            id = instance.nextTicket();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        //再试一次
        if (id == 0) {
            try {
                Thread.sleep(3);//等待3ms
                id = instance.nextTicket();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (id == 0) {
            //应急措施：返回当前时间戳 + 随机数
            return System.currentTimeMillis() + (int) Math.random() * 10000;
        } else {
            return id;
        }
    }

    private synchronized long nextTicket() {
        // 时钟校验
        long currentMillisecond = System.currentTimeMillis();
        if (currentMillisecond < lastMillisecond) {
            throw new RuntimeException("time is out of sync by " + (lastMillisecond - currentMillisecond) + "ms");
        }
        long ts = currentMillisecond & TIME_BITS_MASK;

        // 时间戳移位到前面41位的地方
        ts = ts << TIME_BITS_SHIFT_SIZE;

        if (currentMillisecond == lastMillisecond) {
            // 只有同一毫秒内，才使用小序号
            int count = counter.incrementAndGet();
            //如果计数器达到上限
            if (count >= MAX_COUNTER) {
                //同一毫秒内，直接抛异常，由调用方处理
                throw new RuntimeException("too much requests cause counter overflow");
            }
        }else{
            // 计数器重设为0,不同毫秒，没有必要使用中间值
            this.counter.set(0);
        }

        // 节点信息移位到指定位置
        int node = (nodeId & NODE_BITS_MASK) << COUNT_BITS_LENGTH;

        lastMillisecond = currentMillisecond;
        return ts + node + counter.get();
    }

    /**
     * 获取指定时间点上产生的ID最小值
     *
     * @param timeMs
     * @return
     */
    public static long timeStartId(long timeMs) {
        // 时钟校验
        long ts = timeMs & TIME_BITS_MASK;

        // 时间戳移位到前面41位的地方
        ts = ts << TIME_BITS_SHIFT_SIZE;
        return ts;
    }
}
