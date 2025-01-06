import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.muratguzel.trackyourtime.R
import com.muratguzel.trackyourtime.databinding.MyDialogBinding

class CustomDialog(

    val context: Context,
    val title: String = "Başlık",
    val message: String = "Mesaj",
    val positiveButtonText: String = "Evet",
    val negativeButtonText: String = "Hayır",
    val positiveButtonAction: (() -> Unit)? = null,
    val negativeButtonAction: (() -> Unit)? = null
) {
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    fun showDialog() {
        val dialogBinding =MyDialogBinding.inflate(layoutInflater)
        val myDialog = Dialog(context)
        myDialog.setContentView(dialogBinding.root)

        myDialog.setCancelable(false)
        myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        myDialog.show()



        // Başlık ve mesajı güncelle
        dialogBinding.alertTitle.text = title
        dialogBinding.alertMessage.text = message

        // Buton metinlerini güncelle
        dialogBinding.alertYes.text = positiveButtonText
        if (negativeButtonText.isEmpty()){
            dialogBinding.alertNo.visibility = View.GONE
        } else {
            dialogBinding.alertNo.text = negativeButtonText
            dialogBinding.alertNo.visibility = View.VISIBLE

        }

        // Yes button tıklama işlemi
        dialogBinding.alertYes.setOnClickListener {
            positiveButtonAction?.invoke()
            myDialog.dismiss()
        }

        // No button tıklama işlemi
        dialogBinding.alertNo.setOnClickListener {
            negativeButtonAction?.invoke()
            myDialog.dismiss()
        }
    }
}
