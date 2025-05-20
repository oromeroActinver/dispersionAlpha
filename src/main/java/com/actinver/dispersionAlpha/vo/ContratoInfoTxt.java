package com.actinver.dispersionAlpha.vo;

public class ContratoInfoTxt {

	private String contrato;
	private String nombreArchivo;
	private String correo;
	private String fecha;

	public String getContrato() {
		return contrato;
	}

	public void setContrato(String contrato) {
		this.contrato = contrato;
	}

	public String getNombreArchivo() {
		return nombreArchivo;
	}

	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public ContratoInfoTxt(String contrato, String nombreArchivo, String correo, String fecha) {
		this.contrato = contrato;
		this.nombreArchivo = nombreArchivo;
		this.correo = correo;
		this.fecha = fecha;
	}

	@Override
	public String toString() {
		return "Contrato: " + contrato + ", Archivo: " + nombreArchivo + ", Correo: " + correo + ", Fecha: " + fecha;
	}

}
