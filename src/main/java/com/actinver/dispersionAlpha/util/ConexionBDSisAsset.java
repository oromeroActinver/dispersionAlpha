package com.actinver.dispersionAlpha.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;

import com.actinver.dispersionAlpha.vo.DatosContrato;
import com.actinver.dispersionAlpha.vo.LogGestorAlpha;
import com.actinver.dispersionAlpha.vo.SoapData;

public class ConexionBDSisAsset {

	private static final String PROPERTIES_FILE = "src/main/resources/properties.properties";
	private static final Logger logger = Logger.getLogger(ConexionBDSisAsset.class.getName());

	public Map<String, String> getPathDMZ() {
		String sql = "SELECT JSON_VALUE([DATOS], '$.dirRutaFileServer') AS dirRutaFileServer, "
				+ "JSON_VALUE([DATOS], '$.dirVirtualFileServer') AS dirVirtualFileServer "
				+ "FROM [SisAsset].[dbo].[DATOS_GESTOR_REPORTES_ALPHA] WHERE CONSTANTE = 'DATOS_TRALIX'";

		Map<String, String> result = new HashMap<>();

		try (Connection conexion = getConnection();
				PreparedStatement ps = conexion.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			if (rs.next()) {
				result.put("dirRutaFileServer", rs.getString("dirRutaFileServer"));
				result.put("dirVirtualFileServer", rs.getString("dirVirtualFileServer"));
			} else {
				System.out.println("No se encontraron resultados para la consulta.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	public Map<String, String> getPathDispercion() {
		String sql = "SELECT JSON_VALUE([DATOS], '$.padtDispercion') AS padtDispercion, "
				+ "JSON_VALUE([DATOS], '$.nameFileAlpha') AS nameFileAlpha, "
				+ "JSON_VALUE([DATOS], '$.extencion') AS extencion "
				+ "FROM [SisAsset].[dbo].[DATOS_GESTOR_REPORTES_ALPHA] " + "WHERE [CONSTANTE] = 'CARPETA_COMPARTIDA'";

		Map<String, String> result = new HashMap<>();

		try (Connection conexion = getConnection();
				PreparedStatement ps = conexion.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			if (rs.next()) {
				result.put("padtDispercion", rs.getString("padtDispercion"));
				result.put("nameFileAlpha", rs.getString("nameFileAlpha"));
				result.put("extencion", rs.getString("extencion"));
			} else {
				System.out.println("No se encontraron resultados para la consulta.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	public Map<String, String> getPathLog() {
		String sql = "SELECT JSON_VALUE([DATOS], '$.padtDispercion') AS padtDispercion, "
				+ "JSON_VALUE([DATOS], '$.fileLogEnv') AS fileLogEnv, " + "JSON_VALUE([DATOS], '$.dirLog') AS dirLog, "
				+ "JSON_VALUE([DATOS], '$.fileLog') AS fileLog, " + "JSON_VALUE([DATOS], '$.extencion') AS extencion "
				+ "FROM [SisAsset].[dbo].[DATOS_GESTOR_REPORTES_ALPHA] " + "WHERE [CONSTANTE] = 'CARPETA_COMPARTIDA'";

		Map<String, String> result = new HashMap<>();

		try (Connection conexion = getConnection();
				PreparedStatement ps = conexion.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			if (rs.next()) {
				result.put("padtDispercion", rs.getString("padtDispercion"));
				result.put("fileLogEnv", rs.getString("fileLogEnv"));
				result.put("dirLog", rs.getString("dirLog"));
				result.put("fileLog", rs.getString("fileLog"));
				result.put("extencion", rs.getString("extencion"));
			} else {
				System.out.println("No se encontraron resultados para la consulta.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	public SoapData getSoapData() {
		String sql = "SELECT " + " JSON_VALUE(DATOS, '$.leagueKey') AS leagueKey, "
				+ " JSON_VALUE(DATOS, '$.passwordKey') AS passwordKey, " + " JSON_VALUE(DATOS, '$.UUID') AS UUID, "
				+ " JSON_VALUE(DATOS, '$.SOAPAction') AS SOAPAction, "
				+ " JSON_VALUE(DATOS, '$.\"Accept-Encoding\"') AS AcceptEncoding, "
				+ " JSON_VALUE(DATOS, '$.Host') AS Host, " + " JSON_VALUE(DATOS, '$.Connection') AS Connection, "
				+ " JSON_VALUE(DATOS, '$.\"User-Agent\"') AS UserAgent, "
				+ " JSON_VALUE(DATOS, '$.soapEndpoint') AS soapEndpoint " + "FROM DATOS_GESTOR_REPORTES_ALPHA "
				+ "WHERE CONSTANTE = 'DATOS_SOAP' " + "AND JSON_VALUE(DATOS, '$.leagueKey') IS NOT NULL";

		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql);
				ResultSet rs = preparedStatement.executeQuery()) {

			if (rs.next()) {
				SoapData soapData = new SoapData();
				soapData.setLeagueKey(rs.getString("leagueKey"));
				soapData.setPasswordKey(rs.getString("passwordKey"));
				soapData.setUUID(rs.getString("UUID"));
				soapData.setSOAPAction(rs.getString("SOAPAction"));
				soapData.setAcceptEncoding(rs.getString("AcceptEncoding"));
				soapData.setHost(rs.getString("Host"));
				soapData.setConnection(rs.getString("Connection"));
				soapData.setUserAgent(rs.getString("UserAgent"));
				soapData.setSoapEndpoint(rs.getString("soapEndpoint"));
				return soapData;
			} else {
				logger.warn("No SOAP data found for CONSTANTE = 'DATOS_SOAP'");
				return null;
			}
		} catch (SQLException e) {
			logger.error("Error executing SQL query " + e);
			return null;
		}
	}

	public DatosContrato getDatosClienteA2k(String contrato) {
		String sql = "SELECT CUENTA, NOMBRE, RFC, EMAIL FROM PRODA2K.S10299EE.PROD.OPNEGO WHERE CUENTA = ? ";

		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, contrato);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return mapRowToArchivoAlphaPDFA2k(rs);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private DatosContrato mapRowToArchivoAlphaPDFA2k(ResultSet rs) {
		DatosContrato archivo = new DatosContrato();
		try {
			archivo.setEmail(rs.getString("EMAIL"));
			archivo.setNameClient(rs.getString("NOMBRE"));
			archivo.setRfcClient(rs.getString("RFC"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return archivo;
	}

	public static Connection getConnection() {
		Properties properties = new Properties();
		try (InputStream input = ConexionBDSisAsset.class.getClassLoader().getResourceAsStream("properties.properties")) {
			
			if (input == null) {
				throw new FileNotFoundException("No se encontró el archivo properties.properties en el classpath");
			}
			properties.load(input);
			String url = properties.getProperty("db.url");
			String user = properties.getProperty("db.user");
			String password = properties.getProperty("db.password");
			String driver = properties.getProperty("db.driver");

			Class.forName(driver);
			return DriverManager.getConnection(url, user, password);
		} catch (IOException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	// Método para insertar una lista de registros (batch insert)
	public boolean insertLogGestorAlphaList(List<LogGestorAlpha> logs) {
		String sql = "INSERT INTO [SisAsset].[dbo].[log_gestor_alpha] "
				+ "(fecha_dispercion, no_contrato, descripcion, nombre_zip, password_zip, status) "
				+ "VALUES (GETDATE(), ?, ?, ?, ?, ?)";

		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			conn.setAutoCommit(false);

			for (LogGestorAlpha log : logs) {
				pstmt.setString(1, log.getNoContrato());
				pstmt.setString(2, log.getDescripcion());
				pstmt.setString(3, log.getNombreZip());
				pstmt.setString(4, log.getPasswordZip());
				pstmt.setString(5, log.getStatus());
				pstmt.addBatch();
			}

			int[] results = pstmt.executeBatch();
			conn.commit();

			for (int result : results) {
				if (result == PreparedStatement.EXECUTE_FAILED) {
					return false;
				}
			}
			return true;

		} catch (SQLException e) {
			System.err.println("Error al insertar lista en log_gestor_alpha: " + e.getMessage());
			return false;
		}
	}

	public Map<String, String> getEmailsInfo() {
		String sql = "SELECT JSON_VALUE([DATOS], '$.destinatarios') AS destinatarios, \r\n"
				+ "JSON_VALUE([DATOS], '$.copias') AS copias, \r\n"
				+ "JSON_VALUE([DATOS], '$.copiasOcultas') AS copiasOcultas\r\n"
				+ "FROM [SisAsset].[dbo].[DATOS_GESTOR_REPORTES_ALPHA] \r\n"
				+ "WHERE [CONSTANTE] = 'INFO_EMAILS_ALPHA'";

		Map<String, String> result = new HashMap<>();

		try (Connection conexion = getConnection();
				PreparedStatement ps = conexion.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			if (rs.next()) {
				result.put("destinatarios", rs.getString("destinatarios"));
				result.put("copias", rs.getString("copias"));
				result.put("copiasOcultas", rs.getString("copiasOcultas"));
			} else {
				System.out.println("No se encontraron resultados para la consulta.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

}
