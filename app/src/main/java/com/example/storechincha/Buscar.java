package com.example.storechincha;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class Buscar extends AppCompatActivity {
    private final String URLWS = "http://192.168.1.32/wstienda/app/services/service-productos.php";
    RequestQueue requestQueue;
    EditText edtTalla, edtPrecio,edtIDPrenda;
    Spinner spinnerTipo,spinnerGenero;
    Button btnActualizarPrenda,btnEliminarPrenda, btnBuscarPrenda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_buscar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
         loadUI();
         btnBuscarPrenda.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 buscarPrenda();
             }
         });
         btnEliminarPrenda.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 confirmarEliminacion();
             }
         });


    }

    private void buscarPrenda() {
        //1 canal de comunicacion (app<>ws)
        requestQueue = Volley.newRequestQueue(this);
        //String URLPARAMS = URLWS + "?q=findByI$id="+edtIDPrenda.getText().toString().trim();
        //estructura la cadena de WS con key value
        String URLparams= Uri.parse(URLWS)
                .buildUpon()
                .appendQueryParameter("q","findById")
                .appendQueryParameter("id",edtIDPrenda.getText().toString())
                .build()
                .toString();
        //solicitud y el valor esperado (retorno)
        JsonArrayRequest jsonRequest = new JsonArrayRequest(
                Request.Method.GET,
                URLparams,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Log.d("Respuesta:",response.toString());
                        if(response.length()==0){
                            resetUI();
                            showToast("no existe el producto");
                            edtIDPrenda.requestFocus();
                        }else{
                            try{
                                JSONObject jsonObject = response.getJSONObject(0);

                                edtTalla.setText(jsonObject.getString("talla"));
                                edtPrecio.setText(jsonObject.getString("precio"));
                                spinnerTipo.setSelection(Integer.parseInt(jsonObject.getString("tipo")));
                                spinnerGenero.setSelection(Integer.parseInt(jsonObject.getString("genero")));



                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
    }

    private void showToast(String message) {
    }

    private void resetUI() {

    }

    private void eliminarPrenda(){
        requestQueue = Volley.newRequestQueue(this);
        String URLDELETE= URLWS+"/"+edtIDPrenda.getText().toString().trim();
        Log.d("URL",URLDELETE);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                URLDELETE,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Recibido", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ErrorWService", error.toString());
                    }
                }
        );
        requestQueue.add(jsonRequest);
    }

    private void confirmarEliminacion() {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);

        dialogo.setTitle("storeChincha");
        dialogo.setMessage("¿Seguro de eliminar?");
        dialogo.setCancelable(false);

        dialogo.setNegativeButton("No", null);
        dialogo.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminarPrenda();
            }
        });

        dialogo.create().show();
    }

    private void loadUI() {
        edtIDPrenda=findViewById(R.id.edtIDPrenda);
        spinnerTipo=findViewById(R.id.spinnerTipo);
        spinnerGenero=findViewById(R.id.spinnerGenero);
        edtTalla=findViewById(R.id.edtTalla);
        edtPrecio=findViewById(R.id.edtPrecio);
        btnEliminarPrenda=findViewById(R.id.btnEliminarPrenda);
        btnActualizarPrenda=findViewById(R.id.btnActualizarPrenda);
    }
}