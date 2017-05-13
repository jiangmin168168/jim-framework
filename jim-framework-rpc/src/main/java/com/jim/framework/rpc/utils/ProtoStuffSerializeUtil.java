package com.jim.framework.rpc.utils;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 序列化工具
 * Created by jiang on 2017/5/10.
 */
public class ProtoStuffSerializeUtil {

    private final static Logger logger = LoggerFactory.getLogger(ProtoStuffSerializeUtil.class);

    private static <T> byte[] serializeList(List<T> objs) {

        if(null==objs||objs.size()==0){
            return null;
        }

        Schema<T> schema = (Schema<T>) RuntimeSchema.getSchema(objs.get(0).getClass());

        LinkedBuffer buffer = LinkedBuffer.allocate();
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        byte[] protostuff = null;
        try {
            ProtostuffIOUtil.writeListTo(byteArrayOutputStream,objs, schema, buffer);
            protostuff=byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            buffer.clear();
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                logger.info("ByteArrayOutputStream close error:",e);
            }
        }
        return protostuff;
    }

    public static <T> List<T> deserializeList(byte[] bytes, Class<T> targetClass) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        Schema<T> schema = RuntimeSchema.getSchema(targetClass);

        try {
            return ProtostuffIOUtil.parseListFrom(new ByteArrayInputStream(bytes), schema);
        } catch (IOException e) {
            logger.info("ProtoStuffSerialize.deserializeList error:",e);
        }
        return null;

    }

    public static <T> byte[] serialize(T obj) {
        if (obj == null) {
            throw new RuntimeException("ProtoStuffSerialize.serialize: obj is null");
        }
        if(obj instanceof List){
            return serializeList((List)obj);
        }

        Schema<T> schema = (Schema<T>) RuntimeSchema.getSchema(obj.getClass());

        LinkedBuffer buffer = LinkedBuffer.allocate();
        try {
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            buffer.clear();
        }
    }

    public static <T> T deserialize(byte[] bytes, Class<T> beanClass) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        T instance = null;
        try {
            instance = beanClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Schema<T> schema = RuntimeSchema.getSchema(beanClass);
        ProtostuffIOUtil.mergeFrom(bytes, instance, schema);
        return instance;
    }
}
