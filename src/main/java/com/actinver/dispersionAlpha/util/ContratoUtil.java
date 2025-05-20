package com.actinver.dispersionAlpha.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

import com.actinver.dispersionAlpha.vo.DatosContrato;
import com.actinver.dispersionAlpha.vo.LogGestorAlpha;
import com.actinver.dispersionAlpha.vo.LogProcesoEnvio;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;

public class ContratoUtil {

	private static final Logger logger = Logger.getLogger(ConexionBDSisAsset.class.getName());

	static ConexionBDSisAsset conexionBDSisAsset = new ConexionBDSisAsset();
	static ContratoUtil contratoUtil = new ContratoUtil();

	public void escribirLogEnArchivo(LogProcesoEnvio logDispercion) {

		Map<String, String> pathsDispersion = conexionBDSisAsset.getPathLog();
		String pathDispersion = pathsDispersion.get("padtDispercion") + pathsDispersion.get("dirLog")
				+ pathsDispersion.get("fileLogEnv");
		String fechaNow = contratoUtil.getFechaNow();
		logger.info("fechaNow: " + fechaNow);

		String fechaFormateada = fechaNow.replace("/", "_").replace(" ", "__").replace(":", "_");

		logger.info("fechaFormateada: " + fechaFormateada);
		String filePath = pathDispersion + " " + fechaFormateada + pathsDispersion.get("extencion");

		if (logDispercion.getDescripcion() == null) {
			logDispercion.setDescripcion(" ");
		}

		try (FileWriter writer = new FileWriter(filePath)) {
			writer.write("*----Ejecucion de Dispercion de Reportes Alpha----* \n\n");
			writer.write(" - Fecha de Dispersión: " + logDispercion.getFechaDispercion() + "\n");
			writer.write(" - Archivos Procesados: " + logDispercion.getArchivoProsesados() + "\n");
			writer.write(" - Archivos con Éxito: " + logDispercion.getArchivosExito() + "\n");
			writer.write(" - Archivos con Error: " + logDispercion.getArchivosError() + "\n");
			writer.write(logDispercion.getDescripcion() + "\n");

			logger.info("Archivo de log generado correctamente: " + filePath);
		} catch (IOException e) {
			logger.error("Error al escribir el archivo: " + e.getMessage());
		}
	}

	public void escribirArchivoLogDetalle(List<LogGestorAlpha> listlogAlpha) {

		Map<String, String> pathsDispersion = conexionBDSisAsset.getPathLog();
		String pathDispersion = pathsDispersion.get("padtDispercion") + pathsDispersion.get("dirLog")
				+ pathsDispersion.get("fileLog");
		String fechaFormateada = contratoUtil.getFechaNow().replace("/", "_").replace(" ", "__").replace(":", "_");
		String filePath = pathDispersion + fechaFormateada + pathsDispersion.get("extencion");

		try (FileWriter writer = new FileWriter(filePath)) {
			writer.write("*----Ejecucion de Dispercion de Reportes Alpha----* \n");
			writer.write(" -Fecha de Dispersión: " + contratoUtil.getFechaNow() + "\n\n");

			for (LogGestorAlpha logAlpha : listlogAlpha) {
				writer.write("-Contrato:" + logAlpha.getNoContrato() + " ");
				writer.write("-Correo:" + logAlpha.getCorreo() + " ");
				if (logAlpha.getNombreZip() != null) {
					writer.write("-Archivo:" + logAlpha.getNombreZip() + " ");
				}
				if (logAlpha.getPasswordZip() != null) {
					writer.write("-Password:" + logAlpha.getPasswordZip() + " ");
				}
				writer.write("-Descripcion: " + logAlpha.getDescripcion() + " ");
				writer.write("-Status:" + logAlpha.getStatus() + "\n\n");
			}

			logger.info("Archivo de log generado correctamente: " + filePath);
		} catch (IOException e) {
			logger.error("Error al escribir el archivo: " + e.getMessage());
		}

	}

	// Método para generar una contraseña única
	public String generatePassword(String contrato) {
		String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String lowerCase = "abcdefghijklmnopqrstuvwxyz";
		String specialCharacters = "!@#$%^&*()?";
		String numbers = "0123456789";
		String allCharacters = upperCase + lowerCase + specialCharacters + numbers;
		SecureRandom random = new SecureRandom();
		Set<Character> usedCharacters = new HashSet<>();
		StringBuilder password = new StringBuilder();
		while (password.length() < 10) {
			int index = random.nextInt(allCharacters.length());
			char randomChar = allCharacters.charAt(index);
			if (!Character.isDigit(randomChar) || !usedCharacters.contains(randomChar)) {
				password.append(randomChar);
				usedCharacters.add(randomChar);
			}
		}
		return password.toString();
	}

	public File compressAndPasswordProtectPDF(byte[] pdfContent, String pdfFileName, String password)
			throws IOException, ZipException {

		String baseFileName = pdfFileName.substring(0, pdfFileName.lastIndexOf('.'));
		File tempPDFFile = new File(System.getProperty("java.io.tmpdir"), baseFileName + ".pdf");

		try (FileOutputStream fos = new FileOutputStream(tempPDFFile)) {
			fos.write(pdfContent);
		} catch (IOException e) {
			throw new IOException("Error al escribir el archivo temporal PDF: " + e.getMessage(), e);
		}

		String zipFileName = baseFileName + ".zip";
		File zipFile = new File(System.getProperty("java.io.tmpdir"), zipFileName);

		char[] fixedPassword = password.toCharArray();
		try (ZipFile zip = new ZipFile(zipFile, fixedPassword)) {
			ZipParameters zipParameters = new ZipParameters();
			zipParameters.setCompressionMethod(CompressionMethod.DEFLATE);
			zipParameters.setCompressionLevel(CompressionLevel.FASTEST);
			zipParameters.setEncryptFiles(true);
			zipParameters.setEncryptionMethod(EncryptionMethod.AES);
			zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);

			zip.addFile(tempPDFFile, zipParameters);
		} catch (ZipException e) {
			throw new ZipException("Error al crear el archivo ZIP: " + e.getMessage());
		} finally {
			if (!tempPDFFile.delete()) {
				logger.error("No se pudo eliminar el archivo temporal: " + tempPDFFile.getAbsolutePath());
			}
		}

		return zipFile;
	}

	public String saveFileToLocalDirectory(File zipFile, String path) {
		File destDir = new File(path);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}

		File destFile = new File(destDir, zipFile.getName());
		if (destFile.exists()) {
			if (!destFile.delete()) {
				System.err.println();
				logger.error("No se pudo eliminar el archivo existente: " + destFile.getAbsolutePath());
				return null;
			}
		}

		if (zipFile.renameTo(destFile)) {
			return destFile.getAbsolutePath();
		} else {
			logger.error("No se pudo mover el archivo a: " + destFile.getAbsolutePath());
			return null;
		}
	}

	public String virtualPathDMZ(String dirLocal, String dirVirtual) {
		String virtualPath = dirLocal.replace("\\\\10.14.2.102\\InversionAlpha\\ReporteRendimientos\\", dirVirtual);
		virtualPath = virtualPath.replace("\\", "/");
		return virtualPath;
	}

	public byte[] getBinaryArchivo(String filePath, String setNameFile) {
		Path path = Paths.get(filePath, setNameFile);

		if (!Files.exists(path)) {
			logger.warn("El archivo " + setNameFile + " no existe en " + filePath);
			return null;
		}

		try {
			return Files.readAllBytes(path);
		} catch (IOException e) {
			logger.error("Error al leer el archivo  " + setNameFile + " en " + filePath);
			return null;
		}
	}

	public boolean deleteArchivo(String filePath) {
		try {
			Path path = Paths.get(filePath);
			Files.delete(path);
			System.out.println("Archivo eliminado correctamente: " + filePath);
			return true;
		} catch (IOException e) {
			System.err.println("Error al eliminar el archivo: " + filePath);
			e.printStackTrace();
			return false;
		}
	}

	public LogGestorAlpha getLog(String mensaje, String contrato) {
		LogGestorAlpha logAlpha = new LogGestorAlpha();
		logger.warn(mensaje);
		logAlpha.setDescripcion(mensaje);
		logAlpha.setNoContrato(contrato);
		logAlpha.setStatus("FAILED");
		return logAlpha;
	}

	public LogGestorAlpha getLogAlpha(DatosContrato datosContrato, String status, String mensaje) {
		LogGestorAlpha logAlpha = new LogGestorAlpha();

		logAlpha.setStatus(status);
		logAlpha.setCorreo(datosContrato.getEmail());
		logAlpha.setNoContrato(datosContrato.getNoContrato());
		logAlpha.setNombreZip(datosContrato.getNameFile());
		logAlpha.setPasswordZip(datosContrato.getPassword());
		logAlpha.setDescripcion(mensaje);

		return logAlpha;
	}

	public String getFechaNow() {
		LocalDateTime fecha = LocalDateTime.now();

		DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a");
		String fechaDescriptiva = fecha.format(formato);
		return fechaDescriptiva;
	}

}
