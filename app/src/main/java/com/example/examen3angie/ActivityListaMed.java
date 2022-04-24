package com.example.examen3angie;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.examen3angie.Configuracion.Medicamentos;
import com.example.examen3angie.Configuracion.SQLiteConexion;
import com.example.examen3angie.Configuracion.bdTransaccion;

import java.util.ArrayList;

public class ActivityListaMed extends AppCompatActivity {


    SQLiteConexion conexion;
    ListView listViewMedicamento;

    ArrayList<Medicamentos> listMedicamentos, listRes;

    ArrayList<String> listaStringContactos;

    Button btnAtras, btnEliminarContacto, btnMostrarImagen,  btnEditar;

    Medicamentos medicamentoSeleccionado;

    EditText textEditBuscar;

    //    ArrayAdapter adapter;
    Adaptador adapter, adc;

    int idBusqueda;

    TextView tv;


    AlertDialog.Builder builder;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_med);


        medicamentoSeleccionado = null;

        listarListViewMed();


        textEditBuscar = (EditText) findViewById(R.id.editTextBuscar);



        btnAtras = (Button) findViewById(R.id.btnAtrasMostrar);
        btnEliminarContacto = (Button) findViewById(R.id.btnEliminarMed);
        btnMostrarImagen = (Button) findViewById(R.id.btnMostrarImagen);
        btnEditar = (Button) findViewById(R.id.btnEditar);

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnEliminarContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(medicamentoSeleccionado != null){
                    builder = new AlertDialog.Builder(ActivityListaMed.this);

                    builder.setMessage("¿Seguro que deseas eliminar este medicamento?").setTitle("Alerta");

                    builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            eliminarMed();

                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    dialog = builder.create();
                    dialog.show();
                }else{
                    mostrarMensaje("Alerta", "No hay Medicamento seleccionado");
                }


            }
        });

        btnMostrarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(medicamentoSeleccionado != null){
                    mostrarImagen();
                }else {
                    mostrarMensaje("Alerta", "No hay Medicamento seleccionado");
                }
            }
        });

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(medicamentoSeleccionado != null){

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("medicamentos", medicamentoSeleccionado);

                    Intent intent = new Intent(getApplicationContext(), ActivityActualizar.class);
                    intent.putExtras(bundle);

                    startActivity(intent);
                }else {
                    mostrarMensaje("Alerta", "No hay Medicamento seleccionado");
                }
            }
        });

        listViewMedicamento.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private long lastTouchTime = 0;
            private long currentTouchTime = 0;

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                lastTouchTime = currentTouchTime;
                currentTouchTime = System.currentTimeMillis();

                tv = (TextView) view.findViewById(R.id.itemObjetId);
                idBusqueda = Integer.parseInt(tv.getText().toString());


                adc =(Adaptador) adapterView.getAdapter();

                listRes = adc.getFilterlist();

                for(int j=0;j<listRes.size();j++){
                    if(listRes.get(j).getId() == idBusqueda){
                        medicamentoSeleccionado = listRes.get(j);
                        break;
                    }
                }

            }
        });

        textEditBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();

        textEditBuscar.setText("");
        obtenerListaMed();
        llenarListViewMed();

        medicamentoSeleccionado = null;
    }

    private void listarListViewMed() {
        conexion = new SQLiteConexion(this, bdTransaccion.NAME_DATABASE, null, 1);
        listViewMedicamento = (ListView) findViewById(R.id.listViewMedicamento);
        listViewMedicamento.setSelector(R.color.pink_200);

        obtenerListaMed();

        adapter = new Adaptador(ActivityListaMed.this, listMedicamentos);

        listViewMedicamento.setAdapter(adapter);
    }

    private void obtenerListaMed() {
        SQLiteDatabase db = conexion.getReadableDatabase();

        Medicamentos ModMedica = null;

        listMedicamentos = new ArrayList<>();

        Cursor cursor = db.rawQuery(bdTransaccion.SELECT_TABLE_Medicamentos, null);


        while (cursor.moveToNext()){

            ModMedica = new Medicamentos();

            ModMedica.setId(cursor.getInt(0));
            ModMedica.setDescripcion(cursor.getString(1));
            ModMedica.setCantidad(cursor.getString(2));
            ModMedica.setTiempo(cursor.getString(3));
            ModMedica.setPeriocidad(cursor.getString(4));
            ModMedica.setImagen(cursor.getString(5));

            listMedicamentos.add(ModMedica);

        }

        cursor.close();

        llenarListStringMed();

    }

    private void llenarListStringMed() {
        listaStringContactos = new ArrayList<>();

        for(Medicamentos c: listMedicamentos){

            listaStringContactos.add(c.toString());
        }
    }

    private void eliminarMed() {
        conexion = new SQLiteConexion(this, bdTransaccion.NAME_DATABASE, null, 1);
        SQLiteDatabase database = conexion.getWritableDatabase();


        int result = database.delete(bdTransaccion.TABLA_Medicamentos, bdTransaccion.ID+"=?",
                new String[]{medicamentoSeleccionado.getId()+""});

        if(result>0){
            obtenerListaMed();
            llenarListViewMed();

            medicamentoSeleccionado = null;
            Toast.makeText(getApplicationContext(), "Medicamento eliminado ", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(), "Error: El Medicamento no se elimino", Toast.LENGTH_LONG).show();
        }
    }

    private void llenarListViewMed() {
        adapter = null;

        adapter = new Adaptador(ActivityListaMed.this, listMedicamentos);
        listViewMedicamento.setAdapter(adapter);
    }

    private void mostrarMensaje(String titulo, String mensaje) {
        builder = new AlertDialog.Builder(ActivityListaMed.this);

        builder.setMessage(mensaje).setTitle(titulo);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        dialog = builder.create();
        dialog.show();

    }

    public void mostrarImagen(){

        if(isTextEmpty(medicamentoSeleccionado.getImagen())){
            mostrarMensaje("Alerta", "El Medicamento no tiene ninguna imagen asignada");
            return;
        }

        builder = new AlertDialog.Builder(ActivityListaMed.this);

        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.mostrar_imagen, null);

        builder.setView(view);

        dialog = builder.create();

        dialog.show();

        TextView text =(TextView) view.findViewById(R.id.textViewDialogPersonalizado);
        text.setText(medicamentoSeleccionado.getDescripcion());

        ImageView imagen = (ImageView) view.findViewById(R.id.imageViewDialog);

        Uri uri = Uri.parse(medicamentoSeleccionado.getImagen());

        imagen.setImageURI(uri);

        Button btnCerrar = (Button) view.findViewById(R.id.buttonDialog);

        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }
    //Si el texto esta vacio entonces
    private static boolean isTextEmpty(String text){
        return (text.length()==0)?true:false;
    }



}