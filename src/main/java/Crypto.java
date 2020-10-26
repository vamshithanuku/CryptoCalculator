import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.net.HttpURLConnection;

public class Crypto {
    public static Map<String,Integer> getCurrencyNamesFromFile() throws FileNotFoundException {
        Map<String, Integer> cryptoMap = new HashMap();
        File myObj = new File("src/main/crypto.txt");
        Scanner myReader = new Scanner(myObj);
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            System.out.println(data);
            int spot = data.indexOf('-');
            String value = data.substring(spot + 1, data.length());
            cryptoMap.put(data.substring(0, spot), Integer.parseInt(value.trim()));
        }
        myReader.close();
        System.out.println(cryptoMap);
        return cryptoMap;
    }

    public static void writeTotalAssetsToAFile(Map<String, Double> map) {
        try {
            FileWriter myWriter = new FileWriter("totalAssets.txt");
            System.out.println("Writing total Assets to a new txt file");
            myWriter.write("Current Value of your Crypto in EURO:");
            for(String name: map.keySet())
            {
                myWriter.write(System.getProperty( "line.separator" ));
                myWriter.write(name + ": " + map.get(name));
            }
            myWriter.close();
            System.out.println("Successfully wrote total Assets to a new txt file");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Map<String, Integer> cryptoMap = getCurrencyNamesFromFile();
        String currency = "USD";
        double price;
        double priceInEuro = 0.00;
        Map<String, Double> totalAssets = new HashMap<String, Double>();
        for(String name : cryptoMap.keySet()) {
            OkHttpClient client = new OkHttpClient();
            URL url = new URL("https://rest.coinapi.io/v1/exchangerate/" + name.trim() + "/" + currency);
            System.out.println("Url : " + url);
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("X-CoinAPI-Key", "3EC4D016-2D0B-4887-B20D-C80583767C80")
                    .build();
            Call call = client.newCall(request);
            Response response = call.execute();
                int responseCode = response.code();
                if (responseCode == HttpURLConnection.HTTP_OK) { // success
                    String jsonData = response.body().string();
                    JSONObject jsonobject = new JSONObject(jsonData);
                    price = Double.parseDouble(jsonobject.get("rate").toString());
                    priceInEuro = price * 0.85;
                    System.out.println("Price : " + price);
                    System.out.println("Price in Euro : " + priceInEuro);
                }
            totalAssets.put(name, priceInEuro * cryptoMap.get(name));
        }
        System.out.println("Total Assets : " + totalAssets);
        writeTotalAssetsToAFile(totalAssets);
    }
}