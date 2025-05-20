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

		String[] destinatarios = {emailsInfo.get("destinatarios")};
		String[] copias = { emailsInfo.get("copias") };
		String[] copiasOcultas = { emailsInfo.get("copiasOcultas") };

		emailSender.sendDispersionReport(destinatarios, copias, copiasOcultas, logDispercion.getFechaDispercion(),
				logDispercion.getArchivoProsesados(), logDispercion.getArchivosExito(),
				logDispercion.getArchivosError());
	}

}
