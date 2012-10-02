package utilities.gui;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JViewport;
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
     * Return a {@code KeyListener}, which just allow to type letters
     *  ({@link KeyListener#keyTyped(java.awt.event.KeyEvent) })
     *
     * @param allowSpaceChars
     * @return an instance of this implementation
     * @see Character#isLetter(char)
     * @see Character#isSpaceChar(char)
     */
    public static KeyListener getJustLettersKeyTypedListener(final boolean allowSpaceChars) {
        KeyListener l = new KeyAdapter() {
            private final boolean allows = allowSpaceChars;

            @Override
            public void keyTyped(KeyEvent e) {
                if (!Character.isLetter(e.getKeyChar())) {
                    if (allows) {
                        if (!Character.isSpaceChar(e.getKeyChar())) {
                            e.consume();
                        }
                    } else {
                        e.consume();
                    }
                }
            }
        };
        return l;
    }

    /**
     * Return a {@code KeyListener}, which inputs typed are validated by the
     * RegEx ({@link KeyListener#keyTyped(java.awt.event.KeyEvent) })
     *
     * @param regEx \s[a-zA-Z0-9]{3,5}
     * @return an instance of this implementation
     * @see Character#isLetter(char)
     * @see Character#isSpaceChar(char)
     */
    public static KeyListener getKeyTypedListener(final String regEx) {
        KeyListener l = new KeyAdapter() {
            private final Pattern compile = Pattern.compile(regEx);

            @Override
            public void keyTyped(KeyEvent e) {
                Matcher matcher = compile.matcher(String.valueOf(e.getKeyChar()));
                if (!matcher.matches()) {
                    e.consume();
                }
            }
        };
        return l;
    }

    /**
     * Verifica que el caracter que se intenta ingresar es un dígido y que la
     * longitud de caracteres ingresados no supere el limite. If not >
     * {@link java.awt.event.InputEvent#consume()}
     *
     * @param jTextField Instance of {@link JTextField}
     * @param evt Must be a instance of
     * {@link KeyListener#keyTyped(java.awt.event.KeyEvent)}
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
     * @param typedEvent Must be a instance of
     * {@link KeyListener#keyTyped(java.awt.event.KeyEvent)}
     */
    public static void checkInputDigit(KeyEvent typedEvent) {
        if (!Character.isDigit(typedEvent.getKeyChar())) {
            typedEvent.consume();
        }
    }

    /**
     * Se obtiene el String del JTextField que disparó el evento y controla que
     * la tecla presionada sea un caracter numérico o un PUNTO '.'
     * ({@link KeyEvent#VK_PERIOD}).
     *
     * @param keyTypedEvent The KeyEvent must come from a {@link JTextField}
     * @param allowOnePeriod if allow one period as a valid input key.
     * @throws ClassCastException if the source of the event doesn't come from a
     * JTextField.
     */
    public static void checkInputDigit(KeyEvent keyTypedEvent, boolean allowOnePeriod) {
        checkInputDigit(keyTypedEvent, allowOnePeriod, null);
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
     * @param cadena String to analize, can not be <code>null</code>
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

    /**
     * Settea la propiedad Enabled de los componentes (y sus sub-componentes,
     * buclea en los JPanels). Se pueden especificar que tipo de componentes que
     * serán setteados y cuales no.
     *
     * @param components
     * @param enable used to set {@link Component#setEnabled(boolean)}
     * @param applyDefaultsExceptionComponents
     * ({@link JScrollPane}, {@link JLabel}, {@link JSeparator})
     * @param exceptionsComponents componentes a los cuales no se les setteara
     * na!
     *
     */
    public static void setComponentsEnabled(Component[] components, boolean enable, boolean applyDefaultsExceptionComponents, Class<? extends Component>... exceptionsComponents) {
        for (Component component : components) {
            if (component instanceof JPanel || component instanceof JScrollPane || component instanceof JViewport) {
                JComponent subPanel = (JComponent) component;
                setComponentsEnabled(subPanel.getComponents(), enable, applyDefaultsExceptionComponents, exceptionsComponents);
            } else {
                setEnableDependingOfType(component, enable, applyDefaultsExceptionComponents, exceptionsComponents);
            }
        }
    }

    private static void setEnableDependingOfType(Component component, boolean enable, boolean applyDefaults, Class<? extends Component>... exceptionsComponents) {
        if (applyDefaults
                && ((component instanceof JScrollPane)
                || (component instanceof JLabel)
                || (component instanceof JSeparator))) {
            return;
        } else if (exceptionsComponents != null) {
            for (Class<? extends Component> exceptionType : exceptionsComponents) {
                if (component.getClass().equals(exceptionType.getClass())) {
                    return;
                }
            }
        }
        component.setEnabled(enable);
    }

    /**
     * Resetea algunos componentes: <br> {@link JTextComponent#setText(java.lang.String)
     * } == null <br> {@link JCheckBox#setSelected(boolean) } == false <br> {@link JComboBox#setSelectedIndex(int)
     * } == 0 <br> AND using reflection to set from JCalendar >
     * JDaceChooser.setDate(null)
     *
     * @param components
     */
    public static void resetJComponets(Component[] components) {
        for (Component component : components) {
            if (component instanceof JTextComponent) {
                JTextComponent c = (JTextComponent) component;
                c.setText(null);
            } else if (component instanceof JCheckBox) {
                JCheckBox c = (JCheckBox) component;
                c.setSelected(false);
            } else if (component instanceof JComboBox) {
                JComboBox c = (JComboBox) component;
                c.setSelectedIndex(0);
            } else if (component.getClass().getSimpleName().equalsIgnoreCase("JDateChooser")) {
                try {
                    Object c = component;
                    c.getClass().getMethod("setDate", Date.class).invoke(c, new Object[]{null});
                } catch (Exception ex) {
                    Logger.getLogger(SwingUtil.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (component instanceof JComponent) {
                JComponent subPanel = (JComponent) component;
                resetJComponets(subPanel.getComponents());
            }
        }
    }

    private SwingUtil() {
    }
}