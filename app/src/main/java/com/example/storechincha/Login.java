package com.example.storechincha;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Login extends AppCompatActivity {

    EditText nombreUsuario, password;
    Button btnLogin,btnRegistrarUsuario;
    ProgressBar progressBar;

    private static final String LOGIN_URL = "http://192.168.1.18/wstienda/app/services/service-usuarios.php?q=login"; // Cambia la URL según tu servidor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nombreUsuario = findViewById(R.id.txtNombre);
        password = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        btnRegistrarUsuario=findViewById(R.id.btnRegistrarUsuario);
        btnRegistrarUsuario.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Registrarse.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> {
            String userName = nombreUsuario.getText().toString().trim();
            String userPassword = password.getText().toString().trim();

            if (userName.isEmpty() || userPassword.isEmpty()) {
                Toast.makeText(Login.this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                new LoginTask().execute(userName, userPassword);
            }
        });
    }

    private class LoginTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String nombre = params[0];
            String password = params[1];
            String result = "";

            try {
                URL url = new URL(LOGIN_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(5000); // Tiempo de espera de conexión
                conn.setReadTimeout(5000); // Tiempo de espera de lectura

                JSONObject json = new JSONObject();
                json.put("NomUsuario", nombre);
                json.put("contrasena", password);

                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    result = sb.toString();
                    reader.close();
                } else {
                    Log.e("LoginError", "Error en la respuesta: " + responseCode);
                }
            } catch (Exception e) {
                Log.e("LoginError", "Error en la conexión: " + e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);

            try {
                JSONObject jsonResponse = new JSONObject(result);
                boolean status = jsonResponse.getBoolean("status");

                if (status) {
                    Toast.makeText(Login.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(Login.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Toast.makeText(Login.this, "Error en el servidor", Toast.LENGTH_SHORT).show();
                Log.e("LoginError", "Error en JSON: " + e.getMessage());
            }
        }
    }
}
