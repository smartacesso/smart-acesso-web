package com.protreino.luxandserver.util;

import java.util.Locale;

import com.protreino.luxandserver.enumeration.OperationalSystem;

public class Utils {
	
	public final static String LUXAND_KEY = "dte7wp+ICiX8FNYiviQNDLgkhuOYb7zd/mu/iBUFbw+BSqz8r7vea0wKQOHrxYe6pxoBOQDxGBNCBahyKvtFq+t3i/32RsOOfHsl/t/PQn4++zz8w4b1fzCd4XQ0kKOqALFSCpNfjb/NPiPCCoSlC1AR9ymK/dksdi5SD06EyH8=";

	public static String getAppDataFolder() {
		return Utils.getOperationalSystem().equals(OperationalSystem.LINUX)
				? System.getProperty("user.home") + "/.appData/Roaming/SmartFaces"
						: System.getenv("ProgramFiles") + "/SmartFaces";
	}
	
	public static OperationalSystem getOperationalSystem(){
		String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
		if ((os.indexOf("mac") >= 0) || (os.indexOf("darwin") >= 0)) 
			return OperationalSystem.MAC_OS;
		else if (os.indexOf("nux") >= 0) 
			return OperationalSystem.LINUX;
		else
			return OperationalSystem.WINDOWS;
		
	}
}
