package com.example.slpn.aktywnosci

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.slpn.R
import com.example.slpn.activity.MainActivity
import com.google.firebase.auth.FirebaseAuth
import java.util.ArrayList
import java.util.HashMap

class RegistrationActivity : AppCompatActivity() {

    private val fbAuth = FirebaseAuth.getInstance()
    val REQUEST_ID_MULTIPLE_PERMISSIONS = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
    }
    override fun onStart() {
        super.onStart()
        isCurrentUser()
    }

    private fun isCurrentUser() {
        fbAuth.currentUser?.let {auth ->
            val intent = Intent(applicationContext, MainActivity::class.java).apply {
                flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            startActivity(intent)
        }
    }

    private fun checkAndRequestPermissions(): Boolean {
        val permissionSendMessage = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.SEND_SMS
        )
        val locationPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS)
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_SMS)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray(),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("Permission", "Permission callback called-------")
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> {
                val perms: MutableMap<String, Int> = HashMap()

                Log.i("Powtorzenie", "1")
                // Initialize the map with both permissions
                perms[Manifest.permission.SEND_SMS] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.RECEIVE_SMS] = PackageManager.PERMISSION_GRANTED

                // Fill with actual results from user
                if (grantResults.size > 0) {
                    var i = 0
                    while (i < permissions.size) {
                        perms[permissions[i]] = grantResults[i]
                        i++
                    }

                    // Check for both permissions
                    if (perms[Manifest.permission.SEND_SMS] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.RECEIVE_SMS] == PackageManager.PERMISSION_GRANTED
                    ) {
                        Log.d("sms", "READ_SMS & RECEIVE_SMS services permission granted")


                        // process the normal flow


                        //else any one or both the permissions are not granted
                    } else {
                        Log.d("Some", "Some permissions are not granted ask again ")


                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission


//                        // shouldShowRequestPermissionRationale will return true


                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.SEND_SMS
                            ) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.RECEIVE_SMS
                            )
                        ) {
                            showDialogOK("READ_SMS and RECEIVE_SMS Services Permission required for this app",
                                DialogInterface.OnClickListener { dialog, which ->
                                    when (which) {
                                        DialogInterface.BUTTON_POSITIVE -> checkAndRequestPermissions()
                                        DialogInterface.BUTTON_NEGATIVE -> {
                                        }
                                    }
                                })
                        } else {


                            //  Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG).show();


                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }
    }

    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", okListener)
            .create()
            .show()
    }
}