package com.bt.speakpad.helper.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import com.bt.speakpad.R
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener


class Permissions {
     fun requestMultiPermission(activity: Activity , context: Context) {
        Dexter.withActivity(activity)
            .withPermissions(
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.RECORD_AUDIO


            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.isAnyPermissionPermanentlyDenied) {
                        showSettingsDialog(context)
                    }
                }


            }).withErrorListener { error ->
                Toast.makeText(
                    context,
                    "Error occurred! $error",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .onSameThread()
            .check()
    }

    private fun showSettingsDialog(context: Context) {
        try {
            val builder = AlertDialog.Builder(context , R.style.MyDialogTheme)
            builder.setTitle("Need Permissions")
            builder.setMessage("Please provide permission to proceed")
            //builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
            builder.setPositiveButton("GOTO SETTINGS") { dialog, which ->
                dialog.cancel()
                openSettings(context)
            }
            builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
            builder.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun openSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", context .packageName, null)
        intent.data = uri
    }
}