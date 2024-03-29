package utilities.general;

import com.toedter.calendar.JDateChooser;
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
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author FiruzzZ
 */
public final class UTIL {

    private static final Logger LOG = Logger.getLogger(UTIL.class.getName());

    /**
     *
     * @param d cannot be null
     * @param from cannot be null
     * @param to si es <code>null</code>, solo se compara que {@code d} sea &gt;= <code>from</code>
     * @param excluirTime disregard TIME fields {@link #clearTimeFields(java.util.Date)}
     * @return true is between (inclusive)
     */
    public static boolean between(Date d, Date from, Date to, boolean excluirTime) {
        Objects.requireNonNull(d);
        Objects.requireNonNull(from);
        if (excluirTime) {
            d = clearTimeFields(d);
            from = clearTimeFields(from);
            if (to != null) {
                to = clearTimeFields(to);
            }
        }
        if (d.compareTo(from) >= 0) {
            if (to == null) {
                return true;
            } else if (d.compareTo(to) <= 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Compare if {@code candidate} is between (inclusive) {@code from} y {@code to}
     *
     * @param candidate
     * @param from
     * @param to
     * @return true is between (inclusive)
     */
    public static boolean between(long candidate, long from, long to) {
        return from <= candidate && candidate <= to;
    }

    /**
     * Retorna la fecha elegida si están dentro de rango de valores mínimo y máximo. Solo compara
     * hasta días
     * <ul>
     * <li>{@link JDateChooser#getDate()} &gt;={@link JDateChooser#getMinSelectableDate()}
     * <li>{@link JDateChooser#getDate()} &lt;={@link JDateChooser#getMaxSelectableDate()}
     * </ul>
     * <br>Este método utiliza {@link #clearTimeFields(java.util.Date) } antes de realizar las
     * comparaciones
     *
     * @param jdc ..
     * @return a valid specified date or null
     */
    public static Date getDate(JDateChooser jdc) {
        if (jdc.getDate() == null) {
            return null;
        }
        Date d = clearTimeFields(jdc.getDate());
        if (clearTimeFields(jdc.getMinSelectableDate()).compareTo(d) > 0
                || clearTimeFields(jdc.getMaxSelectableDate()).compareTo(d) < 0) {
            return null;
        }
        return d;
    }

    /**
     * Suma o resta la cantidad especificada
     *
     * @param date
     * @param cant de días
     * @return ..
     * @see Calendar#add(int, int)
     */
    public static Date addDays(Date date, int cant) {
        return add(date, Calendar.DAY_OF_MONTH, cant);
    }

    /**
     * Suma o resta la cantidad especificada
     *
     * @param date
     * @param cant de meses
     * @return ..
     * @see Calendar#add(int, int)
     */
    public static Date addMonths(Date date, int cant) {
        return add(date, Calendar.MONTH, cant);
    }

    /**
     * Suma o resta la cantidad especificada
     *
     * @param date
     * @param cant de años
     * @return ..
     * @see Calendar#add(int, int)
     */
    public static Date addYears(Date date, int cant) {
        return add(date, Calendar.YEAR, cant);
    }

    /**
     * Suma o resta la cantidad especificada
     *
     * @param date
     * @param calendar field (example: {@link Calendar#SECOND})
     * @param Adds or subtracts the specified amount of time to the given date
     * @return ..
     * @see Calendar#add(int, int)
     */
    public static Date add(Date date, int field, int cant) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(field, cant);
        return c.getTime();
    }

    /**
     *
     * @param tfPuntoVenta
     * @param tfNumero
     * @return
     * @see #getNumeroComprobante(java.lang.String, java.lang.String)
     */
    public static Long getNumeroComprobante(JTextField tfPuntoVenta, JTextField tfNumero) {
        return getNumeroComprobante(tfPuntoVenta.getText(), tfNumero.getText());
    }

    /**
     * Combine both strings ({@code puntoVenta} + {@code numero}) into a Long.
     * <p>
     * Examples:
     * <br>puntoVenta="1", numero="99" =&gt; 100000099
     * <br>puntoVenta="10", numero="12345678" =&gt; 1012345678
     *
     * @param puntoVenta must be casteable to a number between 1 - 99999
     * @param numero must be casteable to a number between 1 - 999999999
     * @return a long!
     * @exception NumberFormatException si puntoVenta o numero no son válidos
     */
    public static Long getNumeroComprobante(String puntoVenta, String numero) throws NumberFormatException {
        Integer pv;
        try {
            if (puntoVenta == null) {
                throw new NumberFormatException("Punto de venta no válido");
            }
            pv = Integer.valueOf(puntoVenta.trim());
            if (pv < 1 || pv > 9999) {
                throw new NumberFormatException("Punto de venta no válido (1 - 9999)");
            }
        } catch (NumberFormatException ex) {
            throw new NumberFormatException("Punto de venta no válido");
        }

        Integer n;
        try {
            if (numero == null) {
                throw new NumberFormatException("N° de comprobante no válido");
            }
            n = Integer.valueOf(numero.trim());
            if (n < 1 || pv > 9999_9999) {
                throw new NumberFormatException("N° de comprobante no válido (1 - 99999999)");
            }
        } catch (NumberFormatException ex) {
            throw new NumberFormatException("N° de comprobante no válido");
        }
        return Long.valueOf(pv + AGREGAR_CEROS(n, 8));
    }

    private UTIL() {
    }

    /**
     * Removes every backslash (\), single quote (') and double quote (")
     *
     * @param text if it is empty/null will return empty
     * @return a less hazardous string to use on queries
     */
    public static String removeEscapesChars(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        String query = text;
        query = query.replaceAll("\\\\", "");
        query = query.replaceAll("'", "");
        query = query.replaceAll("\"", "");
        return query;
    }
    public final static String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    /**
     *
     * Regular expression para validar cadenas que: <UL> <LI>Empiece con un caracter alfabético.
     * <LI>Seguidos de [0..*] caracteres alfabeticos y/o espacios. <LI>Termine en un caracter
     * alfabético. </UL> Útil para validar Nombres y Apellidos Ejemplos válidos: "a", "jose luis",
     * "a B cd".
     */
    public final static String REGEX_ALFA_TEXT_WITH_WHITE = "[ñáéíóúÑÁÉÍÓÚa-zA-Z]+((\\s)?[ñáéíóúÑÁÉÍÓÚa-zA-Z]+)*";
    /**
     *
     * Regular expression para validar cadenas que: <UL> <LI>Empiece con un caracter alfanumérico.
     * <LI>Seguidos de [0..*] caracteres alfanuméricos y/o espacios. <LI>Termine en un caracter
     * alfanumérico. </UL> Ejemplos válidos: "a", "jose luis", "1a", "a1 1 a", "a B cd".
     */
    public final static String REGEX_ALFANUMERIC_WITH_WHITE = "[ñáéíóúÑÁÉÍÓÚa-zA-Z0-9]+((\\s)?[ñáéíóúÑÁÉÍÓÚa-zA-Z0-9]+)*";
    /**
     * adminte puntos intermedios y guión medio, pero debe finalizar con alfa numérico
     */
    public final static String REGEX_ALFANUMERIC_PLUS = "[ñáéíóúÑÁÉÍÓÚa-zA-Z0-9]+(\\s?[\\.\\-ñáéíóúÑÁÉÍÓÚa-zA-Z0-9]+)*(?<![\\s.-])$";
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
     * formato de salida del Time HH:mm:ss
     */
    public final static SimpleDateFormat TIME_FORMAT;
    /**
     * formato de salida Timestamp dd/MM/yyyy HH:mm:ss
     */
    public final static SimpleDateFormat TIMESTAMP_FORMAT;
    /**
     * formato de salida del <code>double</code> = #,###.00 Con separador de millares y COMA de
     * separador decimal (formato no casteable a double/Double).
     */
    public final static DecimalFormat DECIMAL_FORMAT;
    /**
     * formato de salida del <code>double</code> a un String casteable nuevamente a double. El punto
     * (.) como separador decimal y sin separadores de millares Formato "#######0.##"
     */
    public static DecimalFormat PRECIO_CON_PUNTO;
    /**
     * Extensiones de imagenes permitidas: "jpeg", "jpg", "gif", "tiff", "tif", "png", "bmp"
     */
    public final static String[] IMAGEN_EXTENSION = {"jpeg", "jpg", "gif", "tiff", "tif", "png", "bmp"};

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
     */
    public static byte[] getBytesFromFile(File file) throws IOException {
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));
        long length = file.length();
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

    /**
     *
     * @param label
     * @param imageInByte
     * @exception java.io.IOException si no puede leer el <code>imageFile</code>
     * @see #setImageAsIconLabel(javax.swing.JLabel, java.awt.image.BufferedImage)
     */
    public static void setImageAsIconLabel(JLabel label, byte[] imageInByte) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageInByte)) {
            setImageAsIconLabel(label, ImageIO.read(bais));
        }
    }

    /**
     *
     * @param label
     * @param imageFile
     * @return
     * @exception java.io.IOException si no puede leer el <code>imageFile</code>
     * @see #setImageAsIconLabel(javax.swing.JLabel, java.awt.image.BufferedImage)
     */
    public static JLabel setImageAsIconLabel(JLabel label, File imageFile) throws IOException {
        return setImageAsIconLabel(label, ImageIO.read(imageFile));
    }

    /**
     * Ajusta la imagen al size de la jLabel, también deja <code>null</code> el texto de la label
     *
     * @param label
     * @param bufferedImage la cual se va ajustar al tamaño de la jLabel.
     * @return el jLabel con la imagen ajustada..
     */
    public static JLabel setImageAsIconLabel(JLabel label, BufferedImage bufferedImage) {
        int labelWidth = label.getWidth();
        int labelHeight = label.getHeight();
        // Get a transform...
        AffineTransform trans = AffineTransform.getScaleInstance(
                (double) labelWidth / bufferedImage.getWidth(), (double) labelHeight / bufferedImage.getHeight());

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration cg = gd.getDefaultConfiguration();
        int transparency = bufferedImage.getColorModel().getTransparency();
        BufferedImage dest = cg.createCompatibleImage(labelWidth, labelHeight, transparency);
        Graphics2D g = dest.createGraphics();
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
     * Settea todos los campos relacionados al "tiempo" (HOUR, MINUTE, SECOND, MILLISECOND) to ZERO
     *
     * @param d instance of Date
     * @return
     */
    public static Date clearTimeFields(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * Compara los Dates (YEAR, MONTH, DAY_OF_MONTH) ignorando los campos relacionados al TIME
     * (HOUR, MINUTE, SECOND...)
     *
     * @param d1
     * @param d2
     * @return 0 = si son iguales, a positive if <code>d1</code> is after or a negative if is before
     * d2
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
     * Ej: 01/01/2009 - 30/01/2009 = 0 <br> 12/05/2013 - 01/12/2012= 5 <br> 12/05/2013 - 01/12/2013=
     * -7
     * <br>Days are disregarded
     *
     * @param date1
     * @param date2
     * @return cantidad de meses entre las fechas
     */
    public static int getMonthsDifference(Date date1, Date date2) {
        Calendar c = Calendar.getInstance();
        c.setTime(date1);
        int m1 = c.get(Calendar.YEAR) * 12 + c.get(Calendar.MONTH);
        c.setTime(date2);
        int m2 = c.get(Calendar.YEAR) * 12 + c.get(Calendar.MONTH);
        return m1 - m2;
    }

    /**
     * Doesn't work on dates wihth different daylight saving
     *
     * @param from
     * @param to
     * @return if {@code to} is after {@code from} returns a positive, if they are equals a 0, or a
     * negative. Igual que cualquier {@link Comparable#compareTo(java.lang.Object) }
     */
    public static int getDaysBetween(Date from, Date to) {
        long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
        //using BigDecimal to avoid Android's problem rounding 1.99 days
        return BigDecimal.valueOf((to.getTime() - from.getTime()) / DAY_IN_MILLIS).setScale(0, RoundingMode.DOWN).intValue();
    }

    /**
     * Genera una coleccion con los "periodos" (año/mes) entre ambas fechas (inclusive from y to).
     *
     * @param from
     * @param to
     * @return empty if {@code from} is after {@code to}
     */
    public static List<Date> generateMonthSeries(Date from, Date to) {
        Calendar desde = GregorianCalendar.getInstance();
        desde.setTime(from);
        desde.set(Calendar.DAY_OF_MONTH, 1);
        Calendar hasta = GregorianCalendar.getInstance();
        hasta.setTime(to);
        hasta.set(Calendar.DAY_OF_MONTH, 1);
        List<Date> series = new ArrayList<>();
        while (desde.compareTo(hasta) < 1) {
            series.add(desde.getTime());
            desde.add(Calendar.MONTH, 1);
        }
        return series;
    }

    public static File imageToFile(byte[] img, String pathFile, String extension) throws IOException {
        File file = File.createTempFile(pathFile, "." + (extension == null ? "png" : extension.replaceAll("\\.", "")));
//        File file = new File("./tempImg" + new Date().getTime() + "." + extension);
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
     * Transforma una IMAGEN de tipo bytea (postgre) a un java.io.File
     *
     * @param img tipo de dato almacenado en la DB (debe ser una imagen)
     * @param extension la extensión de la imagen. if == null, se le asigna "png"
     * @return el archivo de la imagen que fue creado en el disco
     * @throws IOException
     */
    public static File imageToFile(byte[] img, String extension) throws IOException {
        return imageToFile(img, "temp", extension);
    }

    /**
     * Return true si la extesión es tiff/tif/gif/jpg/jpeg/png/bmp
     *
     * @param imageFile
     * @return si la extensión del archivo es de algún tipo de imagen.
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
     * @throws IllegalArgumentException si la length != 11. Si los 2 1ros dígitos no corresponden a
     * ningún tipo. Si el dígito identificador (el último) no se corresponde al cálculo.
     * @throws NumberFormatException if can not be castable to a Long type.
     */
    public static void VALIDAR_CUIL(String cuil) throws IllegalArgumentException {
        String c = cuil.trim();
        if (c.length() != 11) {
            throw new IllegalArgumentException("Longitud de la CUIT/CUIL no es correcta (" + c.length() + ")");
        }
        try {
            Long.valueOf(cuil);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("La CUIT/CUIL no es válida (ingrese solo números)");
        }
        //ctrl del verificador...//
        int digito, suma = 0;
        int[] codigo = {5, 4, 3, 2, 7, 6, 5, 4, 3, 2};
        for (int index = 0; index < 10; index++) {
            digito = Integer.parseInt(cuil.substring(index, index + 1));
            suma += digito * codigo[index];
        }
        int xxx = (suma % 11);
        xxx = xxx == 0 ? 0 : xxx == 1 ? 9 : 11 - xxx;
        if (Integer.parseInt(cuil.substring(10)) != xxx) {
            throw new IllegalArgumentException("El dígito verificador de la CUIT/CUIL no es correcto " + Integer.parseInt(cuil.substring(10)) + " > " + xxx);
        }
    }

    /**
     * Validador de CBU
     *
     * @param cbu .. formato del String ##################### (2)
     * @throws IllegalArgumentException si la length != 22. Si el dígito validador de la info del
     * banco no coincide. Si el digito validador de a infode la cuenta no coincide
     * @throws NumberFormatException if can not be castable to a Long type.
     */
    public static void VALIDAR_CBU(String cbu) throws IllegalArgumentException {
        String c = cbu.trim();
        if (c.length() != 22) {
            throw new IllegalArgumentException("Longitud de la CBU no es correcta (" + c.length() + ")");
        }
        try {
            for (int index = 0; index < 22; index++) {
                Integer.parseInt(cbu.substring(index, index + 1));
            }
        } catch (NumberFormatException e) {
            throw new NumberFormatException("La CBU no es válida (ingrese solo números)");
        }
        //ctrl del verificador...//
        int digito, suma = 0;
        //2  8  5  0  5  9  0 - 9
        int[] codigo = {7, 1, 3, 9, 7, 1, 3};
        for (int index = 0; index < 7; index++) {
            digito = Integer.parseInt(cbu.substring(index, index + 1));
            suma += digito * codigo[index];
        }
        int valid = 10 - ((suma % 10) == 0 ? 10 : (suma % 10));//Integer.parseInt(("" + suma).substring(("" + suma).length() - 1, ("" + suma).length()));
        if (Integer.parseInt(cbu.substring(7, 8)) != valid) {
            throw new IllegalArgumentException("La CBU no es válida, dígito validador del primer cuerpo");
        }

        suma = 0;
        //4  0  0  9  0  4  1  8  1  3  5  2  0 - 1
        int[] codigo2 = {3, 9, 7, 1, 3, 9, 7, 1, 3, 9, 7, 1, 3};
        for (int index = 8; index < 21; index++) {
            digito = Integer.parseInt(cbu.substring(index, index + 1));
            suma += digito * codigo2[index - 8];
        }
        valid = 10 - ((suma % 10) == 0 ? 10 : (suma % 10));//Integer.parseInt(("" + suma).substring(("" + suma).length() - 1, ("" + suma).length()));
        if (Integer.parseInt(cbu.substring(21, 22)) != valid) {
            throw new IllegalArgumentException("La CBU no es válida dígito validador del segundo cuerpo");
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

    public static JTable getDefaultTableModel(
            JTable tabla, String[] columnNames, int[] columnWidths, Class[] columnClassType) {
        return getDefaultTableModel(tabla, columnNames, columnWidths, columnClassType, null);
    }

    public static JTable getDefaultTableModel(JTable tabla, String[] columnNames, int[] columnWidths) {
        return getDefaultTableModel(tabla, columnNames, columnWidths, null, null);
    }

    /**
     * Personaliza una DefaulTableModel
     *
     * @param tabla en la cual se insertará el modelo, if this is null, a new one will be
     * initialized
     * @param columnNames Nombre de las HeaderColumns
     * @param columnWidths Ancho de columnas
     * @param columnClassType Tipo de datos que va contener cada columna
     * @param editableColumns Index de las columnas las cuales podrán ser editables ( &gt; -1
     * &amp;&amp; cantidadColumnas &gt;= columnsCount)
     * @return una JTable con el modelo
     */
    public static JTable getDefaultTableModel(JTable tabla,
            String[] columnNames, int[] columnWidths, Class[] columnClassType,
            int[] editableColumns) {

        if (columnNames == null) {
            throw new IllegalArgumentException();
        }
        if (columnWidths != null) {
            if (columnNames.length < columnWidths.length) {
                throw new IllegalArgumentException("el array columnWidths tiene mas elementos (" + columnWidths.length + ")"
                        + " que columnNames = " + columnNames.length);
            } else if (columnNames.length > columnWidths.length) {
                LOG.info("columnNames.length(" + columnNames.length + ") <> columnWidths(" + columnWidths.length + "):\n" + Arrays.toString(columnNames) + "\n" + Arrays.toString(columnWidths));
            }
        }
        if (columnClassType != null && (columnNames.length < columnClassType.length)) {
            throw new IllegalArgumentException("el array columnClassType tiene mas elementos (" + columnClassType.length + ")"
                    + " que columnNames (" + columnNames.length + "):\n" + Arrays.toString(columnNames));
        }
        if (editableColumns != null) {
            for (int i : editableColumns) {
                if (i < 0 || i > (columnNames.length - 1)) {
                    throw new IndexOutOfBoundsException("El array editableColumns[]"
                            + " contiene un índice de columna no válido: index = " + i);
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

    /**
     * Devuelve un dtm con los nombres de las columnas
     *
     * @param dtm if == null a new instance of a PRIVATE implementation of will be created.
     * @param columnNames los nombres de las respectivas columns que va tener la tabla.
     * @return a DefaultTableModel with column names set.
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

    /**
     * Crea un java.util.Date con los parámetros.
     *
     * @param year &gt;=1900
     * @param month 0=enero and so on...
     * @param day if == NULL.. will be set to 1, si == -1 al último día de
     * <b>month</b>
     * @return java.util.Date inicializado con la fecha.
     * @throws IllegalArgumentException si month &lt; 0 || month &gt; 11
     */
    public static Date customDate(int year, int month, Integer day) {
        if (month < 0 || month > 11) {
            throw new IllegalArgumentException("Mes (month) no válido, must be >= 0 AND <= 11: " + month);
        }
        Calendar c = Calendar.getInstance();
        c.clear();
        if (day == null) {
            c.set(year, month, 1);
        } else if (day == -1) {
            c.set(year, month + 1, 1);
            c.add(Calendar.DAY_OF_MONTH, -1);
        } else {
            c.set(year, month, day);
        }
        return c.getTime();
    }

    /**
     * Crea un java.util.Date con los parámetros.
     *
     * @param year &gt;=1900
     * @param month
     * @param day if == NULL.. will be set to 1, si == -1 al último día de
     * <b>month</b>
     * @return java.util.Date inicializado con la fecha.
     * @see #customDate(int, int, java.lang.Integer)
     */
    public static Date customDate(int year, Month month, Integer day) {
        return customDate(year, month.getValue() - 1, day);
    }

    /**
     * Personaliza una fecha según los parámetros. <code>fecha</code> no debe ser <code>null</code>.
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
     *
     * @param date cannot be null
     * @return return an integer representative of a period (yyyyMM), where MM &gt;=1 and &gt;=12
     */
    public static Integer getPeriodo(Date date) {
        return Integer.valueOf(new SimpleDateFormat("yyyyMM").format(date));
    }

    /**
     * Devuelte un Date modificada según los <code>dias</code>
     *
     * @param fecha Date base sobre la cual se va trabajar. If <code>fecha</code> is
     * <code>null</code> will return null Date
     * @param dias cantidad de días por adicionar o restar a <code>fecha</code>
     * @return customized Date!!!...
     */
    public static Date customDateByDays(Date fecha, int dias) {
        Calendar c = Calendar.getInstance();
        c.setTime(fecha);
        c.add(Calendar.DAY_OF_MONTH, dias);
        return c.getTime();
    }

    public static Date getDateFromLocal(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Search the last dot and returns the string after, if there is none, returns empty
     *
     * @param fileName
     * @return
     */
    public static String getExtensionFile(String fileName) {
        return (fileName.lastIndexOf(".") == -1) ? ""
                : fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
    }

    /**
     * Borra las imagenes temporales creadas por reportes e informes
     *
     * @param pathName si es == a "777".. va buscar todos los archivos img.IMG_EXTENSION en
     * /reportes/
     */
    public static void borrarFile(String pathName) {
        File f;
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
     * Ctrla que sea un caracter numérico el apretado, sinó el evento es consumido
     *
     * @param evt
     */
    public static void soloNumeros(KeyEvent evt) {
        if (!Character.isDigit(evt.getKeyChar())) {
            evt.consume();
        }
    }

    /**
     * Controla que la tecla presionada sea un caracter numérico o un PUNTO '.' y que este no
     * aparezca en la cadena (si este ya aparece en la cadena, el evento se consume al igual que si
     * no fuera un numérico).
     *
     * @param cadena
     * @param e
     */
    public static void solo_numeros_y_un_punto(final String cadena, KeyEvent e) {
        // si es != de '.'
        if (e.getKeyChar() != 46) {
            soloNumeros(e);
        } else if (countChacarter(cadena, '.') > 0) {
            e.consume();
        }
    }

    /**
     * Se obtiene el String del JTextField
     *
     * @param evt The KeyEvent must come from a {@link JTextField}
     * @throws ClassCastException if the source of the event doesn't come from a JTextField.
     * @see #solo_numeros_y_un_punto(java.lang.String, java.awt.event.KeyEvent)
     */
    public static void solo_numeros_y_un_punto(KeyEvent evt) {
        JTextField tf = (JTextField) evt.getSource();
        String cadena = tf.getText();
        solo_numeros_y_un_punto(cadena, evt);
    }

    /**
     * Parse a Double formatted String like <code>DecimalFormat("#,##0.00")</code> to a Double: <br>
     * 1234 parse to 1234.00
     * <br>1.234 parse to 1234.00
     * <br>12.34 parse to 12.34
     * <br>123.4 parse to 123.40
     * <br>123.456 parse to 123456.00
     * <br>1234.56 parse to 1234.56
     * <br>------------------------------
     * <br>12.345,6 parse to 12345.60
     * <br>123.456, parse to 123456.00
     * <br>123.456,7 parse to 123456.70
     * <br>1.234.567 parse to 1234567.00
     *
     * @param s a String formated like <code>DecimalFormat("#,##0.00")</code>
     * @return
     */
    public static Double parseToDouble(String s) {
        String string = s;
        if (string.contains(",")) {
            string = string.replaceAll("\\.", "").replaceAll(",", ".");
        } else {
            int countChacarter = UTIL.countChacarter(string, '.');
            for (int i = 1; i < countChacarter; i++) {
                string = string.replaceFirst("\\.", "");
            }
            int indexOf = string.indexOf('.');
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

    /**
     * Remueve de la ColumnModel las columnas que se le indique, deja de ser visible para el usuario
     * pero sigue siendo accesible desde el TableModel
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
     * Remueve de la ColumnModel las columnas que se le indique, deja de ser visible para el usuario
     * pero sigue siendo accesible desde el TableModel
     *
     * @param jTable tabla de la cual se desea sacar las columnas
     * @param columnName nombre de la columna (a.k.a. Header column and identifier)
     */
    public static void hideColumnTable(JTable jTable, String columnName) {
        jTable.getColumnModel().removeColumn(jTable.getColumn(columnName));
    }

    /**
     * Remueve de la ColumnModel la columna que se le indique, deja de ser visible para el usuario
     * pero sigue siendo accesible desde el TableModel (DefaultTableModel)
     *
     * @param jTable tabla de la cual se desea sacar la columna
     * @param columnIndex número de la columna a quitar de la vista del usuario
     */
    public static void hideColumnTable(JTable jTable, int columnIndex) {
        hideColumnsTable(jTable, new int[]{columnIndex});
    }

    /**
     * Setea como selected al item del comboBox que coincida con el <code>candidato</code>
     *
     * @param combo if is null or combo<code>.getItemCount() </code> is less than 1, no selectedItem
     * @param candidato if this is <code>null</code>, no habrá selectedItem
     * @return an index of the selectedItem, or <code>-1</code> if 1 &gt; combo.getItemCount() || if
     * <code>candidato</code> does not match any item
     */
    public static int setSelectedItem(JComboBox combo, String candidato) {
        if (candidato == null) {
            return -1;
        }

        int index = 0;
        while (index < combo.getItemCount()) {
            if ((combo.getItemAt(index)).toString().equals(candidato)) {
                combo.setSelectedIndex(index);
                return index;
            }
            index++;
        }
        return -1;
    }

    /**
     * Setea como selected al item del comboBox que coincida con el <code>candidate</code>.
     * <p>
     * Este método utiliza {@code equals} para la comparación, SO THE CLASS MUST OVERRIDE
     * {@link Object#equals(java.lang.Object)}
     *
     * @param combo El cual podría contener el item {@code candidate}
     * @param candidate Can not be null.
     * @return an index of the selectedItem, or -1 if 1 &gt; combo.getItemCount() || if
     * {@code candidato} does not match any item
     */
    public static int setSelectedItem(JComboBox combo, Object candidate) {
        return setSelectedItem(combo, candidate, false);
    }

    /**
     * Setea como selected al item del comboBox que coincida con <code>candidate</code>.
     * <p>
     * Este método utiliza {@code equals} para la comparación, SO THE CLASS MUST OVERRIDE
     * {@link Object#equals(java.lang.Object)}
     *
     * @param combo El cual podría contener el item {@code candidate}
     * @param candidate Can not be null.
     * @param allowNullCandidate
     * @return an index of the selectedItem, or -1 if 1 &gt; combo.getItemCount() || if
     * {@code candidato} does not match any item
     */
    public static int setSelectedItem(JComboBox combo, Object candidate, boolean allowNullCandidate) {
        if (candidate == null) {
            if (allowNullCandidate) {
                combo.setSelectedIndex(-1);
                return -1;
            } else {
                throw new IllegalArgumentException("El Objeto candidato can't be null");
            }
        }
        if (combo == null) {
            throw new IllegalArgumentException("JComboBox combo can't be null");
        }
        if (combo.getItemCount() < 1) {
            return -1;
        }
        try {
            Class<?> declaringClass = candidate.getClass().getMethod("equals", Object.class).getDeclaringClass();
            if (!declaringClass.equals(candidate.getClass()) && !candidate.getClass().isEnum()) {
                Logger.getLogger(UTIL.class.getName()).log(Level.WARNING,
                        "The " + candidate.getClass() + " must override the method equals(Object o)");
                return -1;
            }
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex.getMessage(), ex.getCause());
        } catch (SecurityException ex) {
            throw new RuntimeException(ex.getMessage(), ex.getCause());
        }
        int index = 0;
        while (index < combo.getItemCount()) {
            Object object = combo.getItemAt(index);
            if (object instanceof EntityWrapper) {
                /**
                 * Cuando candidato es EntityWrapper y MUY probablemente no tenga seteado el
                 * atributo EntityWrapper#entity (es null), es porque se quiere comparar los
                 * atributos id (EntityWrapper#id) nomas y no usar el método equals() del objeto
                 * wrappeado ..wakatta?
                 */
                if (candidate instanceof EntityWrapper && object.equals(candidate)) {
                    combo.setSelectedIndex(index);
                    return index;
                }
                object = ((EntityWrapper) object).getEntity();
            }
            if (object.equals(candidate)) {
                combo.setSelectedIndex(index);
                return index;
            }
            index++;
        }
        return -1;
    }

    /**
     *
     * @param comboBox
     * @param objectList
     * @param elegible
     * @param itemWhenIsEmpty se agrega como 1er item del combo si la lista está vacía o es null
     * @throws IllegalArgumentException if <code>objectList.isEmpty() || objectList == null</code>
     * and <code>itemWhenIsEmpty == null</code>
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
     * @param objectList Si la List está vacía o es null se carga un String Item "&lt;Vacio&gt;".
     * @param elegible Si es true, se agrega un Item "&lt;Elegir&gt;" en el index 0; sino solo se
     * cargar los objetos.
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
     * Personaliza la carga de datos en un JComboBox, según una List y bla bla...
     *
     * @param comboBox ...
     * @param objectList <code>List</code> la cual se va cargar
     * @param firstItem mensaje del 1er item del combo, dejar <code>null</code> si no hay
     * preferencia
     * @param itemWhenIsEmpty item que se va cargar cuando el <tt>objectList == null or empty</tt>.
     * @throws IllegalArgumentException if objectList == null or empty AND itemWhemIsEmpy == null.
     * @see #loadComboBox(javax.swing.JComboBox, java.util.List, java.lang.String)
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
     * Remueve todos los items, carga en el comboBox todos los objectos en objectList. Si no hay
     * elementos para cargar ( <code>objectList == null &amp;&amp; empty</code>) message1stItem no
     * se agrega.
     *
     * @param comboBox
     * @param objectList collection la cual se va cargar
     * @param message1stItem 1er item del combo, puede ser <code>null</code> y solo se carga la
     * lista.
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
     * Sort a List based on the property indicated. This method use the Reflection API to
     * performance the work ( qué loco no!?).
     *
     * @param lista List to be ordered.
     * @param propiedad represent the getter signature from the property that the objects in the
     * List must has. <br>EXAMPLE: <br>if == <code>"name"</code> ==&gt; <b>getName</b>. <br>if ==
     * <code>"lastNAmE"</code> ==&gt; <b>getLastNAmE</b>.
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
                        } else if (comparableValue.compareTo(comparableValue2) == 1) {
                            return -1;
                        } else {
                            return 1;
                        }
                    } else if (value.equals(value2)) {
                        return 0;
                    } else {
                        return 1;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return 0;
            }
        });
    }

    public static String AGREGAR_CARACTERES(String cadena, char c, int longitudMaxima) {
        if (cadena == null) {
            throw new NullPointerException("El parámetro cadena es NULL!!!");
        }
        StringBuilder newCadena = new StringBuilder(cadena);
        for (int i = newCadena.toString().length(); i < longitudMaxima; i++) {
            newCadena.insert(0, c);
        }
        return newCadena.toString();
    }

    /**
     * Agrega "0" a la IZQUIERDA de <code>cadena</code> hasta que esta tenga la longitudMaxima
     *
     * @param cadena If == <code>null</code> will do nothing!
     * @param longitudMaxima agrega "0" hasta que <code>cadena</code> tenga la longitud deseada
     * @return <code>cadena</code> overclocking..
     */
    public static String AGREGAR_CEROS(String cadena, int longitudMaxima) {
        return AGREGAR_CARACTERES(cadena, '0', longitudMaxima);
    }

    public static String AGREGAR_CEROS(long numero, int longitudMaxima) {
        return AGREGAR_CEROS(String.valueOf(numero), longitudMaxima);
    }

    /**
     * Devuelte el % del monto
     *
     * @param monto sobre el cual se calcula el %
     * @param porcentaje debe ser &gt;=0
     * @return El porcentaje (%) del monto, being <code>0 &gt;= monto</code> or 0 &gt;=
     * <code>porcentaje</code>, otherwise will return 0.0!
     */
    public static Double getPorcentaje(double monto, double porcentaje) {
        return getPorcentaje(BigDecimal.valueOf(monto), BigDecimal.valueOf(porcentaje)).doubleValue();
    }

    /**
     * Devuelte el % del monto. default rounding {@link RoundingMode#HALF_UP}
     *
     * @param monto sobre el cual se calcula el %
     * @param porcentaje debe ser &gt;=0
     * @return El porcentaje (%) del monto, being <code>0 &gt;= monto</code> or 0 &gt;=
     * <code>porcentaje</code>, otherwise will return 0.0!
     */
    public static BigDecimal getPorcentaje(BigDecimal monto, BigDecimal porcentaje) {
        return getPorcentaje(monto, porcentaje, 2, RoundingMode.HALF_UP);
    }

    /**
     * Devuelte el % del monto
     *
     * @param monto sobre el cual se calcula el %
     * @param porcentaje debe ser &gt;=0
     * @param scale
     * @param rounding
     * @return El porcentaje (%) del monto, being <code>0 &gt;= monto</code> or 0 &gt;=
     * <code>porcentaje</code>, otherwise will return 0.0!
     */
    public static BigDecimal getPorcentaje(BigDecimal monto, BigDecimal porcentaje, int scale, RoundingMode rounding) {
        if (porcentaje.compareTo(BigDecimal.ZERO) == -1) {
            throw new IllegalArgumentException("Parameter \"porcentaje\" can not be negative.");
        }
        if (monto.compareTo(BigDecimal.ZERO) == -1) {
            return BigDecimal.ZERO;
        }
        return porcentaje.divide(new BigDecimal("100")).multiply(monto).setScale(scale, rounding);
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
     * Devuelve el Objeto en la celda de la fila selecciada y la columna indicada
     *
     * @param jTable De la cual se va obtener el DefaultTableModel y la selectedRow.
     * @param modelColumnIndex La columna de la que se solicita el Object
     * @return the value Object at the specified cell, or <code>null</code> if no row is selected
     */
    public static Object getSelectedValue(JTable jTable, int modelColumnIndex) {
        if (jTable.getSelectedRow() != -1) {
            return jTable.getModel().getValueAt(
                    jTable.getSelectedRow(), modelColumnIndex);
        } else {
            return null;
        }
    }

    /**
     * Cuando la tabla tiene algún tipo de {@link TableRowSorter} o
     * {@link JTable#setAutoCreateRowSorter(boolean)}
     *
     * @param table ..
     * @param modelColumnIndex ..
     * @return <code>null</code> if {@link JTable#getSelectedRow()} == -1
     */
    public static Object getSelectedValueFromModel(JTable table, int modelColumnIndex) {
        if (table.getSelectedRow() != -1) {
            int modelRoxIndex = table.convertRowIndexToModel(table.getSelectedRow());
            return table.getModel().getValueAt(
                    modelRoxIndex, modelColumnIndex);
        } else {
            return null;
        }
    }

    /**
     *
     * @param table ..
     * @param modelColumnIndex ..
     * @return a list, empty is nothing is selected
     * @see #getSelectedValueFromModel(javax.swing.JTable, int)
     */
    public static List<?> getSelectedValuesFromModel(JTable table, int modelColumnIndex) {
        List<Object> l = new ArrayList<>(table.getSelectedRowCount());
        for (int rowIndex : table.getSelectedRows()) {
            int modelRoxIndex = table.convertRowIndexToModel(rowIndex);
            l.add(table.getModel().getValueAt(modelRoxIndex, modelColumnIndex));
        }
        return l;
    }

    /**
     * Elimina las filas del modelo seleccionadas desde la tabla (usando
     * {@link JTable#convertRowIndexToModel(int)}, por si la tabla is sorted/filtered).
     *
     * @param jTable ...
     * @return cantidad de filas removidas
     * @see DefaultTableModel#removeRow(int)
     */
    public static int removeSelectedRows(JTable jTable) {
        int[] selectedRows = jTable.getSelectedRows();
        if (selectedRows.length > 0) {
            DefaultTableModel dtm = (DefaultTableModel) jTable.getModel();
            //Empezando desde la última hasta la 1ra, sino se corren las filas a medida que se va eliminando
            for (int index = selectedRows.length - 1; index >= 0; index--) {
                int modelRowIndex = jTable.convertRowIndexToModel(selectedRows[index]);
                dtm.removeRow(modelRowIndex);
            }
        }
        return selectedRows.length;
    }

    /**
     * Settea un alineamiento horizontal en las celdas de la tabla, para todas las celdas que sean
     * del tipo de class especificado.
     *
     * @param jTable1 tabla a la cual se le va aplicar.
     * @param columnClass class a cual afectará el alineamiento.
     * @param alignment One of the following constants defined in <code>SwingConstants</code>:
     * <code>LEFT</code>, <code>CENTER</code> (the default for image-only labels),
     * <code>RIGHT</code>, <code>LEADING</code> (the default for text-only labels) or
     * <code>TRAILING</code>.
     *
     * Ver SwingConstants
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
         * This property can be null or contain nulls values, in both case a default value will be
         * used ( <code>Object.class</code>).
         */
        private Class[] columnTypes = null;
        /**
         * Contain the indexes of columns which can be editable
         */
        private int[] editableColumns = null;

        /**
         * Constructor por defecto igual al javax.swing.table.DefaultTableModel Me parece que está
         * alpedo..
         */
        public DefaultTableModelImpl() {
        }

        /**
         * Este constructor permite especificar a que class pertenecen los datos que se van a
         * insertar en cada columna
         *
         * @param columnTypes tipo de objeto que contendrá cada columna, the array values can be
         * null
         */
        public DefaultTableModelImpl(Class[] columnTypes) {
            this.columnTypes = columnTypes;
        }

        /**
         * Este constructor permite especificar a que class pertenecen los datos que se van a
         * insertar en cada columna y cuales será editables
         *
         * @param columnTypes tipo de objecto que contendrá cada columna, the array values can be
         * null
         * @param editableColumns debe contenedor los números (index de las columnas) que se desea
         * que puedan ser editables.
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
     * @param regex for pattern compile
     * @param stringToEvaluate to match
     * @return if matches the expression
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
     * @return edad {@code &gt;= 0}
     */
    public static int getAge(Date dateOfBirth) {
        return getAge(dateOfBirth, Calendar.getInstance().getTime());
    }

    /**
     * Calcula la edad actual, según {@code dob}
     *
     * @param dob no puede ser posterior a HOY
     * @return edad {@code &gt;= 0}
     */
    public static int getAge(LocalDate dob) {
        if (dob.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException();
        }
        return Period.between(dob, LocalDate.now()).getYears();
    }

    /**
     *
     * @param dateOfBirth
     * @param dateToday
     * @return if dateOfBirth if after today, -1 will be returned
     */
    public static int getAge(Date dateOfBirth, Date dateToday) {
        Calendar today = Calendar.getInstance();
        today.clear();
        today.setTime(dateToday);
        return getAge(dateOfBirth, today);
    }

    public static int getAge(Date dateOfBirth, Calendar today) {
        Calendar birthDate = Calendar.getInstance();
        birthDate.setTime(dateOfBirth);
        if (birthDate.after(today)) {
            return -1;
        }
        int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
        if (age > 0) {
            age--;
        }

        if (today.get(Calendar.YEAR) > birthDate.get(Calendar.YEAR)) {
            if (today.get(Calendar.MONTH) > birthDate.get(Calendar.MONTH)) {
                age++;
            } else if (today.get(Calendar.MONTH) == birthDate.get(Calendar.MONTH)
                    && today.get(Calendar.DAY_OF_MONTH) >= birthDate.get(Calendar.DAY_OF_MONTH)) {
                age++;
            }
        }
        return age;
    }

    /**
     *
     * @param comboBox
     * @return if selectedIndex == -1 returns {@code null}
     * @exception ClassCastException si el selectedItem no es una instancia de {@link EntityWrapper}
     */
    public static EntityWrapper<?> getEntityWrapped(JComboBox<?> comboBox) {
        if (comboBox.getSelectedIndex() == -1) {
            return null;

        }
        return (EntityWrapper<?>) comboBox.getSelectedItem();
    }

    /**
     * Utilities related to Images
     */
    public final static class Image {

        /**
         * Converts a given Image into a BufferedImage
         *
         * @param img The Image to be converted
         * @return The converted BufferedImage
         */
        public static BufferedImage toBufferedImage(java.awt.Image img) {
            if (img instanceof BufferedImage) {
                return (BufferedImage) img;
            }
            BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            Graphics2D bGr = bimage.createGraphics();
            bGr.drawImage(img, 0, 0, null);
            bGr.dispose();
            return bimage;
        }

        public static boolean reduceImageQuality(int sizeThreshold, String srcImg, String destImg) throws IOException {
            File fileIn = new File(srcImg);
            File fileOut = new File(destImg);
            return reduceImageQuality(sizeThreshold, fileIn, fileOut);
        }

        /**
         * Reduce the quality of the picture until reach the <code>threshold</code> desired.
         *
         * @param sizeThreshold
         * @param fileIn
         * @param fileOut this file may will be delete and recreated many times
         * @return <code>true</code> if at least one time reduction quality was applied,
         * <code>false</code> if <code>fileIn</code> size is already beneath the
         * <code>threshold</code> specified.
         */
        public static boolean reduceImageQuality(int sizeThreshold, File fileIn, File fileOut) throws IOException {
            float quality = 1.0f;
            long fileSize = fileIn.length();
            if (fileSize <= sizeThreshold) {
                return false;
            }
            Iterator iter = ImageIO.getImageWritersByFormatName("jpeg");
            ImageWriter writer = (ImageWriter) iter.next();
            ImageWriteParam iwp = writer.getDefaultWriteParam();
            iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            FileInputStream inputStream = new FileInputStream(fileIn);
            BufferedImage originalImage = ImageIO.read(inputStream);
            IIOImage image = new IIOImage(originalImage, null, null);
            float percent = 0.1f;   // 10% of 1  
            while (fileSize > sizeThreshold) {
                if (percent >= quality) {
                    percent *= 0.1f;
                }
                quality -= percent;
                if (fileOut.exists()) {
                    fileOut.delete();
                }
                FileImageOutputStream output = new FileImageOutputStream(fileOut);
                writer.setOutput(output);
                iwp.setCompressionQuality(quality);
                writer.write(null, image, iwp);
                File fileOut2 = new File(fileOut.getPath());
                long newFileSize = fileOut2.length();
                if (newFileSize == fileSize) {
                    // cannot reduce more, return  
                    break;
                } else {
                    fileSize = newFileSize;
                }
                output.close();
            }
            writer.dispose();
            return true;
        }

        private Image() {
        }
    }

    /**
     * Quita todas las:
     * <br>Artículos: el/lo/las/los al principio "^" o intermedios
     * <br>Acentos: á/é/í/ó/ú/ü
     * <br>Abreviaciones: (Convierte las siguientes):
     * <br>{@code gral -> general}, {@code gdor -> gobernador}, {@code ^pto -> puerto},
     * {@code col -> colonia}
     *
     * @param candidate
     * @return a clear, trimmed, uppercased string
     */
    public static String quitarAAAs(String candidate) {
        String s = quitarAcentos(candidate.toLowerCase())
                .replaceAll("\\.", "")
                .replaceAll("¥", "ñ")
                //que empiezan con los artículos 
                .replaceAll("^el ", "").replaceAll("^la ", "").replaceAll("^las ", "").replaceAll("^los ", "")
                //que contiene artículos intermedios
                .replaceAll(" el ", "").replaceAll(" la ", "").replaceAll(" las ", "").replaceAll(" los ", "")
                .replace("gral", "general")
                .replace("gdor", "gobernador")
                .replaceAll("^pto ", "puerto ").replace(" pto", "puerto") //fix con palabra prescriPTOr
                .replace("col ", "colonia ");
        return s.trim().toUpperCase();
    }

    /**
     * Replace áéíóúüÁÉÍÓÚÜ for aeiouAEIOU
     *
     * @param candidate
     * @return
     */
    public static String quitarAcentos(String candidate) {
        return candidate.replaceAll("á", "a").replaceAll("é", "e").replaceAll("í", "i").replaceAll("ó", "o")
                .replaceAll("ü", "u").replaceAll("ú", "u")
                .replaceAll("Á", "A").replaceAll("É", "E").replaceAll("Í", "I").replaceAll("Ó", "O")
                .replaceAll("Ü", "U").replaceAll("Ú", "U");
    }

    /**
     * Returns the 1st not null
     *
     * @param <T>
     * @param o
     * @return the 1st not null value
     * @throws NoSuchElementException if all values are null
     */
    public static <T> T coalesce(T... o) {
        return Stream.of(o).filter(Objects::nonNull).findFirst().get();
    }

    /**
     * Métodos utilitarios para {@link BigDecimal}
     */
    public static class BigDecimals {

        /**
         * Create an instance of BigDecimal from a string. If the string is null or empty (after
         * trimmed) will return null
         *
         * @param str can be null or empty
         * @return BigDecimal instance or null if <code>str is null or empty</code>
         */
        public static BigDecimal toNull(String str) {
            return (str == null || str.trim().isEmpty()) ? null : new BigDecimal(str);
        }

        /**
         * Create an instance of BigDecimal from a string. If the string is null or empty (after
         * trimmed) will return {@link BigDecimal#ZERO}
         *
         * @param str can be null or empty
         * @return BigDecimal instance or {@link BigDecimal#ZERO} if
         * <code>str is null or empty</code>
         */
        public static BigDecimal toZero(String str) {
            return (str == null || str.trim().isEmpty()) ? BigDecimal.ZERO : new BigDecimal(str);
        }
    }

}
