package idea.verlif.justdata.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/25 11:27
 */
public class FileUtils {

    /**
     * 续写文件
     *
     * @param append 续写的内容
     * @param target 目标文件
     * @return 是否续写成功
     */
    public static boolean append(String append, File target) {
        if (target == null) {
            return false;
        }
        try (FileOutputStream fos = new FileOutputStream(target, true);
             PrintWriter writer = new PrintWriter(fos)) {
            writer.append(append);
            writer.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
