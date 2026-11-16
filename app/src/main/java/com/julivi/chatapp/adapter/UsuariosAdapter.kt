package com.julivi.chatapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.julivi.chatapp.databinding.ItemUsuarioBinding
import com.julivi.chatapp.model.Utilizador

class UsuariosAdapter(
    private val listaUsuarios: List<Utilizador>,
    private val onUsuarioClick: (Utilizador) -> Unit
) : RecyclerView.Adapter<UsuariosAdapter.UsuarioViewHolder>() {

    inner class UsuarioViewHolder(val binding: ItemUsuarioBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val binding = ItemUsuarioBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return UsuarioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = listaUsuarios[position]
        holder.binding.tvNomeUsuario.text = usuario.nome
        holder.binding.tvEmailUsuario.text = usuario.email
        holder.itemView.setOnClickListener { onUsuarioClick(usuario) }
    }

    override fun getItemCount(): Int = listaUsuarios.size
}
