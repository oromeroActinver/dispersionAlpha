package com.actinver.dispersionAlpha.vo;

public class SoapData {

	private String leagueKey;
	private String passwordKey;
	private String UUID;
	private String SOAPAction;
	private String AcceptEncoding;
	private String Host;
	private String Connection;
	private String UserAgent;
	private String soapEndpoint;

	public String getLeagueKey() {
		return leagueKey;
	}

	public void setLeagueKey(String leagueKey) {
		this.leagueKey = leagueKey;
	}

	public String getPasswordKey() {
		return passwordKey;
	}

	public void setPasswordKey(String passwordKey) {
		this.passwordKey = passwordKey;
	}

	public String getUUID() {
		return UUID;
	}

	public void setUUID(String uUID) {
		UUID = uUID;
	}

	public String getSOAPAction() {
		return SOAPAction;
	}

	public void setSOAPAction(String sOAPAction) {
		SOAPAction = sOAPAction;
	}

	public String getAcceptEncoding() {
		return AcceptEncoding;
	}

	public void setAcceptEncoding(String acceptEncoding) {
		AcceptEncoding = acceptEncoding;
	}

	public String getHost() {
		return Host;
	}

	public void setHost(String host) {
		Host = host;
	}

	public String getConnection() {
		return Connection;
	}

	public void setConnection(String connection) {
		Connection = connection;
	}

	public String getUserAgent() {
		return UserAgent;
	}

	public void setUserAgent(String userAgent) {
		UserAgent = userAgent;
	}

	public String getSoapEndpoint() {
		return soapEndpoint;
	}

	public void setSoapEndpoint(String soapEndpoint) {
		this.soapEndpoint = soapEndpoint;
	}

}
