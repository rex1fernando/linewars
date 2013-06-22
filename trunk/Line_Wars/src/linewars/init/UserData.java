package linewars.init;

public class UserData extends PlayerData {
	private String ipAddress;
	private boolean observer;
	
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public boolean isObserver() {
		return observer;
	}
	public void setObserver(boolean observer) {
		this.observer = observer;
	}
}
