package br.com.startjob.acesso.modelo.enumeration;

public enum Dedo {
	
	RIGHT_INDEX,
	RIGHT_THUMB,
	RIGHT_MIDDLE,
	RIGHT_RING,
	RIGHT_LITTLE,
	LEFT_INDEX,
	LEFT_THUMB,
	LEFT_MIDDLE,
	LEFT_RING,
	LEFT_LITTLE;
	
	public String toString() {
		
		if(this.equals(RIGHT_INDEX))
			return "enum.dedo.RIGHT_INDEX";
		else if(this.equals(RIGHT_THUMB))
			return "enum.dedo.RIGHT_THUMB";
		else if(this.equals(RIGHT_MIDDLE))
			return "enum.dedo.RIGHT_MIDDLE";
		else if(this.equals(RIGHT_RING))
			return "enum.dedo.RIGHT_RING";
		else if(this.equals(RIGHT_LITTLE))
			return "enum.dedo.RIGHT_LITTLE";
		else if(this.equals(LEFT_INDEX))
			return "enum.dedo.LEFT_INDEX";
		else if(this.equals(LEFT_THUMB))
			return "enum.dedo.LEFT_THUMB";
		else if(this.equals(LEFT_MIDDLE))
			return "enum.dedo.LEFT_MIDDLE";
		else if(this.equals(LEFT_RING))
			return "enum.dedo.LEFT_RING";
		else if(this.equals(LEFT_LITTLE))
			return "enum.dedo.LEFT_LITTLE";
		return "";
	}
	
	public static Dedo valueFromImport(String importValue) {
		if(importValue.equals("Indicador direito")){
			return Dedo.RIGHT_INDEX;
		}else if(importValue.equals("Polegar direito")){
			return Dedo.RIGHT_THUMB;
		}else if(importValue.equals("Médio direito")){
			return Dedo.RIGHT_MIDDLE;
		}else if(importValue.equals("Anelar direito")){
			return Dedo.RIGHT_RING;
		}else if(importValue.equals("Mínimo direito")){
			return Dedo.RIGHT_LITTLE;
		}else if(importValue.equals("Indicador esquerdo")){
			return Dedo.LEFT_INDEX;
		}else if(importValue.equals("Polegar esquerdo")){
			return Dedo.LEFT_THUMB;
		}else if(importValue.equals("Médio esquerdo")){
			return Dedo.LEFT_MIDDLE;
		}else if(importValue.equals("Anelar esquerdo")){
			return Dedo.LEFT_RING;
		}else if(importValue.equals("Mínimo esquerdo")){
			return Dedo.LEFT_LITTLE;
		}
		else
			return null;
	}

}
