package com.jim.framework.web;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CollectionSerializer;
import com.esotericsoftware.kryo.serializers.MapSerializer;
import com.google.common.collect.Lists;
import com.jim.framework.web.model.Student;
import com.jim.framework.web.utils.FastjsonSerialize;
import com.jim.framework.web.utils.JacksonSerialize;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.junit.Test;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * Created by jiang on 2017/3/29.
 */
//@RunWith(SpringRunner.class)
//@SpringBootTest
public class SerializerTest {

    private int count=1;

    public static final int arrayBufferSize=512;

    private Student getStudent(long id){
        Student student=new Student();
        student.setId(id);
        student.setName("jim");
        student.setTitle("BOSS");
        student.setAddress("beijing"+id);
        student.setAge(Long.valueOf(id).intValue());
        student.setHeight(id);
        student.setWeight(id);
        student.setRemark("remark..."+id);
        return student;
    }

    @Test
    public void testProtoStuffSerializer(){

        byte[] arr = null;
        Student deSerialize = null;
        long startTime = System.currentTimeMillis();
        Integer arrSize=null;
        for (int i = 0; i < this.count; i++) {
            Student student=this.getStudent(i);
            arr = ProtoStuffSerialize.serialize(student);
            if(null==arrSize){
                arrSize=arr.length;
                System.out.println("protostuff serialize size:" + arrSize );
            }
            deSerialize = ProtoStuffSerialize.deserialize(arr, Student.class);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("protostuff serialize:" + (endTime - startTime) + "ms");

    }

    @Test
    public void testJdkSerializer(){

        byte[] arr = null;
        Student deSerialize = null;
        long startTime = System.currentTimeMillis();
        Integer arrSize=null;
        for (int i = 0; i < this.count; i++) {
            Student student=this.getStudent(i);
            arr = JdkSerialize.serialize(student);
            if(null==arrSize){
                arrSize=arr.length;
                System.out.println("jdk serialize size:" + arrSize );
            }
            deSerialize = JdkSerialize.deserialize(arr);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("jdk serialize:" + (endTime - startTime) + "ms");
    }

    @Test
    public void testKryoSerializer(){

        byte[] arr = null;
        Student deSerialize = null;
        long startTime = System.currentTimeMillis();
        Integer arrSize=null;
        for (int i = 0; i < this.count; i++) {
            Student student=this.getStudent(i);
            arr = KroySerialize.serialize(student);
            if(null==arrSize){
                arrSize=arr.length;
                System.out.println("kryo serialize size:" + arrSize );
            }
            deSerialize = KroySerialize.deserialize(arr,Student.class);

            List<Student> students= Lists.newArrayList();
            students.add(student);
            byte[] bytes=KroySerialize.serialize(students);
            List<Student> students2=KroySerialize.deserialize(bytes,students.getClass());
            System.out.println("0");
        }
        long endTime = System.currentTimeMillis();
        System.out.println("kryo serialize:" + (endTime - startTime) + "ms");
    }

    @Test
    public void testJacksonSerializer(){

        byte[] arr = null;
        Student deSerialize = null;
        long startTime = System.currentTimeMillis();
        Integer arrSize=null;
        for (int i = 0; i < this.count; i++) {
            Student student=this.getStudent(i);
            arr = JacksonSerialize.serialize(student);
            if(null==arrSize){
                arrSize=arr.length;
                System.out.println("jackson serialize size:" + arrSize );
            }
            deSerialize = JacksonSerialize.deserialize(arr,Student.class);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("jackson serialize:" + (endTime - startTime) + "ms");
    }

    @Test
    public void testFastjsonSerializer(){

        String result = null;
        Student deSerialize = null;
        long startTime = System.currentTimeMillis();
        Integer arrSize=null;
        for (int i = 0; i < this.count; i++) {
            Student student=this.getStudent(i);
            result = FastjsonSerialize.serialize(student);
            if(null==arrSize){
                arrSize=result.length();
                System.out.println("fastjson serialize size:" + arrSize );
            }
            deSerialize = FastjsonSerialize.deserialize(result,Student.class);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("fastjson serialize:" + (endTime - startTime) + "ms");
    }

}

class ProtoStuffSerialize {

    //private static LinkedBuffer buffer = LinkedBuffer.allocate(SerializerTest.arrayBufferSize);

    public static <T> byte[] serialize(T obj) {
        if (obj == null) {
            throw new RuntimeException("JdkSerialize.serialize: obj is null");
        }
        @SuppressWarnings("unchecked")
        Schema<T> schema = (Schema<T>) RuntimeSchema.getSchema(obj.getClass());
        LinkedBuffer buffer = LinkedBuffer.allocate();
        byte[] protostuff = null;
        try {
            protostuff = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            throw e;
        } finally {
            buffer.clear();
        }
        return protostuff;
    }

    public static <T> T deserialize(byte[] bytes, Class<T> targetClass) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        T instance = null;
        try {
            instance = targetClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Schema<T> schema = RuntimeSchema.getSchema(targetClass);
        ProtostuffIOUtil.mergeFrom(bytes, instance, schema);
        return instance;
    }
}

class JdkSerialize{
    public static <T> byte[] serialize(T obj) {
        if (obj == null) {
            throw new RuntimeException("JdkSerialize.serialize: obj is null");
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ObjectOutputStream objectOut;
        try {
            objectOut = new ObjectOutputStream(output);
            objectOut.writeObject(obj);
            objectOut.close();
            output.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return output.toByteArray();

    }

    public static <T> T deserialize(byte[] bytes) {
        if (null==bytes||bytes.length==0){
            return null;
        }

        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        ObjectInputStream objectIn;
        Object object = null;
        try {
            objectIn = new ObjectInputStream(input);
            object = objectIn.readObject();
            objectIn.close();
            input.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return (T)object;
    }
}


//class KroySerialize{
//
//    private static Kryo kryo;
//
//    static {
//        kryo = new Kryo();
//        //kryo.register(Student.class);
//    }
//
//    public static <T> byte[] serialize(T obj) {
//        if (obj == null) {
//            throw new RuntimeException("KroySerialize.serialize:obj is null");
//        }
//        ByteArrayOutputStream output = new ByteArrayOutputStream();
//        Output ko = new Output(output);
//
//        kryo.writeObject(ko, obj);
//
//        byte[] bytes= ko.toBytes();
//        ko.close();
//        return bytes;
//
//    }
//
//    public static <T> T deserialize(byte[] bytes,Class<T> targetClass) {
//
//        if (null==bytes||bytes.length==0){
//            return null;
//        }
//
//        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
//        Input ki = new Input(inputStream);
//        T instance= kryo.readObject(ki, targetClass);
//        ki.close();
//        return instance;
//
//    }
//}
class KroySerialize{

    private static Kryo kryo = null;

    static {
        kryo=new Kryo();
        kryo.register(List.class, new CollectionSerializer());
        kryo.register(Map.class,new MapSerializer());

    }

    public static <T> byte[] serialize(T obj) {
        if (obj == null) {
            throw new RuntimeException("KroySerialize.serialize:obj is null");
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Output ko = new Output(output);

        try {
            kryo.writeObject(ko, obj);
            return ko.toBytes();
        }
        finally {
            ko.close();
        }


    }

    public static <T> T deserialize(byte[] bytes,Class<T> targetClass) {

        if (null==bytes||bytes.length==0){
            return null;
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        Input ki = new Input(inputStream);

        try {

            return kryo.readObject(ki, targetClass);
        }
        finally {
            ki.close();
        }

    }
}