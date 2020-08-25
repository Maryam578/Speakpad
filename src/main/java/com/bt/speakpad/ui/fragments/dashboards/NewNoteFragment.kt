package com.bt.speakpad.ui.fragments.dashboards

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bt.speakpad.R
import com.bt.speakpad.helper.utils.CheckInternet
import com.bt.speakpad.helper.utils.Const
import com.bt.speakpad.helper.utils.Permissions
import com.bt.speakpad.ui.activities.Dashboard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_new_note.*
import net.gotev.speech.GoogleVoiceTypingDisabledException
import net.gotev.speech.Speech
import net.gotev.speech.SpeechDelegate
import net.gotev.speech.SpeechRecognitionNotAvailable
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap

class NewNoteFragment : Fragment(), SpeechDelegate {

    private val TAG = NewNoteFragment::class.java.name
    private lateinit var mAuth: FirebaseAuth
    private var note = ""
    private var timer = Timer()
    private var speech: Speech? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mAuth = FirebaseAuth.getInstance()
        return inflater.inflate(R.layout.fragment_new_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Speech.init(requireContext(), requireContext().packageName)
        val bundle = arguments
        if (bundle != null) {
            if (bundle.getString(Const.NOTE) != null) {
                note = bundle.getString(Const.NOTE) ?: ""
                editor.html = note
            }
        }
        speech = Speech.getInstance()
        Permissions().requestMultiPermission(requireActivity() , requireContext())
        editor.setPlaceholder("Insert text here...")
        editor.requestFocus()

        fab.setOnClickListener {
            if (editor.html == null) {
                Toast.makeText(context, "Empty note not allowed to save ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val text = editor.html.toString()
            postNotesOnFirebase(note = text)
        }
    }

    private fun getSpeech() {
        Log.e(TAG, "getSpeech: ${timer.purge()}" )
        timer.schedule(object : TimerTask() {
            override fun run() {
                requireActivity().runOnUiThread {
                    try {
                        speech?.startListening (this@NewNoteFragment)
                    } catch (exc: SpeechRecognitionNotAvailable) {
                       // showSpeechNotSupportedDialog()
                    } catch (exc: GoogleVoiceTypingDisabledException) {
                        //showEnableGoogleVoiceTyping()
                    }

                }

            }
        }, 0, 1000)
    }

    private fun postNotesOnFirebase(note: String) {
        if (CheckInternet().isNetworkAvailable(requireContext())) {
            val dialog  = ProgressDialog(requireContext())
            dialog.setMessage("Please wait...")
            dialog.setCancelable(false)
            dialog.show()

            val database = FirebaseDatabase.getInstance().reference
            val map = HashMap<String , String>()
            map["note"] = note
            database.child("Notes").child(mAuth.currentUser?.uid ?: "").push().setValue(map).addOnCompleteListener {
                dialog.dismiss()
                if (it.isSuccessful){
                    Toast.makeText(requireContext(), "Note is saved successfully ", Toast.LENGTH_SHORT).show()
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

    override fun onResume() {
        super.onResume()
        Speech.init(requireContext(), requireContext().packageName)
        try {
            getSpeech()
        }
        catch (e: Exception) {
            timer = Timer()
            getSpeech()
        }
        //getSpeech()
    }

    override fun onStartOfSpeech() {

    }

    override fun onSpeechPartialResults(results: MutableList<String>?) {
        TODO("Not yet implemented")
    }

    override fun onSpeechRmsChanged(value: Float) {
        TODO("Not yet implemented")
    }

    override fun onSpeechResult(result: String) {
        Log.e(TAG, "onSpeechResult: $result")

        try {
            if (result.isNotEmpty() ) {
                var text = editor?.html?.toString() ?: ""

                text += if (text.isEmpty()) {
                    "$result "
                } else {
                    " $result"
                }

                Log.e(TAG, "onSpeechResult: text = $text" )


                if (result.contains("Please save the note" , ignoreCase = true)) {
                    postNotesOnFirebase(note = text)
                }
                else {
                    editor.html = text
                }
            }
        }
        catch (e: Exception) {
            Log.e(TAG, "onSpeechResult: exceoption = ", e)
        }

    }

    override fun onPause() {
        super.onPause()

        Log.e(TAG, "onPause: ")
        this.timer.cancel()//cancel()
        speech?.stopTextToSpeech()
        speech?.stopListening()

    }

    override fun onDestroy() {
        super.onDestroy()
        this.timer.cancel()
        Log.e(TAG, "onDestroy: ")
        speech?.stopTextToSpeech()
        speech?.stopListening()
        speech?.shutdown()
    }

    override fun onDetach() {
        super.onDetach()
        Log.e(TAG, "onDetach: ")
        speech?.stopTextToSpeech()
        speech?.stopListening()
        speech?.shutdown()
    }
}