package utilities.swing.components;

import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * Permite especificar un foreground color para cada item mediante la implementación del método
 * {@link #getColorByItem(java.lang.Object)}.
 *
 * @author FiruzzZ
 */
public abstract class ComboBoxColorRenderer extends DefaultListCellRenderer {

    public ComboBoxColorRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        JLabel c = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        c.setForeground(getColorByItem(value));
        return this;
    }

    /**
     * Es llamado por cada item contenido en el JList del combobox
     *
     * @param value item del combo
     * @return algún {@link Color} o null
     */
    public abstract Color getColorByItem(Object value);
}
