package com.example.storechincha;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.storechincha.adaptadores.ProductosAdapter;
import com.example.storechincha.entidades.Productos;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Listar extends AppCompatActivity {
    private final String URLWS = "http://192.168.1.32/wstienda/app/services/service-productos.php";
    RequestQueue requestQueue;
    ListView lsRopa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_listar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loadUI();
        obtenerRegistro();

    }

    private void obtenerRegistro() {
        //PASO N°1 : CANAL DE COMUNICACION
        requestQueue = Volley. newRequestQueue(this);
        //URL ALTERNATIVA(INDICAR UNA OPERACION ESPECIFICA)
        final String URL_LISTA=URLWS+"?q=showAll";

        //paso 2 solicitud (enviar>recibir)
        //devuelve un coleccion en JSON
        JsonArrayRequest jsonRequest= new JsonArrayRequest(
                Request.Method.GET,
                URL_LISTA,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        procesarDatos(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ErrorWS", error.toString());
                    }
                }
        );
        requestQueue.add(jsonRequest);

    }

    private void procesarDatos(JSONArray response) {
        //Log.d("Respuesta", response.toString());
        //Crearemos un algoritmo que divida el JSONARRAY => JSONOBJECT
        try {
            Productos productos; //Reutilizar
            ArrayList<Productos> listaProductos = new ArrayList<>();


            for (int i = 0; i < response.length(); i++){
                JSONObject jsonObject = response.getJSONObject(i);

                productos = new Productos();
                productos.setId(jsonObject.getInt("id"));
                productos.setTipo(jsonObject.getString("tipo"));
                productos.setGenero(jsonObject.getString("genero"));
                productos.setPrecio(jsonObject.getDouble("precio"));
                productos.setTalla(jsonObject.getString("talla"));

                listaProductos.add(productos);
            }

            //ArrayAdapter adaptador =  new ArrayAdapter(this, android.R.layout.simple_list_item_1, listaAlumnos);
            ProductosAdapter adaptador = new ProductosAdapter(this, listaProductos);
            lsRopa.setAdapter(adaptador);
        }
        catch (Exception e){
            Log.e("ErrorResponse", e.toString());
        }
    }

    private void loadUI() {
        lsRopa=findViewById(R.id.lsRopa);
    }
}