package com.my.blog.website.basic;/*
 * create by shuanglin on 19-1-28
 */

import com.my.blog.website.exception.TipException;

public class TestException {
    public TestException() {
    }

    boolean testEx() throws TipException {
        boolean ret = true;
        try {
            ret = testEx1();
        } catch (TipException e) {
            System.out.println("testEx, catch exception");
            ret = false;
            throw new TipException("method testEx()");
        } finally {
            System.out.println("testEx, finally; return value=" + ret);
            return ret;
        }
    }

    boolean testEx1() throws TipException {
        boolean ret = true;
        try {
            ret = testEx2();
            if (!ret) {
                return false;
            }
            System.out.println("testEx1, at the end of try");
            return ret;
        } catch (TipException e) {
            System.out.println("testEx1, catch exception");
            ret = false;
            throw new TipException("method testEx1()");
        } finally {
            System.out.println("testEx1, finally; return value=" + ret);
//            return ret;
        }
    }

    boolean testEx2() throws TipException {
        boolean ret = true;
        try {
            int b = 12;
            int c;
            for (int i = 2; i >= -2; i--) {
                c = b / i;
                System.out.println("i=" + i);
            }
            return true;
        } catch (Exception e) {
            System.out.println("testEx2, catch exception");
            ret = false;
            throw new TipException("method testEx2()");
        } finally {
            System.out.println("testEx2, finally; return value=" + ret);
//            return ret;
        }
    }

    public static void main(String[] args) {
        TestException testException1 = new TestException();
        try {
            testException1.testEx();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
//public class TestException {
//    static int quotient(int x, int y) throws MyException { // 定义方法抛出异常
//        if (y < 0) { // 判断参数是否小于0
//            throw new MyException("除数不能是负数"); // 异常信息
//        }
//        return x / y; // 返回值
//    }
//
//    public static void main(String args[]) { // 主方法
//        int a = 3;
//        int b = -1;
//        try { // try语句包含可能发生异常的语句
//            int result = quotient(a, b); // 调用方法quotient()
//        } catch (MyException e) { // 处理自定义异常
//            System.out.println(e.getMessage()); // 输出异常信息
//        } catch (ArithmeticException e) { // 处理ArithmeticException异常
//            System.out.println("除数不能为0"); // 输出提示信息
//        } catch (Exception e) { // 处理其他异常
//            System.out.println("程序发生了其他的异常"); // 输出提示信息
//        }
//    }
//
//}
//
//class MyException extends Exception { // 创建自定义异常类
//    String message; // 定义String类型变量
//
//    public MyException(String ErrorMessagr) { // 父类方法
//        message = ErrorMessagr;
//    }
//
//    public String getMessage() { // 覆盖getMessage()方法
//        return message;
//    }
//}






