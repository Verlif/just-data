package idea.verlif.justdata;

import idea.verlif.justdata.util.RsaUtils;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/8 10:27
 */
public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println(RsaUtils.encryptByPublicKey(
                "123",
                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCHFT2wHI2YGDFvqjDy+FzQkbgDTmOemIaVW87ZytERcl9oHggi2Gc56PPY5iEXkO+urDf0vlZJXAkvv1Ak7HaS1jts2XSpQhd0gE+S/Wu9Yd1w1xa0XCdl6lxNBD+WP+KZGjaIylkbQRoWpuwggo8uLdyVskiyQ01wtcrnYjJpRwIDAQAB"));
    }
}
