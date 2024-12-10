package net.oldschoolminecraft.poseidon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class ObjectLogger
{
    private static final String LOG_FILE = "object_log.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final int MAX_ARRAY_SIZE = 10; // Configure the maximum number of elements to display

    // Function to log object fields, their values, and the class name to a JSON file
    public static void logObject(Object obj, String... flags)
    {
        logObjectOverride(LOG_FILE, obj, flags);
    }

    public static void logObjectOverride(String LOG_FILE, Object obj, String... flags)
    {
        Class<?> objClass = obj.getClass();
        JsonObject jsonObject = new JsonObject();

        // Add the class name to the JSON object
        jsonObject.addProperty("className", objClass.getName());

        if (flags != null && flags.length > 0)
        {
            JsonArray flagsArray = new JsonArray();
            for (String flag : flags) flagsArray.add(flag);
            jsonObject.add("flags", flagsArray);
        }

        // Iterate over all fields in the object and its superclass hierarchy
        while (objClass != null)
        {
            for (Field field : objClass.getDeclaredFields())
            {
                // Skip static fields
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                field.setAccessible(true); // Make private fields accessible
                try
                {
                    Object value = field.get(obj); // Get the value of the field
                    if (value != null) {
                        // Special handling for arrays
                        if (value.getClass().isArray()) {
                            String arrayString = arrayToString(value, MAX_ARRAY_SIZE);
                            jsonObject.addProperty(field.getName(), arrayString);
                        } else {
                            jsonObject.addProperty(field.getName(), value.toString());
                        }
                    } else {
                        jsonObject.addProperty(field.getName(), "null");
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            objClass = objClass.getSuperclass(); // Move to superclass
        }

        // Convert JsonObject to JSON string using Gson
        String jsonString = gson.toJson(jsonObject);

        if (LOG_FILE.equalsIgnoreCase("stdout"))
        {
            System.out.println(jsonString);
            return;
        }

        // Append the JSON string to the data log file
        try (FileWriter fileWriter = new FileWriter(LOG_FILE, true); PrintWriter printWriter = new PrintWriter(fileWriter))
        {
            printWriter.println(jsonString); // Add JSON object to the log file
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Converts an array to a string, truncating it if it exceeds the maxSize.
     * Appends an ellipsis and the count of omitted items.
     *
     * @param array   The array to convert.
     * @param maxSize The maximum number of elements to display.
     * @return A string representation of the array with possible truncation.
     */
    private static String arrayToString(Object array, int maxSize)
    {
        int length = Array.getLength(array);
        if (length <= maxSize) {
            // For object arrays, use deepToString; for primitives, use appropriate Arrays.toString
            if (array instanceof Object[]) {
                return Arrays.deepToString((Object[]) array);
            } else if (array instanceof byte[]) {
                return Arrays.toString((byte[]) array);
            } else if (array instanceof int[]) {
                return Arrays.toString((int[]) array);
            } else if (array instanceof long[]) {
                return Arrays.toString((long[]) array);
            } else if (array instanceof double[]) {
                return Arrays.toString((double[]) array);
            } else if (array instanceof float[]) {
                return Arrays.toString((float[]) array);
            } else if (array instanceof short[]) {
                return Arrays.toString((short[]) array);
            } else if (array instanceof boolean[]) {
                return Arrays.toString((boolean[]) array);
            } else if (array instanceof char[]) {
                return Arrays.toString((char[]) array);
            } else {
                return "Unsupported array type";
            }
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("[");

            for (int i = 0; i < maxSize; i++) {
                Object element = Array.get(array, i);
                if (element != null && element.getClass().isArray()) {
                    // Recursively handle nested arrays
                    sb.append(arrayToString(element, maxSize));
                } else {
                    sb.append(element);
                }
                if (i < maxSize - 1) {
                    sb.append(", ");
                }
            }

            int omitted = length - maxSize;
            sb.append(", ... ").append(omitted).append(" more]");
            return sb.toString();
        }
    }
}
