package dispositivo.interfaces;

import java.util.Collection;

public interface IDispositivo {

	public String getId();
	
	public IDispositivo iniciar();
	public IDispositivo detener();
	
	public IDispositivo addFuncion(IFuncion f);
	public IFuncion getFuncion(String funcionId);
	public Collection<IFuncion> getFunciones();

	// Ejercicio 5 - Funciones habilitar/deshabilitar
	public Boolean estaHabilitado();
	public IDispositivo habilita();
	public IDispositivo deshabilita();

	// Returns the master ID of the device, throws an exception if the device is not a slave
	public String getMasterId() throws Exception;
	public Boolean isSlave();
}
