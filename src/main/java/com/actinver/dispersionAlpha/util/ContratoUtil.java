package com.actinver.dispersionAlpha.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.actinver.dispersionAlpha.vo.DatosContrato;
import com.actinver.dispersionAlpha.vo.LogGestorAlpha;
import com.actinver.dispersionAlpha.vo.LogProcesoEnvio;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;

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

	// Contraseña últimos 6 dígitos del contrato
	public String generatePassword(String contrato) {
		String contratoNumerico = contrato.replaceAll("\\D", "");
		if (contratoNumerico.length() < 6) {
			contratoNumerico = String.format("%06d", Integer.parseInt(contratoNumerico));
		}

		String password = contratoNumerico.substring(contratoNumerico.length() - 6);

		return password;
	}

	public File protectAndZipPDF(byte[] pdfBytes, String fileName, String password)
			throws IOException, DocumentException {
		
		File protectedPdf = protectPdfWithPassword(pdfBytes, fileName, password);
		String baseFileName = fileName.substring(0, fileName.lastIndexOf('.'));
		File zipFile = new File(System.getProperty("java.io.tmpdir"), baseFileName + ".zip");

		@SuppressWarnings("resource")
		ZipFile zip = new ZipFile(zipFile);
		ZipParameters zipParameters = new ZipParameters();
		zipParameters.setCompressionMethod(CompressionMethod.DEFLATE);
		zipParameters.setCompressionLevel(CompressionLevel.NORMAL);
		zipParameters.setEncryptFiles(false); // 🔓 No cifrado

		zip.addFile(protectedPdf, zipParameters);
		
		if (!protectedPdf.delete()) {
			System.err.println("No se pudo eliminar el archivo temporal: " + protectedPdf.getAbsolutePath());
		}

		return zipFile;
	}

	public static File protectPdfWithPassword(byte[] pdfBytes, String fileName, String password)
			throws IOException, DocumentException {

		File originalPdf = File.createTempFile("original", ".pdf");
		Files.write(originalPdf.toPath(), pdfBytes);

		File protectedPdf = new File(originalPdf.getParent(), fileName);

		PdfReader reader = new PdfReader(originalPdf.getAbsolutePath());

		// Escribir el PDF protegido con contraseña
		try (FileOutputStream outputStream = new FileOutputStream(protectedPdf)) {
			PdfStamper stamper = new PdfStamper(reader, outputStream);
			stamper.setEncryption(password.getBytes(), // usuario
					password.getBytes(), // propietario
					PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_128);
			stamper.close();
		}

		reader.close();
		originalPdf.delete();

		return protectedPdf;
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
