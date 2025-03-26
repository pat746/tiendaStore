package com.example.storechincha.adaptadores;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.storechincha.R;
import com.example.storechincha.entidades.Productos;

import java.util.List;

public class ProductosAdapter extends ArrayAdapter<Productos> {
    private Context context;
    private List<Productos> listaProductos;

    public ProductosAdapter(@NonNull Context context, List<Productos> listaProductos) {
        super(context, R.layout.list_item, listaProductos);
        this.context = context;
        this.listaProductos = listaProductos;
    }

    private void showModal(String message) {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(this.context);
        dialogo.setTitle("StoreChincha");
        dialogo.setMessage(message);
        dialogo.setCancelable(false);
        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogo.create().show();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        Productos productos = listaProductos.get(position);

        // Corrige el uso de TextView en lugar de Spinner
        TextView txtTipo = convertView.findViewById(R.id.txtItemTipo);
        TextView txtGenero = convertView.findViewById(R.id.txtItemGenero);
        Button button = convertView.findViewById(R.id.btnInLineItem);

        txtTipo.setText(productos.getTipo());  // Muestra el tipo como texto
        txtGenero.setText(convertirGenero(productos.getGenero()));  // Muestra el género como texto

        // Acción del botón
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mensaje = "";
                mensaje += "Precio: " + productos.getPrecio() + "\n";
                mensaje += "Talla: " + productos.getTalla() + "\n";
                showModal(mensaje);
            }
        });

        return convertView;
    }
    private String convertirGenero(String genero) {
        switch (genero) {
            case "F": return "Dama";
            case "M": return "Varón";
            case "U": return "Unisex";
            default: return "Desconocido";
        }
    }
}
