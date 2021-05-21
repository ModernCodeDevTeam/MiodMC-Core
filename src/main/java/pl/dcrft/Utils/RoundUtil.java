package pl.dcrft.Utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RoundUtil {
    public static float round(float val, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        } else {
            BigDecimal bigDecimal;
            bigDecimal = BigDecimal.valueOf(val);
            bigDecimal = bigDecimal.setScale(places, RoundingMode.HALF_UP);
            return bigDecimal.floatValue();
        }
    }
}
