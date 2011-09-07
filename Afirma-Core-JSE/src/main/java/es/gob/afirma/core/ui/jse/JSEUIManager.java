/*
 * Este fichero forma parte del Cliente @firma.
 * El Cliente @firma es un aplicativo de libre distribucion cuyo codigo fuente puede ser consultado
 * y descargado desde www.ctt.map.es.
 * Copyright 2009,2010,2011 Gobierno de Espana
 * Este fichero se distribuye bajo  bajo licencia GPL version 2  segun las
 * condiciones que figuran en el fichero 'licence' que se acompana. Si se distribuyera este
 * fichero individualmente, deben incluirse aqui las condiciones expresadas alli.
 */

package es.gob.afirma.core.ui.jse;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.ui.AOUIManager;

/** Gestor de componentes de interfas gr&aacute;fico (tanto para Applet como para
 * aplicaci&oacute;n de escritorio) de la aplicaci&oacute;n.
 * @version 0.4 */
public final class JSEUIManager implements AOUIManager {


    /** Construye un filtro para la selecci&oacute;n de ficheros en un <code>JFileChooser</code>.
     * @param exts
     *        Extensiones de fichero permitidas
     * @param desc
     *        Descripci&oacute;n del tipo de fichero correspondiente a
     *        las extensiones
     */
    public static FileFilter getFileFilter(final String[] extensions, String description) {
        return new ExtFilter(extensions, description);
    }

    /** Pregunta al usuario por una contrase&ntilde;a.
     * @param text
     *        Texto que se muestra en el di&aacute;logo para pedir la
     *        contrase&ntilde;a
     * @param c
     *        Componente padre (para la modalidad)
     * @return Contrase&ntilde;a introducida por el usuario
     * @throws AOCancelledOperationException
     *         Cuando el usuario cancela el proceso de solicitud de
     *         contrase&ntilde;a */
    public final char[] getPassword(final String text, final Object c) throws AOCancelledOperationException {
        return getPassword(text, null, false, c);
    }
    
    /** Muestra un di&aacute;logo para pedir una contrase&ntilde;a al usuario.
     * @param text
     *        Texto con el que se solicitar&aacute; la entrada de texto al
     *        usuario (<i>prompt</i>)
     * @param charSet
     *        Juego de caracteres aceptados para la contrase&ntilde;a
     * @param beep
     *        <code>true</code> si se desea un sonido de advertencia al
     *        introducir un caracter no v&aacute;lido, <code>false</code> en
     *        caso contrario
     * @param c
     *        Componente padre (para la modalidad)
     * @return Array de caracteres del texto introducido como contrase&ntilde;a
     * @throws AOCancelledOperationException
     *         Cuando el usuario cancela o cierra el di&aacute;logo */
    public final char[] getPassword(String text, final String charSet, final boolean beep, final Object c) throws AOCancelledOperationException {
        final JPasswordField pwd = new JPasswordField(10);
        if (charSet != null) {
            pwd.setDocument(new JTextFieldFilter(charSet, beep));
        }
        final JLabel lbText = new JLabel((text != null) ? text : JSEUIMessages.getString("JSEUIManager.24")); //$NON-NLS-1$
        lbText.setMinimumSize(new Dimension(lbText.getFontMetrics(lbText.getFont()).stringWidth(text), lbText.getSize().height));
        lbText.setLabelFor(pwd);
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(lbText);
        panel.add(pwd);

        final JOptionPane pane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION) {
            private static final long serialVersionUID = -3012522768561175760L;

            @Override
            public void selectInitialValue() {
                pwd.requestFocusInWindow();
            }
        };
        
        Component parent = null;
        if (c instanceof Component) {
            parent = (Component) c;
        }
        
        pane.createDialog(parent, JSEUIMessages.getString("JSEUIManager.24")).setVisible(true); //$NON-NLS-1$

        final Object selectedValue = pane.getValue();
        if (selectedValue == null) {
            return new char[0];
        }
        if (((Integer) selectedValue).intValue() == JOptionPane.OK_OPTION) {
            return pwd.getPassword();
        }
        throw new AOCancelledOperationException("La insercion de contrasena ha sido cancelada por el usuario" //$NON-NLS-1$
        );

    }

    public Object showInputDialog(final Object parentComponent,
                                  final Object message,
                                  final String title,
                                  final int messageType,
                                  final Object icon,
                                  final Object[] selectionValues,
                                  final Object initialSelectionValue) {
        Component parent = null;
        if (parentComponent instanceof Component) {
            parent = (Component) parentComponent;
        }
        Icon dialogIcon = null;
        if (icon instanceof Icon) {
            dialogIcon = (Icon) icon;
        }
        return JOptionPane.showInputDialog(parent, message, title, messageType, dialogIcon, selectionValues, initialSelectionValue);
    }
    
    /** Original code: <a
     * href="http://tactika.com/realhome/realhome.html">http://
     * tactika.com/realhome/realhome.html</a>
     * @author Real Gagnon */
    private static final class JTextFieldFilter extends PlainDocument {

        private static final long serialVersionUID = -5746396042117084830L;

        private String acceptedChars = null;

        /** Crea un nuevo filtro para campo de entrada de texto.
         * @param acceptedchars
         *        Cadena que debe contener todos los caracteres aceptados.
         *        Cualquier caracter no incluido en esta cadena ser&aacute;
         *        considerado inv&aacute;lido
         * @param beepOnError
         *        <code>true</code> si desea que se reproduzca un sonido
         *        cuando el usuario introduce un caracter no v&aacute;lido,
         *        false en caso contrario */
        JTextFieldFilter(final String acceptedchars, final boolean beepOnError) {
            this.beep = beepOnError;
            this.acceptedChars = acceptedchars;
        }

        private boolean beep = false;

        @Override
        public void insertString(final int offset, final String str, final AttributeSet attr) throws BadLocationException {
            if (str == null) {
                return;
            }
            for (int i = 0; i < str.length(); i++) {
                if (this.acceptedChars.indexOf(String.valueOf(str.charAt(i))) == -1) {
                    if (this.beep) {
                        Toolkit.getDefaultToolkit().beep();
                    }
                    return;
                }
            }
            super.insertString(offset, str, attr);
        }

    }

    /** Filtro de caracteres ASCCI imprimibles. */
    public static final class JTextFieldASCIIFilter extends PlainDocument {

        private static final long serialVersionUID = 1979726487852842735L;

        private boolean beep = false;

        /** Crea un nuevo filtro para campo de entrada de texto.
         * @param beepOnError
         *        <code>true</code> si desea que se reproduzca un sonido
         *        cuando el usuario introduce un caracter no v&aacute;lido,
         *        false en caso contrario */
        public JTextFieldASCIIFilter(final boolean beepOnError) {
            this.beep = beepOnError;
        }

        @Override
        public void insertString(final int offset, final String str, final AttributeSet attr) throws BadLocationException {
            if (str == null) {
                return;
            }

            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) < 32 || str.charAt(i) > 126) {
                    if (this.beep) {
                        Toolkit.getDefaultToolkit().beep();
                    }
                    return;
                }
            }
            super.insertString(offset, str, attr);
        }

    }
    
    public int showConfirmDialog(Object parentComponent, Object message, String title, int optionType, int messageType) {
        Component parent = null;
        if (parentComponent instanceof Component) {
            parent = (Component) parentComponent;
        }
        return JOptionPane.showConfirmDialog(parent, message, title, optionType);
    }

    public int getPlainMessageCode() {
        return JOptionPane.PLAIN_MESSAGE;
    }

    public int getYesNoOptionCode() {
        return JOptionPane.YES_NO_OPTION;
    }

    public int getWarningMessageCode() {
        return JOptionPane.WARNING_MESSAGE;
    }

    public int getYesOptionCode() {
        return JOptionPane.YES_OPTION;
    }

    public int getNoOptionCode() {
        return JOptionPane.NO_OPTION;
    }

    public int getOkCancelOptionCode() {
        return JOptionPane.OK_CANCEL_OPTION;
    }

    public int getOkOptionCode() {
        return JOptionPane.OK_OPTION;
    }

    public int getInformationMessageCode() {
        return JOptionPane.INFORMATION_MESSAGE;
    }
    
    /** Pregunta al usuario por un nombre de fichero para su carga.
     * @param extensions
     *        Extensiones predeterminadas para el fichero
     * @param description
     *        Descripci&oacute;n del tipo de fichero correspondiente con las
     *        extensiones
     * @param parentComponent
     *        Componente padre (para la modalidad)
     * @return Nombre de fichero (con ruta) seleccionado por el usuario */
    public String getLoadFileName(final String[] extensions, final String description, final Object parentComponent) {
        return getLoadFileName(null, extensions, description, parentComponent);
    }

    /** Pregunta al usuario por un nombre de fichero para su carga.
     * @param dialogTitle
     *        T&iacute;tulo de la ventana de di&aacute;logo.
     * @param extensions
     *        Extensiones predeterminadas para el fichero
     * @param description
     *        Descripci&oacute;n del tipo de fichero correspondiente con las
     *        extensiones
     * @param parentComponent
     *        Componente padre (para la modalidad)
     * @return Nombre de fichero (con ruta) seleccionado por el usuario */
    public String getLoadFileName(final String dialogTitle,
                                               final String[] extensions,
                                               final String description,
                                               final Object parent) {
        Component parentComponent = null;
        if (parent instanceof Component) {
            parentComponent = (Component) parent;
        }
        
        final JFileChooser jfc = new JFileChooser();
        if (dialogTitle != null && dialogTitle.length() > 0) {
            jfc.setDialogTitle(dialogTitle);
        }
        if (extensions != null && extensions.length > 0) {
            jfc.setFileFilter(new ExtFilter(extensions, description));
        }
        final int ret = jfc.showOpenDialog(parentComponent);
        if (ret == JFileChooser.APPROVE_OPTION) {
            return jfc.getSelectedFile().getAbsolutePath();
        }
        return null;
    }
    
    /** Muestra un di&aacute;logo de guardado para almacenar los datos indicados.
     * Los datos ser&aacute;n almacenados en el directorio y con el nombre que
     * indique el usuario. Si el fichero ya existe se le preguntar&aacute; al
     * usuario si desea sobreescribirlo. En caso de cancelar la operaci&oacute;n
     * se devolvera null, si la operaci&oacute;n finaliza correctamente se
     * devolver&aacute; el path completo del fichero.
     * @param data
     *        Datos que se desean almacenar.
     * @param selectedFile
     *        Nombre de fichero por defecto.
     * @param fileFilter
     *        Filtro de fichero para el di&aacute;logo de guardado.
     * @param parent
     *        Componente padre sobre el que se mostrar&aacute; el
     *        di&aacute;logo de guardado.
     * @return Fichero guardado.
     * @throws NullPointerException
     *         No se introdujeron los datos que se desean almacenar. */
    public File saveDataToFile(final byte[] data, final File selectedFile, final FileFilter fileFilter, final Object parent) {

        if (data == null) {
            Logger.getLogger("es.gob.afirma").warning("No se han introducido los datos que se desean guardar. Se cancelara la operacion"); //$NON-NLS-1$ //$NON-NLS-2$
            throw new NullPointerException("No se introdujeron datos que almacenar"); //$NON-NLS-1$
        }

        Component parentComponent = null;
        if (parent instanceof Component) {
            parentComponent = (Component) parent;
        }
        
        File resultFile = null;
        boolean tryAgain = true;
        File file = null;
        while (tryAgain) {

            tryAgain = false;
            final JFileChooser fileChooser = new JFileChooser();
            fileChooser.getAccessibleContext().setAccessibleName(JSEUIMessages.getString("AOUIManager.81")); //$NON-NLS-1$
            fileChooser.getAccessibleContext().setAccessibleDescription(JSEUIMessages.getString("AOUIManager.82")); //$NON-NLS-1$
            fileChooser.setToolTipText(JSEUIMessages.getString("AOUIManager.81")); //$NON-NLS-1$
            fileChooser.setSelectedFile(file);

            // Si se nos ha indicado un nombre de fichero por defecto, lo
            // establecemos
            if (selectedFile != null) {
                fileChooser.setSelectedFile(selectedFile);
            }

            // Solo aplicamos el filtro cuando este definido para evitar que el
            // desplegable de la ventana de guardado nos aparecezca vacio
            if (fileFilter != null) {
                fileChooser.setFileFilter(fileFilter);
            }

            int selectedOption = JOptionPane.YES_OPTION;
            if (JFileChooser.APPROVE_OPTION == fileChooser.showSaveDialog(parentComponent)) {
                file = fileChooser.getSelectedFile();
                if (file.exists()) {
                    selectedOption =
                        JOptionPane.showConfirmDialog(parentComponent,
                                JSEUIMessages.getString("AOUIManager.77", file.getAbsolutePath()), JSEUIMessages.getString("AOUIManager.85"), JOptionPane.YES_NO_CANCEL_OPTION); //$NON-NLS-1$ //$NON-NLS-2$
                    if (selectedOption == JOptionPane.CANCEL_OPTION) {
                        Logger.getLogger("es.gob.afirma").info("Se ha cancelado la operacion de guardado."); //$NON-NLS-1$ //$NON-NLS-2$
                        return null;
                    }
                    // Si se ha seleccionado la opcion YES (se desea
                    // sobreescribir) continuamos
                    // normalmente con el guardado del fichero
                }

                if (selectedOption == JOptionPane.NO_OPTION) {
                    tryAgain = true;
                }
                else { // Hemos seleccionado la opcion de sobreescribir
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(file);
                        fos.write(data);
                    }
                    catch (final Exception ex) {
                        Logger.getLogger("es.gob.afirma").warning("No se pudo guardar la informacion en el fichero indicado: " + ex); //$NON-NLS-1$ //$NON-NLS-2$
                        JOptionPane.showMessageDialog(parentComponent,
                                JSEUIMessages.getString("AOUIManager.88"), JSEUIMessages.getString("AOUIManager.89"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
                        fos = null;
                        // Volvemos a intentar guardar
                        tryAgain = true;
                    }
                    if (fos != null) {
                        try {
                            fos.flush();
                        }
                        catch (final Exception e) { /** No hacemos nada. */ }
                        try {
                            fos.close();
                        }
                        catch (final Exception e) { /** No hacemos nada. */ }
                    }
                    resultFile = file;
                }
            }
        }

        // Devolvemos el path del fichero en el que se han guardado los datos
        return resultFile;
    }
    
    /** Filtra los ficheros por extensi&oacute;n para los di&aacute;logos de
     * carga y guardado. Se declara como p&uacute;blico para que pueda ser usado
     * tambi&eacute;n por el interfaz de aplicaci&oacute;n de escritorio. No
     * usamos <code>FileNameExtensionFilter</code> directamente para
     * compatibilizar con Java 1.4
     * @version 0.3 */
    private final static class ExtFilter extends FileFilter implements java.io.FileFilter {

        private String[] extensions;
        private String description;

        /** Construye un filtro para la selecci&oacute;n de ficheros en un <code>JFileChooser</code>.
         * @param exts
         *        Extensiones de fichero permitidas
         * @param desc
         *        Descripci&oacute;n del tipo de fichero correspondiente a
         *        las extensiones */
        public ExtFilter(final String[] exts, String desc) {
            if (exts == null || exts.length < 1) {
                throw new IllegalArgumentException("No se puede crear un filtro vacio"); //$NON-NLS-1$
            }
            this.extensions = exts.clone();
            this.description = (desc != null) ? desc : JSEUIMessages.getString("JSEUIManager.0"); //$NON-NLS-1$
        }

        @Override
        public boolean accept(final File f) {
            if (f.isDirectory()) {
                return true;
            }
            // getExtension() pasa la extension a minusculas, no hace falta
            // el "ignoreCase"
            final String extension = getExtension(f);
            for (final String extension2 : this.extensions) {
                if (extension2.equalsIgnoreCase(extension)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String getDescription() {
            return this.description;
        }

        /** Devuelve la extensi&oacute;n de un fichero.
         * @param f
         *        Fichero del cual queremos conocer la extensi&oacute;n
         * @return Extensi&oacute;n del fichero o cadena vac&iacute;a si este no
         *         tiene extensi&oacute;n */
        private final static String getExtension(final File f) {
            final String s = f.getName();
            final int i = s.lastIndexOf('.');
            if (i > 0 && i < s.length() - 1) {
                return s.substring(i + 1).toLowerCase();
            }
            return ""; //$NON-NLS-1$
        }

    }
    
    /** Pregunta al usuario por la localizaci&oacute;n de un fichero espec&iacute;fico para su carga.
     * @param dialogTitle
     *        T&iacute;tulo de la ventana de di&aacute;logo.
     * @param fileName
     *        Nombre del fichero a localizar
     * @param description
     *        Descripci&oacute;n del tipo de fichero correspondiente con las
     *        extensiones
     * @param parentComponent
     *        Componente padre (para la modalidad, debe ser de tipo <code>java.awt.Component</code>)
     * @return Fichero seleccionado por el usuario */
    public File getLoadFile(final String dialogTitle, final String fileName, final String description, final Object parent) {
        
        Component parentComponent = null;
        if (parent instanceof Component) {
            parentComponent = (Component) parent;
        }
        
        final JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(dialogTitle);
        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(final File f) {
                if (f == null) {
                    return false;
                }
                if (f.getName().equalsIgnoreCase(fileName)) {
                    return true;
                }
                return false;
            }

            @Override
            public String getDescription() {
                return description;
            }
        });
        if (fc.showOpenDialog(parentComponent) != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        return fc.getSelectedFile();
    }

}
