package com.cleansound.cleansound.controller

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cleansound.cleansound.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import service.UserService


class RegisterActivity : AppCompatActivity() {
    lateinit var editTextEmail: EditText
    lateinit var editTextPassword: EditText
    lateinit var editTextRepeatPassword: EditText
    lateinit var buttonRegister: Button
    lateinit var textViewIniciarSesion: TextView
    lateinit var buttonBackRegister: ImageButton
    private lateinit var auth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        editTextEmail = findViewById(R.id.etCorreo)
        editTextPassword = findViewById(R.id.etPassword)
        editTextRepeatPassword = findViewById(R.id.etRepeatPassword)
        buttonBackRegister = findViewById(R.id.btnBackRegister)
        buttonRegister = findViewById(R.id.btnRegistrarse)
        textViewIniciarSesion = findViewById(R.id.tvIniciarSesion)
        auth = Firebase.auth
        buttonBackRegister.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        buttonRegister.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            val repeatPassword = editTextRepeatPassword.text.toString()
            //Validaciones de datos requeridos y formatos
            if (!validateRequiredData())
                return@setOnClickListener
            if (!validatePasswords())
                return@setOnClickListener
            //Si pasa validación de datos requeridos, ir a pantalla principal
            SignUpNewUser(email, password)
        }
        textViewIniciarSesion.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    fun SignUpNewUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(EXTRA_LOGIN, "createUserWithEmail:success")

                    val firebaseUser = auth.currentUser
                    if (firebaseUser == null) {
                        Toast.makeText(baseContext, "Error: usuario nulo.", Toast.LENGTH_SHORT).show()
                        return@addOnCompleteListener
                    }

                    val uid = firebaseUser.uid
                    val username = email.substringBefore("@")

                    UserService.createProfile(
                        uid = uid,
                        email = email,
                        username = username,
                        onOk = {
                            Toast.makeText(baseContext, "Perfil creado en Firestore.", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        },
                        onFail = { e ->
                            Log.e(EXTRA_LOGIN, "Error creando perfil Firestore", e)
                            Toast.makeText(baseContext, "Registro ok, pero Firestore falló: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    )

                } else {
                    Log.w(EXTRA_LOGIN, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, task.exception?.message ?: "Authentication failed.", Toast.LENGTH_SHORT).show()
                }

            }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
    }

    private fun validateRequiredData(): Boolean {
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()
        val repeatPassword = editTextRepeatPassword.text.toString().trim()

        // Email vacío
        if (email.isEmpty()) {
            editTextEmail.error = getString(R.string.error_email_required)
            editTextEmail.requestFocus()
            return false
        }

        // Email inválido
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.error = getString(R.string.error_email_invalid)
            editTextEmail.requestFocus()
            return false
        }

        // Password vacío
        if (password.isEmpty()) {
            editTextPassword.error = getString(R.string.error_password_required)
            editTextPassword.requestFocus()
            return false
        }

        // Longitud mínima (recomendado 6 para Firebase)
        if (password.length < 6) {
            editTextPassword.error = getString(R.string.error_password_min_length)
            editTextPassword.requestFocus()
            return false
        }

        // Repeat password vacío
        if (repeatPassword.isEmpty()) {
            editTextRepeatPassword.error = getString(R.string.error_password_required)
            editTextRepeatPassword.requestFocus()
            return false
        }

        return true
    }


    private fun validatePasswords():Boolean{
        val password = editTextPassword.text.toString()
        val repeatPassword = editTextRepeatPassword.text.toString()
        if(password != repeatPassword){
            editTextRepeatPassword.error = getString(R.string.error_password_match)
            editTextRepeatPassword.requestFocus()
            return false
        }
        return true
    }

}
