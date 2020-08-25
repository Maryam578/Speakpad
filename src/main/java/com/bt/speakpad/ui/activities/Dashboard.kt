package com.bt.speakpad.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bt.speakpad.R
import com.bt.speakpad.helper.utils.LoadFragment
import com.bt.speakpad.ui.fragments.dashboards.MainFragment

class Dashboard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        LoadFragment().activity(MainFragment() , MainFragment::class.java.name , this)
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