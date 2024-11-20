package dispositivo.interfaces;

import java.util.Collection;

public interface IDispositivo {

	public String getId();
	
	public IDispositivo iniciar();
	public IDispositivo detener();
	
	public IDispositivo addFuncion(IFuncion f);
	public IFuncion getFuncion(String funcionId);
	public Collection<IFuncion> getFunciones();

	// TO-DO: Ejercicio 5 - Funciones habilitar/deshabilitar

	//inizio codice mio
	
	public Boolean estaHabilitado();
	public IDispositivo setHabilitado(boolean new_habilitado);

	// fine codice mio
}
