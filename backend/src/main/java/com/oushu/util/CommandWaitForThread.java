package com.oushu.util;

import com.oushu.config.SpringContextUtils;
import com.oushu.config.Studio;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 脚本函数执行线程
 */
@Slf4j
public class CommandWaitForThread extends Thread {

    private String cmd;
    private String fileName;
    private boolean finish = false;
    private int exitValue = -1;

    public CommandWaitForThread(String cmd, String fileName) {
        this.cmd = cmd;
        this.fileName = fileName;
    }

    public void run(){
        try {
            Studio studio = SpringContextUtils.getBean(Studio.class);
            String log_path = studio.getLog_path();
            File file = new File(log_path + File.separator + this.fileName);
            if (!file.exists()){
                file.createNewFile();
            } else {
                file.delete();
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file, true);
            //执行脚本并等待脚本执行完成
            log.info("开始执行脚本: " + cmd);
            Process process = Runtime.getRuntime().exec(cmd);

            //写出脚本执行中的过程信息
            BufferedReader infoInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorInput = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line = "";
            while ((line = infoInput.readLine()) != null) {
                log.info(line);
                fileWriter.write(formatLog(line));
            }
            while ((line = errorInput.readLine()) != null) {
                log.error(line);
                fileWriter.write(formatLog(line));
            }
            infoInput.close();
            errorInput.close();
            fileWriter.close();

            //阻塞执行线程直至脚本执行完成后返回
            this.exitValue = process.waitFor();
        } catch (Throwable e) {
            log.error("CommandWaitForThread accure exception,shell " + cmd, e);
            exitValue = 110;
        } finally {
            finish = true;
        }
    }

    private String formatLog(String line){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date) + ": " + line + "\n";
    }

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public int getExitValue() {
        return exitValue;
    }
}