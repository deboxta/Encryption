package ca.csf.mobile1.tp2.application

import android.app.Application
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import org.koin.android.ext.android.startKoin
import org.koin.dsl.module.module

/*
*              _______ _______ ______ _   _ _______ _____ ____  _   _    _   _   _   _   _   _   _
*           /\|__   __|__   __|  ____| \ | |__   __|_   _/ __ \| \ | |  | | | | | | | | | | | | | |
*          /  \  | |     | |  | |__  |  \| |  | |    | || |  | |  \| |  | | | | | | | | | | | | | |
*         / /\ \ | |     | |  |  __| | . ` |  | |    | || |  | | . ` |  | | | | | | | | | | | | | |
*        / ____ \| |     | |  | |____| |\  |  | |   _| || |__| | |\  |  |_| |_| |_| |_| |_| |_| |_|
*       /_/    \_\_|     |_|  |______|_| \_|  |_|  |_____\____/|_| \_|  (_) (_) (_) (_) (_) (_) (_)
*
* Toute modification au sein de ce fichier peut avoir de graves conséquenes, telles que, mais sans s'y limiter :
*  - Bogues
*  - Erreurs de compilation
*  - Céphalée de tension
*
* Veuillez donc quitter immédiatement ce fichier et ne plus jamais y revenir.
*
*/

internal class Tp2Application : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin(this, listOf(module {
            single { OkHttpClient() }
            single { ObjectMapper() }
        }))

    }

}