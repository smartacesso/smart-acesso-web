package br.com.startjob.acesso.tasks;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

public class ActivatedTasks {

	private static ActivatedTasks instancia;
	public Map<String, Timer> timers;
	
	public static ActivatedTasks getInstancia() {
		if(instancia == null) 
			instancia = new ActivatedTasks();
		return instancia;
	}
	
	private ActivatedTasks(){
		timers = new HashMap<String, Timer>();
	}
	
	public void limpaTimers() {
		for (String key : timers.keySet())
			timers.get(key).cancel();
	}
	
	public void limpaTimersSOC() {
		for (String key : timers.keySet()) {
			if(key.contains("SOC")) {
				timers.get(key).cancel();
			}
		}
	}
	
	public void limpaTimersSOC(String idClinete) {
		for (String key : timers.keySet())
			if(key.contains("SOC") && key.contains("cliente"+idClinete))
				timers.get(key).cancel();
	}
}
