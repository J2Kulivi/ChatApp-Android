package com.julivi.chatapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.julivi.chatapp.databinding.ActivityLoginBinding
import com.julivi.chatapp.model.Utilizador

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        avancarParaUsuarios()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        if (auth.currentUser != null) {
            atualizarTokenFCM(auth.currentUser!!.uid)
            verificarPermissaoNotificacaoEAvancar()
            return
        }

        binding.btnEntrar.setOnClickListener { fazerLogin() }
        binding.btnRegistar.setOnClickListener { criarConta() }
    }

    private fun fazerLogin() {
        val email = binding.etEmail.text.toString().trim()
        val senha = binding.etSenha.text.toString().trim()

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preenche o e-mail e a palavra-passe.", Toast.LENGTH_SHORT).show()
            return
        }

        mostrarCarregamento(true)
        auth.signInWithEmailAndPassword(email, senha)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener
                atualizarTokenFCM(uid)
                mostrarCarregamento(false)
                verificarPermissaoNotificacaoEAvancar()
            }
            .addOnFailureListener { e ->
                mostrarCarregamento(false)
                Toast.makeText(this, "Erro ao entrar: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun criarConta() {
        val nome = binding.etNome.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val senha = binding.etSenha.text.toString().trim()

        if (nome.isEmpty() || email.isEmpty() || senha.length < 6) {
            Toast.makeText(this, "Preenche nome, e-mail e palavra-passe (mín. 6 caracteres).", Toast.LENGTH_LONG).show()
            return
        }

        mostrarCarregamento(true)
        auth.createUserWithEmailAndPassword(email, senha)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    val token = if (task.isSuccessful) task.result ?: "" else ""
                    val novoUtilizador = Utilizador(
                        uid = uid,
                        nome = nome,
                        email = email,
                        fcmToken = token
                    )

                    db.collection("utilizadores").document(uid)
                        .set(novoUtilizador)
                        .addOnSuccessListener {
                            mostrarCarregamento(false)
                            verificarPermissaoNotificacaoEAvancar()
                        }
                        .addOnFailureListener { e ->
                            mostrarCarregamento(false)
                            Toast.makeText(this, "Erro ao guardar perfil: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                mostrarCarregamento(false)
                Toast.makeText(this, "Erro ao registar: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun atualizarTokenFCM(uid: String) {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            db.collection("utilizadores").document(uid)
                .set(mapOf("fcmToken" to token), SetOptions.merge())
        }
    }

    private fun verificarPermissaoNotificacaoEAvancar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                return
            }
        }
        avancarParaUsuarios()
    }

    private fun avancarParaUsuarios() {
        startActivity(Intent(this, UsuariosActivity::class.java))
        finish()
    }

    private fun mostrarCarregamento(ativo: Boolean) {
        binding.progressBar.visibility = if (ativo) View.VISIBLE else View.GONE
        binding.btnEntrar.isEnabled = !ativo
        binding.btnRegistar.isEnabled = !ativo
    }
}
