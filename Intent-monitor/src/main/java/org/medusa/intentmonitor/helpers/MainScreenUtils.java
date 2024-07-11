package org.medusa.intentmonitor.helpers;

import org.json.JSONException;
import org.json.JSONObject;

public class MainScreenUtils {

    public static String beautifyBundleString(String bundleString) {
        StringBuilder beautified = new StringBuilder();

        try {
            // Remove "Bundle[" from the beginning and "]" from the end
            String cleanedString = bundleString.substring(7, bundleString.length() - 1);

            // Split by ", " to get key-value pairs
            String[] pairs = cleanedString.split(", ");

            // Indentation level
            int indentLevel = 1;

            // Start building the beautified string
            beautified.append("Bundle[\n");

            // Iterate through each pair
            for (String pair : pairs) {
                // Indent based on current level
                beautified.append(indent(indentLevel)).append(pair).append(",\n");

                // Check for nested Bundles and increase indentation
                if (pair.contains("Bundle[")) {
                    indentLevel++;
                }
            }

            // Remove the last comma and newline
            beautified.setLength(beautified.length() - 2);

            // Add closing bracket
            beautified.append("\n");

            // Add closing square bracket
            beautified.append(indent(indentLevel - 1)).append("]");

        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace(); // Handle or log the exception as needed
        }

        return beautified.toString();
    }

    // Helper method to generate spaces for indentation
    private static String indent(int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level * 4; i++) { // 4 spaces per indentation level
            sb.append(" ");
        }
        return sb.toString();
    }

    public static String removeExtraNoise(String bundleString) {
        String keyToRemove = "marked_as_dumped";
        String cleanedString = bundleString.replaceAll("\\b" + keyToRemove + "=\\d+,?\\s*", "");
        cleanedString = cleanedString.replaceAll(",?\\s*\\]", "]");
        return cleanedString;
    }

}
