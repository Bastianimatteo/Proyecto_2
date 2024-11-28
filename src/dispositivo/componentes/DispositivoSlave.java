package dispositivo.componentes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import dispositivo.api.iot.infraestructure.Dispositivo_RegistradorMQTT;
import dispositivo.api.mqtt.Dispositivo_APIMQTT;
import dispositivo.api.rest.Dispositivo_APIREST;
import dispositivo.interfaces.IDispositivo;
import dispositivo.interfaces.IFuncion;

// Ejercicio 11 - Implementar un controlador maestro-esclavos
public class DispositivoSlave implements IDispositivo {
	
	protected String deviceId = null;
	protected Map<String, IFuncion> functions = null;
	protected Dispositivo_RegistradorMQTT registrador = null;
	protected Dispositivo_APIMQTT apiFuncionesMQTT = null;
	protected Dispositivo_APIREST apiFuncionesREST = null;

	protected Boolean habilitado = true;

	protected boolean slave = true;
	protected String masterId = null;
	
	
	public static DispositivoSlave build(String deviceId, String ip, String mqttBrokerURL, String masterId) { // crea un dispositivo con ID, IP, URL del broker MQTT e inizializza le 3 cose in verde
		DispositivoSlave dispositivo = new DispositivoSlave(deviceId);
		dispositivo.registrador = Dispositivo_RegistradorMQTT.build(deviceId, ip, mqttBrokerURL);
		dispositivo.apiFuncionesMQTT = Dispositivo_APIMQTT.build(dispositivo, mqttBrokerURL);
		dispositivo.apiFuncionesREST = Dispositivo_APIREST.build(dispositivo);
        dispositivo.masterId = masterId;
		return dispositivo;
	}

	// It would have been better to create a separate class for a slave device since the behavior is different, but I will keep it this way for simplicity since is just a small project
	public static DispositivoSlave build(String deviceId, String ip, int port, String mqttBrokerURL, String masterId) { // variante che consente di specificare il master da copiare
		DispositivoSlave dispositivo = new DispositivoSlave(deviceId);
		dispositivo.registrador = Dispositivo_RegistradorMQTT.build(deviceId, ip, mqttBrokerURL);
		dispositivo.apiFuncionesMQTT = Dispositivo_APIMQTT.build(dispositivo, mqttBrokerURL);
		dispositivo.apiFuncionesREST = Dispositivo_APIREST.build(dispositivo, port);
		dispositivo.masterId = masterId;
		return dispositivo;
	}

	protected DispositivoSlave(String deviceId) { //costruttore che inizializza l'ID del dispositivo
		this.deviceId = deviceId;
	}
	
	@Override
	public String getId() { // restituisce l'ID del dispositivo
		return this.deviceId;
	}

	@Override
	public String getMasterId() throws Exception { // restituisce l'ID del master del dispositivo
		if ( !this.slave )
			throw new Exception("This device is not a slave");
		return this.masterId;
	}

	@Override
	public Boolean isSlave() { // restituisce true se il dispositivo è uno slave
		return this.slave;
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
	public IDispositivo iniciar() { // avvia tutte le funzioni del dispositivo, registra il dispositivo tramite MQTT, avvia le interfacce MQTT e REST
		for(IFuncion f : this.getFunciones()) {
			f.iniciar();
		}

		this.registrador.registrar();
		// If the device is a slave it will subscribe to the master's f1/info topic, else it will start as a normal device
		this.apiFuncionesMQTT.iniciar(); // avvia il client MQTT
		this.apiFuncionesREST.iniciar(); // avvia il server REST
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

	// Ejercicio 4
	@Override
	public Boolean estaHabilitado(){
		return this.habilitado;
	}

	@Override 
	public IDispositivo habilita(){
		this.habilitado = true;
		// Activate all functions
		for(IFuncion f : this.getFunciones()) {
			f.habilita();
		}
		return this;
	}

	@Override
	public IDispositivo deshabilita(){
		this.habilitado = false;
		// Deactivate all functions
		for(IFuncion f : this.getFunciones()) {
			f.deshabilita();
		}
		return this;
	}
}
