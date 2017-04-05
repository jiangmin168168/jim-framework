package com.jim.framework.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

/**
 * Created by jiang on 2017/3/18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CollectionTest {

    @Test
    public void testMyArrayList(){
        int capacity=32;
        MyArrayList list=new MyArrayList(capacity);
        for(int i=0;i<capacity;i++){
            list.add(i);
        }
        System.out.println(list.getCount());
        System.out.println(list.toString());
    }
}

class MyArrayList{
    private transient Object[] data;

    private static final int DEFAULT_CAPACITY=16;

    private static final float DEFAULT_FACTOR=0.75F;

    private int capacity;

    private float factor;

    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public float getFactor() {
        return factor;
    }

    public void setFactor(float factor) {
        this.factor = factor;
    }

    private Object[] getData() {
        return data;
    }

    private void setData(Object[] data) {
        this.data = data;
    }

    private int getCapacity() {
        return capacity;
    }

    private void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public MyArrayList(){
        this(DEFAULT_CAPACITY);

    }

    public MyArrayList(int capacity){
        this(capacity,DEFAULT_FACTOR);
    }
    public MyArrayList(int capacity,float factor){
        this.setCapacity(capacity);
        this.setFactor(factor);
        this.data=new Object[this.getCapacity()];
    }

    private void ensureCapacity(int minCapacity){
        if(minCapacity>this.getCapacity()*this.getFactor()){
            //growth
            int newCapacity=this.getCapacity()+this.getCapacity()>>1;
            Object[] newArrays=new Object[newCapacity];
            Arrays.copyOf(this.getData(),this.getCount(),newArrays.getClass());
        }
    }

    public void add(Object value){
        this.ensureCapacity(this.getCount()+1);
        this.data[count++]=value;
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder=new StringBuilder();
        for(int i=0;i<this.getCount();i++){
            stringBuilder.append(this.data[i]+",");
        }
        return stringBuilder.toString();
    }

}
