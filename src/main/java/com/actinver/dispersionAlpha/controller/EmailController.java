package com.actinver.dispersionAlpha.controller;

import java.util.Map;

import com.actinver.dispersionAlpha.util.ConexionBDSisAsset;
import com.actinver.dispersionAlpha.util.Email;
import com.actinver.dispersionAlpha.vo.LogProcesoEnvio;

public class EmailController {
	
	static ConexionBDSisAsset conexionBDSisAsset = new ConexionBDSisAsset();

	public void sendDispersionReport(LogProcesoEnvio logDispercion) {
		Email emailSender = new Email();
		
		Map<String, String> emailsInfo = conexionBDSisAsset.getEmailsInfo();
		
		String destinatariosRaw = emailsInfo.get("destinatarios");
		String copiasRaw = emailsInfo.get("copias");
		String copiasOcultasRaw = emailsInfo.get("copiasOcultas");

		String[] destinatarios = destinatariosRaw != null && !destinatariosRaw.trim().isEmpty()
		    ? destinatariosRaw.split(",")
		    : new String[0];

		String[] copias = copiasRaw != null && !copiasRaw.trim().isEmpty()
		    ? copiasRaw.split(",")
		    : new String[0];

		String[] copiasOcultas = copiasOcultasRaw != null && !copiasOcultasRaw.trim().isEmpty()
		    ? copiasOcultasRaw.split(",")
		    : new String[0];


		emailSender.sendDispersionReport(destinatarios, copias, copiasOcultas, logDispercion.getFechaDispercion(),
				logDispercion.getArchivoProsesados(), logDispercion.getArchivosExito(),
				logDispercion.getArchivosError());
	}

}
