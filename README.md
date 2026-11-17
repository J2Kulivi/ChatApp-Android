# 📱 ChatApp Android (`com.julivi.chatapp`)

Aplicativo Android nativo desenvolvido em **Kotlin** para troca de mensagens instantâneas em tempo real, utilizando uma arquitetura híbrida que combina **Firebase** (autenticação, sincronização em tempo real e notificações *push*) com um **servidor próprio em Node.js** alojado no Render.

---

## 🏗️ Arquitetura do Sistema

1. **Autenticação:** Feita via **Firebase Authentication** (E-mail e Palavra-passe).
2. **Envio de Mensagens:** O aplicativo envia pedidos HTTP (`POST`) autenticados com Token JWT para a API **Node.js (Render)** utilizando **Retrofit + Coroutines**.
3. **Receção em Tempo Real:** O cliente escuta diretamente o **Cloud Firestore** através de *Snapshot Listeners* para atualizar o ecrã instantaneamente.
4. **Notificações Push:** Geridas pelo **Firebase Cloud Messaging (FCM)** através do serviço em segundo plano `MeuFirebaseMessagingService`.

---

## 🛠️ Tecnologias Utilizadas

* **Linguagem:** Kotlin 2.4.21
* **SDK Android:** Compile SDK 34 | Min SDK 24 (Android 7.0+)
* **Firebase BoM 35.2.0:** Auth, Cloud Firestore, Cloud Messaging (FCM) e Analytics
* **Rede:** Retrofit 2.11.0, OkHttp 4.12.0 e Gson
* **Assincronismo:** Kotlin Coroutines
* **CI/CD:** GitHub Actions (compilação automática do APK na nuvem)

---

## 📂 Estrutura do Projeto

```text
app/src/main/
├── AndroidManifest.xml
├── java/com/julivi/chatapp/
│   ├── adapter/
│   │   ├── MensagensAdapter.kt          # Balões de chat (enviadas e recebidas)
│   │   └── UsuariosAdapter.kt           # Lista de contactos disponíveis
│   ├── model/
│   │   └── Modelos.kt                   # Data classes (Utilizador, Mensagem, Payloads API)
│   ├── network/
│   │   ├── ApiService.kt                # Rotas REST do servidor Node.js
│   │   └── RetrofitClient.kt            # Configuração do cliente HTTP e URL do Render
│   ├── service/
│   │   └── MeuFirebaseMessagingService.kt # Gestão de tokens e alertas Push (FCM)
│   ├── ChatActivity.kt                  # Sala de conversação em tempo real
│   ├── LoginActivity.kt                 # Ecrã de Login, Registo e permissão de notificações
│   └── UsuariosActivity.kt              # Lista de utilizadores registados
└── res/
    └── layout/                          # Interfaces XML (Material Design 3)
```

---

## ⚙️ Como Configurar e Compilar

1. **Firebase:**
   * Adicione o ficheiro `google-services.json` do seu projeto Firebase dentro da pasta `app/`.
2. **Ligação ao Servidor (Render):**
   * Abra `app/src/main/java/com/julivi/chatapp/network/RetrofitClient.kt`.
   * Atualize a constante `BASE_URL` com o endereço HTTPS do seu serviço no Render (ex: `https://onrender.com`).
3. **Gerar o APK pelo GitHub Actions:**
   * Qualquer *commit* no ramo `main` dispara automaticamente o fluxo `.github/workflows/build-apk.yml`.
   * Aceda ao separador **Actions** no GitHub, entre na execução concluída e descarregue o artefacto **`ChatApp-APK`**.
