package br.com.startjob.acesso.to;

public class DeviceTO {

	public String manufacturer;
	public String identifier;
	public String name;
	public String login;
	public String password;
	public String location;
	public String desiredStatus;
	public String defaultDevice;
	public String mirrorDevice;
	
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getDesiredStatus() {
		return desiredStatus;
	}
	public void setDesiredStatus(String desiredStatus) {
		this.desiredStatus = desiredStatus;
	}
	public String getDefaultDevice() {
		return defaultDevice;
	}
	public void setDefaultDevice(String defaultDevice) {
		this.defaultDevice = defaultDevice;
	}
	public String getMirrorDevice() {
		return mirrorDevice;
	}
	public void setMirrorDevice(String mirrorDevice) {
		this.mirrorDevice = mirrorDevice;
	}
	
}
