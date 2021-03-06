package com.zilla.dbzilla;

import org.junit.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @Before 该方法在每次测试方法调用前都会调用
 * @BeforeClass 该方法在所有测试方法之前调用，只会被调用一次
 * @Test 说明了该方法需要测试
 * @After 该方法在每次测试方法调用后都会调用
 * @AfterClass 该方法在所有测试方法之后调用，只会被调用一次
 * @Ignore 忽略该方法
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
//    @Test
//    public void addition_isCorrect() throws Exception {
//        assertEquals(4, 2 + 2);
//    }

    ///////////////////demo
    @Before
    public void init() {
        System.out.println("------method init called------");
    }

    @BeforeClass
    public static void prepareDataForTest() {
        System.out.println("------method prepareDataForTest called------");
    }

    @Test
    public void test1() {
        System.out.println("------method test1 called------");
    }

    @Test
    public void test2() {
        System.out.println("------method test2 called------");
    }

    @Test
    public void test3() {
        System.out.println("------method test3 called------");
    }

    @After
    public void clearDataForTest() {
        System.out.println("------method clearDataForTest called------");
    }

    @AfterClass
    public static void finish() {
        System.out.println("------method finish called------");
    }
}