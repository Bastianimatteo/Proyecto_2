package dispositivo.componentes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import dispositivo.api.iot.infraestructure.Dispositivo_RegistradorMQTT;
import dispositivo.api.mqtt.Dispositivo_APIMQTT;
import dispositivo.api.rest.Dispositivo_APIREST;
import dispositivo.interfaces.IDispositivo;
import dispositivo.interfaces.IFuncion;

public class Dispositivo implements IDispositivo {

	// inizio codice mio
	protected Boolean habilitado = true;
	// fine codice mio
	
	protected String deviceId = null; // id univoco del dispositivo, assegnato in fase di creazione
	protected Map<String, IFuncion> functions = null; // mappa chd associa l'ID di una funzione a un'istanza di IFuncion
	protected Dispositivo_RegistradorMQTT registrador = null; // oggetto che si occupa di registrare il dispositivo con un sistema MQTT
	protected Dispositivo_APIMQTT apiFuncionesMQTT = null; // interfaccia MQTT per gestire le funzioni del dispositivo
	protected Dispositivo_APIREST apiFuncionesREST = null; // interfaccia REST per gestire le funzioni del dispositivo
	
	
	public static Dispositivo build(String deviceId, String ip, String mqttBrokerURL) { // crea un dispositivo con ID, IP, URL del broker MQTT e inizializza le 3 cose in verde
		Dispositivo dispositivo = new Dispositivo(deviceId);
		dispositivo.registrador = Dispositivo_RegistradorMQTT.build(deviceId, ip, mqttBrokerURL);
		dispositivo.apiFuncionesMQTT = Dispositivo_APIMQTT.build(dispositivo, mqttBrokerURL);
		dispositivo.apiFuncionesREST = Dispositivo_APIREST.build(dispositivo);
		return dispositivo;
	}

	public static Dispositivo build(String deviceId, String ip, int port, String mqttBrokerURL) { // variante che consente di specificare anche la porta per l'API REST
		Dispositivo dispositivo = new Dispositivo(deviceId);
		dispositivo.registrador = Dispositivo_RegistradorMQTT.build(deviceId, ip, mqttBrokerURL);
		dispositivo.apiFuncionesMQTT = Dispositivo_APIMQTT.build(dispositivo, mqttBrokerURL);
		dispositivo.apiFuncionesREST = Dispositivo_APIREST.build(dispositivo, port);
		return dispositivo;
	}

	protected Dispositivo(String deviceId) { //costruttore che inizializza l'ID del dispositivo
		this.deviceId = deviceId;
	}
	
	@Override
	public String getId() { // restituisce l'ID del dispositivo
		return this.deviceId;
	}

	protected Map<String, IFuncion> getFunctions() { // restituisce la mappa delle funzioni del dispositivo
		return this.functions;
	}
	
	protected void setFunctions(Map<String, IFuncion> fs) { // imposta la mappa delle funzioni
		this.functions = fs;
	}
	
	@Override
	public Collection<IFuncion> getFunciones() { // restituisce tutte le funzioni associate al dispositivo come una collezione
		if ( this.getFunctions() == null )
			return null;
		return this.getFunctions().values();
	}
	
	
	@Override
	public IDispositivo addFuncion(IFuncion f) { // aggiunge una funzione al dispositivo. Se null ne crea una nuova. Inserisce la funzione nella mappa, associandola al suo ID f.getId()
		if ( this.getFunctions() == null )
			this.setFunctions(new HashMap<String, IFuncion>());
		this.getFunctions().put(f.getId(), f);
		return this;
	}
	
	
	@Override
	public IFuncion getFuncion(String funcionId) { // recupera una funzione specifica dalla mappa "functions" usando il suo ID
		if ( this.getFunctions() == null )
			return null;
		return this.getFunctions().get(funcionId);
	}
	
		
	@Override
	public IDispositivo iniciar() { // avvia tutte le funzioni associate, registra il dispositivo tramite MQTT, avvia le interfacce MQTT e REST
		for(IFuncion f : this.getFunciones()) {
			f.iniciar();
		}

		this.registrador.registrar();
		this.apiFuncionesMQTT.iniciar();
		this.apiFuncionesREST.iniciar();
		return this;
	}

	@Override
	public IDispositivo detener() { // deregistra il dispositivo da MQTT, ferma le interfacce MQTT e REST, ferma tutte le funzioni associate
		this.registrador.desregistrar();
		this.apiFuncionesMQTT.detener();
		this.apiFuncionesREST.detener();
		for(IFuncion f : this.getFunciones()) {
			f.detener();
		}
		return this;
	}

	//inizio codice mio

	@Override
	public Boolean estaHabilitado(){
		return this.habilitado;
	}

	@Override 
	public IDispositivo setHabilitado(boolean new_habilitado){
		this.habilitado = new_habilitado;

		if(new_habilitado == false){
			for(IFuncion f : this.getFunciones()) {
				f.setHabilitada(false);
			}
		}
		else
		{
			for(IFuncion f : this.getFunciones()) {
				f.setHabilitada(true);
			}
		}
		return this;
	}
	
	// fine codice mio
}
