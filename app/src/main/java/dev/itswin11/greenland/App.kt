package dev.itswin11.greenland

import android.app.Application
import dev.itswin11.greenland.services.TokenService

class App : Application() {
    companion object {
        lateinit var instance: App
    }

    init {
        instance = this
    }

    val tokenService: TokenService = TokenService(this)
}