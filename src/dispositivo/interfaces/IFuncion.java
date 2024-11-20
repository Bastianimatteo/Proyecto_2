package dispositivo.interfaces;

import dispositivo.api.mqtt.FunctionPublisher_APIMQTT;

public interface IFuncion {
	
	public String getId();
	
	public IFuncion iniciar();
	public IFuncion detener();
	
	public IFuncion encender();
	public IFuncion apagar();
	public IFuncion parpadear();
	
	public FuncionStatus getStatus();

	// Ejercicio 5 - Funciones habilitar/deshabilitar
	public Boolean estaHabilitada();
	public IFuncion habilita();
	public IFuncion deshabilita();

	// fine codice mio

}
