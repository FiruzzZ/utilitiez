package utilities.swing.components;

import java.text.Format;
import java.text.DateFormat;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * Use a formatter to format the cell Object
 *
 * @author FiruzzZ
 */
public class FormatRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;
    private Format formatter;

    /**
     * Use the specified formatter to format the Object
     *
     * @param formatter
     */
    public FormatRenderer(Format formatter) {
        this.formatter = formatter;
    }

    @Override
    public void setValue(Object value) {
        //Format the Object before setting its value in the renderer
        try {
            if (value != null) {
                value = formatter.format(value);
            }
        } catch (IllegalArgumentException e) {
        }
        super.setValue(value);
    }

    /**
     * Use the default date/time formatter for the default locale
     *
     * @return
     */
    public static FormatRenderer getDateTimeRenderer() {
        return new FormatRenderer(DateFormat.getDateTimeInstance());
    }

    /**
     * Use the default date formatter for the default locale
     *
     * @return
     */
    public static FormatRenderer getDateRenderer() {
        return new FormatRenderer(DateFormat.getDateInstance());
    }

    /**
     * Use the default time formatter for the default locale
     *
     * @return
     */
    public static FormatRenderer getTimeRenderer() {
        return new FormatRenderer(DateFormat.getTimeInstance());
    }
}
