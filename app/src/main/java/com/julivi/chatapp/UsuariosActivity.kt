package com.julivi.chatapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.julivi.chatapp.adapter.UsuariosAdapter
import com.julivi.chatapp.databinding.ActivityUsuariosBinding
import com.julivi.chatapp.model.Utilizador

class UsuariosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUsuariosBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private val listaUsuarios = mutableListOf<Utilizador>()
    private lateinit var adapter: UsuariosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsuariosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        configurarToolbar()
        configurarRecyclerView()
        carregarUtilizadores()
    }

    private fun configurarToolbar() {
        binding.toolbarUsuarios.menu.add("Sair").setOnMenuItemClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            true
        }
    }

    private fun configurarRecyclerView() {
        adapter = UsuariosAdapter(listaUsuarios) { usuarioSelecionado ->
            val intent = Intent(this, ChatActivity::class.java).apply {
                putExtra("DESTINATARIO_ID", usuarioSelecionado.uid)
                putExtra("DESTINATARIO_NOME", usuarioSelecionado.nome)
            }
            startActivity(intent)
        }

        binding.rvUsuarios.layoutManager = LinearLayoutManager(this)
        binding.rvUsuarios.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )
        binding.rvUsuarios.adapter = adapter
    }

    private fun carregarUtilizadores() {
        val meuUid = auth.currentUser?.uid ?: return

        db.collection("utilizadores")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Toast.makeText(this, "Erro ao carregar contactos: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    listaUsuarios.clear()
                    for (doc in snapshots.documents) {
                        val utilizador = doc.toObject(Utilizador::class.java)
                        if (utilizador != null && utilizador.uid != meuUid) {
                            listaUsuarios.add(utilizador)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }
    }
}
