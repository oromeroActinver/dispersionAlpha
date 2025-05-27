package com.actinver.dispersionAlpha;

import org.apache.log4j.PropertyConfigurator;

import com.actinver.dispersionAlpha.controller.ContratosTxtController;
import com.actinver.dispersionAlpha.controller.EmailController;
import com.actinver.dispersionAlpha.util.ContratoUtil;
import com.actinver.dispersionAlpha.vo.LogProcesoEnvio;

public class App {

	public static void main(String[] args) {
		PropertyConfigurator.configure("C:/Users/oromero/Documents/SissAset/REPORTES/Sisasset-Dispercion/EJECUTABLE/log4j.properties");
		ContratoUtil contratoUtil = new ContratoUtil();

		ContratosTxtController contratosTxtController = new ContratosTxtController();
		EmailController emailController = new EmailController();

		LogProcesoEnvio logDispercion = contratosTxtController.dispersionReportes();
		contratoUtil.escribirLogEnArchivo(logDispercion);
		emailController.sendDispersionReport(logDispercion);

	}
}
