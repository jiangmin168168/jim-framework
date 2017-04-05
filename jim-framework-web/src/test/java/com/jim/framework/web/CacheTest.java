package com.jim.framework.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by jiang on 2017/3/8.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CacheTest {


    @Test
    public void testFIFOCache(){
        int maxCount=5;

        FIFOCache<Integer,Integer> fifoCache=new FIFOCache(maxCount);
        fifoCache.put(1,11);
        fifoCache.put(2,11);
        fifoCache.put(3,11);
        fifoCache.put(4,11);
        fifoCache.put(5,11);

        System.out.println(fifoCache.toString());

        fifoCache.put(6,66);
        fifoCache.get(2);
        fifoCache.put(7,77);
        fifoCache.get(4);

        System.out.println(fifoCache.toString());
    }

    @Test
    public void testLRUCache() {
        int maxCount=5;

        LRUCache<Integer,Integer> lruCache=new LRUCache(maxCount);
        lruCache.put(1,11);
        lruCache.put(2,11);
        lruCache.put(3,11);
        lruCache.put(4,11);
        lruCache.put(5,11);

        System.out.println(lruCache.toString());

        lruCache.put(6,66);
        lruCache.get(2);
        lruCache.put(7,77);
        lruCache.get(4);

        System.out.println(lruCache.toString());
    }

}

class FIFOCache<K,V> extends LinkedHashMap<K,V> {

    private int maxCount;

    public FIFOCache(int maxCount){
        super(maxCount,0.75f, false);
        this.maxCount=maxCount;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
        return super.size()>this.maxCount;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry entry : this.entrySet()) {
            sb.append(String.format("%s:%s ", entry.getKey(), entry.getValue()));
        }
        return sb.toString();
    }
}

class LRUCache<K,V> extends LinkedHashMap<K,V> {

    private int maxCount;

    public LRUCache(int maxCount){
        super(maxCount,0.75f, true);
        this.maxCount=maxCount;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
        return super.size()>this.maxCount;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry entry : this.entrySet()) {
            sb.append(String.format("%s:%s ", entry.getKey(), entry.getValue()));
        }
        return sb.toString();
    }
}
