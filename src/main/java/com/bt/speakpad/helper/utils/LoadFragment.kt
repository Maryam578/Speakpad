package com.bt.speakpad.helper.utils

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bt.speakpad.R
import java.lang.Exception

class LoadFragment {
    fun activity(fragment: Fragment, name: String , activity: FragmentActivity) {
        try {
            val fm = activity.supportFragmentManager
            val fragmentTransaction = fm.beginTransaction()
            fragmentTransaction.replace(R.id.frameLayout, fragment, name).addToBackStack(null)
            fragmentTransaction.commit()
        }
        catch (e: Exception) {

        }
    }

    fun activity(fragment: Fragment, name: String , activity: FragmentActivity , bundle: Bundle) {
        val fm = activity.supportFragmentManager
        val fragmentTransaction = fm.beginTransaction()
        fragment.arguments = bundle
        fragmentTransaction.replace(R.id.frameLayout, fragment  , name).addToBackStack(null)
        fragmentTransaction.commit()
    }
}