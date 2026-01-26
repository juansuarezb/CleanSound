package com.cleansound.cleansound.controller

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.cleansound.cleansound.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {
    lateinit var editTextEmail: EditText
    lateinit var editTextPassword: EditText
    lateinit var buttonLogin: Button
    lateinit var buttonCerrar: Button
    lateinit var textViewRegistrarse: TextView

    private lateinit var auth: FirebaseAuth;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        editTextEmail = findViewById(R.id.etCorreo)
        editTextPassword = findViewById(R.id.etPassword)
        buttonLogin = findViewById(R.id.btnLogin)
        textViewRegistrarse = findViewById(R.id.tvRegistrate)
        buttonCerrar = findViewById(R.id.btnCerrar)
        auth = Firebase.auth
        //Eventos clic
        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString()
            val clave = editTextPassword.text.toString()
            //Validaciones de datos requeridos y formatos
            if (!validateRequiredData())
                return@setOnClickListener
            //Si pasa validación de datos requeridos, ir a pantalla principal
//            val intent = Intent(this, MainActivity::class.java)
//            intent.putExtra(EXTRA_LOGIN, email)
//            startActivity(intent)
//            finish()
            AutenticarUsuario(email, clave)
        }
        textViewRegistrarse.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        buttonCerrar.setOnClickListener {
            limpiarCache()
            cerrar()
        }
    }
    fun limpiarCache(){
        try{
            cacheDir.deleteRecursively()
            Toast.makeText(this, "Cache borrado", Toast.LENGTH_SHORT).show()
        }catch(e: Exception){
            e.printStackTrace()
        }
    }
    fun cerrar(){
        finishAffinity()
        System.exit(0)
    }
    fun AutenticarUsuario(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(EXTRA_LOGIN, "signInWithEmail:success")
                    //Si pasa validación de datos requeridos, ir a pantalla principal
                    val intencion = Intent(this, MainActivity::class.java)
                    intencion.putExtra(EXTRA_LOGIN, auth.currentUser!!.email)
                    startActivity(intencion)
                    //finish()
                } else {
                    Log.w(EXTRA_LOGIN, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, task.exception!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

    }
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
    }
    private fun validateRequiredData():Boolean{
        val email = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()
        if (email.isEmpty()) {
            editTextEmail.setError(getString(R.string.error_email_required))
            editTextEmail.requestFocus()
            return false
        }
        if (password.isEmpty()) {
            editTextPassword.setError(getString(R.string.error_password_required))
            editTextPassword.requestFocus()
            return false
        }
        if (password.length < 8) {
            editTextPassword.setError(getString(R.string.error_password_min_length))
            editTextPassword.requestFocus()
            return false
        }
        return true
    }
}