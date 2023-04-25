package helio.providers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.JsonObject;

import helio.blueprints.DataProvider;
import io.reactivex.rxjava3.core.FlowableEmitter;

public class SystemProvider implements DataProvider{

	private JsonObject object = new JsonObject();
	
	public SystemProvider() {
		super();
	}
	
	@Override
	public void configure(JsonObject configuration) {
		if(configuration.get("OS").getAsBoolean()) {
			JsonObject os = new JsonObject();
			os.addProperty("Name", System.getProperty("os.name"));
			os.addProperty("Version", System.getProperty("os.version"));
			os.addProperty("Arch", System.getProperty("os.arch"));
			object.add("os", os);
		}
		if(configuration.get("IP").getAsBoolean()){
			String urlString = "http://checkip.amazonaws.com/";
			try {
				URL url = new URL(urlString);
				try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
					JsonObject ip = new JsonObject();
					ip.addProperty("Ip", br.readLine());
					object.add("Network", ip);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		if(configuration.isJsonNull() && !(configuration.get("IP").getAsBoolean() && configuration.get("OS").getAsBoolean())) {
			throw new IllegalArgumentException("Put at least in true one parameter");
		}
	}
	
	@Override
	public void subscribe(FlowableEmitter<String> emitter) throws Throwable {
		try {
			emitter.onNext(object.toString());
			emitter.onComplete();
		}catch(Exception e) {
			emitter.onError(e);
		}
	}

}
