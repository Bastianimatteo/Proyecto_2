import sys
import json
import paho.mqtt.client as mqtt
from urllib.parse import urlparse


# Ejercicio 13 - Script de gestion de dispositivos
def on_connect(client, userdata, flags, rc):
    print("Connected with result code " + str(rc))
    # Subscribe to the topic
    client.subscribe("es/upv/inf/muiinf/ina/gestion/dispositivos")
    print("Subscribed to topic: es/upv/inf/muiinf/ina/gestion/dispositivos")

def on_message(client, userdata, msg):
    try:
        # Parse JSON payload
        payload = json.loads(msg.payload.decode())
        
        print(payload.get("dispositivo") + " is " + payload.get("status"))
    except json.JSONDecodeError as e:
        print(f"Error decoding JSON: {e}, Raw message: {msg.payload.decode()}")

def on_disconnect(client, userdata, rc):
    print("Disconnected from broker")


# Function to start the MQTT client
def start_client(mqtt_broker, port):
    # Initialize the MQTT client
    client = mqtt.Client()
    client.on_connect = on_connect
    client.on_disconnect = on_disconnect
    client.on_message = on_message

    # Connect to the MQTT broker
    print(f"Connecting to broker: {mqtt_broker}:{port}")
    client.connect(mqtt_broker, port)
    print("Connected to broker")

    return client


# Main function
if __name__ == "__main__":
    if len(sys.argv) < 3:
        print("Usage: python controlador.py mqttBroker")
        print("Example: python controlador.py tcp://ttmi008.iot.upv.es:1883")
        sys.exit(1)

    mqtt_broker = sys.argv[1]
    port = int(sys.argv[2])

    # Start the MQTT client
    client = start_client(mqtt_broker, port)

    try:
        # Start the control loop
        client.loop_forever()
    except KeyboardInterrupt:
        print("Exiting program")
        client.disconnect()
