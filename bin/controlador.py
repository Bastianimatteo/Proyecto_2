import random
import sys
import json
import time
import paho.mqtt.client as mqtt


# MQTT Callbacks
def on_connect(client, userdata, flags, rc):
    print("Connected with result code " + str(rc))


def on_disconnect(client, userdata, rc):
    print("Disconnected from broker")


def on_message(client, userdata, msg):
    print(f"Message arrived. Topic: {msg.topic}, Message: {msg.payload.decode()}")


def on_publish(client, userdata, mid):
    print(f"Delivery complete. MID: {mid}")


# Function to start the MQTT client
def start_client(mqtt_broker, port):
    # Initialize the MQTT client
    client_id = f'publish-{random.randint(0, 1000)}'
    client = mqtt.Client()
    client.on_connect = on_connect
    client.on_disconnect = on_disconnect
    client.on_message = on_message
    client.on_publish = on_publish

    # Connect to the MQTT broker
    print(f"Connecting to broker: {mqtt_broker}")
    client.connect(mqtt_broker, port)
    print("Connected to broker")

    # Start the loop in a separate thread
    client.loop_start()

    return client


# Publish a message
def publish_message(client, topic, content):
    try:
        message = json.dumps(content)
        client.publish(topic, message)
        print(f"Message published: {message}. ")
    except Exception as e:
        print(f"Error publishing message: {e}. ")


# Light control functions
def turn_red(client, topic):
    publish_message(client, f"{topic}/f1/comandos", {"accion": "encender"})
    publish_message(client, f"{topic}/f2/comandos", {"accion": "apagar"})
    publish_message(client, f"{topic}/f3/comandos", {"accion": "apagar"})


def turn_yellow(client, topic):
    publish_message(client, f"{topic}/f1/comandos", {"accion": "apagar"})
    publish_message(client, f"{topic}/f2/comandos", {"accion": "encender"})
    publish_message(client, f"{topic}/f3/comandos", {"accion": "apagar"})


def turn_green(client, topic):
    publish_message(client, f"{topic}/f1/comandos", {"accion": "apagar"})
    publish_message(client, f"{topic}/f2/comandos", {"accion": "apagar"})
    publish_message(client, f"{topic}/f3/comandos", {"accion": "encender"})


# Main control loop
def loop_control(client):
    topic_dispositivo1 = "es/upv/inf/muiinf/ina/dispositivo/ttmi050/funcion"
    topic_dispositivo2 = "es/upv/inf/muiinf/ina/dispositivo/ttmi051/funcion"

    # All lights to red initially
    turn_red(client, topic_dispositivo1)
    turn_red(client, topic_dispositivo2)

    while True:
        # Dispositivo 1 cycle
        turn_green(client, topic_dispositivo1)
        time.sleep(2)
        turn_yellow(client, topic_dispositivo1)
        time.sleep(2)
        turn_red(client, topic_dispositivo1)

        # Dispositivo 2 cycle
        turn_green(client, topic_dispositivo2)
        time.sleep(2)
        turn_yellow(client, topic_dispositivo2)
        time.sleep(2)
        turn_red(client, topic_dispositivo2)


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
        loop_control(client)
    except KeyboardInterrupt:
        print("Exiting program")
        client.loop_stop()
        client.disconnect()
