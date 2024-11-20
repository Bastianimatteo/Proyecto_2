package dispositivo.componentes;

import dispositivo.interfaces.FuncionStatus;
import dispositivo.interfaces.IFuncion;
import dispositivo.utils.MySimpleLogger;

public class Funcion implements IFuncion {

	// inizio codice mio
	protected Boolean habilitada = true;
	// fine codice mio
	
	protected String id = null;
	protected FuncionStatus initialStatus = null; // stato iniziale quando viene avviata con iniciar()
	protected FuncionStatus status = null; //stato attuale della funzione
	
	private String loggerId = null;
	
	public static Funcion build(String id) { // crea una funzione con stato iniziale OFF
		return new Funcion(id, FuncionStatus.OFF);
	}
	
	public static Funcion build(String id, FuncionStatus initialStatus) { // crea una funzione con uno stato iniziale specificato
		return new Funcion(id, initialStatus);
	}

	protected Funcion(String id, FuncionStatus initialStatus) { // costruttore, inizializza ID, stato iniziale, ID per il logger
		this.id = id;
		this.initialStatus = initialStatus;
		this.loggerId = "Funcion " + id;
	}
	
	
	@Override
	public String getId() { // restiuisce l'ID della funzione
		return this.id;
	}
		
	@Override
	public IFuncion encender() { // accende la funzione, impostando lo stato su ON verificando prima se la funzione è abilitata e registra il log
		if ( !this.estaHabilitada() ) {
			MySimpleLogger.warn(this.loggerId, "Funcion deshabilitada, no se puede modificar");
			return this;
		}
		MySimpleLogger.info(this.loggerId, "==> Encender");
		this.setStatus(FuncionStatus.ON);
		// TO-DO: Ejercicio 9 - Publicar estado de Funcion (publishStatusChange())
		return this;
	}

	@Override
	public IFuncion apagar() { // spegne la funzione impostando lo stato su OFF veriricando prima se la funzione è abilitata
		if ( !this.estaHabilitada() ) {
			MySimpleLogger.warn(this.loggerId, "Funcion deshabilitada, no se puede modificar");
			return this;
		}
		MySimpleLogger.info(this.loggerId, "==> Apagar");
		this.setStatus(FuncionStatus.OFF);
		// TO-DO: Ejercicio 9 - Publicar estado de Funcion (publishStatusChange())
		return this;
	}

	@Override
	public IFuncion parpadear() { // imposta la funzione in modalità BLINK verificando prima se è abilitata
		if ( !this.estaHabilitada() ) {
			MySimpleLogger.warn(this.loggerId, "Funcion deshabilitada, no se puede modificar");
			return this;
		}
		MySimpleLogger.info(this.loggerId, "==> Parpadear");
		this.setStatus(FuncionStatus.BLINK);
		// TO-DO: Ejercicio 9 - Publicar estado de Funcion (publishStatusChange())
		return this;
	}
	
	protected IFuncion _putIntoInitialStatus() { // imposta lo stato della funzione sul valore di "initialStatus"
		switch (this.initialStatus) {
		case ON:
			this.encender();
			break;
		case OFF:
			this.apagar();
			break;
		case BLINK:
			this.parpadear();
			break;

		default:
			break;
		}
		
		return this;

	}

	@Override
	public FuncionStatus getStatus() { // restituisce lo stato attuale della funzione
		return this.status;
	}
	
	protected IFuncion setStatus(FuncionStatus status) { // imposta il valore di status con il nuovo stato e restiuisce l'istanza corrente della funzione
		this.status = status;
		return this;
	}
	
	@Override
	public IFuncion iniciar() { // avvia la funzione, impostandola nello stato "initialStatus" tramite la funzione "putIntoInitiaStatus" che è descritta sopra
		this._putIntoInitialStatus();
		return this;
	}
	
	@Override
	public IFuncion detener() { // ferma la funzione senza eseguire lcuna operazione
		return this;
	}
	
	// inizio codice mio

	@Override
	public Boolean estaHabilitada() {
		//return true; // TO-DO: Ejercicio 4

		return this.habilitada;
	}

	@Override
	public IFuncion habilita(){
		this.habilitada = true;
		return this;
	}

	@Override
	public IFuncion deshabilita(){
		this.habilitada = false;
		return this;
	}

	// fine codice mio


	protected void publishStatusChange() { // notifica push quando lo stato della funziona cambia
		
		// TO-DO: Ejercicio 9 - Implementar notificaciones 'push'

	}
}
