package mx.tecnm.tepic.ladm_u3_practica1_basededatosmixta

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.FirebaseFirestore
import mx.tecnm.tepic.ladm_u3_practica1_basededatosmixta.databinding.ActivityFirebaseRegistrarBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FirebaseRegistrar : AppCompatActivity() {
    lateinit var binding: ActivityFirebaseRegistrarBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirebaseRegistrarBinding.inflate(layoutInflater)
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

        binding.btnRegresar.setOnClickListener{
            val intent = Intent(binding.root.context, Firebase::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish()
        }

        binding.btnRegistrar.setOnClickListener {
            val current = LocalDateTime.now()

            val formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
            val formatted = current.format(formatter)

            var fechaHoy = "" + binding.txtFecha.year + "/" +
                    binding.txtFecha.month + "/" +
                    binding.txtFecha.dayOfMonth + " " + formatted

            val datos =
                hashMapOf(
                    "nombre" to binding.txtNombre.text.toString(),
                    "preparatoria" to binding.txtEscuela.getSelectedItem().toString(),
                    "telefono" to binding.txtTelefono.text.toString(),
                    "carrera1" to binding.txtCarrera1.getSelectedItem().toString(),
                    "carrera2" to binding.txtCarrera2.getSelectedItem().toString(),
                    "correo" to binding.txtCorreo.text.toString(),
                    "fecha" to fechaHoy,
                )

            FirebaseFirestore.getInstance()
                .collection("candidatos")
                .add(datos)
                .addOnSuccessListener {
                    Toast.makeText(this, "Alerta: Se registro el candidato con exito", Toast.LENGTH_LONG).show()

                    val intent = Intent(binding.root.context, FirebaseRegistrar::class.java)
                    startActivity(intent)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.message!!, Toast.LENGTH_LONG).show()
                }
        }
    }
}