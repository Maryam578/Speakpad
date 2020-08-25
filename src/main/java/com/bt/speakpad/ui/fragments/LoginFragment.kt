package com.bt.speakpad.ui.fragments

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bt.speakpad.R
import com.bt.speakpad.helper.utils.CheckInternet
import com.bt.speakpad.helper.utils.LoadFragment
import com.bt.speakpad.ui.activities.Dashboard
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*
import net.gotev.speech.SpeechDelegate


class LoginFragment : Fragment(),  SpeechDelegate {
    private val TAG = LoginFragment::class.java.name
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mAuth = FirebaseAuth.getInstance()
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnLogin.setOnClickListener {
            val email = edTxtEmail.text.toString()
            val password = edTxtPassword.text.toString()
            login(email = email , password = password)
        }

        btnRegister.setOnClickListener {
            LoadFragment().activity(RegisterFragment() , RegisterFragment::class.java.name , requireActivity())
        }

    }

    private fun login(email: String , password: String) {
        if (CheckInternet().isNetworkAvailable(requireContext())) {
            if (email.isEmpty()){
                edTxtEmail.error = "Required field"
                return
            }
            if (password.isEmpty())
            {
                edTxtPassword.error = "Required field"
                return
            }
            firebaseLogin(email = email , password = password)
        }
        else {
            Toast.makeText(requireContext(), "Unable to connect to server. Please make sure you have an active internet.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseLogin(email: String , password: String) {
        val dialog  = ProgressDialog(requireContext())
        dialog.setMessage("Authentication")
        dialog.setCancelable(false)
        dialog.show()
        mAuth.signInWithEmailAndPassword(email , password)
            .addOnCompleteListener {
                dialog.dismiss()
                if (it.isSuccessful){
                    val user = mAuth.currentUser
                    Log.e(TAG, "firebaseLogin: ${user?.email} , ${user?.displayName}" )
                    val intent = Intent(requireContext() , Dashboard::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
                else
                {
                    Toast.makeText(requireContext(), "Invalid email or password please try again later", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "firebaseLogin: exception => ", it.exception)
                }

            }
    }

    override fun onStartOfSpeech() {

    }

    override fun onSpeechPartialResults(results: MutableList<String>?) {

    }

    override fun onSpeechRmsChanged(value: Float) {

    }

    override fun onSpeechResult(result: String?) {

    }


}