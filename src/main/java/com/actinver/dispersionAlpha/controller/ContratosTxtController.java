package com.actinver.dispersionAlpha.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.actinver.dispersionAlpha.util.ConexionBDSisAsset;
import com.actinver.dispersionAlpha.util.ContratoUtil;
import com.actinver.dispersionAlpha.util.SoapUtil;
import com.actinver.dispersionAlpha.vo.DatosContrato;
import com.actinver.dispersionAlpha.vo.LogGestorAlpha;
import com.actinver.dispersionAlpha.vo.LogProcesoEnvio;

public class ContratosTxtController {

	private static final Logger logger = Logger.getLogger(ContratosTxtController.class.getName());
	static ConexionBDSisAsset conexionBDSisAsset = new ConexionBDSisAsset();
	LogGestorAlpha logGestorAlpha = new LogGestorAlpha();
	static ContratoUtil contratoUtil = new ContratoUtil();
	SoapUtil soapUtil = new SoapUtil();
	@SuppressWarnings("unused")
	private static final int TAMANO_LOTE = 100; // Tamaño del lote

	public LogProcesoEnvio dispersionReportes() {
		LogProcesoEnvio logProcesoEnvio = new LogProcesoEnvio();
		List<LogGestorAlpha> listlogAlpha = new ArrayList<>();

		Map<String, String> paths = conexionBDSisAsset.getPathDMZ();
		Map<String, String> pathsDispersion = conexionBDSisAsset.getPathDispercion();
		String pathDispersion = pathsDispersion.get("padtDispercion");
		String nameReporte = pathsDispersion.get("nameFileAlpha") + pathsDispersion.get("extencion");
		Integer exito = 0;
		Integer failed = 0;

		try {
			List<String> lineas = Files.readAllLines(Paths.get(pathDispersion + nameReporte));
			for (String linea : lineas) {
				LogGestorAlpha logAlpha = new LogGestorAlpha();
				String[] datosArchivo = linea.split("\\|");

				if (datosArchivo.length < 4) {
					logger.error("Error: línea con datos insuficientes: " + String.join("|", datosArchivo));
					return null;
				}

				logAlpha = dispercionXcontrato(datosArchivo, paths, pathDispersion);
				listlogAlpha.add(logAlpha);

				if (logAlpha.getStatus().equals("SUCCESS")) {
					exito++;
				} else if (logAlpha.getStatus().equals("FAILED")) {
					failed++;
				}

			}

			@SuppressWarnings("unused")
			boolean success = conexionBDSisAsset.insertLogGestorAlphaList(listlogAlpha);
			if (failed > 1) {

				logProcesoEnvio.setDescripcion(
						"Se ecnontrarón errores al enviar los estados de cuenta verifique el LOG de errores.");
			}
			contratoUtil.escribirArchivoLogDetalle(listlogAlpha);
			logProcesoEnvio.setFechaDispercion(contratoUtil.getFechaNow());
			logProcesoEnvio.setArchivoProsesados(lineas.size());
			logProcesoEnvio.setArchivosExito(exito);
			logProcesoEnvio.setArchivosError(failed);
		} catch (IOException e) {
			logger.error("Error al leer el archivo: " + e.getMessage());
		}

		return logProcesoEnvio;
	}

	private LogGestorAlpha dispercionXcontrato(String[] datosArchivo, Map<String, String> paths,
			String pathsDispersion) {
		LogGestorAlpha logAlpha = new LogGestorAlpha();
		DatosContrato datosContrato = new DatosContrato();
		ContratoUtil contratoUtil = new ContratoUtil();
		String contrato = datosArchivo[0];
		String pathDMZ = paths.get("dirRutaFileServer").replaceAll("\\\\", "\\\\\\\\");
		String virtualPathDMZ = paths.get("dirVirtualFileServer");

		if (datosArchivo == null || datosArchivo.length < 4) {
			return contratoUtil
					.getLog("La estructura dentro del archivo de Dispercion no es correcta rebiza para el contrato "
							+ contrato + " e intenta de nuevo", contrato);
		}

		datosContrato.setNoContrato(contrato);
		datosContrato.setNameFile(datosArchivo[1]);
		datosContrato.setEmail(datosArchivo[2]);
		datosContrato.setPeriodo(datosArchivo[3]);

		DatosContrato datosCliente = conexionBDSisAsset.getDatosClienteA2k(contrato);
		if (datosCliente != null) {
			datosContrato.setNameClient(datosCliente.getNameClient() != null ? datosCliente.getNameClient() : "Vacio");
			datosContrato.setRfcClient(datosCliente.getRfcClient() != null ? datosCliente.getRfcClient() : "Vacio");
		} else {
			return contratoUtil.getLog(
					"No se recupero adecuadamnete los datos del cliente como nombre y rfc para el contrato" + contrato,
					contrato);
		}

		byte[] pdfContent = contratoUtil.getBinaryArchivo(pathsDispersion, datosContrato.getNameFile());
		if (pdfContent == null) {
			return contratoUtil.getLog(
					"No se encontro el PDF " + datosContrato.getNameFile() + " en el directorio " + pathsDispersion,
					contrato);
		}

		try {
			String password = contratoUtil.generatePassword(contrato);
			File zipFile = contratoUtil.compressAndPasswordProtectPDF(pdfContent, datosContrato.getNameFile(),
					password);
			String directorioDMZ = contratoUtil.saveFileToLocalDirectory(zipFile, pathDMZ);

			datosContrato.setNameFile(zipFile.getName());
			datosContrato.setDirFile(contratoUtil.virtualPathDMZ(directorioDMZ, virtualPathDMZ));
			datosContrato.setPassword(password);
			datosContrato.setFecheGen(new SimpleDateFormat("yyyyMMdd").format(new Date()));

			logAlpha = soapUtil.createDataRequest(datosContrato);

		} catch (IOException e) {
			logger.error("Error procesando el contrato: " + datosContrato.getNoContrato() + " " + e);
		}

		return logAlpha;
	}

	

}
