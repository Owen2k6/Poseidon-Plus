package com.legacyminecraft.poseidon.uuid;

import com.legacyminecraft.poseidon.PoseidonConfig;
import com.projectposeidon.johnymuffin.LoginProcessHandler;
import com.projectposeidon.johnymuffin.UUIDManager;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class AsyncUUIDLookup extends Thread
{
    private static final String URL = PoseidonConfig.getInstance().getString("settings.fetch-uuids-from", "https://api.mojang.com/users/profiles/minecraft");
    private static final String METHOD = PoseidonConfig.getInstance().getBoolean("settings.use-get-for-uuids.enabled", true) ? "GET" : "POST";

    private final String username;
    private final LoginProcessHandler loginProcessHandler;

    public AsyncUUIDLookup(String username, LoginProcessHandler loginProcessHandler)
    {
        this.username = username;
        this.loginProcessHandler = loginProcessHandler;
    }

    public void run()
    {
        RemoteJSONResponse apiRes = null;

        try
        {
            apiRes = readRemoteJSON(URL);
        } catch (Exception ignored) {}

        boolean success = (apiRes != null && apiRes.getResponseCode() == 200 && apiRes.getResponseObject() != null);
        UUID uuid = success ? getWithDashes(String.valueOf(apiRes.getResponseObject().get("id"))) : UUIDManager.generateOfflineUUID(username);
        loginProcessHandler.userUUIDReceived(uuid, success);
    }

    private UUID getWithDashes(String uuid)
    {
        return UUID.fromString(uuid.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
    }

    private static String readAll(Reader rd) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) sb.append((char) cp);
        return sb.toString();
    }

    private static RemoteJSONResponse readRemoteJSON(String url) throws IOException, ParseException
    {
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(METHOD);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:121.0) Gecko/20100101 Firefox/121.0");
        connection.setRequestProperty("Accept", "application/json");

        int responseCode = connection.getResponseCode();

        if (responseCode != 200) return new RemoteJSONResponse(responseCode);

        try (InputStream is = connection.getInputStream())
        {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            JSONParser jsp = new JSONParser();
            return new RemoteJSONResponse(responseCode, (JSONObject) jsp.parse(jsonText));
        }
    }

    public static class RemoteJSONResponse
    {
        private final JSONObject resObj;
        private final int responseCode;

        public RemoteJSONResponse(int responseCode)
        {
            this(responseCode, null);
        }

        public RemoteJSONResponse(int responseCode, JSONObject resObj)
        {
            this.responseCode = responseCode;
            this.resObj = resObj;
        }

        public int getResponseCode()
        {
            return responseCode;
        }

        public JSONObject getResponseObject()
        {
            return resObj;
        }
    }
}
