package com.julivi.chatapp.model

import com.google.firebase.Timestamp

data class Utilizador(
    val uid: String = "",
    val nome: String = "",
    val email: String = "",
    val fcmToken: String = ""
)

data class Mensagem(
    val id: String = "",
    val remetenteId: String = "",
    val destinatarioId: String = "",
    val texto: String = "",
    val dataEnvio: Timestamp? = null,
    val lida: Boolean = false
)

data class EnviarMensagemRequest(
    val destinatarioId: String,
    val texto: String
)

data class EnviarMensagemResponse(
    val sucesso: Boolean,
    val mensagemId: String? = null,
    val chatId: String? = null,
    val notificacaoEnviada: Boolean? = null,
    val erro: String? = null
)
