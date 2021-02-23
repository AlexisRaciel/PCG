package com.example.pcg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.transition.Transition;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.renderscript.ScriptGroup;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.InputMismatchException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    Button conectarD, btnlimpiar;
    TextView status, data;
    BluetoothAdapter btAdapter;
    BluetoothDevice hc05;
    BluetoothSocket btSocket;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        conectarD = findViewById(R.id.conectar);
        status = findViewById(R.id.status);
        data = findViewById(R.id.data);
        btnlimpiar = findViewById(R.id.btnlimpiar);
        loadingDialog = new LoadingDialog(MainActivity.this);
        btSocket = null;
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        conectarD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btAdapter.isEnabled()){
                    loadingDialog.startLoadingDialog();
                    TareaAsyncTask tareaAsyncTask = new TareaAsyncTask();
                    tareaAsyncTask.execute();
                }
                else{
                    Toast.makeText(MainActivity.this, "El bluetooth no está activo", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnlimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpiarDatos();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_opciones, menu);
        return true;
    }

    public void checarEstado(){
        if(btSocket.isConnected()){
            status.setText("Conectado");
            Toast.makeText(this, "Conectado", Toast.LENGTH_SHORT).show();
        }else
            status.setText("Desconectado");
    }

    public void limpiarDatos(){
        data.setText("");
    }
/*
    public void conectarDispositivo(){
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        hc05 = btAdapter.getRemoteDevice("98:D3:51:F5:C8:77");

        System.out.println(btAdapter.getBondedDevices());
        System.out.println((hc05.getName()));


        try {
            btSocket = hc05.createRfcommSocketToServiceRecord(mUUID);
            btSocket.connect();
            System.out.println(btSocket.isConnected());
            checarEstado();
        } catch (IOException e) {
            e.printStackTrace();
        }
/*        try {
            OutputStream outputStream = btSocket.getOutputStream();
            outputStream.write(48);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
/*
        InputStream inputStream = null;
        try {
            inputStream = btSocket.getInputStream();
            inputStream.skip(inputStream.available());
            Integer b;
            for(int i = 65; i <= 1000; i++){
                b = (int) inputStream.read();
                System.out.println(i);
                data.setText("\n Hola " + i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            btSocket.close();
            checarEstado();
            System.out.println(btSocket.isConnected());
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }*/

    private class TareaAsyncTask extends AsyncTask<Void, Integer, String>{
        @Override
        protected void onPreExecute(){

        }
        // Hilo secundario
        @Override
        protected String doInBackground(Void... voids) {
            hc05 = btAdapter.getRemoteDevice("98:D3:51:F5:C8:77");

            try {
                btSocket = hc05.createRfcommSocketToServiceRecord(mUUID);
                btSocket.connect();
                publishProgress(-1);
            } catch (IOException e) {
                e.printStackTrace();
            }
/*        try {
            OutputStream outputStream = btSocket.getOutputStream();
            outputStream.write(48);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

            InputStream inputStream = null;
            try {
                inputStream = btSocket.getInputStream();
                inputStream.skip(inputStream.available());
                Integer b;
                for(int i = 65; i <= 1000; i++){
                    b = (int) inputStream.read();
                    publishProgress(b);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                btSocket.close();
                publishProgress(-1);
            }
            catch (IOException e){
                e.printStackTrace();
            }
            return "Desconectado";
        }
        // Hilo principal
        @Override
        protected void onProgressUpdate(Integer... values){
            if(values[0] == -1){
                loadingDialog.dismissDialog();
                checarEstado();
            }else {
                Float voltaje;
                voltaje = values[0].floatValue() / 51;
                data.append(voltaje + " V \n");
            }
        }
        // Finalización del hilo secundario.
        @Override
        protected void onPostExecute(String resultado) {
            Toast.makeText(MainActivity.this, resultado, Toast.LENGTH_SHORT).show();
        }
    }
}