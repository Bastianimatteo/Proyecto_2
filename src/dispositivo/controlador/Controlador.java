package dispositivo.controlador;

import org.eclipse.paho.client.mqttv3.*;

public class Controlador implements MqttCallback {

    private MqttClient client;

    public static void main(String[] args) {
        if (args.length < 1) { // Check for the MQTT broker argument
            System.out.println("Usage: java -jar controlador.jar mqttBroker");
            System.out.println("Example: java -jar controlador.jar tcp://ttmi008.iot.upv.es:1883");
            return;
        }

        String mqttBroker = args[0];
        Controlador controlador = new Controlador();
        controlador.start(mqttBroker);

        // Publish a message to the topic "test"
        controlador.publishMessage("test", "Hello, MQTT!");
    }

    public void start(String mqttBroker) {
        try {
            // Create a unique client ID
            String clientId = "Controlador-" + MqttClient.generateClientId();

            // Initialize the MQTT client
            client = new MqttClient(mqttBroker, clientId);
            client.setCallback(this);

            // Set connection options
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            // Connect to the MQTT broker
            System.out.println("Connecting to broker: " + mqttBroker);
            client.connect(connOpts);
            System.out.println("Connected to broker");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void publishMessage(String topic, String content) {
        try {
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(0); // Set QoS (0, 1, or 2)
            client.publish(topic, message);
            System.out.println("Message published: " + content);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("Connection lost! Cause: " + cause.getMessage());
        // Reconnection logic could go here
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        System.out.println("Message arrived. Topic: " + topic + ", Message: " + new String(message.getPayload()));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        try {
            System.out.println("Delivery complete. Token: " + token.getMessage());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
