package com.actinver.dispersionAlpha;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import com.actinver.dispersionAlpha.controller.ContratosTxtController;
import com.actinver.dispersionAlpha.controller.EmailController;
import com.actinver.dispersionAlpha.util.ContratoUtil;
import com.actinver.dispersionAlpha.vo.LogProcesoEnvio;
import com.itextpdf.text.DocumentException;

public class App {
	
	private static final Logger logger = Logger.getLogger(App.class.getName());

	public static void main(String[] args) throws DocumentException, ClassNotFoundException {
		PropertyConfigurator.configure("C:/Users/oromero/Documents/SissAset/REPORTES/Sisasset-Dispercion/EJECUTABLE/log4j.properties");
		
		logger.info("\n*******************"
				+ "Inicia Dispercion de Reportes para contratos ALPHA"
				+ "*******************\n");
		
		ContratoUtil contratoUtil = new ContratoUtil();

		ContratosTxtController contratosTxtController = new ContratosTxtController();
		EmailController emailController = new EmailController();

		LogProcesoEnvio logDispercion = contratosTxtController.dispersionReportes();
		contratoUtil.escribirLogEnArchivo(logDispercion);
		emailController.sendDispersionReport(logDispercion);

	}
}
