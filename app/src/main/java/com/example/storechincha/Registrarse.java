package com.example.storechincha;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Registrarse extends AppCompatActivity {

    private EditText etNomUsuario, etNombre, etApellidoP, etApellidoM, etEmail, etContrasena, etTelefono;
    private Button btnRegistrar,btnCancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);  // Cambié aquí el nombre del archivo XML

        // Inicializar vistas
        etNomUsuario = findViewById(R.id.etNomUsuario);
        etNombre = findViewById(R.id.etNombre);
        etApellidoP = findViewById(R.id.etApellidoP);
        etApellidoM = findViewById(R.id.etApellidoM);
        etEmail = findViewById(R.id.etEmail);
        etContrasena = findViewById(R.id.etContrasena);
        etTelefono = findViewById(R.id.etTelefono);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnCancelar= findViewById(R.id.btnCancelar);

        // Configurar el botón de registro
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarUsuario();
            }
        });
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Registrarse.this, Login.class);
                startActivity(intent);
                finish(); // Opcional: Finaliza la actividad actual
            }
        });
    }

    private void registrarUsuario() {
        // Obtener los datos del formulario
        String nomUsuario = etNomUsuario.getText().toString().trim();
        String nombre = etNombre.getText().toString().trim();
        String apellidoP = etApellidoP.getText().toString().trim();
        String apellidoM = etApellidoM.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();

        // Validar los datos
        if (nomUsuario.isEmpty() || nombre.isEmpty() || apellidoP.isEmpty() || apellidoM.isEmpty() || email.isEmpty() || contrasena.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(Registrarse.this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear el JSON para enviar al servidor
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("NomUsuario", nomUsuario);
            jsonBody.put("nombre", nombre);
            jsonBody.put("apellidoP", apellidoP);
            jsonBody.put("apellidoM", apellidoM);
            jsonBody.put("email", email);
            jsonBody.put("contrasena", contrasena);
            jsonBody.put("telefono", telefono);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Crear la solicitud HTTP
        String url = "http://192.168.1.18/wstienda/app/services/service-usuarios.php?q=register"; // Cambia esta URL
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                Toast.makeText(Registrarse.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                finish();  // Cerrar la actividad de registro
                            } else {
                                Toast.makeText(Registrarse.this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Registrarse.this, "Error en la conexión", Toast.LENGTH_SHORT).show();
                    }
                });

        // Agregar la solicitud a la cola de peticiones
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonRequest);
    }
}
