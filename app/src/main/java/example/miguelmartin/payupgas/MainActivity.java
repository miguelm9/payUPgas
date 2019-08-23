package example.miguelmartin.payupgas;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;


//App made by Miguel Martín 7/24/2019 //mm/dd/yyyy



public class MainActivity extends AppCompatActivity {


    private TextView textAutonomia;
    private TextView textPasajeros;
    private TextView textKm;
    private TextView textPagar;
    private Button btnCalcular;
    private TextView textError;

    private double sumaGas = 0;
    private int counter = 0;

    RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mQueue = Volley.newRequestQueue(this);
        textAutonomia = findViewById(R.id.autonomiaTedit);
        textPagar = findViewById(R.id.euros);
        textKm = findViewById(R.id.numKmTedit);
        textPasajeros = findViewById(R.id.pasajerosTedit);
        textError = findViewById(R.id.textError);
        textError.setText("");
        btnCalcular = findViewById(R.id.btnCalcular);

        textPagar.setText("");

        btnCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calcular();
            }
        });
    }

    private void calcular(){
        Double autonomia = Double.parseDouble(textAutonomia.getText().toString());
        int pasajeros = Integer.parseInt(textPasajeros.getText().toString());
        Double kilometers = Double.parseDouble(textKm.getText().toString());
        Double precioGas = getGas();


        while (Double.isNaN(precioGas)){
            precioGas = getGas();
        }
        if (precioGas == 0){
            textError.setText("PULSA OTRA VEZ");
        }
        else {
            double litrosTotales = (autonomia * kilometers) / 100;
            double litrosPasajero = litrosTotales / pasajeros;

            double euros = litrosPasajero * precioGas;

            textPagar.setText(String.valueOf(euros));
        }
    }

    private double getGas(){


        String url = "https://www.mapabase.es/arcgis/rest/services/Otros/Gasolineras/FeatureServer/0/query?where=1%3D1&outFields=*&outSR=4326&f=json";

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray mFeatures = response.getJSONArray("features");
                            for(int i=0;i< 15; i++){
                                JSONObject attr = mFeatures.getJSONObject(i).getJSONObject("attributes");
                                Double gasolina95 =attr.getDouble("precio_gasóleo_a");
                                System.out.println("souting: PRECIO GASOLINA " + gasolina95);
                                if (gasolina95 == null){
                                    System.out.println("souting: GAS NULL");
                                }
                                else {
                                    sumaGas += gasolina95;
                                    counter++;
                                }


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            System.out.println(e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String mediaGasS = decimalFormat.format(sumaGas/counter);
        Double mediaGas = Double.parseDouble(mediaGasS);

        if (Double.isNaN(mediaGas)){
            return 0;
        }
        else {
            return mediaGas;
        }
    }


}
