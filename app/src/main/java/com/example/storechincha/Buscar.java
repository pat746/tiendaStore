package com.example.storechincha;

import android.content.DialogInterface;
import android.net.Uri;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Buscar extends AppCompatActivity {
    private static final String URLWS = "http://192.168.1.18/wstienda/app/services/service-productos.php";
    private RequestQueue requestQueue;

    private EditText edtTalla, edtPrecio, edtIDPrenda;
    private Spinner spinnerTipo, spinnerGenero;
    private Button btnActualizarPrenda, btnEliminarPrenda, btnBuscarPrenda;

    private List<String> tiposList = new ArrayList<>();
    private List<String> generosList = new ArrayList<>();
    private Map<String, String> generoMap = new HashMap<>();
    private Map<String, String> generoMapReverse = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_buscar);

        loadUI();
        cargarSpinners();

        requestQueue = Volley.newRequestQueue(this);

        btnBuscarPrenda.setOnClickListener(v -> buscarPrenda());
        btnEliminarPrenda.setOnClickListener(v -> confirmarEliminacion());
        btnActualizarPrenda.setOnClickListener(v -> actualizarPrenda());
    }

    private void cargarSpinners() {
        tiposList.add("Falda");
        tiposList.add("Camisa");
        tiposList.add("Pantalon");
        tiposList.add("Polo");
        tiposList.add("Short");

        generosList.add("Masculino");
        generosList.add("Femenino");
        generosList.add("Unisex");

        generoMap.put("M", "Masculino");
        generoMap.put("F", "Femenino");
        generoMap.put("U", "Unisex");

        for (Map.Entry<String, String> entry : generoMap.entrySet()) {
            generoMapReverse.put(entry.getValue(), entry.getKey());
        }

        spinnerTipo.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tiposList));
        spinnerGenero.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, generosList));
    }

    private void buscarPrenda() {
        String idPrenda = edtIDPrenda.getText().toString().trim();
        if (idPrenda.isEmpty()) {
            showToast("Ingrese un ID de prenda");
            return;
        }

        String URLparams = URLWS + "?q=findById&id=" + idPrenda;

        JsonArrayRequest jsonRequest = new JsonArrayRequest(
                Request.Method.GET,
                URLparams,
                null,
                response -> {
                    if (response.length() == 0) {
                        resetUI();
                        showToast("No existe el producto");
                    } else {
                        try {
                            JSONObject jsonObject = response.getJSONObject(0);
                            edtTalla.setText(jsonObject.optString("talla", ""));
                            edtPrecio.setText(jsonObject.optString("precio", ""));

                            int tipoIndex = tiposList.indexOf(jsonObject.optString("tipo", ""));
                            int generoIndex = generosList.indexOf(generoMap.get(jsonObject.optString("genero", "")));

                            if (tipoIndex >= 0) spinnerTipo.setSelection(tipoIndex);
                            if (generoIndex >= 0) spinnerGenero.setSelection(generoIndex);
                        } catch (JSONException e) {
                            showToast("Error al obtener los datos");
                        }
                    }
                },
                error -> showToast("Error en la conexión")
        );

        requestQueue.add(jsonRequest);
    }

    private void confirmarEliminacion() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmación")
                .setMessage("¿Seguro de eliminar?")
                .setNegativeButton("No", null)
                .setPositiveButton("Sí", (dialog, which) -> eliminarPrenda())
                .show();
    }

    private void eliminarPrenda() {
        String idPrenda = edtIDPrenda.getText().toString().trim();
        if (idPrenda.isEmpty()) {
            showToast("Ingrese un ID de prenda");
            return;
        }

        String URLDELETE = URLWS + "?id=" + idPrenda;

        StringRequest deleteRequest = new StringRequest(
                Request.Method.DELETE,
                URLDELETE,
                response -> {
                    resetUI();
                    showToast("Prenda eliminada correctamente");
                },
                error -> showToast("Error al eliminar la prenda")
        );

        requestQueue.add(deleteRequest);
    }


    private void actualizarPrenda() {
        String idPrenda = edtIDPrenda.getText().toString().trim();
        String talla = edtTalla.getText().toString().trim();
        String precio = edtPrecio.getText().toString().trim();
        String tipo = spinnerTipo.getSelectedItem().toString();
        String genero = generoMapReverse.get(spinnerGenero.getSelectedItem().toString());

        if (idPrenda.isEmpty() || talla.isEmpty() || precio.isEmpty() || genero == null) {
            showToast("Complete todos los campos");
            return;
        }

        String urlUpdate = URLWS;
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id", idPrenda);
            jsonBody.put("tipo", tipo);
            jsonBody.put("genero", genero);
            jsonBody.put("talla", talla);
            jsonBody.put("precio", precio);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest putRequest = new JsonObjectRequest(
                Request.Method.PUT,
                urlUpdate,
                jsonBody,
                response -> showToast("Prenda actualizada correctamente"),
                error -> showToast("Error al actualizar la prenda")
        );

        requestQueue.add(putRequest);
    }


    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void resetUI() {
        edtIDPrenda.setText(null);
        edtPrecio.setText(null);
        edtTalla.setText(null);
        spinnerTipo.setSelection(0);
        spinnerGenero.setSelection(0);
    }

    private void loadUI() {
        edtIDPrenda = findViewById(R.id.edtIDPrenda);
        spinnerTipo = findViewById(R.id.spinnerTipo);
        spinnerGenero = findViewById(R.id.spinnerGenero);
        edtTalla = findViewById(R.id.edtTalla);
        edtPrecio = findViewById(R.id.edtPrecio);
        btnEliminarPrenda = findViewById(R.id.btnEliminarPrenda);
        btnActualizarPrenda = findViewById(R.id.btnActualizarPrenda);
        btnBuscarPrenda = findViewById(R.id.btnBuscarPrenda);
    }
}
