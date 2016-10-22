package com.lxl.im.utils;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.reflect.Reflection;

/*
 * @author tl 2011-4-4
 * 
 * ��־�����࣬������һЩ���÷�����ע���а���һЩ�����ʹ�÷�ʽ�����������޸ĺ�ʹ�á�
 */

public abstract class LogUtil
{
    private static final Level newLevel(String name,int l)
    {
        return new Level(name,l){};
    }
    /*
     * ϰ������log4j��java.util.logging�����Level��ϰ�ߣ�
     * ͬʱҲҪ����ʵ����Ҫ��������Level
     */
    private static final Level ERROR = newLevel("ERROR",950);
    private static final Level IO_ERR = newLevel("IO_ERR",850);
    private static final boolean DEBUG = true;
    private static final Level DEV = newLevel("DEV",1000);
    private static final Level SERVICE_ERR = newLevel("SERVICE_ERR",890);

    static Logger logger=Logger.getLogger("LogUtil");
    /*
     * �����logger���巽ʽ:
     *        static final Logger logger = LogEnv.getLogger();
     * ʹ��logger��classΪ��λ��������̫��logger��
     * Ҳ��Ҫ̫���ģ���ͬ��Դ�ļ���ò�Ҫ����logger,�������Զ�λlog��Ϣ�����Դ�ļ��� 
     * ��Ҫ�����private���������ڲ���/��������ʹ��loggerʱ�����������ⲿclass�϶ඨ��һ������������
     * 
     * ʹ����sun.reflect.Reflection����࣬����֤������JVM���Ժ�İ汾����ʹ�ã�
     * ��ͨ��Thread.getStackTrace()ͬ�����Ի��CallerClass
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
     * û�������쳣�ķ���Ҳ�����׳��쳣��
     * ���߷���û���������쳣��������Runtime��Exception)Ҳ���ܻ��׳�
     * ��Ҫ�ĵط�Ҫ���������쳣��
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
     * �ڷ������˻��д�����Socket��ص�IO�쳣����������״̬�¿��Բ������
     * ��Ҫֱ���̵��κ��쳣����ҪʱӦ�����������־��
     */
    public static void logIOErr(Logger logger,Throwable ex)
    {
        Level level = (ex instanceof IOException)?IO_ERR:ERROR;
        if(logger.isLoggable(level))
            logger.log(level,String.valueOf(ex),ex);
    }
    
    /*
     * �������˴�������ʱ���ܻ����쳣����ʽ����Ӧ��Ĵ�����Ϣ�����ܶ������
     * ���ڰ�ȫ��Э�����ƣ����ظ��ͻ��˵Ĵ�����Ϣ��������쳣�Ķ�ջ��Ϣ��
     * ��Ҫʱ��Ҫ�ڷ������˼�¼�쳣�Ķ�ջ��Ϣ��
     */
    public static void logServiceErr(Logger logger,Throwable ex)
    {
        logger.log(SERVICE_ERR,String.valueOf(ex),ex);
    }
    
    /*
     * ʹ��logger������ҪӰ�����ܣ�Log��Level��DEBUG�Լ����ͼ����ʱ��
     * Ҫ���жϸ�Level��Log�Ƿ������
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
     * ���ڿ��������������log��Ϊ�˽�һ������ϵͳ�����ܺ���Դ��ģ�
     * ������Ԥ����ķ�ʽ���Log
     *     public static final boolean DEVELOP = true;

     *        if(DEVELOP) LogEnv.dev(...)
     * ������ɺ��DEVELOP���false,����������Ե��ⲿ�ִ��롣
     */
    public static void dev(Logger logger,Object msg,Throwable ex)
    {
        logger.log(DEV,String.valueOf(msg),ex);
    }
}