package utilities.swing.components;

import java.text.NumberFormat;
import javax.swing.SwingConstants;

public class NumberRenderer extends FormatRenderer {

    private static final long serialVersionUID = 777L;

    /**
     * Use the specified number formatter and right align the text
     *
     * @param formatter
     */
    public NumberRenderer(NumberFormat formatter) {
        super(formatter);
        setHorizontalAlignment(SwingConstants.RIGHT);
    }

    public static NumberRenderer getCurrencyRenderer(int fractionDigits) {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        nf.setMinimumFractionDigits(fractionDigits);
        nf.setMaximumFractionDigits(fractionDigits);
        return new NumberRenderer(nf);
    }

    /**
     * Use the default currency formatter for the default locale
     *
     * @return ..
     */
    public static NumberRenderer getCurrencyRenderer() {
        return new NumberRenderer(NumberFormat.getCurrencyInstance());
    }

    /**
     * Use the default integer formatter for the default locale
     *
     * @return ..
     */
    public static NumberRenderer getIntegerRenderer() {
        return new NumberRenderer(NumberFormat.getIntegerInstance());
    }

    public static NumberRenderer getNumberRenderer() {
        return new NumberRenderer(NumberFormat.getNumberInstance());
    }

    /**
     * Use the default percent formatter for the default locale
     *
     * @return
     */
    public static NumberRenderer getPercentRenderer() {
        return new NumberRenderer(NumberFormat.getPercentInstance());
    }
}
