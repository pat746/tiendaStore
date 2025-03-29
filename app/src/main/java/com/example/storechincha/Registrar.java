package com.example.storechincha;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class Registrar extends AppCompatActivity {
    private final String URLWS = "http://192.168.1.18/wstienda/app/services/service-productos.php";
    RequestQueue requestQueue;

    EditText edtTalla, edtPrecio;
    Spinner spinnerTipo,spinnerGenero;
    Button btnRegistrarRopa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loadUI();

        btnRegistrarRopa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmar();
            }
        });

    }
    /**
     * REGISTRAR UNA NUEVA PRENDA ENVIANDO LOS DATOS EN JSON
     */
    private void registrarDatos() {
        requestQueue = Volley.newRequestQueue(this);
        JSONObject datos= new JSONObject();
        try{
            datos.put("tipo",spinnerTipo.getSelectedItem().toString());
            datos.put("genero",spinnerGenero.getSelectedItem().toString());
            datos.put("precio",edtPrecio.getText().toString());
            datos.put("talla",edtTalla.getText().toString());

        }catch (Exception e){
            Log.e("ErroJSON",e.toString());

        }
        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.POST,
                URLWS,
                datos,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Guardado", response.toString());
                        try{
                            boolean guardado = response.getBoolean("status");
                            if(guardado){
                                showToast("proceso ejecutado correctamente");
                                resetUI();
                                edtTalla.requestFocus();
                            }
                            else{
                                showToast("no se puedo registrar");
                            }
                        }
                        catch(Exception e){
                            Log.e("ErrorWS", e.toString());

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        showToast("Error en la comunicación WS");
                    }
                }

        );
        requestQueue.add(jsonRequest);

    }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    private void resetUI(){
        edtTalla.setText(null);
        edtPrecio.setText(null);
        spinnerTipo.setSelection(0);
        spinnerGenero.setSelection(0);

    }
    private void cargarDatosSpinner() {
        // Lista de tipos de ropa según la tabla
        String[] tipos = {"Falda", "Pantalon", "Camisa", "Polo", "Short"};
        String[] generos = {"F", "M", "U"}; // F = Dama, M = Hombre, U = Unisex

        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tipos);
        ArrayAdapter<String> adapterGenero = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, generos);

        spinnerTipo.setAdapter(adapterTipo);
        spinnerGenero.setAdapter(adapterGenero);
    }




    private void confirmar() {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);

        dialogo.setTitle("STORECHINCHA");
        dialogo.setMessage("¿Registramos Ropa?");
        dialogo.setCancelable(true);

        dialogo.setNegativeButton("No", null);
        dialogo.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Log.d("Aviso", "Guardado correctamente");
                //registrarRopa();    //$_POST envío
                registrarDatos();       //JSON envío
            }
        });

        dialogo.create().show();
    }


    private void loadUI() {
        edtPrecio=findViewById(R.id.edtPrecio);
        edtTalla=findViewById(R.id.edtTalla);
        spinnerGenero=findViewById(R.id.spinnerGenero);
        spinnerTipo=findViewById(R.id.spinnerTipo);
        btnRegistrarRopa=findViewById(R.id.btnRegistrarRopa);
        cargarDatosSpinner();
    }
}