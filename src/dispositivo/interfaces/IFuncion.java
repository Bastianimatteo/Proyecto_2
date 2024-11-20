package dispositivo.interfaces;

public interface IFuncion {
	
	public String getId();
	
	public IFuncion iniciar();
	public IFuncion detener();
	
	public IFuncion encender();
	public IFuncion apagar();
	public IFuncion parpadear();
	
	public FuncionStatus getStatus();

	// TO-DO: Ejercicio 5 - Funciones habilitar/deshabilitar

	// inizio codice mio

	public Boolean estaHabilitada();
	public IFuncion setHabilitada(boolean new_habilitada);

	// fine codice mio

}
