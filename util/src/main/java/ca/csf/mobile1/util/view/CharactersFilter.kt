package ca.csf.mobile1.util.view

import android.text.InputFilter
import android.text.SpannableStringBuilder
import android.text.Spanned

private val DEFAULT_ACCEPTED_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ .".toCharArray()

/**
 * Filtre de caractères. Permet d'indiquer à un "EditText" d'accepter seulement un certain lot de caractères.
 * <p>
 * Par défaut, accepte uniquement les caractères suivants :
 * <p>
 * a b c d e f g h i j k l m n o p q r s t u v w x y z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z (espace) (point)
 */
class CharactersFilter(private val acceptedCharacters: CharArray = DEFAULT_ACCEPTED_CHARACTERS) : InputFilter {

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        destinationStart: Int,
        destinationEnd: Int
    ): CharSequence? {
        //Source code taken from class android.text.method.NumberKeyListener. Added little tweaks.
        var end = end
        var i: Int = start
        while (i < end) {
            if (isNotAccepted(source[i]))
                break
            i++
        }

        if (i == end) return null
        if (end - start == 1) return ""

        val filtered = SpannableStringBuilder(source, start, end)
        i -= start
        end -= start

        for (j in end - 1 downTo i) {
            if (isNotAccepted(source[j]))
                filtered.delete(j, j + 1)
        }

        return filtered
    }

    private fun isNotAccepted(charToVerify: Char): Boolean {
        return !acceptedCharacters.contains(charToVerify)
    }
}