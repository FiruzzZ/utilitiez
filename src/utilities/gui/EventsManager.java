/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTextField;

/**
 * Contiene todo tipo de controles y validaciones para GUI y sus posibles
 * componentes.
 * @author FiruzzZ
 */
public class EventsManager {

    private static EventsManager eventsManager;

    private EventsManager() {
    }

    public static EventsManager getInstance() {
        if (eventsManager == null) {
            eventsManager = new EventsManager();
        }
        
        return eventsManager;
    }

    /**
     * Verifica que el caracter que se intenta ingresar es un dígido y que la
     * longitud de caracteres ingresados no supere el limite.
     * If not > {@link java.awt.event.InputEvent#consume()}
     * @param jTextField Instance of {@link JTextField}
     * @param evt Must be a instance of {@link KeyListener#keyTyped(java.awt.event.KeyEvent)}
     * @param maxLenght maximun lenght 
     */
    public static void checkInputDigit(JTextField jTextField, KeyEvent evt, int maxLenght) {
        if (!Character.isDigit(evt.getKeyChar())) {
            evt.consume();
        } else if (maxLenght >= jTextField.getText().length()) {
            evt.consume();
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
}
