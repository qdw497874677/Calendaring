package com.qdw.calendaing.dataCollecting;

import lombok.Data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @PackageName:com.qdw.calendaing.dataCollecting
 * @ClassName: toFileDataCollecter
 * @Description:
 * @date: 2020/11/30 0030 19:58
 */
@Data
public class ToFileDataCollecter implements DataCollecter {

    private File file;

    public ToFileDataCollecter(File file){
        this.file = file;
    }

    @Override
    public boolean addData(String data) {
        LocalDateTime now = LocalDateTime.now();
        data = now.toString() + "\n" + data;

        try {
            FileWriter fileWriter = new FileWriter(file,true);
            fileWriter.write(data);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            return false;
        }

        return true;
    }
}
