package co.cdmunoz.displaycontacts

import android.Manifest
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.Contacts
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_load_contacts.listContacts
import kotlinx.android.synthetic.main.activity_load_contacts.loadContacts

class LoadContactsActivity : AppCompatActivity() {

  companion object {
    val PERMISSIONS_REQUEST_READ_CONTACTS = 100
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_load_contacts)

    loadContacts.setOnClickListener { loadContacts() }
  }

  private fun loadContacts() {
    var builder = StringBuilder()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
        Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
      requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS),
          PERMISSIONS_REQUEST_READ_CONTACTS)
      //callback onRequestPermissionsResult
    } else {
      builder = getContacts()
      listContacts.text = builder.toString()
    }
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
      grantResults: IntArray) {
    if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        loadContacts()
      } else {
        toast("Permission must be granted in order to display contacts information")
      }
    }
  }

  private fun getContacts(): StringBuilder {
    val builder = StringBuilder()
    val resolver: ContentResolver = contentResolver;
    val cursor = resolver.query(Contacts.CONTENT_URI, null, null, null,
        null)

    if (cursor.count > 0) {
      while (cursor.moveToNext()) {
        val id = cursor.getString(cursor.getColumnIndex(Contacts._ID))
        val name = cursor.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME))
        val phoneNumber = (cursor.getString(
            cursor.getColumnIndex(Contacts.HAS_PHONE_NUMBER))).toInt()

        if (phoneNumber > 0) {
          val cursorPhone = contentResolver.query(
              Phone.CONTENT_URI,
              null, Phone.CONTACT_ID + "=?", arrayOf(id), null)

          if(cursorPhone.count > 0) {
            while (cursorPhone.moveToNext()) {
              val phoneNumValue = cursorPhone.getString(
                  cursorPhone.getColumnIndex(Phone.NUMBER))
              builder.append("Contact: ").append(name).append(", Phone Number: ").append(
                  phoneNumValue).append("\n\n")
            }
          }
          cursorPhone.close()
        }
      }
    } else {
      toast("No contacts available!")
    }
    cursor.close()
    return builder
  }
}