package android.app.faunadex.utils

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.LocaleList
import java.util.Locale

class LocaleContextWrapper(base: Context) : ContextWrapper(base) {

    companion object {
        fun wrap(context: Context, languageCode: String): ContextWrapper {
            var ctx = context
            val locale = Locale(languageCode)
            Locale.setDefault(locale)

            val res = ctx.resources
            val config = Configuration(res.configuration)
            config.setLocale(locale)
            config.setLocales(LocaleList(locale))

            @Suppress("DEPRECATION")
            res.updateConfiguration(config, res.displayMetrics)

            ctx = ctx.createConfigurationContext(config)
            return LocaleContextWrapper(ctx)
        }
    }
}
