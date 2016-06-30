package utilities.general;

import java.math.BigDecimal;

/**
 * Esta clase provee la funcionalidad de convertir un numero representado en
 * digitos a una representacion en letras. Nota: valor máximo aceptado
 * 999999999.99 (9 enteros, 2 decimales)
 *
 * @author FiruzzZ
 * @version 1.1
 */
public abstract class NumberToLetterConverter {

    private NumberToLetterConverter() {
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("No te hagás del vivo pinkiwinki!");
    }
    private static final String[] UNIDADES = {"", "UN ", "DOS ", "TRES ",
        "CUATRO ", "CINCO ", "SEIS ", "SIETE ", "OCHO ", "NUEVE ", "DIEZ ",
        "ONCE ", "DOCE ", "TRECE ", "CATORCE ", "QUINCE ", "DIECISEIS",
        "DIECISIETE", "DIECIOCHO", "DIECINUEVE", "VEINTE"};
    private static final String[] DECENAS = {"VENTI", "TREINTA ", "CUARENTA ",
        "CINCUENTA ", "SESENTA ", "SETENTA ", "OCHENTA ", "NOVENTA ",
        "CIEN "};
    private static final String[] CENTENAS = {"CIENTO ", "DOSCIENTOS ",
        "TRESCIENTOS ", "CUATROCIENTOS ", "QUINIENTOS ", "SEISCIENTOS ",
        "SETECIENTOS ", "OCHOCIENTOS ", "NOVECIENTOS "};

    public static String convertNumberToLetter(String number, boolean centavosEscritos) {
        return convertNumberToLetter(Double.parseDouble(number), centavosEscritos);
    }

    /**
     *
     * @param number ..
     * @param centavosEscritos ..
     * @return ..
     * @see #convertNumberToLetter(double, boolean)
     * @since 1.1
     */
    public static String convertNumberToLetter(BigDecimal number, boolean centavosEscritos) {
        return convertNumberToLetter(number.doubleValue(), centavosEscritos);
    }

    /**
     * Convierte un numero en representacion numerica a uno en representacion de
     * texto. El numero es valido si esta entre 0 y 999'999.999 <p> Creation
     * date 3/05/2006 - 05:37:47 PM
     *
     * @param number Numero a convertir
     * @param centavosEscritos if false ==
     * <code> 00/100 </code>, else .. adiviná!
     * @return Numero convertido a texto
     * @throws NumberFormatException Si el numero esta fuera del rango
     * @since 1.0
     */
    public static String convertNumberToLetter(double number, boolean centavosEscritos)
            throws NumberFormatException {

        String format = String.format("%.2f", number);
        String splitNumber[] = format.replace(',', '#').split("#");
        if (splitNumber[0].length() > 9) {
            throw new NumberFormatException(
                    "El numero es mayor de 999.999.999, "
                    + "no es posible convertirlo");
        }
        String converted = "";
        // Descompone el trio de millones
        int millon = Integer.parseInt(String.valueOf(getDigitAt(splitNumber[0],
                8))
                + String.valueOf(getDigitAt(splitNumber[0], 7))
                + String.valueOf(getDigitAt(splitNumber[0], 6)));
        if (millon == 1) {
            converted = "UN MILLON ";
        }
        if (millon > 1) {
            converted = convertNumber(String.valueOf(millon)) + "MILLONES ";
        }

        // Descompone el trio de miles - ¡SGT!
        int miles = Integer.parseInt(String.valueOf(getDigitAt(splitNumber[0],
                5))
                + String.valueOf(getDigitAt(splitNumber[0], 4))
                + String.valueOf(getDigitAt(splitNumber[0], 3)));
        if (miles == 1) {
            converted += "MIL ";
        }
        if (miles > 1) {
            converted += convertNumber(String.valueOf(miles)) + "MIL ";
        }

        // Descompone el ultimo trio de unidades - ¡SGT!
        int cientos = Integer.parseInt(String.valueOf(getDigitAt(
                splitNumber[0], 2))
                + String.valueOf(getDigitAt(splitNumber[0], 1))
                + String.valueOf(getDigitAt(splitNumber[0], 0)));
        if (cientos == 1) {
            converted += "UN";
        }
        if (millon + miles + cientos == 0) {
            converted += "CERO";
        }
        if (cientos > 1) {
            converted += convertNumber(String.valueOf(cientos));
        }
        converted += "PESOS";
        // Descompone los centavos
        Integer centavos = Integer.parseInt(String.valueOf(getDigitAt(
                splitNumber[1], 2))
                + String.valueOf(getDigitAt(splitNumber[1], 1))
                + String.valueOf(getDigitAt(splitNumber[1], 0)));
        if (centavosEscritos) {
            if (centavos == 1) {
                converted += " CON UN CENTAVO";
            }
            if (centavos > 1) {
                converted += " CON " + convertNumber(String.valueOf(centavos))
                        + "CENTAVOS";
            }
        } else {
            converted += " CON " + (centavos.toString().length() == 1 ? "0" + centavos : centavos) + "/100";
        }
        return converted;
    }

    /**
     * Convierte los trios de numeros que componen las unidades, las decenas y
     * las centenas del numero. <p> Creation date 3/05/2006 - 05:33:40 PM
     *
     * @param number Numero a convetir en digitos
     * @return Numero convertido en letras
     * @since 1.0
     */
    private static String convertNumber(String number) {
        if (number.length() > 3) {
            throw new NumberFormatException(
                    "La longitud maxima debe ser 3 digitos: " + number);
        }

        String output = new String();
        if (getDigitAt(number, 2) != 0) {
            output = CENTENAS[getDigitAt(number, 2) - 1];
        }

        int k = Integer.parseInt(String.valueOf(getDigitAt(number, 1))
                + String.valueOf(getDigitAt(number, 0)));

        if (k <= 20) {
            output += UNIDADES[k];
        } else {
            if (k > 30 && getDigitAt(number, 0) != 0) {
                output += DECENAS[getDigitAt(number, 1) - 2] + "Y "
                        + UNIDADES[getDigitAt(number, 0)];
            } else {
                output += DECENAS[getDigitAt(number, 1) - 2]
                        + UNIDADES[getDigitAt(number, 0)];
            }
        }

        // Caso especial con el 100
        if (getDigitAt(number, 2) == 1 && k == 0) {
            output = "CIEN";
        }

        return output;
    }

    /**
     * Retorna el digito numerico en la posicion indicada de derecha a izquierda
     * <p> Creation date 3/05/2006 - 05:26:03 PM
     *
     * @param origin Cadena en la cual se busca el digito
     * @param position Posicion de derecha a izquierda a retornar
     * @return Digito ubicado en la posicion indicada o ZERO si la posición no
     * es válida
     * @since 1.0
     */
    private static int getDigitAt(String origin, int position) {
        if (origin.length() > position && position >= 0) {
            return origin.charAt(origin.length() - position - 1) - 48;
        }
        return 0;
    }
//    public static void main(String[] args) {
//        String s = convertNumberToLetter(999999999.99);
//        System.out.println(s);
//    }
}
