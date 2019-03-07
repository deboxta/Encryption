package ca.csf.mobile1.util.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.GridLayout
import android.widget.TextView
import ca.csf.mobile1.util.R

private const val DEFAULT_KEY = 0

/**
 * Dialogue permettant de saisir une clé de chiffrement via une interface simplifiée.
 */
@SuppressLint("InflateParams")
class KeyPickerDialog private constructor(context: Context, private val keyLength: Int) {
    private var confirmListener: ConfirmListener? = null
    private var cancelListener: CancelListener? = null

    private var dialog: AlertDialog? = null
    private val dialogBuilder: AlertDialog.Builder
    private val digitsTextViews: MutableList<TextView>

    init {
        assert(keyLength <= 0) { "\"keyLength\" must be greater than 0." }

        val layoutInflater = LayoutInflater.from(context)
        val gridLayout = layoutInflater.inflate(R.layout.view_key_picker_dialog, null) as GridLayout

        gridLayout.columnCount = keyLength
        gridLayout.setTag(R.id.keyPickerDialogTag, this)

        //Top "Up" buttons
        for (i in 0 until keyLength) {
            layoutInflater.inflate(R.layout.view_arrow_up, gridLayout)
            gridLayout.getChildAt(i).setOnClickListener { onArrowButtonClicked(i, 1) }
        }

        //Middle "Digits" text views
        digitsTextViews = mutableListOf()
        for (i in 0 until keyLength) {
            layoutInflater.inflate(R.layout.view_number, gridLayout)
            digitsTextViews.add(gridLayout.getChildAt(i + keyLength) as TextView)
        }

        //Bottom "Down" buttons
        for (i in 0 until keyLength) {
            layoutInflater.inflate(R.layout.view_arrow_down, gridLayout)
            gridLayout.getChildAt(i + keyLength * 2).setOnClickListener { onArrowButtonClicked(i, -1) }
        }

        //Set initial key
        setKey(DEFAULT_KEY)

        dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setTitle(R.string.text_key)
        dialogBuilder.setView(gridLayout)
        dialogBuilder.setPositiveButton(android.R.string.ok) { _, _ -> onOkButtonClicked() }
        dialogBuilder.setNegativeButton(android.R.string.cancel) { _, _ -> onCancelButtonClicked() }
    }

    /**
     * Assigne un [ConfirmListener] à ce KeyPickerDialog. Cet objet sera appellé lorsque
     * l'utilisateur cliquera sur "Ok".
     *
     * @param confirmListener [ConfirmListener] à notifier d'un clic sur le bouton "Ok".
     * @return KeyPickerDialog actuel.
     */
    fun setConfirmAction(confirmListener: ConfirmListener): KeyPickerDialog {
        this.confirmListener = confirmListener

        return this
    }

    /**
     * Assigne un [CancelListener] à ce KeyPickerDialog. Cet objet sera appellé lorsque
     * l'utilisateur cliquera sur "Cancel".
     *
     * @param cancelListener [CancelListener] à notifier d'un clic sur le bouton "Cancel".
     * @return KeyPickerDialog actuel.
     */
    fun setCancelAction(cancelListener: CancelListener): KeyPickerDialog {
        this.cancelListener = cancelListener

        return this
    }

    /**
     * Affiche le KeyPickerDialog.
     *
     * @return KeyPickerDialog actuel.
     */
    fun show(): KeyPickerDialog {
        val dialog = dialogBuilder.create()
        dialog.show()

        this.dialog = dialog
        return this
    }

    /**
     * Modifie la clé affichée par ce KeyPickerDialog.
     *
     * @param key Clé à afficher.
     * @return KeyPickerDialog actuel.
     */
    @SuppressLint("DefaultLocale")
    fun setKey(key: Int): KeyPickerDialog {
        if (key < 0)
            throw IllegalArgumentException("\"key\" must be greater or equal to 0.")
        if (key > Math.pow(10.0, keyLength.toDouble()).toInt() - 1)
            throw IllegalArgumentException("\"key\" must be less than 10^$keyLength.")

        val keyAsString = ("%0" + keyLength + "d").format(key)
        for (i in digitsTextViews.indices) {
            digitsTextViews[i].text = keyAsString[i].toString()
        }

        return this
    }

    /**
     * Appelle le [ConfirmListener] du dialogue, s'il existe. Effectue aussi toutes les actions reliées à un clic,
     * tel que les animations, comme si un utilisateur avait effectué le clic.
     */
    fun performOk() {
        dialog?.getButton(DialogInterface.BUTTON_POSITIVE)?.performClick()
    }

    /**
     * Appelle le [CancelListener] du dialogue, s'il existe. Effectue aussi toutes les actions reliées à un clic,
     * tel que les animations, comme si un utilisateur avait effectué le clic.
     */
    fun performCancel() {
        dialog?.getButton(DialogInterface.BUTTON_NEGATIVE)?.performClick()
    }

    private fun onArrowButtonClicked(index: Int, increment: Int) {
        val numberTextView = digitsTextViews[index]

        var currentValue = numberTextView.text.toString().toInt()
        currentValue = (currentValue + increment) % 10
        currentValue = if (currentValue < 0) currentValue + 10 else currentValue

        numberTextView.text = currentValue.toString()
    }

    private fun onOkButtonClicked() {
        confirmListener?.onKeyPickerDialogConfirm(digitsTextViews.joinToString(separator = "") { it.text }.toInt())
        dialog?.dismiss()
    }

    private fun onCancelButtonClicked() {
        cancelListener?.onKeyPickerDialogCancel()
        dialog?.cancel()
    }

    /**
     * Interface que tous les objets désirant être notifié du clic sur le bouton "Ok" du [KeyPickerDialog]
     * doivent implémenter. Voir [KeyPickerDialog.setConfirmAction].
     */
    interface ConfirmListener {
        /**
         * Survient lorsque le bouton "Ok" d'un [KeyPickerDialog] a été appuyé par l'utilisteur.
         *
         * @param key Clé sélectionnée par l'utilisateur via le [KeyPickerDialog].
         */
        fun onKeyPickerDialogConfirm(key: Int)
    }

    /**
     * Interface que tous les objets désirant être notifié du clic sur le bouton "Cancel" du [KeyPickerDialog]
     * doivent implémenter. Voir [KeyPickerDialog.setCancelAction].
     */
    interface CancelListener {
        /**
         * Survient lorsque le bouton "Cancel" d'un [KeyPickerDialog] a été appuyé par l'utilisteur.
         */
        fun onKeyPickerDialogCancel()
    }

    companion object {

        /**
         * Crée un nouveau KeyPickerDialog. Notez qu'après appel à cette fonction, le dialogue n'est pas encore affiché.
         *
         * @param context Contexte de création du dialogue. Généralement, l'activité courante.
         * @return KeyPickerDialog nouvellement créé.
         */
        @JvmStatic
        fun make(context: Context, keyLength: Int): KeyPickerDialog {
            return KeyPickerDialog(context, keyLength)
        }

    }

}