package com.julivi.chatapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.julivi.chatapp.adapter.MensagensAdapter
import com.julivi.chatapp.databinding.ActivityChatBinding
import com.julivi.chatapp.model.EnviarMensagemRequest
import com.julivi.chatapp.model.Mensagem
import com.julivi.chatapp.network.RetrofitClient
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var meuUid: String
    private lateinit var destinatarioId: String
    private lateinit var destinatarioNome: String
    private lateinit var chatId: String

    private val listaMensagens = mutableListOf<Mensagem>()
    private lateinit var adapter: MensagensAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        meuUid = auth.currentUser?.uid ?: run {
            finish()
            return
        }

        destinatarioId = intent.getStringExtra("DESTINATARIO_ID") ?: ""
        destinatarioNome = intent.getStringExtra("DESTINATARIO_NOME") ?: "Conversa"

        if (destinatarioId.isEmpty()) {
            Toast.makeText(this, "Destinatário inválido.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        chatId = listOf(meuUid, destinatarioId).sorted().joinToString("_")

        configurarToolbar()
        configurarRecyclerView()
        escutarMensagensEmTempoReal()

        binding.btnEnviar.setOnClickListener {
            val texto = binding.etMensagem.text.toString().trim()
            if (texto.isNotEmpty()) {
                enviarMensagemParaServidor(texto)
            }
        }
    }

    private fun configurarToolbar() {
        binding.toolbarChat.title = destinatarioNome
        binding.toolbarChat.setNavigationOnClickListener {
            finish()
        }
    }

    private fun configurarRecyclerView() {
        adapter = MensagensAdapter(listaMensagens, meuUid)
        val layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        binding.rvMensagens.layoutManager = layoutManager
        binding.rvMensagens.adapter = adapter
    }

    private fun escutarMensagensEmTempoReal() {
        db.collection("conversas")
            .document(chatId)
            .collection("mensagens")
            .orderBy("dataEnvio", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    listaMensagens.clear()
                    for (doc in snapshots.documents) {
                        val msg = doc.toObject(Mensagem::class.java)?.copy(id = doc.id)
                        if (msg != null) {
                            listaMensagens.add(msg)
                        }
                    }
                    adapter.notifyDataSetChanged()
                    if (listaMensagens.isNotEmpty()) {
                        binding.rvMensagens.scrollToPosition(listaMensagens.size - 1)
                    }
                }
            }
    }

    private fun enviarMensagemParaServidor(texto: String) {
        binding.etMensagem.setText("")
        binding.btnEnviar.isEnabled = false

        lifecycleScope.launch {
            try {
                val tokenResult = auth.currentUser?.getIdToken(false)?.await()
                val token = tokenResult?.token

                if (token == null) {
                    Toast.makeText(this@ChatActivity, "Erro de autenticação.", Toast.LENGTH_SHORT).show()
                    binding.btnEnviar.isEnabled = true
                    return@launch
                }

                val request = EnviarMensagemRequest(
                    destinatarioId = destinatarioId,
                    texto = texto
                )

                val response = RetrofitClient.api.enviarMensagem("Bearer $token", request)

                if (!response.isSuccessful || response.body()?.sucesso != true) {
                    val erroMsg = response.body()?.erro ?: "Falha no servidor (${response.code()})"
                    Toast.makeText(this@ChatActivity, erroMsg, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ChatActivity,
                    "Sem ligação ao servidor: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                binding.btnEnviar.isEnabled = true
            }
        }
    }
}
