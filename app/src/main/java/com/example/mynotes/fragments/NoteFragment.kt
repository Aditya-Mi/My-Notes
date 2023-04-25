package com.example.mynotes.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.mynotes.NoteAdapter
import com.example.mynotes.R
import com.example.mynotes.SwipeToDelete
import com.example.mynotes.database.Note
import com.example.mynotes.database.NoteViewModel
import com.example.mynotes.databinding.FragmentNoteBinding
import com.google.android.material.snackbar.Snackbar
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator


class NoteFragment : Fragment() {
    private val mViewModel: NoteViewModel by viewModels()
    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!
    private val adapter:NoteAdapter by lazy { NoteAdapter() }
    private val emptyDatabase:MutableLiveData<Boolean> = MutableLiveData(true)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding= FragmentNoteBinding.inflate(inflater,container,false)

        val recyclerview = binding.recyclerView
        recyclerview.adapter=adapter
        recyclerview.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        recyclerview.itemAnimator = SlideInUpAnimator().apply {
            addDuration = 300
        }
        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        mViewModel.getAllData.observe(viewLifecycleOwner, Observer{ data->
            checkIfDatabaseEmpty(data)
            adapter.setData(data)
        })
        swipeToDelete(recyclerview)

        emptyDatabase.observe(viewLifecycleOwner,Observer{
            showEmptyDatabaseViews(it)
        })
        binding.search.setOnEditorActionListener {v,actionId,_ ->
            if(actionId==EditorInfo.IME_ACTION_SEARCH){
                v.clearFocus()
            }
            return@setOnEditorActionListener true
        }
        binding.search.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.noNotes.isVisible = false
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().isNotEmpty()){
                    val text = s.toString()
                    val searchQuery = "%$text%"
                    if(searchQuery.isNotEmpty()){
                        mViewModel.searchDatabase(searchQuery).observe(viewLifecycleOwner){
                            adapter.setData(it)
                        }
                    }
                    else{
                        observerDataChanges()
                    }
                }
                else{
                    observerDataChanges()
                }
            }
            override fun afterTextChanged(s: Editable?) {
            }

        })
        return binding.root
    }

    private fun observerDataChanges() {
        mViewModel.getAllData.observe(viewLifecycleOwner, Observer{ data->
            checkIfDatabaseEmpty(data)
            adapter.setData(data)
        })
    }

    private fun showEmptyDatabaseViews(emptyDatabase: Boolean) {
        if(emptyDatabase){
            binding.noNotes.visibility = View.VISIBLE
        }else{
            binding.noNotes.visibility = View.INVISIBLE
        }
    }

    private fun swipeToDelete(recyclerview: RecyclerView) {
        val swipeToDeleteCallBack = object : SwipeToDelete(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedItem =adapter.noteList[viewHolder.absoluteAdapterPosition]
                mViewModel.deleteData(deletedItem)
                adapter.notifyItemRemoved(viewHolder.absoluteAdapterPosition)
                restoreDeletedData(viewHolder.itemView,deletedItem,viewHolder.absoluteAdapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallBack)
        itemTouchHelper.attachToRecyclerView(recyclerview)
    }

    private fun restoreDeletedData(view: View,deletedItem: Note,position: Int){
        val snackBar = Snackbar.make(
            view,"Deleted ${deletedItem.title}",
            Snackbar.LENGTH_LONG
        )
        snackBar.setAction("Undo"){
            mViewModel.insertData(deletedItem)

        }
        snackBar.show()
    }
    private fun checkIfDatabaseEmpty(note: List<Note>){
        emptyDatabase.value = note.isEmpty()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }




}