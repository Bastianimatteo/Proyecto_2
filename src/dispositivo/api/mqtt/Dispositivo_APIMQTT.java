package dispositivo.api.mqtt;

import java.io.IOException;
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Status;

import dispositivo.interfaces.Configuracion;
import dispositivo.interfaces.IDispositivo;
import dispositivo.interfaces.IFuncion;
import dispositivo.utils.MySimpleLogger;

public class Dispositivo_APIMQTT implements MqttCallback {

	protected MqttClient myClient;
	protected MqttConnectOptions connOpt;
	protected String clientId = null;
	
	protected IDispositivo dispositivo;
	protected String mqttBroker = null;
	protected String mqttUser = null;
	protected String mqttPassword = null;
	
	private String loggerId = null;
	
	public static Dispositivo_APIMQTT build(IDispositivo dispositivo, String brokerURL) { // associa l'istanza a un dispositivo e configura il broker
		Dispositivo_APIMQTT api = new Dispositivo_APIMQTT(dispositivo);
		api.setBroker(brokerURL);
		return api;
	}
	
	protected Dispositivo_APIMQTT(IDispositivo dev) { // costruttore base, utilizza l'id del dispositivo per log e identificazione MQTT
		this.dispositivo = dev;
		this.loggerId = dev.getId() + "-apiMQTT";
	}
	
	protected void setBroker(String mqttBrokerURL) {
		this.mqttBroker = mqttBrokerURL;
	}
	
	protected void setBrokerCredentials(String user, String password) {
		this.mqttUser = user;
		this.mqttPassword = password;
	}
	
	@Override
	public void connectionLost(Throwable t) { // gestisce la perdita di connessione
		MySimpleLogger.debug(this.loggerId, "Connection lost!");
		// code to reconnect to the broker would go here if desired
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) { // notifica la consegna di un messaggio
		//System.out.println("Pub complete" + new String(token.getMessage().getPayload()));
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception { // metodo che processa i messaggi ricevuti, decodifica il topic per identificare la funzione del dispositivo coinvolta, interpreta il messaggio come un comando (encender, apagar, parpadear) ed esegue l'azione corrispondente
		
		String payload = new String(message.getPayload());
		
		System.out.println("-------------------------------------------------");
		System.out.println("| Topic:" + topic);
		System.out.println("| Message: " + payload);
		System.out.println("-------------------------------------------------");
		
	
		// DO SOME MAGIC HERE!
		
		//
		// Obtenemos el id de la función
		//   Los topics están organizados de la siguiente manera:
		//         $topic_base/dispositivo/funcion/$ID-FUNCION/commamnd
		//   Donde el $topic_base es parametrizable al arrancar el dispositivo
		//   y la $ID-FUNCION es el identificador de la dunción
		
		String[] topicNiveles = topic.split("/");
		String funcionId = topicNiveles[topicNiveles.length-2];
		
		IFuncion f = this.dispositivo.getFuncion(funcionId);
		if ( f == null ) {
			MySimpleLogger.warn(this.loggerId, "No encontrada funcion " + funcionId);
			return;
		}
		
		//
		// Definimos una API con mensajes de acciones básicos
		//

		// Ejercicio 7 - Codificar mensajes en JSON
		JSONObject jsonPayload = new JSONObject(message.getPayload());
		String accion = jsonPayload.getString("accion");
		
		switch (accion) {
			/**
			case "encender":
				f.encender();
				break;
			case "apagar":
				f.apagar();
				break;
			case "parpadear":
				f.parpadear();
				break;		*/
			default:
				MySimpleLogger.warn(this.loggerId, "Acción '" + payload + "' no reconocida. Sólo admitidas: encender, apagar o parpadear");
				break;
		}
		
		// TO-DO: Ejercicio 8 - Extender API para habilitar/deshabilitar dispositivo (y sus funciones)

	}

	/**
	 * 
	 * runClient
	 * The main functionality of this simple example.
	 * Create a MQTT client, connect to broker, pub/sub, disconnect.
	 * 
	 */
	public void connect() { //configura e stabilisce la connessione al broker MQTT
		// setup MQTT Client
		String clientID = this.dispositivo.getId() + UUID.randomUUID().toString() + ".subscriber";
		connOpt = new MqttConnectOptions();
		
		connOpt.setCleanSession(true);
		connOpt.setKeepAliveInterval(30);
		if ( this.mqttUser != null ) {
			connOpt.setUserName(this.mqttUser);
			connOpt.setPassword(this.mqttPassword.toCharArray());
		}
		
		// Connect to Broker
		try {
			MqttDefaultFilePersistence persistence = null;
			try {
				persistence = new MqttDefaultFilePersistence("/tmp");
			} catch (Exception e) {
			}
			if ( persistence != null )
				myClient = new MqttClient(this.mqttBroker, clientID, persistence);
			else
				myClient = new MqttClient(this.mqttBroker, clientID);


			myClient.setCallback(this);
			myClient.connect(connOpt);
		} catch (MqttException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		MySimpleLogger.info(this.loggerId, "Conectado al broker " + this.mqttBroker);

	}
	
	
	public void disconnect() { // disconnette il client dal broker
		
		// disconnect
		try {
			// wait to ensure subscribed messages are delivered
			Thread.sleep(10000);

			myClient.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}

		
	}

	
	protected void subscribe(String myTopic) { // sottoscrive il client ad un determinato topic
		
		// subscribe to topic
		try {
			int subQoS = 0;
			myClient.subscribe(myTopic, subQoS);
			MySimpleLogger.info(this.loggerId, "Suscrito al topic " + myTopic);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	

	protected void unsubscribe(String myTopic) { // annulla la sottoscrizione del client ad un determinato topic
		
		// unsubscribe to topic
		try {
			int subQoS = 0;
			myClient.unsubscribe(myTopic);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void iniciar() { // avvia il client MQTT e sottoscrive i topic associati alle funzioni del dispositivo

		if ( this.myClient == null || !this.myClient.isConnected() )
			this.connect();
		
		if ( this.dispositivo == null )
			return;
		
		for(IFuncion f : this.dispositivo.getFunciones())
			this.subscribe(this.calculateCommandTopic(f)); // calculateCommandTopic(f) è un metodo per calcolare i topic dei comandi per ciascuna funzione

		// inizio codice mio: sottoscrizione al topic dispositivo/{ID}/comandos
		String dispositivoComandosTopic = Configuracion.TOPIC_BASE + "dispositivo/" + dispositivo.getId() + "/comandos";
		this.subscribe(dispositivoComandosTopic);

		// fine codice mio
	}
	
	
	
	public void detener() {
		
		
		// To-Do
		
	}
	
	
	protected String calculateCommandTopic(IFuncion f) {
		return Configuracion.TOPIC_BASE + "dispositivo/" + dispositivo.getId() + "/funcion/" + f.getId() + "/comandos";
	}
	
	protected String calculateInfoTopic(IFuncion f) {
		return Configuracion.TOPIC_BASE + "dispositivo/" + dispositivo.getId() + "/funcion/" + f.getId() + "/info";
	}
	

}
