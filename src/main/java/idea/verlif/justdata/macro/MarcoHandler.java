package idea.verlif.justdata.macro;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/24 16:45
 */
public interface MarcoHandler {

    /**
     * 获取宏变量Key
     * @return 宏变量Key
     */
    String getKey();

    /**
     * 获取宏变量值
     *
     * @return 宏变量值
     */
    String getValue();
}