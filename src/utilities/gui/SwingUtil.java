package utilities.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.JTextComponent;
import utilities.general.UTIL;

/**
 *
 * @author FiruzzZ
 */
public class SwingUtil {

    private static final SwingUtil singleton = new SwingUtil();
    private static FocusListener selectAllFocus;

    public static void addSelectAllOnFocusGainedListener(Component[] components) {
        for (Component component : components) {
            if (component instanceof JPanel) {
                JPanel subPanel = (JPanel) component;
                addSelectAllOnFocusGainedListener(subPanel.getComponents());
            } else if (component instanceof JTextComponent) {
                addSelectAllOnFocusGainedListener((JTextComponent) component);
            }
        }
    }

    public static void addSelectAllOnFocusGainedListener(JTextComponent tf) {
        tf.addFocusListener(getSelectAllFocus());
    }

    private static FocusListener getSelectAllFocus() {
        if (selectAllFocus == null) {
            selectAllFocus = new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    final JTextComponent t = (JTextComponent) e.getSource();
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            t.selectAll();
                        }
                    });
                }
            };
        }
        return selectAllFocus;
    }

    private SwingUtil() {
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public static SwingUtil getInstance() {
        return singleton;
    }

    /**
     * Sets selected all the text in the TextComponent.
     *
     * @param textComponent ..
     */
    public static void setSelectedAll(JTextComponent textComponent) {
        textComponent.setSelectionStart(0);
        textComponent.setSelectionEnd(textComponent.getText().length());
    }

    /**
     * Return a {@code KeyListener}, which just allow to type letters
     *  ({@link KeyListener#keyTyped(java.awt.event.KeyEvent) })
     *
     * @param allowSpaceChars ..
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

    public void getCharacterCountListener(final JTextComponent t, final JLabel l, final int limit) {
        KeyFocusListener kfl = new KeyFocusListener() {
            private final int limitt = limit;

            private synchronized void counter() {
                int length = t.getText().length();
                l.setText(length + "/" + limitt);
                if (length > limitt) {
                    t.setBackground(Color.RED);
                } else {
                    t.setBackground(null);
                }
            }

            @Override
            public void focusGained(FocusEvent e) {
                counter();
            }

            @Override
            public void focusLost(FocusEvent e) {
                counter();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                counter();
            }
        };
        t.addFocusListener(kfl);
        t.addKeyListener(kfl);
    }

    /**
     * Return a {@code KeyListener}, which inputs typed are validated by the RegEx
     * ({@link KeyListener#keyTyped(java.awt.event.KeyEvent)})
     *
     * @param regEx \s[a-zA-Z0-9]{3,5}
     * @return an instance of this implementation
     * @see Character#isLetter(char)
     * @see Character#isSpaceChar(char)
     */
    public static KeyListener getKeyTypedListener(final String regEx) {
        return new KeyAdapter() {
            private final Pattern compile = Pattern.compile(regEx);

            @Override
            public void keyTyped(KeyEvent e) {
                JTextField tf = (JTextField) e.getSource();
                Matcher matcher = compile.matcher(tf.getText() + String.valueOf(e.getKeyChar()));
                if (!matcher.matches()) {
                    e.consume();
                }
            }
        };
    }

    /**
     *
     * @param textField ..
     * @param allowPeriod ..
     * @param maxLenght numbers of digits (the count also include the period)
     * @see #checkInputDigit(java.awt.event.KeyEvent, boolean, java.lang.Integer)
     */
    public static void addDigitsInputListener(JTextField textField, final boolean allowPeriod, final Integer maxLenght) {
        textField.addKeyListener(new KeyAdapter() {

            @Override
            public void keyTyped(KeyEvent e) {
                checkInputDigit(e, allowPeriod, maxLenght);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE
                        || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    super.keyPressed(e);
                }
            }

        });

    }

    public static void addDigitsInputListener(JComboBox<?> combo, Integer maxLenght) {
        addDigitsInputListener((JTextField) combo.getEditor().getEditorComponent(), maxLenght);
    }

    public static void addDigitsInputListener(JTextField textField, Integer maxLenght) {
        addDigitsInputListener(textField, false, maxLenght);

    }

    /**
     *
     * @param textField ..
     * @see #checkInputDigit(java.awt.event.KeyEvent, boolean, java.lang.Integer)
     */
    public static void addDigitsInputListener(JTextField textField) {
        addDigitsInputListener(textField, false, null);
    }

    /**
     * Verifica que el caracter que se intenta ingresar es un dígido y que la longitud de caracteres
     * ingresados en el componente no supere el limite. If not &lt;
     * {@link java.awt.event.InputEvent#consume()}
     *
     * @param keyTypedEvent Must be a instance of
     * {@link KeyListener#keyTyped(java.awt.event.KeyEvent)} and {@link KeyEvent#getSource()} must
     * be <code>instanceof</code> {@link JTextField}
     * @param maxLength a positive value
     */
    public static void checkInputDigit(KeyEvent keyTypedEvent, int maxLength) {
        JTextField jTextField = (JTextField) keyTypedEvent.getSource();
        checkInputDigit(keyTypedEvent);
        if (maxLength <= jTextField.getText().length()) {
            keyTypedEvent.consume();
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
     * Se obtiene el String del JTextField que disparó el evento y controla que la tecla presionada
     * sea un caracter numérico o un PUNTO '.' ({@link KeyEvent#VK_PERIOD}).
     *
     * @param keyTypedEvent The KeyEvent must come from a {@link JTextField}
     * @param allowOnePeriod if allow one period as a valid input key.
     * @throws ClassCastException if the source of the event doesn't come from a JTextField.
     */
    public static void checkInputDigit(KeyEvent keyTypedEvent, boolean allowOnePeriod) {
        checkInputDigit(keyTypedEvent, allowOnePeriod, null);
    }

    /**
     * Se obtiene el String del JTextField que disparó el evento y controla que la tecla presionada
     * sea un caracter numérico o un PUNTO "." ({@link KeyEvent#VK_PERIOD}).
     *
     * @param keyTypedEvent The KeyEvent must come from a {@link JTextField}
     * @param allowOnePeriod if allow one period as a valid input key.
     * @param maxLength limiate
     * @throws ClassCastException if the source of the event doesn't come from a JTextField.
     */
    public static void checkInputDigit(KeyEvent keyTypedEvent, boolean allowOnePeriod, Integer maxLength) {
        JTextField tf = (JTextField) keyTypedEvent.getSource();
        String cadena = tf.getText();
        if (allowOnePeriod && keyTypedEvent.getKeyChar() == KeyEvent.VK_PERIOD) {
            if (cadena.contains(".")) {
                keyTypedEvent.consume();
            }
        } else if (maxLength != null) {
            checkInputDigit(keyTypedEvent, maxLength);
        } else {
            checkInputDigit(keyTypedEvent);
        }
    }

    /**
     * Cuenta la cantidad de veces que está presente el char candidate tiene la cadena
     *
     * @param cadena String to analize, can not be <code>null</code>
     * @param candidate char to count
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

    public static void setJTextsEditable(Component[] components, boolean editable) {
        for (Component component : components) {
            if (component instanceof JPanel || component instanceof JScrollPane || component instanceof JViewport) {
                JComponent subPanel = (JComponent) component;
                setJTextsEditable(subPanel.getComponents(), editable);
            } else if (component instanceof JTextComponent) {
                JTextComponent text = (JTextComponent) component;
                text.setEditable(editable);
            }
        }
    }

    /**
     * Settea {@link Component#enabled} de los componentes en el {@link Container} (y sus sub
     * {@link Container} que puedan haber, buclea recursivo). Se pueden especificar que tipo de
     * componentes serán excluidos de este proceso ({@code exceptionsComponents})
     *
     * @param components ..
     * @param enable used to set {@link Component#setEnabled(boolean)}
     * @param applyDefaultsExceptionComponents
     * ({@link JScrollPane}, {@link JScrollBar}, {@link JTableHeader}, {@link JLabel}, {@link JSeparator})
     * @param exceptionsComponents componentes a los cuales no se les setteara na!
     *
     */
    public static void setComponentsEnabled(Component[] components, boolean enable, boolean applyDefaultsExceptionComponents, Class<? extends Component>... exceptionsComponents) {
        for (Component component : components) {
            if (component instanceof JRootPane
                    || component instanceof JLayeredPane // <--- Java 7
                    || component instanceof JPanel || component instanceof JScrollPane || component instanceof JViewport
                    || component instanceof JTabbedPane) {
                JComponent subPanel = (JComponent) component;
                setComponentsEnabled(subPanel.getComponents(), enable, applyDefaultsExceptionComponents, exceptionsComponents);
            } else {
                setEnableDependingOfType(component, enable, applyDefaultsExceptionComponents, exceptionsComponents);
            }
        }
    }

    private static void setEnableDependingOfType(Component component, boolean enable, boolean applyDefaults, Class<? extends Component>... exceptionsComponents) {
        if (applyDefaults
                && (component instanceof JLabel
                || component instanceof JSeparator
                || component instanceof JScrollBar
                || component instanceof JTableHeader)) {
            return;
        }
        if (exceptionsComponents != null) {
            for (Class<? extends Component> exceptionType : exceptionsComponents) {
                if (component.getClass().equals(exceptionType)) {
                    return;
                }
            }
        }
        component.setEnabled(enable);
    }

    /**
     * Resetea algunos componentes: <br> {@link JTextComponent#setText(java.lang.String)} == null
     * <br> {@link JCheckBox#setSelected(boolean) } == false <br>
     * {@link JComboBox#setSelectedIndex(int)} == 0 <br> {@link JTable} and
     * {@link JTable#getModel() } {@code instance of} {@link DefaultTableModel} do
     * {@link DefaultTableModel#setRowCount(int)} to zero <br> AND using reflection to set from
     * JCalendar &gt; JDaceChooser.setDate(null)
     *
     * @param components ..
     * @param expections ..
     */
    public static void resetJComponets(Component[] components, Class<? extends Component>... expections) {
        for (Component component : components) {
            if (expections != null) {
                boolean skip = false;
                for (Class<? extends Component> classToSkip : expections) {
                    if (component.getClass().equals(classToSkip)) {
                        skip = true;
                        break;
                    }
                }
                if (skip) {
                    continue;
                }
            }
            if (component instanceof JTextComponent) {
                JTextComponent c = (JTextComponent) component;
                c.setText(null);
            } else if (component instanceof JCheckBox) {
                JCheckBox c = (JCheckBox) component;
                c.setSelected(false);
            } else if (component instanceof JComboBox) {
                JComboBox c = (JComboBox) component;
                c.setSelectedIndex(c.getItemCount() > 0 ? 0 : -1);
            } else if (component.getClass().getSimpleName().equalsIgnoreCase("JDateChooser")) {
                try {
                    Object c = component;
                    c.getClass().getMethod("setDate", Date.class).invoke(c, new Object[]{null});
                } catch (Exception ex) {
                    Logger.getLogger(SwingUtil.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (component instanceof JTable) {
                JTable c = (JTable) component;
                if (c.getModel() instanceof DefaultTableModel) {
                    DefaultTableModel dtm = (DefaultTableModel) c.getModel();
                    dtm.setRowCount(0);
                }
            } else if (component instanceof JComponent) {
                JComponent subPanel = (JComponent) component;
                resetJComponets(subPanel.getComponents(), expections);
            }
        }

    }

    /**
     *
     * @param components ..
     */
    public static void resetJComponets(Component[] components) {
        resetJComponets(components, (Class<? extends Component>[]) null);
    }

    /**
     * On focusGained does:
     * <p>
     * {@link UTIL#parseToDouble(java.lang.String)} and
     * {@link #setSelectedAll(javax.swing.text.JTextComponent)} <br>On focusLost does:
     * <p>
     * format the text with {@code df = new DecimalFormat("#,##0.00")}. if
     * {@link DecimalFormat#format(java.lang.Object)} fails, the backGround is setted to
     * {@link Color#RED} otherwise sets(keeps) the default color.
     *
     * @return ..
     */
    public static FocusListener getCurrencyFormatterFocusListener() {
        FocusListener fl = new FocusListener() {
            final DecimalFormat df = new DecimalFormat("#,##0.00");

            @Override
            public void focusGained(FocusEvent e) {
                JTextField t = (JTextField) e.getSource();
                if (!t.getText().trim().isEmpty()) {
                    t.setText(BigDecimal.valueOf(UTIL.parseToDouble(t.getText())).toString());
                }
                setSelectedAll(t);
            }

            @Override
            public void focusLost(FocusEvent e) {
                JTextField t = (JTextField) e.getSource();
                try {
                    if (!t.getText().trim().isEmpty()) {
                        t.setText(df.format(new BigDecimal(t.getText())));
                    }
                    t.setBackground(UIManager.getColor("TextField.background"));
                } catch (Exception ex) {
                    t.setBackground(Color.RED);
                }
            }
        };
        return fl;
    }

    private class KeyFocusListener implements KeyListener, FocusListener {

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }

        @Override
        public void focusGained(FocusEvent e) {
        }

        @Override
        public void focusLost(FocusEvent e) {
        }
    }

    public static File showImageFileDialogChooser() {
        File file = null;
        JFileChooser filec = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Imagenes", UTIL.IMAGEN_EXTENSION);
        filec.setFileFilter(filter);
        filec.addChoosableFileFilter(filter);
        int val = filec.showOpenDialog(null);
        if (val == JFileChooser.APPROVE_OPTION) {
            file = filec.getSelectedFile();
            if (!UTIL.isImagenExtension(file)) {
                file = null;
            }
        }
        return file;
    }

    /**
     * Abre un JFileChooser con 2 tipos de archivos permitido: XLS, XLSX
     *
     * @param parent
     * @param fileDir the directory to point to, is also set as "suggested file name"
     * @return
     * @throws IOException
     */
    public static File showSaveDialogExcelFileChooser(Component parent, File fileDir) throws IOException {
        Map<String, String> exts = new HashMap<>(1);
        exts.put("xls", "Excel 97-2003");
        exts.put("xlsx", "Excel");
        return showSaveDialogFileChooser(parent, fileDir, exts);
    }

    /**
     *
     * @param parent
     * @param description
     * @param fileDir the directory to point to, is also set as "suggested file name"
     * @param fileExtension
     * @return
     * @throws IOException
     */
    public static File showSaveDialogFileChooser(Component parent, String description, File fileDir, String fileExtension) throws IOException {
        Map<String, String> exts = new HashMap<>(1);
        exts.put(fileExtension, description);
        return showSaveDialogFileChooser(parent, fileDir, exts);
    }

    /**
     * Abre un JFileChooser
     *
     * @param parent
     * @param fileDir the directory to point to, is also set as "suggested file name"
     * @param fileExtensionAllowed map&lt;Extension, Descripcion&gt; permitidas ej: xls, "Excel
     * 97-2003"
     * @return
     * @throws IOException
     */
    public static File showSaveDialogFileChooser(Component parent, File fileDir, Map<String, String> fileExtensionAllowed) throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        for (Map.Entry<String, String> ext : fileExtensionAllowed.entrySet()) {
            FileNameExtensionFilter filter = new FileNameExtensionFilter(ext.getValue(), ext.getKey());
            fileChooser.setFileFilter(filter);
            fileChooser.addChoosableFileFilter(filter);
        }
        fileChooser.setCurrentDirectory(fileDir);
        if (fileDir != null) {
            fileChooser.setSelectedFile(fileDir);
        }
        File file = null;
        int stateFileChoosed = fileChooser.showSaveDialog(parent);
        if (stateFileChoosed == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            String extension = null;
            for (Map.Entry<String, String> ext : fileExtensionAllowed.entrySet()) {
                if (ext.getValue().equals(fileChooser.getFileFilter().getDescription())) {
                    extension = "." + ext.getKey();
                    break;
                }
            }
            final int lastIndexOf = file.getName().lastIndexOf('.');
            if (lastIndexOf > -1) {
                String fileExtension = file.getName().substring(lastIndexOf);
                if (fileExtension != null && !file.getName().endsWith("." + fileExtension)) {
                    //trim porque el Chooser permite crear archivos con espacios iniciales, pero Windows no
                    file = new File(file.getPath().trim() + extension);
                }
            } else {
                //trim porque el Chooser permite crear archivos con espacios iniciales, pero Windows no
                file = new File(file.getPath().trim() + extension);
            }
            if (file.exists() && JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(parent, "Ya existe el archivo " + file.getName() + ", ¿Desea reemplazarlo?", null, JOptionPane.YES_NO_OPTION)) {
                return null;
            }
        }
        return file;
    }
}
