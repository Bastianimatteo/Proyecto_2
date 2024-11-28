package dispositivo.api.mqtt;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.json.JSONException;
import org.json.JSONObject;

import dispositivo.api.iot.infraestructure.Dispositivo_RegistradorMQTT;
import dispositivo.interfaces.Configuracion;
import dispositivo.utils.MySimpleLogger;

/**
 * This class is responsible for publishing the state 
 * of the functions of a device to an MQTT broker.
 */
public class FunctionPublisher_APIMQTT {
    protected MqttClient myClient;
	protected MqttConnectOptions connOpt;

	protected String dispositivoId = null;
	protected String dispositivoIP = null;
	protected String mqttBroker = null;

    private String loggerId = null;

    public static FunctionPublisher_APIMQTT build(String dispositivoId, String dispositivoIP, String mqttBroker) {
		FunctionPublisher_APIMQTT reg = new FunctionPublisher_APIMQTT();
		reg.setDispositivoID(dispositivoId);
		reg.setDispositivoIP(dispositivoIP);
		reg.setBrokerURL(mqttBroker);
		return reg;
	}
	
	protected FunctionPublisher_APIMQTT() {
	}
	
	protected void setDispositivoID(String dispositivoID) {
		this.dispositivoId = dispositivoID;
		this.loggerId = dispositivoID + "-RegisterService";
	}
	
	protected void setDispositivoIP(String dispositivoIP) {
		this.dispositivoIP = dispositivoIP;
	}
	
	protected void setBrokerURL(String brokerURL) {
		this.mqttBroker = brokerURL;
	}

    public void connect() {
		// setup MQTT Client
		String clientID = this.dispositivoId + UUID.randomUUID().toString();
		connOpt = new MqttConnectOptions();
		
		connOpt.setCleanSession(true);
		connOpt.setKeepAliveInterval(30);

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
			
			myClient.connect(connOpt);
		} catch (MqttException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		MySimpleLogger.info(this.loggerId, "FuncionPublisher conectado al broker " + this.mqttBroker);

	}
	
	public void disconnect() {
		// disconnect
		try {
			myClient.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Ejercicio 9 - Implementar notificaciones 'push' sobre funciones
    public void publish(String functionId, JSONObject message) {
		String topic = Configuracion.TOPIC_BASE
					+ "dispositivo/" + this.dispositivoId 
					+ "/funcion/" + functionId + "/info";
		MqttTopic mqttTopic = myClient.getTopic(topic);
		
   		int pubQoS = 0;
		MqttMessage mqttMessage = new MqttMessage(message.toString().getBytes());
    	mqttMessage.setQos(pubQoS);
    	mqttMessage.setRetained(false);

    	// Publish the message
    	MySimpleLogger.debug(this.loggerId, "Publicando en topic \"" + topic + "\" qos " + pubQoS);
    	MqttDeliveryToken token = null;
    	try {
    		// publish message to broker
			token = mqttTopic.publish(mqttMessage);
			MySimpleLogger.debug(this.loggerId, mqttMessage.toString());
	    	// Wait until the message has been delivered to the broker
			token.waitForCompletion();
			Thread.sleep(100);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
