package com.actinver.dispersionAlpha.vo;

public class LogProcesoEnvio {

	private String fechaDispercion;
	private Integer archivoProsesados;
	private Integer archivosExito;
	private Integer archivosError;
	private String descripcion;

	public String getFechaDispercion() {
		return fechaDispercion;
	}

	public void setFechaDispercion(String fechaDispercion) {
		this.fechaDispercion = fechaDispercion;
	}

	public Integer getArchivoProsesados() {
		return archivoProsesados;
	}

	public void setArchivoProsesados(Integer archivoProsesados) {
		this.archivoProsesados = archivoProsesados;
	}

	public Integer getArchivosExito() {
		return archivosExito;
	}

	public void setArchivosExito(Integer archivosExito) {
		this.archivosExito = archivosExito;
	}

	public Integer getArchivosError() {
		return archivosError;
	}

	public void setArchivosError(Integer archivosError) {
		this.archivosError = archivosError;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

}
