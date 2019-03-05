package com.my.blog.website.config.redis;
/*
 * create by shuanglin on 19-2-27
 */

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class KryoRedisSerializer<T> implements RedisSerializer<T> {

    private static Logger logger = LoggerFactory.getLogger(KryoRedisSerializer.class);

    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    private static final ThreadLocal<Kryo> kryos = ThreadLocal.withInitial(Kryo::new);

    private Class<T> tClass;

    public KryoRedisSerializer(Class<T> tClass) {
        super();
        this.tClass = tClass;
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (t == null) {
            return EMPTY_BYTE_ARRAY;
        }
        Kryo kryo = kryos.get();
        kryo.setReferences(false);
        kryo.register(tClass);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             Output output = new Output(baos)) {
            kryo.writeClassAndObject(output, t);
            output.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return EMPTY_BYTE_ARRAY;
    }

    @Override
    @SuppressWarnings("unchecked cast")
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        Kryo kryo = kryos.get();
        kryo.setReferences(false);
        kryo.register(tClass);

        try (Input input = new Input(bytes)) {
            return (T) kryo.readClassAndObject(input);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
