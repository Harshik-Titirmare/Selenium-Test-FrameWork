package com.orangehrm.dataprovider;

import java.io.FileReader;
import java.io.IOException;

import org.testng.annotations.DataProvider;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JSONDataReader {

    @DataProvider(name = "getLoginData", parallel = true)
    public Object[][] getLoginData() throws IOException {

        // Define path to the JSON file
        String filePath = System.getProperty("user.dir") + "/src/test/resources/testdata/loginData.json";
        JsonArray jsonArray = JsonParser.parseReader(new FileReader(filePath)).getAsJsonArray();

        // Create simple 2D matrix array to hold the row data values
        Object[][] data = new Object[jsonArray.size()][4];

        // Extract values one by one from JSON keys and put them into simple index fields
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();

            data[i][0] = jsonObject.get("scenario").getAsString();
            data[i][1] = jsonObject.get("username").getAsString();
            data[i][2] = jsonObject.get("password").getAsString();
            data[i][3] = jsonObject.get("expectedError").getAsString();
        }
        return data;
    }
}
