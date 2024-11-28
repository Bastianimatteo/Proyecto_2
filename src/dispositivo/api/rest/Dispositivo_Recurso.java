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
				jsonResult.put("habilitado", dispositivo.estaHabilitado());
			}

		} catch (JSONException e) {
		}
		
		return jsonResult;
	}
	
	public IDispositivo getDispositivo() {
		return this.getDispositivo_RESTApplication().getDispositivo();
	}

    @Get
    public Representation get() { // gestisce le richieste HTTP

    	// Obtenemos el dispositivo
		IDispositivo d = this.getDispositivo();

		// Construimos el mensaje de respuesta
		// Ejercicio 2 - Codificar Mensaje respuesta API
		// Hint : en esta clase se ha definido un método estático serialize que puede ser útil
    	JSONObject resultJSON = new JSONObject();

		resultJSON = Dispositivo_Recurso.serialize(d);
    	
		// Si todo va bien, devolvemos el resultado calculado
    	this.setStatus(Status.SUCCESS_OK);
        return new StringRepresentation(resultJSON.toString(), MediaType.APPLICATION_JSON);
    }
    
	@Put
	public Representation put(Representation entity) {

    	// Obtenemos la función indicada como parámetro en la Ruta

		IDispositivo d = this.getDispositivo();
		if ( d == null ) {
			return generateResponseWithErrorCode(Status.CLIENT_ERROR_NOT_FOUND);
		}

		// Dispositivo encontrado
		JSONObject payload;
		String accion;
		try {
			payload = new JSONObject(entity.getText());	
			accion = payload.getString("accion");		
		} catch (JSONException | IOException e) {
			return this.generateResponseWithErrorCode(Status.CLIENT_ERROR_BAD_REQUEST);
		}

		// Ejercicio 5 - Implementar funciones habilitar/deshabilitar
		switch (accion) {
			case "habilitar":
				d.habilita();
				break;
			case "deshabilitar":
				d.deshabilita();
				break;
			default:
				MySimpleLogger.warn("Dispositivo-Recurso", 
				"Acción '" + payload + "' no reconocida. Sólo admitidas: habilitar o deshabilitar");
				return this.generateResponseWithErrorCode(Status.CLIENT_ERROR_BAD_REQUEST);
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
