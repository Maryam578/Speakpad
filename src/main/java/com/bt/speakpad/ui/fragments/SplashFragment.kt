package com.bt.speakpad.ui.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.bt.speakpad.R
import com.bt.speakpad.helper.utils.LoadFragment
import com.bt.speakpad.helper.utils.Permissions
import com.bt.speakpad.helper.utils.TimerClass
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_splash.*
import net.gotev.speech.*
import java.lang.Exception
import java.sql.Time
import java.util.*


class SplashFragment : Fragment()  , SpeechDelegate {
    private var timer = TimerClass.getInstance()
    private var speech: Speech? = null
    private val TAG = SplashFragment::class.java.name
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Speech.init(requireContext(), requireContext().packageName)

        speech = Speech.getInstance()
        Permissions().requestMultiPermission(requireActivity() , requireContext())
        btnLogin.setOnClickListener {
            if (Speech.getInstance().isListening) {
                Speech.getInstance().stopListening()

            }
            LoadFragment().activity(LoginFragment() , LoginFragment::class.java.name , requireActivity())
        }
    }

    private fun getSpeech() {
        Log.e(TAG, "getSpeech: ${timer.purge()}" )
        timer.schedule(object : TimerTask() {
            override fun run() {
                requireActivity().runOnUiThread {
                    try {
                        speech?.startListening (this@SplashFragment)
                    } catch (exc: SpeechRecognitionNotAvailable) {
                        showSpeechNotSupportedDialog()
                    } catch (exc: GoogleVoiceTypingDisabledException) {
                        showEnableGoogleVoiceTyping()
                    }

                }

            }
        }, 0, 1000)
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

    override fun onStartOfSpeech() {

    }

    override fun onSpeechPartialResults(results: MutableList<String>?) {

    }

    override fun onSpeechRmsChanged(value: Float) {

    }

    override fun onSpeechResult(result: String) {
        Log.e(TAG, "onSpeechResult: $result")
        if (result.contains("Login Please" , ignoreCase = true)  ) {
            LoadFragment().activity(LoginFragment() , LoginFragment::class.java.name , requireActivity())
        }

    }


    private fun showSpeechNotSupportedDialog() {
        val dialogClickListener =
            DialogInterface.OnClickListener { _, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> SpeechUtil.redirectUserToGoogleAppOnPlayStore(
                        requireContext()
                    )
                    DialogInterface.BUTTON_NEGATIVE -> {
                    }
                }
            }
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Speech is not available in this device")
            .setCancelable(false)
            .setPositiveButton("YES", dialogClickListener)
            .setNegativeButton("NO", dialogClickListener)
            .show()
    }

    private fun showEnableGoogleVoiceTyping() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Please enable google voice typing")
            .setCancelable(false)
            .setPositiveButton("YES",
                DialogInterface.OnClickListener { _, _ ->
                    // do nothing
                })
            .show()
    }
}