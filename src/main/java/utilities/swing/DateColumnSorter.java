package utilities.swing;

import java.util.Comparator;
import java.util.Date;
import java.util.Vector;

/**
 *
 * @author FiruzzZ
 */
@SuppressWarnings("UseOfObsoleteCollectionType")
public class DateColumnSorter implements Comparator<Vector<Object>> {

    private final int colIndex;

    /**
     * 
     * @param colIndex columna que contiene {@link java.util.Date} or <code>null</code>'s
     */
    public DateColumnSorter(int colIndex) {
        this.colIndex = colIndex;
    }

    @Override
    public int compare(Vector<Object> a, Vector<Object> b) {
        Date o1 = (Date) a.get(colIndex);
        Date o2 = (Date) b.get(colIndex);
        if (o1 == null && o2 == null) {
            return 0;
        } else if (o1 == null) {
            //null's 1ros
            return -1;
        } else if (o2 == null) {
            return 1;
        } else {
            return o1.compareTo(o2);
        }
    }
}
