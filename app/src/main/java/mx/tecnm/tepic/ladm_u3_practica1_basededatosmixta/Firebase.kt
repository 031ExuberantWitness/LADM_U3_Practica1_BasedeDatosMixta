package mx.tecnm.tepic.ladm_u3_practica1_basededatosmixta

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.google.firebase.firestore.FirebaseFirestore
import mx.tecnm.tepic.ladm_u3_practica1_basededatosmixta.databinding.ActivityFirebaseBinding

class Firebase : AppCompatActivity() {
    lateinit var binding: ActivityFirebaseBinding
    val listaIds = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirebaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mostrar()

        binding.btnRegresar.setOnClickListener {
            val intent = Intent(binding.root.context, PantallaInicio::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }

        binding.btnRegistrar.setOnClickListener {
            val intent = Intent(binding.root.context, FirebaseRegistrar::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }

        binding.quitarFiltro.setOnClickListener {
            mostrar()

            binding.quitarFiltro.isVisible = false
        }

        binding.btnBuscar.setOnClickListener {
            var layin = LinearLayout(this)
            var comboCampos = Spinner(this)
            var itemCampos = ArrayList<String>()
            var claveBusqueda = EditText(this)

            itemCampos.add("Fecha (poner en formato Año/Mes/Dia unicamente)")
            itemCampos.add("Carrera 1")
            itemCampos.add("Carrera 2")

            comboCampos.adapter = ArrayAdapter<String>(this, R.layout.simple_list_item_1, itemCampos)

            layin.orientation = LinearLayout.VERTICAL
            claveBusqueda.setHint("Clave a buscar")
            layin.addView(comboCampos)
            layin.addView(claveBusqueda)

            android.app.AlertDialog.Builder(this)
                .setTitle("Atención:")
                .setView(layin)
                .setMessage("Elija un campo a buscar")
                .setPositiveButton("Buscar"){d, i ->
                    consulta(comboCampos, claveBusqueda)
                    binding.quitarFiltro.isVisible = true
                }
                .setNeutralButton("Cancelar"){d, i ->

                }
                .show()
        }
    }

    private fun consulta(comboCampos: Spinner, claveBusqueda: EditText) {
        val campo = comboCampos.selectedItemId.toInt()
        val lista = ArrayList<String>()
        FirebaseFirestore.getInstance()
            .collection("candidatos")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    alerta("No se pudo realizar la consulta $error")
                    return@addSnapshotListener
                }

                listaIds.clear()
                for (documento in value!!) {
                    when (campo) {
                        0 -> { //Fecha
                            val fecha = documento.getString("fecha")
                            if (fecha != null) {
                                if(fecha.startsWith(claveBusqueda.text.toString())){
                                    lista.add(
                                        "${documento.getString("nombre")}\n" +
                                                "${documento.getString("preparatoria")}\n" +
                                                "${documento.getString("telefono")}\n" +
                                                "${documento.getString("carrera1")}\n" +
                                                "${documento.getString("carrera2")}\n" +
                                                "${documento.getString("correo")}\n" +
                                                "${documento.getString("fecha")}\n"
                                    )
                                    listaIds.add(documento.id)
                                }
                            }
                        }
                        1 -> { //Carrera1
                            val carrera1 = documento.getString("carrera1")
                            if (carrera1 != null) {
                                if(carrera1.startsWith(claveBusqueda.text.toString())){
                                    lista.add(
                                        "${documento.getString("nombre")}\n" +
                                                "${documento.getString("preparatoria")}\n" +
                                                "${documento.getString("telefono")}\n" +
                                                "${documento.getString("carrera1")}\n" +
                                                "${documento.getString("carrera2")}\n" +
                                                "${documento.getString("correo")}\n" +
                                                "${documento.getString("fecha")}\n"
                                    )
                                    listaIds.add(documento.id)
                                }
                            }
                        }
                        2 -> { //Carrera2
                            val carrera2 = documento.getString("carrera2")
                            if (carrera2 != null) {
                                if(carrera2.startsWith(claveBusqueda.text.toString())){
                                    lista.add(
                                        "${documento.getString("nombre")}\n" +
                                                "${documento.getString("preparatoria")}\n" +
                                                "${documento.getString("telefono")}\n" +
                                                "${documento.getString("carrera1")}\n" +
                                                "${documento.getString("carrera2")}\n" +
                                                "${documento.getString("correo")}\n" +
                                                "${documento.getString("fecha")}\n"
                                    )
                                    listaIds.add(documento.id)
                                }
                            }
                        }
                    }
                }
                binding.listaCandidatos.adapter = ArrayAdapter<String>(
                    this, R.layout
                        .simple_list_item_1, lista
                )
            }

        binding.listaCandidatos.adapter = ArrayAdapter<String>(
            this,
            R.layout.simple_list_item_1, lista
        )
        binding.listaCandidatos.setOnItemClickListener { adapterView, view, i, l ->
            val idSeleccionado = listaIds.get(i)

            var nombre = lista.get(i)
            nombre = nombre.substring(0, nombre.indexOf("\n")).uppercase()

            AlertDialog.Builder(this)
                .setTitle("Alerta: ")
                .setMessage("Que desea hacer con: ${nombre}?")
                .setPositiveButton("Nada") { d, i -> }
                .setNegativeButton("Eliminar") { d, i -> eliminar(idSeleccionado) }
                .setNeutralButton("Actualizar") { d, i -> actualizar(idSeleccionado) }
                .show()
        }
    }

    private fun mostrar() {
        val lista = ArrayList<String>()
        FirebaseFirestore.getInstance()
            .collection("candidatos")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    alerta("No se pudo realizar la consulta $error")
                    return@addSnapshotListener
                }

                listaIds.clear()
                for (documento in value!!) {
                    lista.add(
                        "${documento.getString("nombre")}\n" +
                                "${documento.getString("preparatoria")}\n" +
                                "${documento.getString("telefono")}\n" +
                                "${documento.getString("carrera1")}\n" +
                                "${documento.getString("carrera2")}\n" +
                                "${documento.getString("correo")}\n" +
                                "${documento.getString("fecha")}\n"
                    )
                    listaIds.add(documento.id)
                }
                binding.listaCandidatos.adapter = ArrayAdapter<String>(
                    this, R.layout
                        .simple_list_item_1, lista
                )
            }

        binding.listaCandidatos.adapter = ArrayAdapter<String>(
            this,
            R.layout.simple_list_item_1, lista
        )
        binding.listaCandidatos.setOnItemClickListener { adapterView, view, i, l ->
            val idSeleccionado = listaIds.get(i)

            var nombre = lista.get(i)
            nombre = nombre.substring(0, nombre.indexOf("\n")).uppercase()

            AlertDialog.Builder(this)
                .setTitle("Alerta: ")
                .setMessage("Que desea hacer con: ${nombre}?")
                .setPositiveButton("Nada") { d, i -> }
                .setNegativeButton("Eliminar") { d, i -> eliminar(idSeleccionado) }
                .setNeutralButton("Actualizar") { d, i -> actualizar(idSeleccionado) }
                .show()
        }
    }

    private fun actualizar(id: String) {
        val intent = Intent(this, FirebaseActualizar::class.java)
        intent.putExtra("id", id.toString())
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    fun eliminar(id: String) {
        Log.d("id", id)
        FirebaseFirestore.getInstance().collection("candidatos")
            .document(id)
            .delete()
            .addOnSuccessListener { toast("Eliminado con exito") }
            .addOnFailureListener { alerta(it.message!!) }

        mostrar()
    }


    fun toast(m: String) {
        Toast.makeText(this, m, Toast.LENGTH_LONG).show()
    }

    fun alerta(m: String) {
        AlertDialog.Builder(this).setTitle("ATENCIÓN").setMessage(m)
            .setPositiveButton("OK") { d, i -> }.show()
    }
}