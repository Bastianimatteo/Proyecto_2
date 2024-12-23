package dispositivo.iniciador;

import dispositivo.api.mqtt.FunctionPublisher_APIMQTT;
import dispositivo.componentes.Dispositivo;
import dispositivo.componentes.Funcion;
import dispositivo.componentes.DispositivoSlave;
import dispositivo.interfaces.FuncionStatus;
import dispositivo.interfaces.IDispositivo;
import dispositivo.interfaces.IFuncion;

public class DispositivoIniciador {

	public static void main(String[] args) {
		
		if ( args.length < 4 ) { // controlla che il programma sia eseguito con almeno 4 argomenti
			System.out.println("Usage: java -jar dispositivo.jar device deviceIP rest-port mqttBroker");
			System.out.println("Example: java -jar dispositivo.jar ttmi050 ttmi050.iot.upv.es 8182 tcp://ttmi008.iot.upv.es:1883");
			return;
		}

		String deviceId = args[0];
		String deviceIP = args[1];
		String port = args[2];
		String mqttBroker = args[3];

		IDispositivo d;

		if (args.length > 4) {
			// Create device as a salve
			String masterId = args[4];
			d = DispositivoSlave.build(deviceId, deviceIP, Integer.valueOf(port), mqttBroker, masterId);
		} else {
			d = Dispositivo.build(deviceId, deviceIP, Integer.valueOf(port), mqttBroker);
		}

		FunctionPublisher_APIMQTT funcionPublisher = FunctionPublisher_APIMQTT.build(deviceId, deviceIP, mqttBroker); // crea un oggetto "FunctionPublisher_APIMQTT" che configura l'ID del dispositivo, l'IP e l'URL del broker MQTT
		funcionPublisher.connect(); // connette il publisher al broker MQTT

		// Añadimos funciones al dispositivo

		// crea 2 funzioni con stato iniziale OFF e associa ogni funzione al dispositivo con il metodo "addFuncion"
		IFuncion f1 = Funcion.build("f1", FuncionStatus.OFF, funcionPublisher);
		d.addFuncion(f1);
		
		IFuncion f2 = Funcion.build("f2", FuncionStatus.OFF, funcionPublisher);
		d.addFuncion(f2);

		// Ejercicio 1 - Añadir función f3
		IFuncion f3 = Funcion.build("f3", FuncionStatus.BLINK, funcionPublisher);
		d.addFuncion(f3);

		// Arrancamos el dispositivo
		d.iniciar();
	}
}
