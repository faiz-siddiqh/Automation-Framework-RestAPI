package core;

public class OAuth_Utils {

	private String accessToken;
	private String authCode;

	public void setUp() {
		BaseUtils.setUp("OAuth");
	}

	public void init(String methodName) {
		BaseUtils.common.setExtentTest(methodName);

	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

}
