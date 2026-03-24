package com.samaali.codememo.ui.extensions

/**
 * Extension properties pour String
 */

// Exemple 1 : Vérifier si une String est vide ou null de manière sûre
val String?.isNullOrBlankExt: Boolean
    get() = this.isNullOrBlank()

// Exemple 2 : Capitaliser la première lettre
val String.capitalized: String
    get() = this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase() else it.toString()
    }

// Exemple 3 : Supprimer les accents (très utile pour la recherche dans ton app)
val String.normalized: String
    get() = java.text.Normalizer.normalize(this, java.text.Normalizer.Form.NFD)
        .replace("\\p{Mn}+".toRegex(), "")
        .lowercase()

// Exemple 4 : Vérifier si c'est un email valide (basique)
val String.isValidEmail: Boolean
    get() = this.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

// Exemple 5 : Tronquer une chaîne avec "..."
fun String.truncate(maxLength: Int): String {
    return if (this.length > maxLength) {
        this.take(maxLength) + "..."
    } else {
        this
    }
}