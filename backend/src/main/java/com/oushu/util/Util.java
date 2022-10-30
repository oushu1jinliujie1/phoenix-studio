package com.oushu.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Util {
    /**
     * 脚本文件具体执行及脚本执行过程探测
     * @param script 脚本文件内容
     * @throws Exception
     */
    public static void callScript(String script) throws Exception{
        try {
            String cmd = script;

            //启动独立线程等待process执行完成
            CommandWaitForThread commandThread = new CommandWaitForThread(cmd);
            commandThread.start();

//            while (!commandThread.isFinish()) {
//                log.info("shell " + script + " 还未执行完毕,10s后重新探测");
//                Thread.sleep(10000);
//            }
//
//            //检查脚本执行结果状态码
//            if(commandThread.getExitValue() != 0){
//                throw new Exception("shell " + script + "执行失败,exitValue = " + commandThread.getExitValue());
//            }
//            log.info("shell " + script + "执行成功,exitValue = " + commandThread.getExitValue());
        }
        catch (Exception e){
            throw new Exception("执行脚本发生异常,脚本: " + script, e);
        }
    }
}
