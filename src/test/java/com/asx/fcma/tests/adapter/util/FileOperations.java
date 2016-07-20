package com.asx.fcma.tests.adapter.util;

import com.asx.fcma.tests.adapter.config.ConfigManager;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by kanchi_m on 11/23/2015.
 */
public class FileOperations {

    public boolean CheckErrorInLogFile()throws Exception {
       // File file = new File("C:\\Docs\\CDM_SQL_DRPSDataRefAdapter_20151207.log");
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        String currentDate = df.format(new Date());
        String errorMsg = null;
        String fileLoc = ConfigManager.getInstance().getEnvConfig().getLogFileLocation();

        fileLoc = fileLoc+"CDM_SQL_DRPSDataRefAdapter_"+currentDate+".log";
        File file = new File(fileLoc);
        Boolean errorFound = false;
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        int lines = 0;
        StringBuilder builder = new StringBuilder();
        long length = file.length();
        length--;
        randomAccessFile.seek(length);
        for(long seek = length; seek >= 0; --seek){
            randomAccessFile.seek(seek);
            char c = (char)randomAccessFile.read();
            builder.append(c);
            if(c == '\n'){
                builder = builder.reverse();
                if ((builder.toString().contains("ERROR")))
                {
                    errorMsg = builder.toString();
                    System.out.println("Error found, the xml will not be processed by the adapter, Error ->>>>");
                    System.out.println(errorMsg.trim());
                    errorFound = true;
                    break;
                }
                lines++;
                builder = null;
                builder = new StringBuilder();
                if (lines == 200){
                    break;
                }

            }
        }
       return errorFound;

    }

    public File convertStringToFile(File file, String content) throws IOException {
        FileUtils.writeStringToFile(file,content);
        return file;
    }

}
