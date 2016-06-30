package utilities.swing;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Mediante la implementación del método {@link #condicionByRow(int)} se puede especificar un
 * foreground color para la fila.
 *
 * @author FiruzzZ
 */
public abstract class RowColorRender extends DefaultTableCellRenderer {

    /**
     * Para evaluar solo una vez la fila y no por cada valor de cada columna de la fila.
     */
    private int lastRowEvaluated = -1;
    private Color fg = null;

    public RowColorRender() {
//        setOpaque(true);
    }

    /**
     *
     * @param rowIndexModel row index of model
     * @return this value will be used to set {@link #fg}
     */
    public abstract Color condicionByRow(int rowIndexModel);

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component comp = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);
        if (lastRowEvaluated != row) {
            /**
             * to prevent porquerías (sorting or filtering)
             */
//            int columnIndexToModel = table.convertColumnIndexToModel(column);
            int rowIndexToModel = table.convertRowIndexToModel(row);
            fg = condicionByRow(rowIndexToModel);
        }
        comp.setForeground(fg);
        return comp;
    }
}
