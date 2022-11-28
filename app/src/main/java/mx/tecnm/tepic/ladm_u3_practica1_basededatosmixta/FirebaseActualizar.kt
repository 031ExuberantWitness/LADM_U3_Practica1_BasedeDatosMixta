package mx.tecnm.tepic.ladm_u3_practica1_basededatosmixta

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import mx.tecnm.tepic.ladm_u3_practica1_basededatosmixta.databinding.ActivityFirebaseActualizarBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FirebaseActualizar : AppCompatActivity() {
    lateinit var binding: ActivityFirebaseActualizarBinding
    var id = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirebaseActualizarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val spEscuela: Spinner = findViewById(R.id.txtEscuela)
        val spCarrera1: Spinner = findViewById(R.id.txtCarrera1)
        val spCarrera2: Spinner = findViewById(R.id.txtCarrera2)

        val adEscuela = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item, resources.getStringArray(R.array.preparatorias))
        val adCarrera = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item, resources.getStringArray(R.array.carreras))

        spEscuela.adapter = adEscuela
        spCarrera1.adapter = adCarrera
        spCarrera2.adapter = adCarrera

        id = intent.extras!!.getString("id")!!

        FirebaseFirestore.getInstance().collection("candidatos")
            .document(id).get()
            .addOnSuccessListener {
                println(id)
                binding.txtNombre.setText(it.getString("nombre"))
                binding.txtTelefono.setText(it.getString("telefono"))
                binding.txtCorreo.setText(it.getString("correo"))
            }

        binding.btnActualizar.setOnClickListener {
            actualizar()
        }

        binding.btnRegresar.setOnClickListener{
            val intent = Intent(binding.root.context, Firebase::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun actualizar() {

        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
        val formatted = current.format(formatter)

        var fechaHoy = "" + binding.txtFecha.year + "/" +
                binding.txtFecha.month + "/" +
                binding.txtFecha.dayOfMonth + " " + formatted

        FirebaseFirestore.getInstance().collection("candidatos")
            .document(id)
            .update(
                "nombre", binding.txtNombre.text.toString(),
                "preparatoria", binding.txtEscuela.getSelectedItem().toString(),
                "telefono", binding.txtTelefono.text.toString(),
                "carrera1", binding.txtCarrera1.getSelectedItem().toString(),
                "carrera2", binding.txtCarrera2.getSelectedItem().toString(),
                "correo", binding.txtCorreo.text.toString(),
                "fecha", fechaHoy.toString()
            )
            .addOnSuccessListener {
                Toast.makeText(this, "Alerta: Se actualizo con exito.", Toast.LENGTH_LONG).show()
                val intent = Intent(binding.root.context, Firebase::class.java)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish()
            }
            .addOnFailureListener {
                AlertDialog.Builder(this).setMessage("Error: No se pudo actualizar").show()
            }
    }
}