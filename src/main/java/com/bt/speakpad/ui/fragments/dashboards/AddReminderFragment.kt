package com.bt.speakpad.ui.fragments.dashboards

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bt.speakpad.R
import com.bt.speakpad.helper.utils.CheckInternet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_add_reminder.*
import java.text.SimpleDateFormat
import java.util.*


class AddReminderFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var calendar: Calendar
    private lateinit var mAuth: FirebaseAuth
    private val TAG = AddReminderFragment::class.java.name

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mAuth = FirebaseAuth.getInstance()
        return inflater.inflate(R.layout.fragment_add_reminder, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calendar = Calendar.getInstance()

        val date = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDate()
        }

        btnDatePicker.setOnClickListener {
            DatePickerDialog(requireContext() , date , calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        btnTimePicker.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
            val minute = mcurrentTime[Calendar.MINUTE]
            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(requireContext(),
                OnTimeSetListener { _, selectedHour, selectedMinute ->
                    txtTime.text = "$selectedHour:$selectedMinute"
                }, hour, minute, true
            ) //Yes 24 hour time

            mTimePicker.setTitle("Select Time")
            mTimePicker.show()

        }

        fabSave.setOnClickListener {
            val taskName = edTxtTaskName.text.toString()
            val description = edTxtDescription.text.toString()
            val time = txtTime.text.toString()
            val dateStr = txtDate.text.toString()

            if (taskName.isEmpty()) {
                edTxtTaskName.error = "Required field"
                return@setOnClickListener
            }
            if (description.isEmpty())
            {
                edTxtDescription.error = "Required field"
                return@setOnClickListener
            }
            if (time == "Time")
            {
                Toast.makeText(requireContext(), "Please select time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (dateStr == "Date") {
                Toast.makeText(requireContext(), "Please select time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            saveReminderOnFirebase(taskName = taskName , description = description , date = dateStr , time = time)
        }


    }

    private fun updateDate() {
        val myFormat = "MM/dd/yy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        txtDate.text = sdf.format(calendar.time)
    }

    private fun saveReminderOnFirebase(taskName: String , description: String , date: String , time: String) {
        if (CheckInternet().isNetworkAvailable(requireContext())) {
            val dialog  = ProgressDialog(requireContext())
            dialog.setMessage("Please wait...")
            dialog.setCancelable(false)
            dialog.show()

            val database = FirebaseDatabase.getInstance().reference
            val map = HashMap<String , String>()
            map["taskName"] = taskName
            map["description"] = description
            map["date"] = date
            map["time"] = time

            database.child("Reminders").child(mAuth.currentUser?.uid ?: "").push().setValue(map).addOnCompleteListener {
                dialog.dismiss()
                if (it.isSuccessful){
                    Toast.makeText(requireContext(), "Reminders is saved successfully ", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(requireContext(), "Something went wrong please try again later", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "postNotesOnFirebase: exception => ", it.exception)
                }
            }
        }
        else {
            Toast.makeText(requireContext(), "Unable to connect to server. Please make sure you have an active internet.", Toast.LENGTH_SHORT).show()
        }
    }

}