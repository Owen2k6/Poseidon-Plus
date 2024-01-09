package com.legacyminecraft.poseidon.uuid;

import com.legacyminecraft.poseidon.PoseidonConfig;
import com.projectposeidon.johnymuffin.LoginProcessHandler;
import com.projectposeidon.johnymuffin.UUIDManager;
import org.bukkit.ChatColor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class AsyncUUIDLookup extends Thread
{
    private static final String URL = PoseidonConfig.getInstance().getString("settings.fetch-uuids-from", "https://api.mojang.com/users/profiles/minecraft");
    private static final String METHOD = "GET"; // ignore config, always use GET because it's better anyway
    private static final boolean GRACEFUL = PoseidonConfig.getInstance().getBoolean("settings.allow-graceful-uuids", true);

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

        try {
            apiRes = readRemoteJSON(URL + "/" + encode(username));
        } catch (FileNotFoundException ignored) {
            // file not found means we got a 404, and the user does not exist
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            System.out.println("[Poseidon Plus] The Mojang profiles API appears to be malfunctioning.");
        }

        boolean success = (apiRes != null && apiRes.getResponseCode() == 200 && apiRes.getResponseObject() != null);
        UUID uuid = success ? getWithDashes(String.valueOf(apiRes.getResponseObject().get("id"))) : UUIDManager.getInstance().getUUIDGraceful(username);
        if (!GRACEFUL)
        {
            System.out.println(username + " does not have a Mojang UUID. They have been kicked as graceful UUIDs is not enabled.");
            loginProcessHandler.cancelLoginProcess(ChatColor.RED + "Sorry, we only support premium accounts.");
            return;
        }
        System.out.println("[Poseidon Plus] User logged in with UUID: " + uuid + " (" + (success ? "ONLINE" : "OFFLINE") + ")");
        loginProcessHandler.userUUIDReceived(uuid, success);
    }

    // muffin man code
    private UUID getWithDashes(String uuid)
    {
        return UUID.fromString(uuid.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
    }

    // muffin man code
    private static String readAll(Reader rd) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) sb.append((char) cp);
        return sb.toString();
    }

    // modified muffin man code
    private static RemoteJSONResponse readRemoteJSON(String url) throws Exception
    {
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(METHOD);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:121.0) Gecko/20100101 Firefox/121.0");

        int responseCode = connection.getResponseCode();
        try (InputStream is = connection.getInputStream())
        {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            JSONParser jsp = new JSONParser();
            return new RemoteJSONResponse(responseCode, (JSONObject) jsp.parse(jsonText));
        }
    }

    // muffin man code
    private String encode(String string) {
        try {
            return URLEncoder.encode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return string;
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
