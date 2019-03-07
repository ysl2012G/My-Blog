package com.my.blog.website.basic;/*
 * create by shuanglin on 19-1-28
 */

import com.my.blog.website.exception.TipException;

public class TestException2 {
    public TestException2() {
    }

    void testEx() throws TipException {

        try {
            testEx1();
        } catch (TipException e) {
            System.out.println("testEx, catch exception");

            throw new TipException("method testEx()");
        } finally {
            System.out.println("testEx, finally; ");

        }
    }

    void testEx1() throws TipException {

        try {
            testEx2();

            System.out.println("testEx1, at the end of try");

        } catch (TipException e) {
            System.out.println("testEx1, catch exception");

            throw new TipException("method testEx1()");
        } finally {
            System.out.println("testEx1, finally; ");

        }
    }

    void testEx2() throws TipException {
        boolean ret = true;
        try {
            int b = 12;
            int c;
            for (int i = 2; i >= -2; i--) {
                c = b / i;
                System.out.println("i=" + i);
            }
        } catch (Exception e) {
            System.out.println("testEx2, catch exception");

            throw new TipException("method testEx2()");
        } finally {
            System.out.println("testEx2, finally; return value=" + ret);
        }
    }

    public static void main(String[] args) {
        TestException2 testException1 = new TestException2();
        try {
            testException1.testEx();
        } catch (TipException e) {
            e.printStackTrace();
        }
    }
}
