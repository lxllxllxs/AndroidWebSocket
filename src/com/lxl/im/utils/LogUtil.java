package com.lxl.im.utils;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.reflect.Reflection;

/*
 * @author tl 2011-4-4
 * 
 * 日志工具类，定义了一些常用方法，注释中包含一些建议的使用方式，可以任意修改和使用。
 */

public abstract class LogUtil
{
    private static final Level newLevel(String name,int l)
    {
        return new Level(name,l){};
    }
    /*
     * 习惯了用log4j，java.util.logging定义的Level不习惯，
     * 同时也要根据实际需要定义更多的Level
     */
    private static final Level ERROR = newLevel("ERROR",950);
    private static final Level IO_ERR = newLevel("IO_ERR",850);
    private static final boolean DEBUG = true;
    private static final Level DEV = newLevel("DEV",1000);
    private static final Level SERVICE_ERR = newLevel("SERVICE_ERR",890);

    static Logger logger=Logger.getLogger("LogUtil");
    /*
     * 建议的logger定义方式:
     *        static final Logger logger = LogEnv.getLogger();
     * 使用logger以class为单位，不会有太多logger。
     * 也不要太吝啬，不同的源文件最好不要共用logger,否则难以定位log信息的输出源文件。 
     * 不要定义成private，否则在内部类/匿名类里使用logger时编译器会在外部class上多定义一个隐含方法。
     * 
     * 使用了sun.reflect.Reflection这个类，不保证在其他JVM或以后的版本可以使用，
     * 但通过Thread.getStackTrace()同样可以获得CallerClass
     */
/*    public static Logger getLogger()
    {
        return getLogger(Reflection.getCallerClass(2));
    }
    public static Logger getLogger(String name)
    {
        return Logger.getLogger(name);
    }
    public static Logger getLogger(Class<?> cls)
    {
        return getLogger(cls.getName());
    }
    */
    /*
     * 没有声明异常的方法也可能抛出异常，
     * 或者方法没有声明的异常（包括非Runtime的Exception)也可能会抛出
     * 必要的地方要捕获这种异常。
     * 
     */
    public static void unexpectedException(Logger logger,Throwable ex)
    {
        logger.log(ERROR,"Unexpected exception: "+ex,ex);
    }
    
    
    public static void logErr(Logger logger,Throwable ex)
    {
        logger.log(ERROR,String.valueOf(ex),ex);
    }
    
    /*
     * 在服务器端会有大量的Socket相关的IO异常，正常运行状态下可以不输出，
     * 不要直接吞掉任何异常，必要时应可以输出到日志。
     */
    public static void logIOErr(Logger logger,Throwable ex)
    {
        Level level = (ex instanceof IOException)?IO_ERR:ERROR;
        if(logger.isLoggable(level))
            logger.log(level,String.valueOf(ex),ex);
    }
    
    /*
     * 服务器端处理请求时可能会以异常的形式处理应答的错误信息，但很多情况下
     * 由于安全或协议限制，返回给客户端的错误信息不会包含异常的堆栈信息，
     * 必要时需要在服务器端记录异常的堆栈信息。
     */
    public static void logServiceErr(Logger logger,Throwable ex)
    {
        logger.log(SERVICE_ERR,String.valueOf(ex),ex);
    }
    
    /*
     * 使用logger尽量不要影响性能，Log的Level在DEBUG以及更低级别的时候
     * 要先判断该Level的Log是否被输出。
     *       if(logger.isLoggable(LogEnv.DEBUG))
     *           LogEnv.debug(...);
     */
    public static void d(Object msg)
    {
    	if(!DEBUG){
    		return;
    	}
    	System.out.println(msg.toString());
    }
    
    /*
     * 仅在开发过程中输出的log，为了进一步减少系统的性能和资源损耗，
     * 建议用预编译的方式输出Log
     *     public static final boolean DEVELOP = true;

     *        if(DEVELOP) LogEnv.dev(...)
     * 开发完成后把DEVELOP设成false,编译器会忽略掉这部分代码。
     */
    public static void dev(Logger logger,Object msg,Throwable ex)
    {
        logger.log(DEV,String.valueOf(msg),ex);
    }
}