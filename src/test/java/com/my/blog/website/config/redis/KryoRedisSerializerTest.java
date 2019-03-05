package com.my.blog.website.config.redis;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Date;

/*
 * create by shuanglin on 19-2-27
 */
public class KryoRedisSerializerTest {
    private static Kryo kryo;
    private static Output output;
    private static Input input;
    private KryoRedisSerializer<String> serializer = new KryoRedisSerializer<>(String.class);

    @Before
    public void setup() throws FileNotFoundException {
        kryo = new Kryo();
        input = new Input(new FileInputStream("file.dat"));
        output = new Output(new FileOutputStream("file.dat"));
    }

    @Test
    public void testKryo() {
        Object someObject = "shuanglin";
        kryo.writeClassAndObject(output, someObject);
        output.close();

        Object readObject = kryo.readClassAndObject(input);
        input.close();

        Assert.assertEquals(readObject, someObject);
    }

    @Test
    public void testKryoWithClass() {
        String someStr = "shuanglin-class";
        Date someDate = new Date(13874907778L);

        kryo.writeObject(output, someStr);
        kryo.writeObject(output, someDate);
        output.close();

        String testStr = kryo.readObject(input, String.class);
        Date testDate = kryo.readObject(input, Date.class);
        input.close();

        Assert.assertEquals(someStr, testStr);
        Assert.assertEquals(testDate.getTime(), 13874907778L);
    }

    @Test
    public void testSerializer() {
        String someStr = "shuanglin-3309";
        byte[] testBytes = serializer.serialize(someStr);
        assert testBytes != null;
        System.out.println(Arrays.toString(testBytes));
        String testStr = serializer.deserialize(testBytes);
        System.out.println(testStr);
        Assert.assertEquals(someStr, testStr);
    }
}
