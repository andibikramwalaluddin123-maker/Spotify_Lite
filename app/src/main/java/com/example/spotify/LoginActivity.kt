package com.example.spotify

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.*
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import androidx.credentials.exceptions.GetCredentialException
import com.google.firebase.auth.FirebaseUser


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var googleButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(this)

        emailEditText = findViewById(R.id.editTextEmail)
        passwordEditText = findViewById(R.id.editTextPassword)
        loginButton = findViewById(R.id.buttonLogin)
        registerButton = findViewById(R.id.buttonRegister)
        googleButton = findViewById(R.id.btnGoogleLogin)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                toast("Email dan password wajib diisi")
                return@setOnClickListener
            }

            loginUser(email, password)
        }





        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                toast("Email dan password wajib diisi")
                return@setOnClickListener
            }

            if (password.length < 6) {
                toast("Password minimal 6 karakter")
                return@setOnClickListener
            }

            registerUser(email, password)
        }


        googleButton.setOnClickListener {
            signInWithGoogle()
        }
    }


    override fun onStart() {
        super.onStart()
       //  updateUI(auth.currentUser)
    }

    // ================= EMAIL LOGIN =================

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                toast("Login berhasil")
                updateUI(auth.currentUser)
            }
            .addOnFailureListener {
                toast("Login gagal: ${it.message}")
            }
    }



    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                toast("Registrasi berhasil, silakan login")
                emailEditText.text.clear()
                passwordEditText.text.clear()
            }
            .addOnFailureListener {
                toast("Registrasi gagal: ${it.message}")
            }
    }

    // ================= GOOGLE LOGIN =================

    private fun signInWithGoogle() {
        try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(getString(R.string.client_id))
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            lifecycleScope.launch {
                try {
                    val result = credentialManager.getCredential(
                        this@LoginActivity,
                        request
                    )
                    handleGoogleResult(result)
                } catch (e: Exception) {
                    Log.e("GOOGLE_LOGIN", "Error", e)
                    toast("Google Login error")
                }
            }
        } catch (e: Exception) {
            Log.e("GOOGLE_LOGIN", "Fatal Error", e)
        }
    }


    private fun handleGoogleResult(result: GetCredentialResponse) {
        val credential = result.credential

        if (
            credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleIdTokenCredential =
                GoogleIdTokenCredential.createFrom(credential.data)

            firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
        } else {
            Log.w("LoginActivity", "Credential bukan Google ID Token")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val firebaseCredential =
            GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(firebaseCredential)
            .addOnSuccessListener {
                toast("Login Google berhasil")
                updateUI(auth.currentUser)
            }
            .addOnFailureListener {
                toast("Login Google gagal")
            }
    }

    // ================= UI =================

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // Login berhasil → pindah ke MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }


    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
