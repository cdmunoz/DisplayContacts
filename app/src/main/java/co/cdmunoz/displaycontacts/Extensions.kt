package co.cdmunoz.displaycontacts

import android.content.Context
import android.database.Cursor
import android.widget.Toast

fun Context.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
  Toast.makeText(applicationContext, message, duration).show()
}

