package com.bt.speakpad.ui.fragments.dashboards

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bt.speakpad.R
import com.bt.speakpad.helper.adapter.NotesAdapter
import com.bt.speakpad.helper.pojo.Notes
import com.bt.speakpad.helper.utils.CheckInternet
import com.bt.speakpad.helper.utils.Const
import com.bt.speakpad.helper.utils.LoadFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_add_notes.*
import kotlinx.android.synthetic.main.fragment_login.*

class AddNotesFragment : Fragment() {

    private var adapter: NotesAdapter? = null
    private var list: ArrayList<Notes>? = ArrayList()
    private val TAG = AddNotesFragment::class.java.name
    private lateinit var mAuth:FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mAuth = FirebaseAuth.getInstance()
        return inflater.inflate(R.layout.fragment_add_notes, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = NotesAdapter(requireContext())
        val manager =  GridLayoutManager(context, 2)
        recyclerView.layoutManager =  manager

        recyclerView.itemAnimator = DefaultItemAnimator() as RecyclerView.ItemAnimator?
        recyclerView.adapter = adapter
        recyclerView.isNestedScrollingEnabled = false

        adapter?.setOnItemClickListener {
            val bundle = Bundle()
            bundle.putString(Const.NOTE , it.note)
            LoadFragment().activity(NewNoteFragment() , NewNoteFragment::class.java.name , requireActivity() , bundle)
        }

        fab.setOnClickListener {
             LoadFragment().activity(NewNoteFragment() , NewNoteFragment::class.java.name , requireActivity())
        }

        prepareNotes()
    }

    private fun prepareNotes() {
        if (CheckInternet().isNetworkAvailable(requireContext())) {
            list?.clear()
            val dialog  = ProgressDialog(requireContext())
            dialog.setMessage("Please wait...")
            dialog.setCancelable(false)
            dialog.show()

            val database = FirebaseDatabase.getInstance().reference
            database.child("Notes").child(mAuth.uid ?: "").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    dialog.dismiss()
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    dialog.dismiss()
                    if (snapshot.exists()) {
                        snapshot.children.forEach {
                            val note = it.getValue(Notes::class.java)
                            if (note != null)
                            {
                                Log.e(TAG, "onDataChange: ${snapshot.value}"  )
                                list?.add(note)
                                adapter?.submitList(list)
                                adapter?.notifyDataSetChanged()
                            }
                        }


                    }
                }

            })
        }
        else {
            Toast.makeText(requireContext(), "Unable to connect to server. Please make sure you have an active internet.", Toast.LENGTH_SHORT).show()
        }
    }
}