package com.julivi.chatapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.julivi.chatapp.databinding.ItemMensagemEnviadaBinding
import com.julivi.chatapp.databinding.ItemMensagemRecebidaBinding
import com.julivi.chatapp.model.Mensagem
import java.text.SimpleDateFormat
import java.util.Locale

class MensagensAdapter(
    private val listaMensagens: List<Mensagem>,
    private val meuUid: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TIPO_ENVIADA = 1
        private const val TIPO_RECEBIDA = 2
    }

    inner class EnviadaViewHolder(val binding: ItemMensagemEnviadaBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class RecebidaViewHolder(val binding: ItemMensagemRecebidaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return if (listaMensagens[position].remetenteId == meuUid) {
            TIPO_ENVIADA
        } else {
            TIPO_RECEBIDA
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TIPO_ENVIADA) {
            val binding = ItemMensagemEnviadaBinding.inflate(inflater, parent, false)
            EnviadaViewHolder(binding)
        } else {
            val binding = ItemMensagemRecebidaBinding.inflate(inflater, parent, false)
            RecebidaViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mensagem = listaMensagens[position]
        val horaFormatada = mensagem.dataEnvio?.toDate()?.let { date ->
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
        } ?: "..."

        if (holder is EnviadaViewHolder) {
            holder.binding.tvTextoMensagem.text = mensagem.texto
            holder.binding.tvHoraMensagem.text = horaFormatada
        } else if (holder is RecebidaViewHolder) {
            holder.binding.tvTextoMensagem.text = mensagem.texto
            holder.binding.tvHoraMensagem.text = horaFormatada
        }
    }

    override fun getItemCount(): Int = listaMensagens.size
}
