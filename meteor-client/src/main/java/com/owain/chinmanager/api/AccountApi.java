package com.owain.chinmanager.api;

import com.google.gson.JsonObject;
import com.owain.chinmanager.ChinManagerPlugin;
import static com.owain.chinmanager.ChinManagerPlugin.JSON;
import io.reactivex.rxjava3.core.Observable;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import static net.runelite.http.api.RuneLiteAPI.GSON;

import meteor.config.ConfigManager;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AccountApi
{
	private final ChinManagerPlugin chinManagerPlugin;
	private final ConfigManager configManager;

	@Inject
	AccountApi(ChinManagerPlugin chinManagerPlugin)
	{
		this.chinManagerPlugin = chinManagerPlugin;
		this.configManager = chinManagerPlugin.getConfigManager();
	}

	public Observable<Boolean> login(String username, String password)
	{
		return Observable.defer(() ->
		{
			HttpUrl httpUrl = new HttpUrl.Builder()
				.scheme("https")
				.host("chinplugins.xyz")
				.addPathSegment("api")
				.addPathSegment("user")
				.addPathSegment("signin")
				.build();

			JsonObject json = new JsonObject();
			json.addProperty("email", username);
			json.addProperty("password", password);

			RequestBody body = RequestBody.create(JSON, json.toString());

			Request request = new Request.Builder()
				.addHeader("accept", "application/json")
				.url(httpUrl)
				.post(body)
				.build();

			try (Response response = chinManagerPlugin.getOkHttpClient().newCall(request).execute())
			{
				ResponseBody responseBody = response.body();
				if (responseBody == null)
				{
					return Observable.just(false);
				}

				String responseBodyString = responseBody.string();
				if (responseBodyString.isEmpty() || responseBodyString.contains("message"))
				{
					responseBody.close();
					return Observable.just(false);
				}

				responseBody.close();
				return Observable.just(true);
			}
			catch (Exception ignored)
			{
				return Observable.just(false);
			}
		});
	}

	public Observable<String> checkLogin()
	{
		return Observable.defer(() ->
		{
			String value = configManager.getConfiguration(ChinManagerPlugin.CONFIG_GROUP, "cookies");

			if (value == null || value.equals(""))
			{
				return Observable.just("");
			}

			HttpUrl httpUrl = new HttpUrl.Builder()
				.scheme("https")
				.host("chinplugins.xyz")
				.addPathSegment("api")
				.addPathSegment("user")
				.addPathSegment("check")
				.build();

			RequestBody body = RequestBody.create(JSON, "{}");

			Request request = new Request.Builder()
				.addHeader("accept", "application/json")
				.post(body)
				.url(httpUrl)
				.build();

			try (Response response = chinManagerPlugin.getOkHttpClient().newCall(request).execute())
			{
				ResponseBody responseBody = response.body();
				if (responseBody == null)
				{
					return Observable.just("");
				}

				String responseBodyString = responseBody.string();
				if (responseBodyString.isEmpty())
				{
					responseBody.close();
					return Observable.just("");
				}
				else if (responseBodyString.contains("message"))
				{
					responseBody.close();
					return Observable.just("");
				}

				JsonObject responseBodyJson = GSON.fromJson(responseBodyString, JsonObject.class);

				if (!responseBodyJson.has("token"))
				{
					responseBody.close();
					return Observable.just("");
				}

				String token = responseBodyJson.get("token").getAsString();

				responseBody.close();
				if (token != null && !token.isEmpty())
				{
					return Observable.just(token);
				}
				else
				{
					return Observable.just("");
				}
			}
			catch (Exception e)
			{
				return Observable.just("");
			}
		});
	}
}
