package com.actinver.dispersionAlpha;

import org.apache.log4j.PropertyConfigurator;

import com.actinver.dispersionAlpha.controller.ContratosTxtController;
import com.actinver.dispersionAlpha.controller.EmailController;
import com.actinver.dispersionAlpha.util.ContratoUtil;
import com.actinver.dispersionAlpha.vo.LogProcesoEnvio;

public class App {

	public static void main(String[] args) {
		PropertyConfigurator.configure("src/main/resources/log4j.properties");
		ContratoUtil contratoUtil = new ContratoUtil();

		ContratosTxtController contratosTxtController = new ContratosTxtController();
		EmailController emailController = new EmailController();

		LogProcesoEnvio logDispercion = contratosTxtController.dispersionReportes();
		contratoUtil.escribirLogEnArchivo(logDispercion);
		emailController.sendDispersionReport(logDispercion);

	}
}
