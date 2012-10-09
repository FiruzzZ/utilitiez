package utilities.general;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author FiruzzZ
 */
public abstract class UTIL {

    private UTIL() {
    }
    public final static String EMAIL_REGEX = "^[\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
    /**
     *
     * Regular expression para validar cadenas que: <UL> <LI>Empiece con un
     * caracter alfabético. <LI>Seguidos de [0..*] caracteres alfabeticos y/o
     * espacios. <LI>Termine en un caracter alfabético. </UL> Útil para validar
     * Nombres y Apellidos Ejemplos válidos: "a", "jose luis", "a B cd".
     */
    public final static String REGEX_ALFA_TEXT_WITH_WHITE =
            "^[a-zA-Z][a-zA-Z\\s]*[a-zA-Z]$|"
            + "^[a-zA-Z]+[a-zA-Z]*$|"
            + "^[a-zA-Z]$";
    /**
     * [a-zA-Z]?
     */
    public final static Pattern azAZ = Pattern.compile("[a-zA-Z]?");
    /**
     * [0-9]?
     */
    public final static Pattern _09 = Pattern.compile("[0-9]?");
    /**
     * [a-zA-Z_0-9]?
     */
    public final static Pattern azAZ09 = Pattern.compile("[a-zA-Z_0-9]?");
    /**
     * Límite de filas que se cargan en una Tabla
     */
    public final static int ROW_LIMIT = 1000;
    public final static String TIME_ZONE = "GMT-03:00";
    /**
     * Porque a la poronga de MySql solo le gusta así yyyy/MM/dd
     */
    public final static SimpleDateFormat yyyy_MM_dd;
    /**
     * formato de salida de la fecha dd/MM/yyyy
     */
    public final static SimpleDateFormat DATE_FORMAT;
    /**
     * formato de salida del Time -> String HH:mm:ss
     */
    public final static SimpleDateFormat TIME_FORMAT;
    /**
     * formato de salida Timestamp dd/MM/yyyy HH:mm:ss
     */
    public final static SimpleDateFormat TIMESTAMP_FORMAT;
    /**
     * formato de salida del
     * <code>double</code> -> #,###.00 Con separador de millares y COMA de
     * separador decimal (formato no casteable a double/Double).
     */
    public final static DecimalFormat DECIMAL_FORMAT;
    /**
     * formato de salida del
     * <code>double</code> a un String casteable nuevamente a double. El punto
     * (.) como separador decimal y sin separadores de millares Formato
     * "#######0.##"
     */
    public static DecimalFormat PRECIO_CON_PUNTO;
    /**
     * Extensiones de imagenes permitidas: "jpeg", "jpg", "gif", "tiff", "tif",
     * "png", "bmp"
     */
    public final static String[] IMAGEN_EXTENSION = {"jpeg", "jpg", "gif", "tiff", "tif", "png", "bmp"};
    /**
     * Tamaño máximo de las imagenes que se puede guardar: 1.048.576 bytes
     */
    public final static int MAX_IMAGEN_FILE_SIZE = 1048576; // en bytes (1Mb/1024Kb/...)

    static {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
        simbolos.setDecimalSeparator('.');
        PRECIO_CON_PUNTO = new DecimalFormat("#######0.00", simbolos);
        DECIMAL_FORMAT = new DecimalFormat("#,##0.00");
        DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
        yyyy_MM_dd = new SimpleDateFormat("yyyy/MM/dd");
        TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
        TIMESTAMP_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    }

    public static SimpleDateFormat instanceOfDATE_FORMAT() {
        return new SimpleDateFormat("dd/MM/yyyy");
    }

    public static SimpleDateFormat instanceOfTIME_FORMAT() {
        return new SimpleDateFormat("HH:mm:ss");
    }

    /**
     * Returns the contents of the file in a byte array
     *
     * @param file File this method should read
     * @return byte[] Returns a byte[] array of the contents of the file
     * @throws IOException
     * @throws Exception
     */
    public static byte[] getBytesFromFile(File file) throws IOException {
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));
//      InputStream is = new BufferedInputStream(new FileInputStream(file));
        long length = file.length();
        controlSizeFile(file, MAX_IMAGEN_FILE_SIZE);

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) length];
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while ((offset < bytes.length)
                && ((numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)) {
            //numRead == -1 cuando sea EOF..
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();
        return bytes;
    }

    public static void setImageAsIconLabel(JLabel label, byte[] imageInByte) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageInByte);
        BufferedImage bufferedImage = ImageIO.read(bais);
        int labelWidth = label.getWidth();
        int labelHeight = label.getHeight();
        // Get a transform...
        AffineTransform trans = AffineTransform.getScaleInstance(
                (double) labelWidth / bufferedImage.getWidth(), (double) labelHeight / bufferedImage.getHeight());
        Graphics2D g = (Graphics2D) label.getGraphics();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration cg = gd.getDefaultConfiguration();
        int transparency = bufferedImage.getColorModel().getTransparency();
        BufferedImage dest = cg.createCompatibleImage(labelWidth, labelHeight, transparency);
        g = dest.createGraphics();
        g.drawRenderedImage(bufferedImage, trans);
        g.dispose();
        label.setIcon(new ImageIcon(dest)); // <-- si hace resize()
        label.setText(null);
    }

    /**
     * Ajusta la imagen al size de la jLabel, también deja
     * <code>null</code> el texto de la label
     *
     * @param label
     * @param imageFile File de una imagen, la cual se va ajustar al tamaño de
     * la jLabel.
     * @return el jLabel con la imagen ajustada..
     * @exception java.io.IOException si no puede leer el <code>imageFile</code>
     * @exception Exception si el tamaño del archivo supera el configurado
     * permitido (default is Integer.MAX_VALUE).
     */
    public static JLabel setImageAsIconLabel(JLabel label, File imageFile)
            throws IOException {
        BufferedImage bufferedImage = ImageIO.read(imageFile);
        int labelWidth = label.getWidth();
        int labelHeight = label.getHeight();
        // Get a transform...
        AffineTransform trans = AffineTransform.getScaleInstance(
                (double) labelWidth / bufferedImage.getWidth(), (double) labelHeight / bufferedImage.getHeight());

        Graphics2D g;
//        g = (Graphics2D) label.getGraphics();
//        g.drawRenderedImage(src, trans);
//        jLabel.setIcon(new ImageIcon(src)); // <-- no resizea la img en la label
        //----------------------------
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration cg = gd.getDefaultConfiguration();
        int transparency = bufferedImage.getColorModel().getTransparency();
        BufferedImage dest = cg.createCompatibleImage(labelWidth, labelHeight, transparency);
        g = dest.createGraphics();
        g.drawRenderedImage(bufferedImage, trans);
        g.dispose();
        label.setIcon(new ImageIcon(dest)); // <-- si hace resize()
        label.setText(null);
        return label;
    }

    public static void controlSizeFile(File file, int size) {
        // Get the size of the file
        long length = file.length();
        if (length > size) {
            throw new IllegalArgumentException("La imagen seleccionada es demasiado grande.\n"
                    + "El tamaño no debe superar los " + size + " bytes (tamaño actual: " + length + "b)");
        }
    }

    /**
     * Compara los Dates (YEAR, MONTH, DAY) ignorando los campos relacionados al
     * TIME (HOUR, MINUTE, SECOND...)
     *
     * @param d1
     * @param d2
     * @return 0 = si son iguales, a positive if <code>d1</code> is after or a
     * negative if is before d2
     */
    public static int compararIgnorandoTimeFields(Date d1, Date d2) {
        if (d1 == null || d2 == null) {
            throw new IllegalArgumentException("d1=" + d1 + "\td2=" + d2);
        }
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);
        if (c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR)) {
            return c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR);
        } else if (c1.get(Calendar.MONTH) != c2.get(Calendar.MONTH)) {
            return c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH);
        } else {
            return c1.get(Calendar.DAY_OF_MONTH) - c2.get(Calendar.DAY_OF_MONTH);
        }
    }

    /**
     * Transforma una IMAGEN de tipo bytea (postgre) a un java.io.File
     *
     * @param img tipo de dato almacenado en la DB (debe ser una imagen)
     * @param extension la extensión de la imagen. if == null, se le asigna
     * "png"
     * @return el archivo de la imagen que fue creado en el disco
     * @throws IOException
     */
    public static File imageToFile(byte[] img, String extension) throws IOException {
        if (extension == null || extension.length() < 1) {
            extension = "png";
        }

        File file = new File("./reportes/img." + extension);
        file.createNewFile();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(img);
        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
        int data;
        while ((data = byteArrayInputStream.read()) != -1) {
            outputStream.write(data);
        }
        outputStream.close();
        return file;
    }

    /**
     * Return true si la extesión es tiff/tif/gif/jpg/jpeg/png/bmp
     *
     * @param imageFile
     * @return si la extesión del archivo es de algún tipo de imagen.
     */
    public static boolean isImagenExtension(File imageFile) {
        String extension = UTIL.getExtensionFile(imageFile.getName());
        if (extension != null) {
            for (String string : IMAGEN_EXTENSION) {
                if (extension.compareToIgnoreCase(string) == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Validador de CUIL/CUIT
     *
     * @param cuil .. formato del String ########### (11)
     * @throws IllegalArgumentException si la length != 11. Si los 2 1ros
     * dígitos no corresponden a ningún tipo. Si el dígito identificador (el
     * último) no se corresponde al cálculo.
     * @throws NumberFormatException if can not be castable to a Long type.
     */
    public static void VALIDAR_CUIL(String cuil) throws IllegalArgumentException, NumberFormatException {
        String c = cuil.trim();
        try {
            Long.valueOf(cuil);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("La CUIT/CUIL no es válida (ingrese solo números)");
        }
        if (c.length() != 11) {
            throw new IllegalArgumentException("Longitud de la CUIT/CUIL no es correcta (" + c.length() + ")");
        }
        //ctrl de los 1ros 2 dígitos...//
        String x = c.substring(0, 2);
        int xx = Integer.parseInt(x);
        if ((xx != 20) && (xx != 23) && (xx != 24) && (xx != 27) && (xx != 28) && (xx != 30) && (xx != 33) && (xx != 34)) {
            throw new IllegalArgumentException("Los primeros 2 dígitos de la CUIT/CUIL no corresponden a ningún tipo."
                    + "\nHombres: 20, 23, 24; Mujeres: 27, 28; Empresas: 30, 33, 34");
        }
        //ctrl del verificador...//
        int digito, suma = 0;
        int[] codigo = {5, 4, 3, 2, 7, 6, 5, 4, 3, 2};
        for (int index = 0; index < 10; index++) {
            digito = Integer.parseInt(cuil.substring(index, index + 1));
            suma += digito * codigo[index];
        }
        if (Integer.parseInt(cuil.substring(10, 11)) != (11 - (suma % 11))) {
            throw new IllegalArgumentException("El dígito verificador de la CUIT/CUIL no es correcto");
        }
    }

    public static DefaultTableModel getDtm(JTable jtable) {
        return (DefaultTableModel) jtable.getModel();
    }

    public static JTable getDefaultTableModel(JTable tabla, String[] columnNames) {
        DefaultTableModel dtm = new DefaultTableModelImpl();
        for (String string : columnNames) {
            dtm.addColumn(string);
        }
        tabla.setModel(dtm);
        tabla.getTableHeader().setReorderingAllowed(false);
        return tabla;
    }

    /**
     * Personaliza una DefaulTableModel
     *
     * @param tabla en la cual se insertará el modelo, if this is null, a new
     * one will be initialized
     * @param columnNames Nombre de las HeaderColumns
     * @param columnWidths Ancho de columnas
     * @param columnClassType Tipo de datos que va contener cada columna
     * @param editableColumns Index de las columnas las cuales podrán ser
     * editables ( > -1 && cantidadColumnas >= columnsCount)
     * @return una JTable con el modelo
     */
    public static JTable getDefaultTableModel(JTable tabla,
            String[] columnNames, int[] columnWidths, Class[] columnClassType,
            int[] editableColumns) {

        if (columnWidths != null && columnNames.length < columnWidths.length) {
            throw new IllegalArgumentException("el array columnWidths tiene mas elementos (" + columnWidths.length + ")"
                    + " que columnNames = " + columnNames.length);
        } else if (columnClassType != null && (columnNames.length < columnClassType.length)) {
            throw new IllegalArgumentException("el array columnWidths tiene mas elementos (" + columnWidths.length + ")"
                    + " que columnClassType = " + columnClassType.length);
        }
        if (editableColumns != null) {
            for (int i : editableColumns) {
                if (i < 0 || i > (columnNames.length - 1)) {
                    throw new IndexOutOfBoundsException("El array editableColumns[]"
                            + " contiene un indice número de columna no válido: index = " + i);
                }
            }
        }

        DefaultTableModel dtm = new DefaultTableModelImpl(columnClassType, editableColumns);
        for (String string : columnNames) {
            dtm.addColumn(string);
        }
        if (tabla == null) {
            tabla = new JTable(dtm);
        } else {
            tabla.setModel(dtm);
        }

        if (columnWidths != null) {
            for (int i = 0; i < columnWidths.length; i++) {
                tabla.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
            }
        }
        tabla.getTableHeader().setReorderingAllowed(false);
        return tabla;
    }

    public static JTable getDefaultTableModel(
            JTable tabla, String[] columnNames, int[] columnWidths, Class[] columnClassType) {
        return getDefaultTableModel(tabla, columnNames, columnWidths, columnClassType, null);
    }

    public static JTable getDefaultTableModel(JTable tabla, String[] columnNames, int[] columnWidths) {
        return getDefaultTableModel(tabla, columnNames, columnWidths, null, null);
    }

    /**
     * Devuelve un dtm con los nombres de las columnas
     *
     * @param dtm if == null a new instance of a PRIVATE implementation of will
     * be created.
     * @param columnNames los nombres de las respectivas columns que va tener la
     * tabla.
     * @return a DefaultTableModel with column names setted.
     * @exception NullPointerException if columnNames is null.
     */
    public static DefaultTableModel setColumnNames(DefaultTableModel dtm, String[] columnNames) {
        if (dtm == null) {
            dtm = new DefaultTableModelImpl();
        }
        if (columnNames != null && columnNames.length > 0) {
            for (String string : columnNames) {
                dtm.addColumn(string);
            }
        }
        return dtm;
    }

    public static boolean hasta2Decimales(String monto) {
        if (monto != null && monto.length() > 0) {
            monto = monto.trim();
            char[] ja = monto.toCharArray();
            for (int i = 0; i < monto.length(); i++) {
                if (ja[i] == '.') {
                    if (i + 3 >= monto.length()) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        } else {
            return false;
        }
        return true; // si no encontró ningún '.'
    }

    /**
     * Crea un java.util.Date con los parámetros de fecha mandados y TimeZone a
     * GTM-3 (no toma del SO ni nada).
     *
     * @param year el año del Date
     * @param month 0=enero and so on...
     * @param day if == NULL.. will be setted to 1, si == -1 al último día de
     * <b>month</b>
     * @return java.util.Date inicializado con la fecha.
     * @throws Exception si month < 0 || month > 11
     */
    public static Date customDate(int year, int month, Integer day) {
        if (month < 0 || month > 11) {
            throw new IllegalArgumentException("Mes (month) no válido, must be >= 0 AND <= 11");
        }
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        if (day == null) {
            c.set(year, month, 1);
        } else if (day == -1) {
            c.set(year, month + 1, -1);
        }
        return c.getTime();
    }

    /**
     * Personaliza una fecha según los parámetros.
     * <code>fecha</code> no debe ser
     * <code>null</code>.
     *
     * @param fecha un java.util.Date como punto de referencia inicial
     * @param year años por adicionar o restar
     * @param month meses por adicionar o restar
     * @param days días por adicionar o restar
     * @return a customized Date
     */
    public static Date customDate(Date fecha, int year, int month, int days) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        c.setTime(fecha);
        c.add(Calendar.YEAR, year);
        c.add(Calendar.MONTH, month);
        c.add(Calendar.DAY_OF_MONTH, days);
        return c.getTime();
    }

    /**
     * Set all fields related to time (like HOUR_OF_DAY, MINUTE, SECOND,
     * MILLISECND) to 0. This allow compare Dates, disregarding fields related
     * to time. NOTE: beware with the TIME_ZONE!
     *
     * @param fecha
     * @return a {@code Date}
     */
    public static Date getDateYYYYMMDD(Date fecha) {
        Calendar c = Calendar.getInstance();
        c.setTime(fecha);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * Devuelte un Date modificada según los
     * <code>dias</code>
     *
     * @param fecha Date base sobre el cual se va trabajar. If
     * <code>fecha</code> is <code>null</code> will return null Date
     * @param dias cantidad de días por adicionar o restar a <code>fecha</code>
     * @return customized Date!!!...
     */
    public static Date customDateByDays(java.util.Date fecha, int dias) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        c.setTime(fecha);
        c.add(Calendar.DAY_OF_MONTH, dias);
        return c.getTime();
    }

    public static String getExtensionFile(String fileName) {
        return (fileName.lastIndexOf(".") == -1) ? ""
                : fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
    }

    /**
     * Borra las imagenes temporales creadas por reportes e informes
     *
     * @param pathName si es == a "777".. va buscar todos los archivos
     * img.IMG_EXTENSION en /reportes/
     */
    public static void borrarFile(String pathName) {
        File f = null;
        if (pathName != null && pathName.length() > 0) {
            if (pathName.equals("777")) {
                for (String string : IMAGEN_EXTENSION) {
                    try {
                        f = new File("./reportes/img." + string);
                        if (f.delete()) {
                            System.out.println("borrado..." + string);
                        } else {
                            System.out.println("no existía " + string);
                        }
                    } catch (NullPointerException e) {
                        System.out.println("Exception.. no existía ->" + string);
                    } catch (SecurityException e) {
                        System.out.println("SECURITY EXCEPTION!!!");
                    }
                }
            } else {
                f = new File(pathName);
                if (f.delete()) {
                    System.out.println("borrado...pathName=" + pathName);
                } else {
                    System.out.println("no Existía");
                }
            }
            //pathName==null
        } else {
            f = new File("./reportes/img.png");
        }

    }

    /**
     * Cuenta la cantidad de puntos (caracter '.') tiene la cadena
     *
     * @param cadena String to analize, can not be <code>null</code>
     * @param candidate
     * @return la cantidad de veces que se encontró al candidato
     */
    public static int countChacarter(String cadena, char candidate) {
        int cantDePuntos = 0;
        if (cadena.length() > 0) {
            for (int i = 0; i < cadena.length(); i++) {
                if (cadena.charAt(i) == candidate) {
                    cantDePuntos++;
                }
            }
        }
        return cantDePuntos;
    }

    /**
     * Ctrla que sea un caracter numérico el apretado, sinó el evento es
     * consumido
     *
     * @param evt
     */
    public static void soloNumeros(KeyEvent evt) {
        if (!Character.isDigit(evt.getKeyChar())) {
            evt.consume();
        }
    }

    /**
     * Controla que la tecla presionada sea un caracter numérico o un PUNTO '.'
     * y que este no aparezca en la cadena (si este ya aparece en la cadena, el
     * evento se consume al igual que si fuera no fuera un numérico).
     *
     * @param cadena
     * @param e
     */
    public static void solo_numeros_y_un_punto(final String cadena, KeyEvent e) {
        // si es != de '.'
        if ((int) e.getKeyChar() != 46) {
            soloNumeros(e);
        } else if (countChacarter(cadena, '.') > 0) {
            e.consume();
        }
    }

    /**
     * Se obtiene el String del JTextField
     *
     * @param evt The KeyEvent must come from a {@link JTextField}
     * @throws ClassCastException if the source of the event doesn't come from a
     * JTextField.
     * @see #solo_numeros_y_un_punto(java.lang.String, java.awt.event.KeyEvent)
     */
    public static void solo_numeros_y_un_punto(KeyEvent evt) {
        JTextField tf = (JTextField) evt.getSource();
        String cadena = tf.getText();
        solo_numeros_y_un_punto(cadena, evt);
    }

    /**
     * Parse a Double formatted String like
     * <code>DecimalFormat("#,##0.00")</code> to a Double: <br> 1234 parse to<t>
     * 1234.00 <br>1.234 parse to<t> 1234.00 <br>12.34 parse to<t> 12.34
     * <br>123.4 parse to<t> 123.40 <br>123.456 parse to<t> 123456.00
     * <br>1234.56 parse to<t> 1234.56 <br>------------------------------ <br>
     * 12.345,6 parse to<t> 12345.60 <br>123.456, parse to<t> 123456.00
     * <br>123.456,7 parse to<t> 123456.70 <br>1.234.567 parse to<t>1234567.00
     *
     * @param s a String formated like <code>DecimalFormat("#,##0.00")</code>
     * @return
     */
    public static Double parseToDouble(String s) {
        String string = s;
        if (string.contains(",")) {
            string = string.replaceAll("\\.", "");
            string = string.replaceAll(",", ".");
        } else {
            int countChacarter = UTIL.countChacarter(string, '.');
            for (int i = 1; i < countChacarter; i++) {
                string = string.replaceFirst("\\.", "");
            }
            int indexOf = string.indexOf(".");
            if (indexOf > -1) {
                int length = string.substring(indexOf + 1).length();
                if (length > 2) {
                    string = string.replaceFirst("\\.", "");
                }
            }
        }
        return Double.valueOf(string);
    }

    /**
     * Devuelve un String del estado (int o short)
     *
     * @param estado 1 = Activo, 2= Baja, 3= Suspendido
     * @return
     */
    public static String ESTADO_TO_STRING(int estado) {
        if (estado == 1) {
            return "Activo";
        } else if (estado == 2) {
            return "Baja";
        } else if (estado == 3) {
            return "Suspendido";
        }
        return null;
    }

//    /**
//     * Ctrla que sea un caracter numérico el apretado
//     * @param KeyEvent evt!!
//     * @return true si es un caracter numérico [1234567890], otherwise will return false.
//     */
//    public static boolean soloNumeros(java.awt.event.KeyEvent evt) {
//        int k = evt.getKeyChar();
//        if(k<48 || k>57) return false;
//        return true;
//    }
    public static String TO_UPPER_CASE(String unString) {
        if (unString != null) {
            return unString.toUpperCase();
        }
        throw new NullPointerException("El String para mayusculizar es NULL!");
    }

    /**
     * Pasa el caracter a mayuscula y retorna.
     *
     * @param letra a mayusculizar.
     * @return el character mayusculizado.
     */
    public static char TO_UPPER_CASE(char letra) {
        return (String.valueOf(letra).toUpperCase()).charAt(0);
    }

    /**
     * Remueve de la ColumnModel las columnas que se le indique, deja de ser
     * visible para el usuario pero sigue siendo accesible desde el TableModel
     *
     * @param jTable tabla de la cual se desea sacar las columnas
     * @param columnsIndex columnas a quitar de la vista del usuario
     */
    public static void hideColumnsTable(JTable jTable, int[] columnsIndex) {
        Arrays.sort(columnsIndex);
        for (int i = 0; i < columnsIndex.length; i++) {
            // se le va restando i al index real indicado, porque estos se van desplazando
            // a medida que se van eliminando las columnas
            jTable.getColumnModel().removeColumn(jTable.getColumnModel().getColumn(columnsIndex[i] - i));
        }
    }

    /**
     * Remueve de la ColumnModel las columnas que se le indique, deja de ser
     * visible para el usuario pero sigue siendo accesible desde el TableModel
     *
     * @param jTable tabla de la cual se desea sacar las columnas
     * @param columnName nombre de la columna (a.k.a. Header column and
     * identifier)
     */
    public static void hideColumnTable(JTable jTable, String columnName) {
        jTable.getColumnModel().removeColumn(jTable.getColumn(columnName));
    }

    /**
     * Remueve de la ColumnModel la columna que se le indique, deja de ser
     * visible para el usuario pero sigue siendo accesible desde el TableModel
     * (DefaultTableModel)
     *
     * @param jTable tabla de la cual se desea sacar la columna
     * @param columnIndex número de la columna a quitar de la vista del usuario
     */
    public static void hideColumnTable(JTable jTable, int columnIndex) {
        hideColumnsTable(jTable, new int[]{columnIndex});
    }

    /**
     * Setea como selected al item del comboBox que coincida con el
     * <code>candidato</code>
     *
     * @param combo if is null or combo<code>.getItemCount() </code> is less
     * than 1, no selectedItem
     * @param candidato if this is <code>null</code>, no habrá selectedItem
     * @return an index of the selectedItem, or <code>-1</code> if 1 >
     * combo.getItemCount() || if <code>candidato</code> does not match any item
     */
    public static int setSelectedItem(JComboBox combo, String candidato) {
        if (candidato == null) {
            return -1;
        }

        boolean encontrado = false;
        int index = 0;
        while (index < combo.getItemCount() && !encontrado) {
            if ((combo.getItemAt(index)).toString().equals(candidato)) {
                combo.setSelectedIndex(index);
                encontrado = true;
            }
            index++;
        }
        return encontrado ? index : -1;
    }

    /**
     * Setea como selected al item del comboBox que coincida con el
     * <code>candidato</code>. <p>Este método utiliza {@code equals} para la
     * comparación, SO THE CLASS MUST OVERRIDE {@link Object#equals(java.lang.Object)
     * }
     *
     * @param combo El cual podría contener el item {@code candidato}
     * @param candidato Can not be null.
     * @return an index of the selectedItem, or -1 if 1 > combo.getItemCount()
     * || if {@code candidato} does not match any item
     */
    public static int setSelectedItem(JComboBox combo, Object candidato) {
        if (candidato == null) {
            throw new IllegalArgumentException("El Objeto candidato can't be null");
        }
        if (combo == null) {
            throw new IllegalArgumentException("JComboBox combo can't be null");
        }
        if (combo.getItemCount() < 1) {
            return -1;
        }
        try {
            Class<?> declaringClass = candidato.getClass().getMethod("equals", Object.class).getDeclaringClass();
            if (!declaringClass.equals(candidato.getClass())) {
                Logger.getLogger(UTIL.class.getName()).log(Level.WARNING,
                        "La Clase " + candidato.getClass() + " must override the method equals(Object o)");
            }
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex.getMessage(), ex.getCause());
        } catch (SecurityException ex) {
            throw new RuntimeException(ex.getMessage(), ex.getCause());
        }
        boolean encontrado = false;
        int index = 0;
        while (index < combo.getItemCount() && !encontrado) {
            if ((combo.getItemAt(index)).equals(candidato)) {
                combo.setSelectedIndex(index);
                encontrado = true;
            } else {
                index++;
            }
        }
        return encontrado ? index : -1;
    }

    /**
     *
     * @param comboBox
     * @param objectList
     * @param elegible
     * @param itemWhenIsEmpty se agrega como 1er item del combo si la lista está
     * vacía o es null
     * @throws IllegalArgumentException if
     * <code>objectList.isEmpty() || objectList == null</code> and
     * <code>itemWhenIsEmpty == null</code>
     * @see #loadComboBox(javax.swing.JComboBox, java.util.List, boolean)
     */
    public static void loadComboBox(JComboBox comboBox, List objectList, boolean elegible, String itemWhenIsEmpty) {
        if (itemWhenIsEmpty == null) {
            throw new IllegalArgumentException("parameter itemWhenIsEmpty can not be null");
        }
        if (objectList == null || objectList.isEmpty()) {
            comboBox.removeAllItems();
            //si la lista a cargar está vacía o es NULL
            comboBox.addItem(itemWhenIsEmpty);
        } else {
            loadComboBox(comboBox, objectList, elegible);
        }
    }

    /**
     * Remueve todos los items y carga la List de objetos en el comboBox.
     *
     * @param comboBox JComboBox donde se van a cargar los Objectos.
     * @param objectList Si la List está vacía o es null se carga un String Item
     * "&lt;Vacio&gt;".
     * @param elegible Si es true, se agrega un Item "&lt;Elegir&gt;" en el
     * index 0; sino solo se cargar los objetos.
     */
    public static void loadComboBox(JComboBox comboBox, List objectList, boolean elegible) {
        comboBox.removeAllItems();
        if (objectList != null && !objectList.isEmpty()) {
            //si se permite que NO se elija ningún elemento del combobox
            if (elegible) {
                comboBox.addItem("<Elegir>");
            }

            for (Object object : objectList) {
                comboBox.addItem(object);
            }
        } else {
            //si la lista a cargar está vacía o es NULL
            comboBox.addItem("<Vacio>");
        }
    }

    /**
     * Personaliza la carga de datos en un JComboBox, según una List y bla
     * bla...
     *
     * @param comboBox ...
     * @param objectList <code>List</code> la cual se va cargar
     * @param firstItem mensaje del 1er item del combo, dejar <code>null</code>
     * si no hay preferencia
     * @param itemWhenIsEmpty item que se va cargar cuando el <tt>objectList ==
     * null or empty</tt>.
     * @throws IllegalArgumentException if objectList == null or empty AND
     * itemWhemIsEmpy == null.
     * @see #loadComboBox(javax.swing.JComboBox, java.util.List,
     * java.lang.String)
     */
    public static void loadComboBox(JComboBox comboBox, List objectList, String firstItem, String itemWhenIsEmpty) {
        if (objectList == null || objectList.isEmpty()) {
            if (itemWhenIsEmpty != null) {
                comboBox.removeAllItems();
                //si la lista a cargar está vacía o es NULL
                comboBox.addItem(itemWhenIsEmpty);
            } else {
                throw new IllegalArgumentException("parameter itemWhenIsEmpty can't be NULL if objectList is empty or NULL.");
            }
        } else {
            loadComboBox(comboBox, objectList, firstItem);
        }
    }

    /**
     * Remueve todos los items, carga en el comboBox todos los objectos en
     * objectList. Si no hay elementos para cargar (
     * <code>objectList == null && empty</code>) message1stItem no se agrega.
     *
     * @param comboBox
     * @param objectList collection la cual se va cargar
     * @param message1stItem 1er item del combo, puede ser <code>null</code> y
     * solo se carga la lista.
     * @see #loadComboBox(javax.swing.JComboBox, java.util.List, boolean)
     */
    public static void loadComboBox(JComboBox comboBox, List objectList, String message1stItem) {
        loadComboBox(comboBox, objectList, false);
        if (objectList != null && !objectList.isEmpty()) {
            comboBox.removeAllItems();
            if (message1stItem != null) {
                comboBox.addItem(message1stItem);
            }
            for (Object object : objectList) {
                comboBox.addItem(object);
            }
        } else {
            //si la lista a cargar está vacía o es NULL
            comboBox.addItem("<Vacio>");
        }
    }

    /**
     * Sort a List based on the property indicated. This method use the
     * Reflection API to performance the work ( qué loco no!?).
     *
     * @param lista List to be ordered.
     * @param propiedad represent the getter signature from the property that
     * the objects in the List must has. <br>EXAMPLE: <br>if ==
     * <code>"name"</code> ==> <b>getName</b>. <br>if == <code>"lastNAmE"</code>
     * ==> <b>getLastNAmE</b>.
     * @param ascending if the List be ordered ASC, if not then will be DESC.
     */
    public static void order(List lista, final String propiedad, final boolean ascending) {
        Collections.sort(lista, new Comparator() {
            @Override
            public int compare(Object obj1, Object obj2) {
                try {
                    Class clase = obj1.getClass();
                    String getter = "get" + Character.toUpperCase(propiedad.charAt(0)) + propiedad.substring(1);
                    @SuppressWarnings("unchecked")
                    Method getterMethod = clase.getMethod(getter);

                    Object value = getterMethod.invoke(obj1);
                    Object value2 = getterMethod.invoke(obj2);

                    if (value instanceof Comparable && value2 instanceof Comparable) {
                        Comparable comparableValue = (Comparable) value;
                        Comparable comparableValue2 = (Comparable) value2;
                        if (ascending) {
                            return comparableValue.compareTo(comparableValue2);
                        } else {
                            if (comparableValue.compareTo(comparableValue2) == 1) {
                                return -1;
                            } else {
                                return 1;
                            }
                        }
                    } else {
                        if (value.equals(value2)) {
                            return 0;
                        } else {
                            return 1;
                        }

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return 0;
            }
        });
    }

    /**
     * Agrega "0" a la DERECHA de
     * <code>cadena</code> hasta que esta tenga la longitudMaxima
     *
     * @param cadena If == <code>null</code> will do nothing!
     * @param longitudMaxima agrega "0" hasta que <code>cadena</code> tenga la
     * longitud deseada
     * @return      <code>cadena<code> overclocking..
     */
    public static String AGREGAR_CEROS(String cadena, int longitudMaxima) {
        if (cadena == null) {
            throw new NullPointerException("El parámetro cadena es NULL!!!");
        }
        StringBuilder newCadena = new StringBuilder(cadena);
        for (int i = newCadena.toString().length(); i < longitudMaxima; i++) {
            newCadena.insert(0, "0");
        }
        return newCadena.toString();
    }

    public static String AGREGAR_CEROS(long numero, int longitudMaxima) {
        return AGREGAR_CEROS(String.valueOf(numero), longitudMaxima);
    }

    /**
     * Devuelte el % del monto
     *
     * @param monto sobre el cual se calcula el %
     * @param porcentaje debe ser >=0
     * @return El porcentaje (%) del monto, being      <code>0 >= monto</conde> or  0 >=
     * <code>porcentaje</code>, otherwise will return 0.0!
     */
    public static Double getPorcentaje(double monto, double porcentaje) {
        if (porcentaje < 0) {
            throw new IllegalArgumentException("Parameter \"porcentaje\" can not be negative.");
        }
        if (monto <= 0) {
            return 0.0;
        }
        return (porcentaje * (monto / 100));
    }

    /**
     * Devuelte el % del monto
     *
     * @param monto sobre el cual se calcula el %
     * @param porcentaje debe ser >=0
     * @return El porcentaje (%) del monto, being      <code>0 >= monto</conde> or  0 >=
     * <code>porcentaje</code>, otherwise will return 0.0!
     */
    public static BigDecimal getPorcentaje(BigDecimal monto, BigDecimal porcentaje) {
        if (porcentaje.intValue() < 0) {
            throw new IllegalArgumentException("Parameter \"porcentaje\" can not be negative.");
        }
        if (monto.compareTo(BigDecimal.ZERO) == -1) {
            return BigDecimal.ZERO;
        }
        try {
            return porcentaje.multiply(monto.divide(new BigDecimal("100"))).setScale(2, RoundingMode.HALF_EVEN);
        } catch (ArithmeticException e) {
            Logger.getLogger(UTIL.class.getSimpleName()).log(Level.INFO, "Error trying to apply RoudingMode.HALF_EVEN (with scale = 2) on getPorcentaje({0}, {1})", new Object[]{monto, porcentaje});
            return porcentaje.multiply(monto.divide(new BigDecimal("100")));
        }
    }

    /**
     * Obtiene la TableModel (DefaultTableModel) y elimina todas las filas
     *
     * @param table
     * @see #limpiarDtm(javax.swing.table.DefaultTableModel)
     */
    public static void limpiarDtm(JTable table) {
        limpiarDtm((DefaultTableModel) table.getModel());
    }

    public static void limpiarDtm(DefaultTableModel dtm) {
        dtm.setRowCount(0);
    }

    /**
     * Devuelve el Objeto en la celda de la fila selecciada y la columna
     * indicada
     *
     * @param jTable De la cual se va obtener el DefaultTableModel y la
     * selectedRow.
     * @param indexColumn La columna de la que se solicita el Object
     * @return the value Object at the specified cell, or <code>null</code> if
     * no row is selected
     */
    public static Object getSelectedValue(JTable jTable, int indexColumn) {
        if (jTable.getSelectedRow() != -1) {
            return ((DefaultTableModel) jTable.getModel()).getValueAt(
                    jTable.getSelectedRow(), indexColumn);
        } else {
            return null;
        }
    }

    /**
     * Elimina las filas del modelo de la tabla, desde la última hasta la 1ra
     * seleccionada.
     *
     * @param jTable ...
     * @return cantidad de filas removidas
     * @see DefaultTableModel#removeRow(int)
     */
    public static int removeSelectedRows(JTable jTable) {
        int[] selectedRows = jTable.getSelectedRows();
        if (selectedRows.length > 0) {
            DefaultTableModel dtm = (DefaultTableModel) jTable.getModel();
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                dtm.removeRow(selectedRows[i]);
            }
        }
        return selectedRows.length;
    }

    /**
     * Settea un alineamiento horizontal en las celdas de la tabla, para todas
     * las celdas que sean del tipo de class especificado.
     *
     * @param jTable1 tabla a la cual se le va aplicar.
     * @param columnClass class a cual afectará el alineamiento.
     * @param alignment One of the following constants defined in
     * <code>SwingConstants</code>: <code>LEFT</code>, <code>CENTER</code> (the
     * default for image-only labels), <code>RIGHT</code>, <code>LEADING</code>
     * (the default for text-only labels) or <code>TRAILING</code>.
     *
     * @see SwingConstants
     * @see JLabel#getHorizontalAlignment
     */
    public static void setHorizonalAlignment(JTable jTable1, Class<?> columnClass, int alignment) {
        DefaultTableCellRenderer defaultTableCellRender = new DefaultTableCellRenderer();
        defaultTableCellRender.setHorizontalAlignment(alignment);
        jTable1.setDefaultRenderer(columnClass, defaultTableCellRender);
    }

    /**
     * Implementación de DefaultTableModel By default editableColumns = false
     */
    private static class DefaultTableModelImpl extends DefaultTableModel {

        /**
         * This property can be null or contain nulls values, in both case a
         * default value will be used (
         * <code>Object.class</code>).
         */
        private Class[] columnTypes = null;
        /**
         * Contain the indexes of columns which can be editable
         */
        private int[] editableColumns = null;

        /**
         * Constructor por defecto igual al javax.swing.table.DefaultTableModel
         * Me parece que está alpedo..
         */
        public DefaultTableModelImpl() {
        }

        /**
         * Este constructor permite especificar a que class pertenecen los datos
         * que se van a insertar en cada columna
         *
         * @param columnTypes tipo de objeto que contendrá cada columna, the
         * array values can be null
         */
        public DefaultTableModelImpl(Class[] columnTypes) {
            this.columnTypes = columnTypes;
        }

        /**
         * Este constructor permite especificar a que class pertenecen los datos
         * que se van a insertar en cada columna y cuales será editables
         *
         * @param columnTypes tipo de objecto que contendrá cada columna, the
         * array values can be null
         * @param editableColumns debe contenedor los números (index de las
         * columnas) que se desea que puedan ser editables.
         */
        public DefaultTableModelImpl(Class[] columnTypes, int[] editableColumns) {
            this.columnTypes = columnTypes;
            this.editableColumns = editableColumns;
        }

        private DefaultTableModelImpl(int[] editableColumns) {
            this.editableColumns = editableColumns;
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            if (editableColumns != null) {
                boolean columnEditable = false;
                for (int i = (editableColumns.length - 1); i > -1; i--) {
                    columnEditable = (column == editableColumns[i]);
                    if (columnEditable) {
                        break;
                    }
                }
                return columnEditable;
            } else {
                return false;
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnTypes != null && columnTypes.length > columnIndex && columnTypes[columnIndex] != null) {
                return columnTypes[columnIndex];
            } else {
                //If the property is not null nor either the specified column
                return super.getColumnClass(columnIndex);
            }
        }
    }

    /**
     * Valida si es un email
     *
     * @param email String to validate
     * @return return <code>true</code> if is a valid email address
     */
    public static boolean VALIDAR_EMAIL(String email) {
        return Pattern.compile(EMAIL_REGEX).matcher(email).matches();
    }

    /**
     * REGEX.. asi nomas
     *
     * @param regex
     * @param stringToEvaluate
     * @return
     * @throws PatternSyntaxException
     * @see <a
     * href="http://download.oracle.com/javase/1.4.2/docs/api/java/util/regex/Pattern.html">
     * http://download.oracle.com/javase/1.4.2/docs/api/java/util/regex/Pattern.html</a>
     */
    public static boolean VALIDAR_REGEX(String regex, String stringToEvaluate) {
        return Pattern.compile(regex).matcher(stringToEvaluate).matches();
    }

    public static synchronized DecimalFormat setPRECIO_CON_PUNTO(String format, RoundingMode roundingMode) {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
        simbolos.setDecimalSeparator('.');
        PRECIO_CON_PUNTO = new DecimalFormat(format, simbolos);
        PRECIO_CON_PUNTO.setRoundingMode(roundingMode);
        return PRECIO_CON_PUNTO;
    }

    public static void createFile(String bytesFile, String pathName)
            throws FileNotFoundException, IOException {
        File file = new File(pathName);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(bytesFile.getBytes());
        fileOutputStream.close();
        System.out.println("TRACE: File created at:" + file.getAbsolutePath()
                + ", size:" + file.length());
    }

    /**
     * Calcula la edad según {@code dateOfBith}
     *
     * @param dateOfBirth no puede ser posterior a HOY
     * @return edad {@code >= 0}
     */
    public static int getAge(Date dateOfBirth) {
        Calendar today = Calendar.getInstance();
        Calendar birthDate = Calendar.getInstance();
        int age = 0;
        birthDate.setTime(dateOfBirth);
        if (birthDate.after(today)) {
            throw new IllegalArgumentException("Can't be born in the future");
        }
        age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
        // If birth date is > todays date (after 2 days adjustment of leap year) then decrement age one year   
        if ((birthDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) > 3)
                || (birthDate.get(Calendar.MONTH) > today.get(Calendar.MONTH))) {
            age--;

        } else if ((birthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH))
                && (birthDate.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH))) {
            age--;
        }
        return age;
    }
}
