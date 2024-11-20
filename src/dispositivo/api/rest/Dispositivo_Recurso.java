package dispositivo.api.rest;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Options;
import org.restlet.resource.Put;

import dispositivo.interfaces.IDispositivo;
import dispositivo.interfaces.IFuncion;
import dispositivo.utils.MySimpleLogger;

public class Dispositivo_Recurso extends Recurso {
	
	public static final String RUTA = "/dispositivo"; // endpoint/percorso base

	public static JSONObject serialize(IDispositivo dispositivo) { // converte un oggetto IDispositivo in una rappresentazione JSON. Serve per restituire i dettagli del dispositivo in formato JSON, utile per le richieste REST
		JSONObject jsonResult = new JSONObject();
		
		try {
			jsonResult.put("id", dispositivo.getId());
			if ( dispositivo.getFunciones() != null ) {
				JSONArray arrayFunciones = new JSONArray();
				for(IFuncion f : dispositivo.getFunciones()) {
					arrayFunciones.put(Funcion_Recurso.serialize(f));
				}

				jsonResult.put("funciones", arrayFunciones);
			}

		} catch (JSONException e) {
		}
		
		return jsonResult;
	}
	
	public IDispositivo getDispositivo() { // recupera un'istanza di IDispositivo dalla classe principale dell'applicazione Recurso.java
		return this.getDispositivo_RESTApplication().getDispositivo();
	}

    @Get
    public Representation get() { // gestisce le richieste HTTP

    	// Obtenemos el dispositivo
		IDispositivo d = this.getDispositivo();

		// Construimos el mensaje de respuesta
		// TO-DO: Ejercicio 2 - Codificar Mensaje respuesta API
		// Hint : en esta clase se ha definido un método estático serialize que puede ser útil
    	JSONObject resultJSON = new JSONObject();

		//inizio codice mio

		try {
			// Aggiunge l'ID del dispositivo
			resultJSON.put("id", d.getId());
			
			// Aggiunge lo stato di abilitazione (assumendo che 'getHabilitado()' restituisca true/false)
			resultJSON.put("habilitado", d.estaHabilitado());
			
			// Aggiunge le funzioni e i loro stati
			JSONArray funcionesArray = new JSONArray();
			for (IFuncion funcion : d.getFunciones()) {
				JSONObject funcionJSON = new JSONObject();
				funcionJSON.put("id", funcion.getId());
				funcionJSON.put("estado", funcion.getStatus().name()); // Stato come stringa (ON, OFF, BLINK)
				funcionesArray.put(funcionJSON);
			}
			resultJSON.put("funciones", funcionesArray);
	
		} catch (JSONException e) {
			// Logga l'errore e restituisce un errore 500
			MySimpleLogger.error("Dispositivo_Recurso", "Error generando el JSON: " + e.getMessage());
			return generateResponseWithErrorCode(Status.SERVER_ERROR_INTERNAL);
		}
	
		//fine codice mio
    	
		// Si todo va bien, devolvemos el resultado calculado
    	this.setStatus(Status.SUCCESS_OK);
        return new StringRepresentation(resultJSON.toString(), MediaType.APPLICATION_JSON);
    }
    
	@Put
	public Representation put(Representation entity) { // gestisce le richieste PUT

    	// Obtenemos la función indicada como parámetro en la Ruta

		IDispositivo d = this.getDispositivo(); // ottiene istanza del dispositivo corrente
		if ( d == null ) {
			return generateResponseWithErrorCode(Status.CLIENT_ERROR_NOT_FOUND);
		}

		// Dispositivo encontrado
		JSONObject payload = null;
		try { // legge il corpo della richiesta e recupera il campo accion
			payload = new JSONObject(entity.getText());
			String action = payload.getString("accion");
			
			if ( action.equalsIgnoreCase("<accion-permitida>") )
				// d.accion());
				;
				
			// Ejercicio 5 - Implementar funciones habilitar/deshabilitar
			if (action.equalsIgnoreCase("habilitar")) {
				d.habilita();
			} else if (action.equalsIgnoreCase("deshabilitar")) {
				d.deshabilita();
			} else {
				MySimpleLogger.warn("Dispositivo-Recurso", "Acción '" + payload + "' no reconocida. Sólo admitidas: habilitar o deshabilitar");
				this.generateResponseWithErrorCode(Status.CLIENT_ERROR_BAD_REQUEST);
			}
			
		} catch (JSONException | IOException e) {
			this.generateResponseWithErrorCode(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		
		// Construimos el mensaje de respuesta

		JSONObject resultJSON = Dispositivo_Recurso.serialize(d);
    	
    	this.setStatus(Status.SUCCESS_OK);
        return new StringRepresentation(resultJSON.toString(), MediaType.APPLICATION_JSON);

	}
    
    
    
    
	@Options
	public void describe() {
		Set<Method> meths = new HashSet<Method>();
		meths.add(Method.GET);
		meths.add(Method.OPTIONS);
		this.getResponse().setAllowedMethods(meths);
	}	

}
