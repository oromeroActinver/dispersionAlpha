package com.actinver.dispersionAlpha.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.log4j.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.actinver.dispersionAlpha.vo.DatosContrato;
import com.actinver.dispersionAlpha.vo.LogGestorAlpha;
import com.actinver.dispersionAlpha.vo.SoapData;

public class SoapUtil {

	private static final Logger logger = Logger.getLogger(ConexionBDSisAsset.class.getName());
	ConexionBDSisAsset conexionBDSisAsset = new ConexionBDSisAsset();
	ContratoUtil contratoUtil = new ContratoUtil();

	public LogGestorAlpha createDataRequest(DatosContrato data) {
		LogGestorAlpha logGestorAlpha = new LogGestorAlpha();
		SoapData soapData = conexionBDSisAsset.getSoapData();

		if (soapData == null) {
			return contratoUtil.getLog("No se pudo obtener datos de SOAP para el contrato " + data.getNoContrato(),
					data.getNoContrato());
		}

		String leagueContent = soapData.getUUID() + "\t" + data.getNoContrato() + "\t" + data.getPeriodo() + "\t"
				+ data.getEmail() + "\t" + data.getNameClient() + "\t" + data.getDirFile() + "\t" + data.getFecheGen()
				+ "\t" + data.getRfcClient();
		// logger.info("leagueContent: " + leagueContent);
		String responceLeagueContent = callSoapService(soapData.getLeagueKey(), leagueContent, soapData);

		String passwordContent = soapData.getUUID() + "\t" + data.getEmail() + "\t" + data.getNameClient() + "\t"
				+ data.getRfcClient() + "\t" + data.getNameFile() + "\t" + data.getPassword();
		String responcePasswordContent = callSoapService(soapData.getPasswordKey(), passwordContent, soapData);
		// logger.info("passwordContent: " + passwordContent);

		if (responseSoap(responceLeagueContent) && responseSoap(responcePasswordContent)) {
			String mensaje = "El reporte corespondiente al contrato " + data.getNoContrato() + " se entrego a "
					+ data.getEmail() + " con éxito";
			logGestorAlpha = contratoUtil.getLogAlpha(data, "SUCCESS", mensaje);

		} else if (!responseSoap(responceLeagueContent) && !responseSoap(responcePasswordContent)) {
			String mensaje = "El reporte corespondiente al contrato " + data.getNoContrato() + " no se entrego a "
					+ data.getEmail();
			logGestorAlpha = contratoUtil.getLogAlpha(data, "FAILED", mensaje);

		} else if (!responseSoap(responceLeagueContent) && responseSoap(responcePasswordContent)) {
			String mensaje = "El correo corespondiente al pasword fue entregado"
					+ "Pero el correo corespondiente a la liga de descarga Fallo" + "\nDatos \nContrato: "
					+ data.getNoContrato() + "\nCorreo: " + data.getEmail();
			logGestorAlpha = contratoUtil.getLogAlpha(data, "FAILED", mensaje);

		} else if (responseSoap(responceLeagueContent) && !responseSoap(responcePasswordContent)) {
			String mensaje = "El correo corespondiente al liga de descarga fue entregado"
					+ "Pero el correo corespondiente a la pasword Fallo" + "\nDatos \nContrato: " + data.getNoContrato()
					+ "\nCorreo: " + data.getEmail();
			logGestorAlpha = contratoUtil.getLogAlpha(data, "FAILED", mensaje);
		}

		return logGestorAlpha;
	}

	public static String callSoapService(String importKey, String importContent, SoapData soapData) {
		String soapEndpoint = soapData.getSoapEndpoint();
		String soapRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
				+ "xmlns:web=\"http://webservices.tea.tralix.com\">" + "<soapenv:Header/>" + "<soapenv:Body>"
				+ "<web:newImportJob>" + "<web:importKey><![CDATA[" + importKey + "]]></web:importKey>"
				+ "<web:importContent><![CDATA[" + importContent + "]]></web:importContent>" + "</web:newImportJob>"
				+ "</soapenv:Body>" + "</soapenv:Envelope>";

		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpPost httpPost = new HttpPost(soapEndpoint);
			httpPost.setHeader("Content-Type", "text/xml; charset=UTF-8");
			httpPost.setHeader("SOAPAction", soapData.getSOAPAction());
			httpPost.setHeader("Accept-Encoding", soapData.getAcceptEncoding());
			httpPost.setHeader("Host", soapData.getHost());
			httpPost.setHeader("Connection", soapData.getConnection());
			httpPost.setHeader("User-Agent", soapData.getUserAgent());

			httpPost.setEntity(new StringEntity(soapRequest, "UTF-8"));

			try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
				int statusCode = response.getStatusLine().getStatusCode();
				String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");

				if (statusCode == 200) {
					return responseBody;
				} else {
					logger.error("Error en la respuesta SOAP. Código: " + statusCode);
					logger.error("Respuesta: " + responseBody);
					return responseBody;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private Boolean responseSoap(String responce) {
		try {

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			ByteArrayInputStream input = new ByteArrayInputStream(responce.getBytes("UTF-8"));
			Document doc = builder.parse(input);

			NodeList returnNodes = doc.getElementsByTagName("ns:return");
			String returnValue = returnNodes.item(0).getTextContent();

			if (returnValue.equals("true")) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			logger.warn("Error: " + e);
			return false;
		}

	}

}
