package com.julivi.chatapp.network

import com.julivi.chatapp.model.EnviarMensagemRequest
import com.julivi.chatapp.model.EnviarMensagemResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("api/enviar-mensagem")
    suspend fun enviarMensagem(
        @Header("Authorization") tokenBearer: String,
        @Body request: EnviarMensagemRequest
    ): Response<EnviarMensagemResponse>
}
