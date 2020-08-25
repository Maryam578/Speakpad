package com.bt.speakpad.ui.fragments

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
import com.bt.speakpad.helper.utils.LoadFragment
import com.bt.speakpad.ui.activities.Dashboard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_register.*


class RegisterFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private val TAG = RegisterFragment::class.java.name
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mAuth = FirebaseAuth.getInstance()
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnRegister.setOnClickListener {
            val email = edTxtEmail.text.toString()
            val password = edTxtPassword.text.toString()
            val name = edTxtName.text.toString()
            register(email = email , password = password , name = name)
        }

        btnLogin.setOnClickListener {
            activity?.supportFragmentManager?.popBackStackImmediate()
        }
    }

    private fun register(email: String , password: String , name: String) {
        if (CheckInternet().isNetworkAvailable(requireContext())) {
            if (name.isEmpty()){
                edTxtName.error = "Required field"
                return
            }
            if (email.isEmpty()){
                edTxtEmail.error = "Required field"
                return
            }
            if (password.isEmpty())
            {
                edTxtPassword.error = "Required field"
                return
            }
            firebaseRegister(email = email , password = password, name = name )
        }
        else {
            Toast.makeText(requireContext(), "Unable to connect to server. Please make sure you have an active internet.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseRegister(email: String , password: String , name: String) {
        val dialog  = ProgressDialog(requireContext())
        dialog.setMessage("Please Wait")
        dialog.setCancelable(false)
        dialog.show()
        mAuth.createUserWithEmailAndPassword(email , password)
            .addOnCompleteListener {

                if (it.isSuccessful){
                    val user = mAuth.currentUser
                    if (user != null) {
                        addDataInDatabase(dialog = dialog , email = email , name = name , user = user )
                    }
                    else
                    {
                        dialog.dismiss()
                        Toast.makeText(requireContext(), "Invalid email or password please try again later", Toast.LENGTH_SHORT).show()

                    }
                }
                else
                {
                    dialog.dismiss()
                    Toast.makeText(requireContext(), "Invalid email or password please try again later", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "firebaseLogin: exception => ", it.exception)
                }
            }
    }

    private fun addDataInDatabase(dialog: ProgressDialog , email: String , name: String , user: FirebaseUser) {
        val database = FirebaseDatabase.getInstance().reference
        val map = HashMap<String , String>()
        map["name"] = name
        map["email"] = email
        map["uuid"] = user.uid
        database.child("Users").child(user.uid).setValue(map).addOnCompleteListener {
            dialog.dismiss()
            if (it.isSuccessful){
                val intent = Intent(requireContext() , Dashboard::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
            else
            {
                Toast.makeText(requireContext(), "Invalid email or password please try again later", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "addDataInDatabase: exception => ", it.exception)
            }
        }
    }

}