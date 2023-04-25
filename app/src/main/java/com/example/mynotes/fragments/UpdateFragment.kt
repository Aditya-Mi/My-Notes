package com.example.mynotes.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mynotes.R
import com.example.mynotes.database.Note
import com.example.mynotes.database.NoteViewModel
import com.example.mynotes.databinding.ColorBottomSheetLayoutBinding
import com.example.mynotes.databinding.FragmentUpdateBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog


class UpdateFragment : Fragment() {

    private var _binding: FragmentUpdateBinding?= null
    private val binding get() = _binding!!
    private val args by navArgs<UpdateFragmentArgs>()
    private var color = -1
    private var note:Note? = null
    private val mNoteViewModel:NoteViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUpdateBinding.inflate(inflater,container,false)
        setUpNote()
        binding.btnBack.setOnClickListener{
            findNavController().navigate(R.id.action_updateFragment_to_noteFragment)
        }
        binding.fabColorPick.setOnClickListener{
            val bottomSheetDialog = BottomSheetDialog(
                requireContext(),
                R.style.BottomSheetDialogTheme
            )
            val bottomSheetView:View = layoutInflater.inflate(
                R.layout.color_bottom_sheet_layout,
                null
            )
            with(bottomSheetDialog){
                setContentView(bottomSheetView)
                show()
            }
            val bottomSheetBinding = ColorBottomSheetLayoutBinding.bind(bottomSheetView)
            bottomSheetBinding.apply {
                colorPicker.apply {
                    setSelectedColor(color)
                    setOnColorSelectedListener { value->
                        color=value
                        binding.apply {
                            noteContentFragmentParent.setBackgroundColor(color)
                            addToolbar.setBackgroundColor(color)
                            activity?.window?.statusBarColor= color
                        }
                        bottomSheetBinding.bottomSheetParent.setCardBackgroundColor(color)
                    }
                }
                bottomSheetParent.setCardBackgroundColor(color)
            }
            bottomSheetView.post {
                bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }

            setUpNote()
        }
        binding.btnSave.setOnClickListener {
            if(binding.etTitle.text.toString().isEmpty() ||
                binding.etNoteContent.text.toString().isEmpty()){
                Toast.makeText(requireContext(),"Please fill all the fields!", Toast.LENGTH_SHORT).show()
            }else{
                updateNote()
                findNavController().navigate(R.id.action_updateFragment_to_noteFragment)
            }
        }

        note=args.currentItem
        return binding.root
    }
    private fun setUpNote() {
        val note: Note = args.currentItem
        binding.etTitle.setText(note.title)
        binding.etNoteContent.setText(note.content)
        binding.noteContentFragmentParent.setBackgroundColor(note.color)
        binding.addToolbar.setBackgroundColor(note.color)
            activity?.window?.statusBarColor= note.color

    }
    private fun updateNote() {
        val mColor: Int = if(color == -1){
            note!!.color
        }else{
            color
        }
        mNoteViewModel.updateData(Note(
            note!!.id,
            binding.etTitle.text.toString(),
            binding.etNoteContent.text.toString(),
            mColor
        ))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}