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
     *
     * @param textComponent
     */
    public static void setSelectedAll(JTextComponent textComponent) {
        textComponent.setSelectionStart(0);
        textComponent.setSelectionEnd(textComponent.getText().length());
    }

    /**
     * Verifica que el caracter que se intenta ingresar es un dígido y que la
     * longitud de caracteres ingresados no supere el limite. If not > {@link java.awt.event.InputEvent#consume()}
     *
     * @param jTextField Instance of {@link JTextField}
     * @param evt Must be a instance of {@link KeyListener#keyTyped(java.awt.event.KeyEvent)}
     * @param maxLength a positive value
     */
    public static void checkInputDigit(JTextField jTextField, KeyEvent evt, int maxLength) {
        if (!Character.isDigit(evt.getKeyChar())) {
            evt.consume();
        } else {
            if (maxLength <= jTextField.getText().length()) {
                evt.consume();
            }
        }
    }

    /**
     * Verifica que el caracter que se intenta ingresar es un dígido.
     *
     * @param typedEvent Must be a instance of {@link KeyListener#keyTyped(java.awt.event.KeyEvent)}
     */
    public static void checkInputDigit(KeyEvent typedEvent) {
        if (!Character.isDigit(typedEvent.getKeyChar())) {
            typedEvent.consume();
        }
    }

    /**
     * Se obtiene el String del JTextField que disparó el evento y controla que
     * la tecla presionada sea un caracter numérico o un PUNTO '.' ({@link KeyEvent#VK_PERIOD}).
     *
     * @param keyTypedEvent The KeyEvent must come from a {@link JTextField}
     * @param allowOnePeriod if allow one period as a valid input key.
     * @throws ClassCastException if the source of the event doesn't come from a
     * JTextField.
     */
    public static void checkInputDigit(KeyEvent keyTypedEvent, boolean allowOnePeriod) {
        JTextField tf = (JTextField) keyTypedEvent.getSource();
        String cadena = tf.getText();
        if (allowOnePeriod && ((int) keyTypedEvent.getKeyChar()) == 46) {
            if (countCharOccurrences(cadena, '.') > 0) {
                keyTypedEvent.consume();
            }
        } else {
            checkInputDigit(keyTypedEvent);
        }
    }

    public static void checkInputDigit(KeyEvent keyTypedEvent, boolean allowOnePeriod, Integer maxLenght) {
        JTextField tf = (JTextField) keyTypedEvent.getSource();
        String cadena = tf.getText();
        if (allowOnePeriod && ((int) keyTypedEvent.getKeyChar()) == 46) {
            if (countCharOccurrences(cadena, '.') > 0) {
                keyTypedEvent.consume();
            }
        } else {
            if (maxLenght != null) {
                checkInputDigit(tf, keyTypedEvent, maxLenght);
            } else {
                checkInputDigit(keyTypedEvent);
            }
        }
    }

    /**
     * Cuenta la cantidad de veces que está presente el char candidate tiene la
     * cadena
     *
     * @param cadena String to analize, can not be
     * <code>null</code>
     * @param candidate
     * @return la cantidad de veces que se encontró al candidato
     */
    public static int countCharOccurrences(final String cadena, char candidate) {
        int occurrences = 0;
        for (int i = 0; i < cadena.length(); i++) {
            if (cadena.charAt(i) == candidate) {
                occurrences++;
            }
        }
        return occurrences;
    }

    private SwingUtil() {
    }
}
