package com.bt.speakpad.ui.activities

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bt.speakpad.R
import com.bt.speakpad.helper.utils.LoadFragment
import com.bt.speakpad.ui.fragments.SplashFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_splash.*
import net.gotev.speech.*
import java.util.*


class SplashActivity : AppCompatActivity(){
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        mAuth = FirebaseAuth.getInstance()

        if (mAuth.currentUser == null) {
            LoadFragment().activity(
                fragment = SplashFragment(),
                name = SplashFragment::class.java.name,
                activity = this@SplashActivity
            )
        }
        else
        {
            val intent = Intent(this@SplashActivity , Dashboard::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count > 1) {
            supportFragmentManager.popBackStackImmediate()
        }
        else {
            finish()
        }

    }


}