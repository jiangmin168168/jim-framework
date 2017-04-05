package com.jim.framework.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by jiang on 2017/3/13.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SortTest {

    private final int[] data=new int[]{100,3,4,0,567,1,99};

    private void swap(int[] attr, int i,int j){
        int temp=attr[i];
        attr[i]=attr[j];
        attr[j]= temp;
    }

    private void printArray(){
        StringBuilder stringBuilder=new StringBuilder();
        for(int i=0;i<this.data.length;i++){
            System.out.println(this.data[i]);
        }
    }

    @Test
    public void testBubbleSort(){
        System.out.println("testBubbleSort");
        for(int i=this.data.length-1;i>=0;i--){
            for(int j=i-1;j>=0;j--){
                if(this.data[i]<this.data[j]){
                    this.swap(this.data,i,j);
                }
            }
        }
        this.printArray();
    }

    @Test
    public void testSelectSort(){
        System.out.println("testSelectSort");
        for(int i=0;i<this.data.length;i++){
            int minIndex=i;
            for(int j=i+1;j<this.data.length;j++){
                if(this.data[minIndex]>this.data[j]){
                    minIndex=j;
                }
            }
            if(i!=minIndex){
                this.swap(this.data,i,minIndex);
            }
        }
        this.printArray();
    }

    @Test
    public void testInsertSort(){
        System.out.println("testInsertSort");
        for(int i=1;i<this.data.length;i++){
            int j=i;
            int target=this.data[i];
            while (j>0&&target<this.data[j-1]){
                this.data[j]=this.data[j-1];
                j--;
            }
            this.data[j]=target;
        }
        this.printArray();
    }

    private int partition(int[] attr,int left,int right){
        int v=attr[left];
        int vIndex=left;
        while (left<right){
            while (left<right&&attr[right]>=v){
                right--;
            }
            while (left<right&&attr[left]<=v){
                left++;
            }
            this.swap(attr,left,right);

        }
        this.swap(attr,vIndex,left);
        return left;
    }

    private void quickSort(int[] attr,int left,int right){
        if(left>=right){
            return;
        }
        int v=this.partition(attr,left,right);
        this.quickSort(attr,left,v-1);
        this.quickSort(attr,v+1,right);
    }

    @Test
    public void testQuickSort(){
        System.out.println("testQuickSort");

        this.quickSort(this.data,0,this.data.length-1);

        this.printArray();
    }

    @Test
    public void testTreeMap(){
        Map<String,Object> collection=new TreeMap<>();
        collection.compute("foo",(k, v) -> (v==null)?new ArrayList<Object>():((List)v).add("bar"));
    }
}
