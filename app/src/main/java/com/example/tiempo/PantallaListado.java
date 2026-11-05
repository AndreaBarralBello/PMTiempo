package com.example.tiempo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PantallaListado extends AppCompatActivity {

    List<String> listaProvincias = new ArrayList<>();
    HashMap <String, List <String>> listaProvinciasMunicipios = new HashMap<>();

    HashMap<String, String> codigosProvincia = new HashMap<>();

    //para abrir los municipios necesito el código de cada municipio
    HashMap<String, String> codigosMunicipio = new HashMap<>();


    ExpandableListView selector;
    ExpandableAdaptardor adaptador;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_listado);



            selector = findViewById(R.id.selector);

            adaptador = new ExpandableAdaptardor(this, listaProvincias, listaProvinciasMunicipios);

            selector.setAdapter(adaptador);

            adaptador.notifyDataSetChanged();


        // 1. Cola de Volley
        RequestQueue queue = Volley.newRequestQueue(this);

        // 2. Petición de provincias
        String url = "https://api.el-tiempo.net/json/v3/provincias";

        JsonObjectRequest peticion = new JsonObjectRequest(
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // La respuesta es { "provincias": [ {...}, {...} ] }
                            JSONArray arrayProvincias = response.getJSONArray("provincias");

                            for (int i = 0; i < arrayProvincias.length(); i++) {
                                JSONObject provincia = arrayProvincias.getJSONObject(i);
                                String nombreProv = provincia.getString("NOMBRE_PROVINCIA");
                                String codProv    = provincia.getString("CODPROV");

                                // Añadimos el grupo
                                listaProvincias.add(nombreProv);

                                // Metemos lista vacía en el HashMap (importante)
                                listaProvinciasMunicipios.put(nombreProv, new ArrayList<>());

                                // Guardamos el código para pedir municipios
                                codigosProvincia.put(nombreProv, codProv);
                            }

                            // 3. Creamos el adaptador AL FINAL, como el profesor
                            adaptador = new ExpandableAdaptardor(
                                    PantallaListado.this,
                                    listaProvincias,
                                    listaProvinciasMunicipios);
                            selector.setAdapter(adaptador);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(PantallaListado.this,
                                    "Error al leer provincias", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PantallaListado.this,
                                "Error de red al cargar provincias", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        queue.add(peticion);

        // 4. Al expandir una provincia → pedir sus municipios
        selector.setOnGroupExpandListener(groupPosition -> {
            String nombreProv = listaProvincias.get(groupPosition);
            if (listaProvinciasMunicipios.get(nombreProv).isEmpty()) {
                cargarMunicipios(codigosProvincia.get(nombreProv), nombreProv);
            }


            //Al pulsar un municipio se abre su informacon en una pantalla nueva
            selector.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
                String nombreProvincia = listaProvincias.get(groupPosition);
                String nombreMunicipio  = listaProvinciasMunicipios.get(nombreProv).get(childPosition);
                String codigoMunicipio     = codigosMunicipio.get(nombreMunicipio);

                Intent intent = new Intent(PantallaListado.this, PantallaMunicipio.class);
                intent.putExtra("CODIGOINE", codigoMunicipio);
                intent.putExtra("NOMBRE_MUNICIPIO", nombreMunicipio);
                startActivity(intent);

                return true;   // 👈 IMPORTANTE: true para que el clic se procese
            });
        });
    }

    // ==========================================================
    // Municipios de una provincia
    // ==========================================================
    private void cargarMunicipios(String codProv, String nombreProv) {
        String url = "https://api.el-tiempo.net/json/v3/provincias/" + codProv + "/municipios";

        JsonObjectRequest peticion = new JsonObjectRequest(
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray arrayMunicipios = response.getJSONArray("municipios");
                            List<String> municipios = new ArrayList<>();

                            for (int i = 0; i < arrayMunicipios.length(); i++) {
                                JSONObject municipio = arrayMunicipios.getJSONObject(i);
                                municipios.add(municipio.getString("NOMBRE"));
                                //Tengo que guardar el código del municipio para poder
                                //abrirlo, pq es lo que pide la urla
                                String nombreMunicipio = municipio.getString("NOMBRE");
                                String codigoMunicipio = municipio.getString("CODIGOINE");

                                municipios.add(nombreMunicipio);
                                municipios.add(codigoMunicipio);
                            }

                            listaProvinciasMunicipios.put(nombreProv, municipios);
                            adaptador.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PantallaListado.this,
                                "Error al cargar municipios", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        Volley.newRequestQueue(this).add(peticion);
    }
}