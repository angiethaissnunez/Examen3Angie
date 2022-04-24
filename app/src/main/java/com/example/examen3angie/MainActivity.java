package com.example.examen3angie;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.examen3angie.Configuracion.SQLiteConexion;
import com.example.examen3angie.Configuracion.bdTransaccion;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {


    TextView textSeleccionarFoto;
    ImageView imageViewIngresar;
    EditText txtdescripcion, txtcantidad, txtperioci;
    Spinner spinnerTiempo;

    Button btnSalvar, btnMostrar;
    ArrayList<String> arrayListPaises;

    String currentPhotoPath;
    ActivityResultLauncher<Intent> launcherTomarFoto;


    AlertDialog.Builder builder;
    AlertDialog dialog;


    static final int PETICION_ACCESO_CAM = 100;
    static final int TAKE_PIC_REQUEST = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentPhotoPath = "";
        builder = null;
        dialog = null;

        arrayListPaises = new ArrayList<>();

        arrayListPaises.add("Horas");
        arrayListPaises.add("Diaria");

        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                arrayListPaises);
        spinnerTiempo = (Spinner) findViewById(R.id.spinnerTiempo);

        spinnerTiempo.setAdapter(spinnerArrayAdapter);


        textSeleccionarFoto = (TextView) findViewById(R.id.btnTomarFoto);
        imageViewIngresar = (ImageView) findViewById(R.id.imageViewTomar);

        txtdescripcion = (EditText) findViewById(R.id.txtDescripcion);
        txtcantidad = (EditText) findViewById(R.id.txtCantidad);
        txtperioci = (EditText) findViewById(R.id.txtPeriocidad);

        btnSalvar = (Button) findViewById(R.id.btnSalvarMed);
        btnMostrar = (Button) findViewById(R.id.btnMostrarILista);

        textSeleccionarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permisosTomarFoto();
            }
        });

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                guardarMedicamento();
            }
        });

        btnMostrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), ActivityListaMed.class);
                startActivity(intent);
            }
        });

        launcherTomarFoto = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        Intent data = result.getData();

                        if (result.getResultCode() == Activity.RESULT_OK) {

                            Uri uri = Uri.parse(currentPhotoPath);
                            imageViewIngresar.setImageURI(uri);
                        }
                    }
                });

    }

    private void guardarMedicamento() {
        if(permitirGuardarMe()){
            SQLiteConexion conexion = new SQLiteConexion(this, bdTransaccion.NAME_DATABASE, null, 1);
            SQLiteDatabase db = conexion.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(bdTransaccion.Descripcion, txtdescripcion.getText().toString());
            values.put(bdTransaccion.Cantidad, txtcantidad.getText().toString());
            values.put(bdTransaccion.Tiempo, spinnerTiempo.getSelectedItem().toString());
            values.put(bdTransaccion.Periocidad, txtperioci.getText().toString());
            values.put(bdTransaccion.IMAGEN, currentPhotoPath);

            Long result = db.insert(bdTransaccion.TABLA_Medicamentos, bdTransaccion.ID, values);

            if(result>0){
                Toast.makeText(getApplicationContext(), "Registro Exitoso Codigo : " + result.toString(),Toast.LENGTH_LONG).show();
                limpiarEntradas();
            }else {
                Toast.makeText(getApplicationContext(), "Error: No se pudo realizar el registro"
                        ,Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean permitirGuardarMe() {
        String tiempo = spinnerTiempo.getSelectedItem().toString();
        String photoPath = currentPhotoPath;
        String descripcion = txtdescripcion.getText().toString();
        String cantidad = txtcantidad.getText().toString();
        String perioci = txtperioci.getText().toString();

        String mensaje="";

        if(isTextEmpty(descripcion)) mensaje = "Debe escribir una Descripcion";
        else if(!isText(descripcion)) mensaje = "El campo Descripcion solo admite letras y espacios";
        else if(isTextEmpty(cantidad))mensaje = "Debe escribir una Cantidad";
        //else if(isTextEmpty(perioci)) mensaje = "Debe escribir una nota";


        if(!isTextEmpty(mensaje)){
            mostrarMensaje("Alerta", mensaje);
            return false;
        }

        return true;
    }

    private void mostrarMensaje(String alerta, String mensaje) {
        builder = new AlertDialog.Builder(MainActivity.this);

        builder.setMessage(mensaje).setTitle(alerta);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        dialog = builder.create();
        dialog.show();
    }

    private void permisosTomarFoto() {

        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PETICION_ACCESO_CAM);
        }else {
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PETICION_ACCESO_CAM){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                dispatchTakePictureIntent();
            }

        }else{
            Toast.makeText(getApplicationContext(), "Se nesecitan permisos de acceso a camara", Toast.LENGTH_LONG).show();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );



        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;

            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.toString();
            }
            // Continue only if the File was successfully created
            try {
                if (photoFile != null) {

                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.example.examen3angie.fileprovider",
                            photoFile);

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                    takePictureIntent.putExtra("request_code", TAKE_PIC_REQUEST);

                    launcherTomarFoto.launch(takePictureIntent);
                }
            }catch (Exception e){
                Log.i("Error", "dispatchTakePictureIntent: " + e.toString());
            }
        }
    }

    private static boolean isText(String text){

        // Validando un texto que solo acepte letras sin importar tamaño
        Pattern pat = Pattern.compile("^[a-zA-ZáéíóúÁÉÓÚÍ ]+$");
        Matcher mat = pat.matcher(text);
        return (mat.matches());
    }

    //Si el texto esta vacio
    private static boolean isTextEmpty(String text){
        return (text.length()==0)?true:false;
    }


    private void limpiarEntradas() {


        txtdescripcion.setText("");
        txtcantidad.setText("");
        txtperioci.setText("");
        spinnerTiempo.setSelection(0);
        currentPhotoPath = "";
        imageViewIngresar.setImageResource(R.drawable.imagen);
    }

}