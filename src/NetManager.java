import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sun.net.ssl.internal.ssl.Provider;

public class NetManager {
	Logger logger = Logger.getLogger(NetManager.class);
	private static final String FAILED_TO_PARSE_JSON = "Failed to parse json";
	private static final String BUILDING_STR = "BUILDING";

	public String getStatus(String jenkinsUrl) {
		String jenkinsStream;
		JSONObject buildStatusJson = null;
		InputStream in = null;
		try {
			try {
				doTrustToCertificates();
			} catch (KeyManagementException e1) {
				e1.printStackTrace();
			} catch (NoSuchAlgorithmException e1) {
				logger.debug(e1);
			}

			URL url = new URL(jenkinsUrl);
			URLConnection urlConnection = url.openConnection();

			String encoded = SettingsManager.getInstance().getParam("encoded");
			if (encoded != null) {
				urlConnection.setRequestProperty("Authorization", "Basic " + encoded);
			}

			in = urlConnection.getInputStream();
			jenkinsStream = IOUtils.toString(in);
			try {
				JSONParser json = new JSONParser();
				buildStatusJson = (JSONObject) json.parse(jenkinsStream);
			} catch (ParseException e) {
				UICustomManager.setStatus(FAILED_TO_PARSE_JSON);
				logger.debug(e);
			}
		} catch (MalformedURLException ex) {
			UICustomManager.setStatus("Url " + jenkinsUrl + " probably wrong");
			logger.debug(ex);
		} catch (IOException ex) {
			UICustomManager.setStatus("Failed to connect to  " + jenkinsUrl);
			logger.debug(ex);
		} finally {
			if (in != null) {
				IOUtils.closeQuietly(in);
			}
		}
		
		if (buildStatusJson == null) {
			throw new RuntimeException("Failed ti parse status");
		}
		
		
		Object result = buildStatusJson.get("result");
		if (result == null) {
			result = new String(BUILDING_STR);
		}
		return result.toString();
	}

	public void doTrustToCertificates() throws NoSuchAlgorithmException,
			KeyManagementException {
		Security.addProvider(new Provider());
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkServerTrusted(X509Certificate[] certs,
					String authType) throws CertificateException {
				return;
			}

			public void checkClientTrusted(X509Certificate[] certs,
					String authType) throws CertificateException {
				return;
			}
		} };

		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		HostnameVerifier hv = new HostnameVerifier() {
			@Override
			public boolean verify(String urlHostName, SSLSession session) {
				if (!urlHostName.equalsIgnoreCase(session.getPeerHost())) {
					logger.info("Warning: URL host '" + urlHostName
							+ "' is different to SSLSession host '"
							+ session.getPeerHost() + "'.");
				}
				return true;
			}
		};
		HttpsURLConnection.setDefaultHostnameVerifier(hv);
	}
}
