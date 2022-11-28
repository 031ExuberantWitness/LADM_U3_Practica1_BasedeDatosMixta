package mx.tecnm.tepic.ladm_u3_practica1_basededatosmixta

import android.R
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.google.firebase.firestore.FirebaseFirestore
import mx.tecnm.tepic.ladm_u3_practica1_basededatosmixta.databinding.ActivityBuscarCandidatoBinding
import java.util.*
import kotlin.collections.ArrayList

class BuscarCandidato : AppCompatActivity() {
    lateinit var binding: ActivityBuscarCandidatoBinding
    val baseDatos = localBaseDatos(this, "ITTEPIC", null, 1)
    var IDs = ArrayList<Int>()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuscarCandidatoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mostrarTodos()

        if(isOnline(this)){
            AlertDialog.Builder(this)
                .setTitle("Alerta: ")
                .setMessage("¿Desea cargar la base de datos local a firebase?")
                .setPositiveButton("Si") { d, i -> cargarBaseFireBase()}
                .setNegativeButton("No") { d, i -> }
                .show()
        }

        binding.btnRecargar.setOnClickListener {
            mostrarTodos()
        }

        binding.btnRegresar.setOnClickListener{
            val intent = Intent(binding.root.context, PantallaInicio::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish()
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
                .setMessage("Elija cmapo para busqueda")
                .setPositiveButton("Buscar"){d, i ->
                    consulta(comboCampos, claveBusqueda)
                    binding.quitarFiltro.isVisible = true
                }
                .setNeutralButton("Cancelar"){d, i ->

                }
                .show()
        }

        binding.quitarFiltro.setOnClickListener {
            mostrarTodos()

            binding.quitarFiltro.isVisible = false
        }
    }

    private fun cargarBaseFireBase() {
        val candidato = baseDatos.readableDatabase
        val lista = ArrayList<String>()

        IDs.clear()
        val resultado = candidato.query("CANDIDATOS", arrayOf("*"), null, null, null, null, null)
        if (resultado.moveToFirst()) {
            do {
                val data = resultado.getString(1) + "\n" +
                        resultado.getString(2) + "\n" +
                        resultado.getString(3) + "\n" +
                        resultado.getString(4) + "\n" +
                        resultado.getString(5) + "\n" +
                        resultado.getString(6) + "\n" +
                        resultado.getString(7) + "\n"
                lista.add(data)
                IDs.add(resultado.getInt(0))

                val datos =
                    hashMapOf(
                        "nombre" to resultado.getString(1),
                        "preparatoria" to resultado.getString(2),
                        "telefono" to resultado.getString(3),
                        "carrera1" to resultado.getString(4),
                        "carrera2" to resultado.getString(5),
                        "correo" to resultado.getString(6),
                        "fecha" to resultado.getString(7)
                    )
                FirebaseFirestore.getInstance()
                    .collection("candidatos")
                    .add(datos)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Alerta: Se subio a Firebase con exitó..", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, it.message!!, Toast.LENGTH_LONG).show()
                    }
            } while (resultado.moveToNext())
        } else {
            Toast.makeText(this, "Alerta: No hay nada que agregar.", Toast.LENGTH_LONG).show()
        }
    }

    fun mostrarTodos() {
        val candidato = baseDatos.readableDatabase
        val lista = ArrayList<String>()

        IDs.clear()
        val resultado = candidato.query("CANDIDATOS", arrayOf("*"), null, null, null, null, null)
        if (resultado.moveToFirst()) {
            do {
                val data = resultado.getString(1) + "\n" +
                           resultado.getString(2) + "\n" +
                           resultado.getString(3) + "\n" +
                           resultado.getString(4) + "\n" +
                           resultado.getString(5) + "\n" +
                           resultado.getString(6) + "\n" +
                           resultado.getString(7) + "\n"
                lista.add(data)
                IDs.add(resultado.getInt(0))
            } while (resultado.moveToNext())
        } else {
            lista.add("LA TABLA ESTA VACIA")
        }

        binding.listaCandidatos.adapter = ArrayAdapter<String>(
            this,
            R.layout.simple_list_item_1, lista
        )
        binding.listaCandidatos.setOnItemClickListener { adapterView, view, i, l ->
            val idSeleccionado = IDs.get(i)

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

    private fun actualizar(id: Int) {
        val intent = Intent(this, ActualizarCandidato::class.java)
        intent.putExtra("id", id.toString())
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private fun eliminar(id: Int) {
        val resultado = baseDatos.writableDatabase.delete("CANDIDATOS", "ID=?", arrayOf(id.toString()))
        if (resultado == 0) {
            AlertDialog.Builder(this).setMessage("Error: No se pudo eliminar.").show()
        } else {
            Toast.makeText(this, "Alerta: Se elimino el registro con exito.", Toast.LENGTH_LONG).show()
            mostrarTodos()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    return true
                }
            }
        }
        return false
    }

    private fun consulta(comboCampos: Spinner, claveBusqueda: EditText) {
        var posicionCampoSeleccionado = comboCampos.selectedItemId.toInt()

        val candidato = baseDatos.readableDatabase
        val lista = ArrayList<String>()

        IDs.clear()

        val resultado = candidato.query("CANDIDATOS", arrayOf("*"), null, null, null, null, null)

        when(posicionCampoSeleccionado){
            0 -> { //Fecha
                if (resultado.moveToFirst()) {
                    println()
                    do {
                        val data = resultado.getString(1) + "\n" +
                                resultado.getString(2) + "\n" +
                                resultado.getString(3) + "\n" +
                                resultado.getString(4) + "\n" +
                                resultado.getString(5) + "\n" +
                                resultado.getString(6) + "\n" +
                                resultado.getString(7) + "\n"

                        if (resultado.getString(7).startsWith(claveBusqueda.text.toString())){
                            lista.add(data)
                            IDs.add(resultado.getInt(0))
                        }

                    } while (resultado.moveToNext())
                } else {
                    lista.add("La busqueda no arrojo ningun resultado")
                }
            }

            1-> { //Carrera 1

                if (resultado.moveToFirst()) {
                    do {
                        val data = resultado.getString(1) + "\n" +
                                resultado.getString(2) + "\n" +
                                resultado.getString(3) + "\n" +
                                resultado.getString(4) + "\n" +
                                resultado.getString(5) + "\n" +
                                resultado.getString(6) + "\n" +
                                resultado.getString(7) + "\n"

                        if (resultado.getString(4).equals(claveBusqueda.text.toString())){
                            lista.add(data)
                            IDs.add(resultado.getInt(0))
                        }

                    } while (resultado.moveToNext())
                } else {
                    lista.add("La busqueda no arrojo ningun resultado")
                }
            }

            2 -> { //Carrera 2

                if (resultado.moveToFirst()) {
                    do {
                        val data = resultado.getString(1) + "\n" +
                                resultado.getString(2) + "\n" +
                                resultado.getString(3) + "\n" +
                                resultado.getString(4) + "\n" +
                                resultado.getString(5) + "\n" +
                                resultado.getString(6) + "\n" +
                                resultado.getString(7) + "\n"
                        if (resultado.getString(5).equals(claveBusqueda.text.toString())){
                            lista.add(data)
                            IDs.add(resultado.getInt(0))
                        }

                    } while (resultado.moveToNext())
                } else {
                    lista.add("La busqueda no arrojo ningun resultado")
                }
            }
        }

        binding.listaCandidatos.adapter = ArrayAdapter<String>(
            this,
            R.layout.simple_list_item_1, lista
        )
    }

    override fun onRestart() {
        super.onRestart()
        mostrarTodos()
    }
}