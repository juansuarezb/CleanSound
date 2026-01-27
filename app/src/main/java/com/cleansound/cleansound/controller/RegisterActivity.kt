package com.cleansound.cleansound.controller

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(EXTRA_LOGIN, "createUserWithEmail:success")
                    val user = auth.currentUser
                    Toast.makeText(
                        baseContext, "New user saved.",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(EXTRA_LOGIN, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    //updateUI(null)
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
        val repeatPassword = editTextRepeatPassword.text.toString()
        if (email.isEmpty()) {
            editTextEmail.setError(getString(R.string.error_email_required))
            editTextEmail.requestFocus()
            return false
        }
        if(password.isEmpty()){
            editTextPassword.setError(getString(R.string.error_password_required))
            editTextPassword.requestFocus()
            return false
        }
        if(repeatPassword.isEmpty()){
            editTextRepeatPassword.setError(getString(R.string.error_password_required))
            editTextRepeatPassword.requestFocus()
            return false
        }
        if (password.length < 8) {
            editTextPassword.setError(getString(R.string.error_password_min_length))
            editTextPassword.requestFocus()
            return false
        }
        if (repeatPassword.length < 8) {
            editTextPassword.setError(getString(R.string.error_password_min_length))
            editTextPassword.requestFocus()
            return false
        }
        return true;
    }
    private fun validatePasswords():Boolean{
        val password = editTextPassword.text.toString()
        val repeatPassword = editTextRepeatPassword.text.toString()
        if(password != repeatPassword){
            editTextRepeatPassword.setError(getString(R.string.error_password_match))
            return false
        }
        return true
    }

}
