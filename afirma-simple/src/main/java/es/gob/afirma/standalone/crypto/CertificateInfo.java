/* Copyright (C) 2011 [Gobierno de Espana]
 * This file is part of "Cliente @Firma".
 * "Cliente @Firma" is free software; you can redistribute it and/or modify it under the terms of:
 *   - the GNU General Public License as published by the Free Software Foundation;
 *     either version 2 of the License, or (at your option) any later version.
 *   - or The European Software License; either version 1.1 or (at your option) any later version.
 * Date: 11/01/11
 * You may contact the copyright holder at: soporte.afirma5@mpt.es
 */

package es.gob.afirma.standalone.crypto;

import java.security.cert.X509Certificate;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.standalone.SimpleAfirmaMessages;

/** Informaci&oacute;n para la visualizaci&oacute;n y validaci&oacute;n del certificado.
 * @author Carlos gamuci Mill&aacute;n */
public final class CertificateInfo {

    /** Icono ilustrativo del Certificado. */
    private final Icon icon;

    private final String iconTooltip;

    /** Texto descriptivo del certificado. */
    private String descriptionText;

    /** Construye el objeto con la informaci&oacute;n del certificado.
     * @param cert Certificado al cual se refierre la informaci&oacute;n
     * @param description Texto descriptivo del certificado.
     * @param ocsp Configuraci&oacute;n de OCSP para la validaci&oacute;n del certificado
     * @param i Icono para el certificado
     * @param iTooltip <i>Tooltip</i> para el icono del certificado */
    CertificateInfo(final X509Certificate cert, final String description, final Icon i, final String iTooltip) {

    	if (description == null || "".equals(description)) {  //$NON-NLS-1$
        	if (cert == null) {
        		this.descriptionText = SimpleAfirmaMessages.getString("CertificateInfo.0"); //$NON-NLS-1$
        	}
        	else {
        		this.descriptionText = "<html>" + (Platform.OS.MACOSX.equals(Platform.getOS()) ? "<br>" : "") + SimpleAfirmaMessages.getString("CertificateInfo.1") + ": <a href=\"#\">" + AOUtil.getCN(cert) + "</a>. " + SimpleAfirmaMessages.getString("CertificateInfo.2") + ": <a href=\"#\">" + AOUtil.getCN(cert.getIssuerX500Principal().toString()) + "</a>" + "</html>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$
        	}
        }
        else {
            this.descriptionText = "<html>" + (Platform.OS.MACOSX.equals(Platform.getOS()) ? "<br>" : "") + "<a href=\"#\">" + description + "</a>" + "</html>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        }

        if (i == null) {
        	this.icon = new ImageIcon(this.getClass().getResource("/resources/default_cert_ico.png")); //$NON-NLS-1$
        }
        else {
        	this.icon = i;
        }

        if (iTooltip == null) {
        	this.iconTooltip = SimpleAfirmaMessages.getString("CertificateInfo.3"); //$NON-NLS-1$
        }
        else {
        	this.iconTooltip = iTooltip;
        }

    }

    /** Obtiene el icono del certificado.
     * @return Icono del certificado
     */
    public Icon getIcon() {
        return this.icon;
    }

    /** Obtiene el texto del <i>tooltip</i> para el icono del certificado.
     * @return Texto del <i>tooltip</i> para el icono del certificado.
     */
    public String getIconTooltip() {
    	return this.iconTooltip;
    }

    /** Obtiene un texto descriptivo del certificado.
     * @return Descripci&oacute;n del certificado
     */
    public String getDescriptionText() {
        return this.descriptionText;
    }
}