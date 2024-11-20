package dispositivo.api.rest;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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

public class Funcion_Recurso extends Recurso {
	
	
	public static final String ID = "FUNCION-ID";
	public static final String RUTA = Dispositivo_Recurso.RUTA + "/funcion/{" + Funcion_Recurso.ID + "}";

	public static JSONObject serialize(IFuncion f) {
		JSONObject jsonResult = new JSONObject();
		try {
			jsonResult.put("id", f.getId());
			jsonResult.put("estado", f.getStatus());
		} catch (JSONException e) {
		}
		return jsonResult;
		
	}
	
	protected IFuncion getFuncion() {
		IDispositivo dispositivo = this.getDispositivo_RESTApplication().getDispositivo();
		String funcionId = getAttribute(Funcion_Recurso.ID);
		return dispositivo.getFuncion(funcionId);
	}

	
    @Get
    public Representation get() {
    	
    	// Obtenemos la función indicada como parámetro en la Ruta
		IFuncion f = this.getFuncion();
		if ( f == null ) {
			return this.generateResponseWithErrorCode(Status.CLIENT_ERROR_NOT_FOUND);
		}
		
		// Construimos el mensaje de respuesta
		// TO-DO: Ejercicio 3 - Codificar Mensaje estado de la función
		// Hint : en esta clase se ha definido un método estático serialize que puede ser útil
    	JSONObject resultJSON = new JSONObject();

		// inizio codice mio

		try {
			resultJSON.put("id", f.getId());
			resultJSON.put("estado", f.getStatus());
		}
		catch(JSONException e){
			// Logga l'errore e restituisce un errore 500
			MySimpleLogger.error("Funcion_Recurso", "Error generando el JSON: " + e.getMessage());
			return generateResponseWithErrorCode(Status.SERVER_ERROR_INTERNAL);
		}

		// fine codice mio
		
		// Si todo va bien, devolvemos el resultado calculado
    	this.setStatus(Status.SUCCESS_OK);
        return new StringRepresentation(resultJSON.toString(), MediaType.APPLICATION_JSON);
    }
    
    
    
	@Put
	public Representation put(Representation entity) {

    	// Obtenemos la función indicada como parámetro en la Ruta

		IFuncion f = this.getFuncion();
		if ( f == null ) {
			return this.generateResponseWithErrorCode(Status.CLIENT_ERROR_NOT_FOUND);
		}

		// Función encontrada
		// TO-DO: Ejercicio 5 - Implementar funciones habilitar/deshabilitar

		// inizio codice mio

		// Dispositivo encontrado
		JSONObject payload = null;
		try { // legge il corpo della richiesta e recupera il campo accion
			payload = new JSONObject(entity.getText());
			String action = payload.getString("accion");
			
			if ( action.equalsIgnoreCase("<accion-permitida>") )
				// d.accion());
				;
				
		// TO-DO: Ejercicio 5 - Implementar funciones habilitar/deshabilitar

			// inizio codice mio

			if (action.equalsIgnoreCase("habilitar")) {
				f.setHabilitada(true);
			} 
			else if (action.equalsIgnoreCase("deshabilitar")) {
				f.setHabilitada(false);
			}
			else {
			MySimpleLogger.warn("Dispositivo-Recurso", "Acción '" + payload + "' no reconocida. Sólo admitidas: habilitar o deshabilitar");
			this.generateResponseWithErrorCode(Status.CLIENT_ERROR_BAD_REQUEST);
			}
			
		} catch (JSONException | IOException e) {
			this.generateResponseWithErrorCode(Status.CLIENT_ERROR_BAD_REQUEST);
		}

		//fine codice mio

		// Construimos el mensaje de respuesta

    	JSONObject resultJSON = Funcion_Recurso.serialize(f);
    	
    	this.setStatus(Status.SUCCESS_OK);
        return new StringRepresentation(resultJSON.toString(), MediaType.APPLICATION_JSON);

	}
    
    
    
	@Options
	public void describe() {
		Set<Method> meths = new HashSet<Method>();
		meths.add(Method.GET);
		meths.add(Method.PUT);
		meths.add(Method.OPTIONS);
		this.getResponse().setAllowedMethods(meths);
	}	
	
	
	

    
}
