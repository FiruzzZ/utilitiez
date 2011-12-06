package utilities.general;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRTextExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author FiruzzZ
 */
public class Report {

    /**
     * Levanta un reporte
     * @param con Conexion a la base de datos relacional
     * @param parametros Parametros del Informe
     * @param path_reporte Url del Reporte (.jasper)
     * @param fileName Nombre del Archivo a generar
     * @param copias  Numeros de hojas que tendra el reporte
     * @param fileExtension Tipo de archivo a generar pdf,rtf,xml,txt,cvc,xls o frame.
     * @param view true si se visualiza el reporte
     * @throws JRException
     * @throws IOException  
     */
    public void load(Connection con, Map<String, Object> parametros,
            String path_reporte, String fileName, int copias, String fileExtension, boolean view)
            throws JRException, IOException {

        JasperReport reporte = null;
        //Obtengo la URL del Reporte
        URL raiz = getClass().getResource(path_reporte);
        //Levanto el Reporte
        reporte = (JasperReport) JRLoader.loadObject(raiz);

        //Crea el reporte
        JasperPrint print = JasperFillManager.fillReport(reporte, parametros, con);

        List<JasperPrint> lista = new ArrayList<JasperPrint>();

        //Settea las copias del reporte
        for (int i = 0; i < copias; i++) {
            lista.add(print);
        }
        if (fileExtension.equalsIgnoreCase("FRAME")) {
            toViewer(lista.get(0));
            return;
        }

        File result = convert(fileExtension, fileName, lista);
        if (view) {
            open(result);
        }
    }

    /**
     * Levanta un reporte
     * @param con Conexion a la base de datos relacional
     * @param parametros Parametros del Informe
     * @param path_reporte Url del Reporte (.jasper)
     */
    public JasperPrint load(Connection con, Map<String, Object> parametros, String path_reporte) throws JRException {
        //Obtengo la URL del Reporte
        URL raiz = getClass().getResource(path_reporte);
        //Levanto el Reporte
        JasperReport reporte = (JasperReport) JRLoader.loadObject(raiz);
        //Compila el reporte
        JasperPrint print = JasperFillManager.fillReport(reporte, parametros, con);
        return print;
    }

    /**
     * Levanta un reporte
     * @param con Conexion a la base de datos relacional
     * @param parametros Parametros del Informe
     * @param path_reporte Url del Reporte (.jasper)
     * @param copias  Numeros de hojas que tendra el reporte
     */
    public List<JasperPrint> load(Connection con, Map<String, Object> parametros, String path_reporte, int copias) throws JRException {
        JasperPrint jasperPrint = load(con, parametros, path_reporte);
        List<JasperPrint> lista = new ArrayList<JasperPrint>(copias);
        //Set copy count
        for (int i = 0; i < copias; i++) {
            lista.add(jasperPrint);
        }
        return lista;
    }

    /**
     * Convierte un report a un tipo de documento especifico
     * @param fileExtension tipo de doc.
     * @param archivo nombre del archivo a generar
     * @param reporte Lista de reportes
     * @return archivo generado
     * @throws net.sf.jasperreports.engine.JRException
     */
    public File convert(String fileExtension, String archivo, List<JasperPrint> reporte) throws JRException {
        fileExtension = fileExtension.toUpperCase();
        File file = new File(archivo);

//        JRAbstractExporter exporter;
        if (fileExtension.equals("PDF")) {
            archivo = archivo + ".pdf";
            JRExporter exporter = new JRPdfExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, reporte);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE, file);
            exporter.exportReport();

        }

        if (fileExtension.equals("RTF")) {
            archivo = archivo + ".rtf";
            JRRtfExporter export = new JRRtfExporter();
            export.setParameter(JRExporterParameter.JASPER_PRINT_LIST, reporte);
            export.setParameter(JRExporterParameter.OUTPUT_FILE, file);
            export.exportReport();
        }

        if (fileExtension.equals("CVC")) {
            archivo = archivo + ".cvc";
            JRExporter exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, reporte);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE, file);
            exporter.exportReport();
        }

        if (fileExtension.equals("XLS")) {
            archivo = archivo + ".xls";
            JRExporter exporter = new JRXlsExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, reporte);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE, file);
            exporter.exportReport();
        }

        if (fileExtension.equals("XML")) {
            archivo = archivo + ".xml";
            JRExporter exporter = new JRXmlExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, reporte);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE, file);
            exporter.exportReport();

        }

        if (fileExtension.equals("TXT")) {
            archivo = archivo + ".txt";
            JRExporter exporter = new JRTextExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, reporte);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE, file);
            exporter.setParameter(JRTextExporterParameter.CHARACTER_WIDTH, 12);//text exporter
            exporter.setParameter(JRTextExporterParameter.CHARACTER_HEIGHT, 12);//text exporter
            exporter.exportReport();
        }
        return file;
    }

    /**
     * Visualiza un report en un Frame
     * @param print
     * @param titulo
     */
    public void toViewer(JasperPrint print, String titulo) {
        JasperViewer rep = new JasperViewer(print, false);
        rep.setTitle(titulo);
        rep.setExtendedState(JasperViewer.MAXIMIZED_BOTH);
        rep.setVisible(true);
        rep.setAlwaysOnTop(false);
        rep.requestFocus();
    }

    /**
     * Retorna un reporte en formato Frame
     * @param jasperPrint
     * @return
     */
    public JasperViewer toViewer(JasperPrint jasperPrint) {
        return new JasperViewer(jasperPrint, false);

    }

    /**
     * Imprime un documento JasperReport en la impresora indicada
     * @param jasperPrint Reporte compilado
     * @param impresora Nombre de la Impresora instalada en el S.O
     * @param copias Numero de copias
     * @throws JRException
     */
    public void print(JasperPrint jasperPrint, String impresora, int copias) throws JRException {
        List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>(1);
        jasperPrintList.add(jasperPrint);
        print(jasperPrintList, impresora, copias);
    }

    /**
     * Imprime el JasperReport en la impresora indicada
     * @param jasperPrint Reporte compilado
     * @param impresora Nombre de la Impresora instalada en el S.O
     * @param copias Numero de copias
     */
    public void print(List<JasperPrint> jasperPrintList, String impresora, int copias) throws JRException {
        //Obtengo las impresoras instaladas en el Sistema Operativo
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        int impresoras = 0;
        //Select printer
        for (int i = 0; i < services.length; i++) {
            if (services[i].getName().contains(impresora)) {
                impresoras = i;
                break;
            }
        }
        //Set copy count
        PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
        printRequestAttributeSet.add(new Copies(copias));
        JRPrintServiceExporter jRPrintServiceExporter = new JRPrintServiceExporter();
        //Set printer parameters
        jRPrintServiceExporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
        jRPrintServiceExporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE, services[impresoras]);
        jRPrintServiceExporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, services[impresoras].getAttributes());
        jRPrintServiceExporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
        jRPrintServiceExporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
        jRPrintServiceExporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);
        //trigger printer event
        jRPrintServiceExporter.exportReport();
    }

    /**
     * Levanta un archivo y lo visualiza con el programa asociado en el Sistema operativo
     * @param f
     * @throws java.io.IOException
     */
    public void open(File f) throws IOException {
        //        Runtime r = Runtime.getRuntime();
        //        r.exec("rundll32 url.dll,FileProtocolHandler " + f.getPath());
        if (Desktop.isDesktopSupported()
                && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
            Desktop.getDesktop().open(f);
        }
    }
}
