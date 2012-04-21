package utilities.gui;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Administrador
 */
public class SwingUtil {

    /**
     * Sets selected all the text in the TextComponent.
     * @param textComponent 
     */
    public static void setSelectedAll(JTextComponent textComponent) {
        textComponent.setSelectionStart(0);
        textComponent.setSelectionEnd(textComponent.getText().length());
    }

    /**
     * Verifica que el caracter que se intenta ingresar es un dígido y que la
     * longitud de caracteres ingresados no supere el limite.
     * If not > {@link java.awt.event.InputEvent#consume()}
     * @param jTextField Instance of {@link JTextField}
     * @param evt Must be a instance of {@link KeyListener#keyTyped(java.awt.event.KeyEvent)}
     * @param maxLength a positive value 
     */
    public static void checkInputDigit(JTextField jTextField, KeyEvent evt, Integer maxLength) {
        if (!Character.isDigit(evt.getKeyChar())) {
            evt.consume();
        } else if (maxLength != null && maxLength > 0) {
            if (maxLength <= jTextField.getText().length()) {
                evt.consume();
            }
        }
    }

    /**
     * Verifica que el caracter que se intenta ingresar es un dígido.
     * @param jTextField instance of {@link JTextField}
     * @param evt Must be a instance of {@link KeyListener#keyTyped(java.awt.event.KeyEvent)}
     */
    public static void checkInputDigit(JTextField jTextField, KeyEvent evt) {
        if (!Character.isDigit(evt.getKeyChar())) {
            evt.consume();
        }
    }

    private SwingUtil() {
    }
}
