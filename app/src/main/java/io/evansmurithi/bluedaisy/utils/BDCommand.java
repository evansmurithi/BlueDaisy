package io.evansmurithi.bluedaisy.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by evans on 3/11/17.
 *
 * BDCommand class.
 */

public class BDCommand {

    private static final String TAG = "BDCommand";

    public static byte[] getCommand(String section, String option) {
        String command;

        JSONObject object = new JSONObject();
        try {
            object.put("section", section);
            object.put("option", option);
            command = object.toString();
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
            command = "{\"section\": \"" + section + "\", \"option\": \"" + option + "\"}";
        }

        return command.getBytes();
    }
}
