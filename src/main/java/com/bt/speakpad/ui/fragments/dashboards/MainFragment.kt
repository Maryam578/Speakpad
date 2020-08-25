package com.bt.speakpad.ui.fragments.dashboards

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bt.speakpad.R
import com.bt.speakpad.helper.utils.LoadFragment
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnAddNotes.setOnClickListener {
            LoadFragment().activity(AddNotesFragment() , AddNotesFragment::class.java.name , requireActivity())
        }
        btnAddReminder.setOnClickListener {
            LoadFragment().activity(AddReminderFragment() , AddReminderFragment::class.java.name , requireActivity())
        }
    }

}