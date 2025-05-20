package com.actinver.dispersionAlpha.util;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Email {


	public void sendDispersionReport(String[] recipients, String[] ccRecipients, String[] bccRecipients,
			String fechaDispersion, int archivosProcesados, int archivosExito, int archivosError) {

		String asunto = "Ejecución de Dispersión de Reportes Alpha";

		String message = "<!DOCTYPE html>\r\n" + "<html lang=\"es\">\r\n" + "<head>\r\n"
				+ "  <meta charset=\"UTF-8\">\r\n"
				+ "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n"
				+ "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n"
				+ "  <title>Reporte de Dispersión Alpha</title>\r\n" + "</head>\r\n"
				+ "<body style=\"font-family: Arial, sans-serif; background-color: #f4f4f4; color: #333; padding: 20px;\">\r\n"
				+ "  <div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 2px 5px rgba(0,0,0,0.1);\">\r\n"
				+ "    <h2 style=\"color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px;\">Ejecución de Dispersión de Reportes Alpha</h2>\r\n"
				+ "    <div style=\"margin-top: 20px;\">\r\n" + "      <p><strong>Fecha de Dispersión:</strong> "
				+ fechaDispersion + "</p>\r\n" + "      <p><strong>Archivos Procesados:</strong> " + archivosProcesados
				+ "</p>\r\n" + "      <p><strong>Archivos con Éxito:</strong> <span style=\"color: #27ae60;\">"
				+ archivosExito + "</span></p>\r\n"
				+ "      <p><strong>Archivos con Error:</strong> <span style=\"color: #e74c3c;\">" + archivosError
				+ "</span></p>\r\n" + "    </div>\r\n"
				+ "    <div style=\"margin-top: 30px; font-size: 0.9em; color: #7f8c8d;\">\r\n"
				+ "      <p>Este reporte fue generado automáticamente por el sistema de dispersión Alpha.</p>\r\n"
				+ "      <p>© " + java.time.Year.now().getValue() + " Actinver - Todos los derechos reservados</p>\r\n"
				+ "    </div>\r\n" + "  </div>\r\n" + "</body>\r\n" + "</html>";

		try {
			Properties properties = new Properties();
			properties.setProperty("mail.smtp.host", "172.16.18.133");
			properties.setProperty("mail.smtp.starttls.enable", "false");
			properties.setProperty("mail.smtp.port", "25");
			properties.setProperty("mail.smtp.auth", "false");
			properties.setProperty("mail.mime.charset", "utf-8");

			Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("ifrancisco", ".actinver15");
				}
			});

			// Convertir arrays de destinatarios a Address[]
			Address[] destinos = new Address[recipients.length];
			for (int i = 0; i < recipients.length; i++) {
				destinos[i] = new InternetAddress(recipients[i]);
			}

			Address[] destinosCC = new Address[ccRecipients.length];
			for (int j = 0; j < ccRecipients.length; j++) {
				destinosCC[j] = new InternetAddress(ccRecipients[j]);
			}

			Address[] destinosBCC = new Address[bccRecipients.length];
			for (int k = 0; k < bccRecipients.length; k++) {
				destinosBCC[k] = new InternetAddress(bccRecipients[k]);
			}

			MimeMessage mimeMessage = new MimeMessage(session);
			mimeMessage.setFrom(new InternetAddress("ACT_SOPPRODUCCION@actinver.com.mx"));

			
			mimeMessage.addRecipients(Message.RecipientType.TO, destinos);
			if (ccRecipients.length > 0) {
				mimeMessage.addRecipients(Message.RecipientType.CC, destinosCC);
			}
			if (bccRecipients.length > 0) {
				mimeMessage.addRecipients(Message.RecipientType.BCC, destinosBCC);
			}

			mimeMessage.setSubject(asunto, "utf-8");
			mimeMessage.setContent(message, "text/html; charset=utf-8");

			Transport.send(mimeMessage);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
