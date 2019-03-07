package com.my.blog.website.basic;/*
 * create by shuanglin on 19-1-28
 */

import com.my.blog.website.exception.TipException;

public class testReThrow {
    public testReThrow() {
    }

    public void test1() throws Exception {
        int a = 3;
        int b = 0;
        int c;
        try {
            c = a / b;
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("test1() has error");
            throw new TipException(e);
        } finally {
            System.out.println("method test1() completed!");
        }
    }

    public static void main(String[] args) throws Exception {
        testReThrow re = new testReThrow();
        try {
            re.test1();
        } catch (TipException e) {
            e.printStackTrace();
            System.out.println("main() has an error");
        } finally {
            System.out.println("method main() completed");
        }

    }
}
