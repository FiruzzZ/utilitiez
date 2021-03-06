package utilities.general;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.swing.JTable;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Export the data from a JTAble (not from the tableModel) to a Excel File (xls, xlsx)
 *
 * @author FiruzzZ
 * @version 1
 * @since 01/06/2013 (ma o meno)
 */
public class TableExcelExporter {

    private final JTable table;
    private List<Integer> columnIdxToSkip;
    private File file;
    private boolean skipColumnHeaders = false;
    private Sheet sheet;
    // the style for Date column
    private CellStyle DATE_CELL_STYLE;
    // the style for Date (with Time) column
    private CellStyle TIMESTAMP_CELL_STYLE;
    // the style for Money cells
    private CellStyle MONEY_CELL_STYLE;
    // the style for Money cells with negative value
    private CellStyle NEGATIVE_MONEY_STYLE;
    // evaluator to evaluate formula cell
    private FormulaEvaluator EVALUATOR_STYLE;
    private HashMap<Integer, CellStyle> cellStylePerColumn;
    private Workbook workBook;

    /**
     *
     * @param file fileName must contain the extension: xls or xlsx
     * @param table where the values are taken (NOT FROM THE MODEL!!"#$%@)
     * @throws IllegalArgumentException if the table does not contain rows
     */
    public TableExcelExporter(File file, JTable table) {
        if (table.getRowCount() < 1) {
            throw new IllegalArgumentException("Tabla no contiene fila para ser exportadas");
        }
        if (file == null) {
            throw new IllegalArgumentException("Archivo de destino de exportación no válido");
        }
        this.table = table;
        this.file = file;
        columnIdxToSkip = new ArrayList<>();
        cellStylePerColumn = new HashMap<>();
        workBook = file.getName().substring(file.getName().lastIndexOf('.') + 1).equalsIgnoreCase("xlsx")
                ? new XSSFWorkbook() : new HSSFWorkbook();
        sheet = workBook.createSheet();
    }

    public void addMergedRegion(int firstRow, int lastRow, int firstCol, int lastCol) {
        sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
    }

    public void setCellStyle(int column, String format) {
        setCellStyle(workBook, column, format);
    }

    public void addColumnsToSkipFromExport(int... colums) {
        for (int i : colums) {
            columnIdxToSkip.add(i);
        }
    }

    public void export() throws FileNotFoundException, IOException {
        createCellStylesAndEvaluator(workBook);
        if (!skipColumnHeaders) {
            addTitleFromColumnHeaders();
        }
        addData();
        // adjust column width
//        autosizeColumns(sheet);
        // save workbook as Excel file
        saveFile(workBook);
    }

    public void setSkipColumnHeaders(boolean skipColumnHeaders) {
        this.skipColumnHeaders = skipColumnHeaders;
    }

    private void addTitleFromColumnHeaders() {
        Row titleRow = sheet.createRow(0);

        Font font = workBook.createFont();
        font.setBold(true);
        CellStyle style = workBook.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        for (int columnIdx = 0; columnIdx < table.getColumnCount(); columnIdx++) {
            Cell celda = titleRow.createCell(columnIdx);
            celda.setCellValue(table.getColumnModel().getColumn(columnIdx).getHeaderValue().toString());
            celda.setCellStyle(style);
        }
    }

    private void addData() {
        Calendar c = Calendar.getInstance();
        int rowExcelIdx = skipColumnHeaders ? 0 : 1;
        for (int rowIdx = 0; rowIdx < table.getRowCount(); rowIdx++) {
            Row row = sheet.createRow(rowExcelIdx);
            rowExcelIdx++;
            for (int columnIdx = 0; columnIdx < table.getColumnCount(); columnIdx++) {
                if (columnIdxToSkip.contains(columnIdx)) {
                    continue;
                }
                Cell cell = row.createCell(columnIdx);
                if (table.getValueAt(rowIdx, columnIdx) != null) {
                    Object value = table.getValueAt(rowIdx, columnIdx);
                    if (value instanceof Date) {
                        Date d = (Date) value;
                        c.setTime(d);
                        cell.setCellValue(d);
                        if ((c.get(Calendar.HOUR_OF_DAY) + c.get(Calendar.MINUTE) + c.get(Calendar.SECOND)) > 0) {
                            cell.setCellStyle(TIMESTAMP_CELL_STYLE);
                        } else {
                            cell.setCellStyle(DATE_CELL_STYLE);
                        }
                    } else if (value instanceof BigDecimal) {
                        cell.setCellValue(((BigDecimal) value).doubleValue());
                    } else if (value instanceof Double) {
                        cell.setCellValue((Double) value);
                    } else if (value instanceof Integer) {
                        cell.setCellValue((Integer) value);
                    } else if (value instanceof Number) {
                        cell.setCellValue(Double.valueOf(value.toString()));
                    } else {
//                        cell.setCellValue(new HSSFRichTextString(value.toString()));
                        cell.setCellValue(value.toString());
                    }
                    if (cellStylePerColumn.containsKey(columnIdx)) {
                        cell.setCellStyle(cellStylePerColumn.get(columnIdx));
                    }
                }
            }
        }
    }

    private void createCellStylesAndEvaluator(Workbook wb) {
        // CreationHelper for create CellStyle
        CreationHelper createHelper = wb.getCreationHelper();
        DATE_CELL_STYLE = wb.createCellStyle();
        DATE_CELL_STYLE.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
        TIMESTAMP_CELL_STYLE = wb.createCellStyle();
        TIMESTAMP_CELL_STYLE.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy HH:mm:ss"));
        // vertical align top
//        _dateCellStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);

        // money style ( >= 0)
        MONEY_CELL_STYLE = wb.createCellStyle();
        MONEY_CELL_STYLE.setDataFormat(createHelper.createDataFormat().getFormat("$__##,##0.##"));
        // money style ( < 0)
        Font font = wb.createFont();
        font.setColor(Font.COLOR_RED);
        NEGATIVE_MONEY_STYLE = wb.createCellStyle();
        NEGATIVE_MONEY_STYLE.setDataFormat(createHelper.createDataFormat().getFormat("$__##,##0.##"));
        NEGATIVE_MONEY_STYLE.setFont(font);

        EVALUATOR_STYLE = wb.getCreationHelper().createFormulaEvaluator();
    }

    /**
     * set style to numeric cell
     *
     * @param cell ..
     * @param isFormula ..
     */
    private void setNumericStyle(Cell cell, boolean isFormula) {
        double value = isFormula ? getFormulaCellValue(cell) : cell.getNumericCellValue();
        if (value >= 0) {
            cell.setCellStyle(MONEY_CELL_STYLE);
        } else {
            cell.setCellStyle(NEGATIVE_MONEY_STYLE);
        }
    }

    /**
     * Evaluate formula cell value
     *
     * @param cell ..
     * @return ..
     */
    private double getFormulaCellValue(Cell cell) {
        EVALUATOR_STYLE.evaluateFormulaCell(cell);
        return cell.getNumericCellValue();
    }

    /**
     * adjust column width
     *
     * @param sheet ..
     */
    private void autosizeColumns(Sheet sheet) {
        // auto size not work with date
        sheet.setColumnWidth(1, 3000);
        sheet.autoSizeColumn(2);
    }

    private void saveFile(Workbook wb) throws FileNotFoundException, IOException {
        // create a new file
        FileOutputStream out = new FileOutputStream(file);
        // Write out the workbook
        wb.write(out);
        out.close();
    }

    /**
     *
     * @param formulaRow ..
     * @param formula ejemplo: "SUM(desde:hasta)"
     * @param colum ..
     */
    private void setFormula(Row formulaRow, String formula, int colum) {
        Cell gtb = formulaRow.createCell(colum);
        gtb.setCellType(CellType.FORMULA);
        gtb.setCellFormula(formula);
        setNumericStyle(gtb, true);
    }

    private void setCellStyle(Workbook wb, int column, String format) {
        // CreationHelper for create CellStyle
        CreationHelper createHelper = wb.getCreationHelper();
        CellStyle cs = wb.createCellStyle();
        cs.setDataFormat(createHelper.createDataFormat().getFormat(format));
        cellStylePerColumn.put(column, cs);
    }
}
