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
	public static final String RUTA = Dispositivo_Recurso.RUTA + "/funcion/{" + Funcion_Recurso.ID + "}"; // endpoint

	public static JSONObject serialize(IFuncion f) {
		JSONObject jsonResult = new JSONObject();
		try {
			jsonResult.put("id", f.getId());
			jsonResult.put("estado", f.getStatus());
			jsonResult.put("habilitada", f.estaHabilitada());
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
		// Ejercicio 3 - Codificar Mensaje estado de la función
    	JSONObject resultJSON = new JSONObject();
		resultJSON = Funcion_Recurso.serialize(f);
		
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

		JSONObject payload;
		String accion;
		try { // legge il corpo della richiesta e recupera il campo accion
			payload = new JSONObject(entity.getText());	
			accion = payload.getString("accion");		
		} catch (JSONException | IOException e) {
			return this.generateResponseWithErrorCode(Status.CLIENT_ERROR_BAD_REQUEST);
		}

		// Función encontrada
		// Ejercicio 5 - Implementar funciones habilitar/deshabilitar
		switch (accion) {
			case "encender":
				f.encender();
				break;
			case "apagar":
				f.apagar();
				break;
			case "parpadear":
				f.parpadear();
				break;
			default:
				MySimpleLogger.warn("Dispositivo-Recurso", "Acción '" + payload + "' no reconocida. Sólo admitidas: habilitar o deshabilitar");
				return this.generateResponseWithErrorCode(Status.CLIENT_ERROR_BAD_REQUEST);
		}

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
