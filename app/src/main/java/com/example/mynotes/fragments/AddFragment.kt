package com.example.mynotes.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mynotes.R
import com.example.mynotes.database.Note
import com.example.mynotes.database.NoteViewModel
import com.example.mynotes.databinding.ColorBottomSheetLayoutBinding
import com.example.mynotes.databinding.FragmentAddBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class AddFragment : Fragment() {
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private val mNoteViewModel: NoteViewModel by viewModels()
    private var color = -1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding= FragmentAddBinding.inflate(inflater,container,false)
        binding.btnBack.setOnClickListener{
            findNavController().navigate(R.id.action_addFragment_to_noteFragment)
        }
        binding.fabColorPick.setOnClickListener{
            val bottomSheetDialog = BottomSheetDialog(
                requireContext(),
                R.style.BottomSheetDialogTheme)
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
                    setOnColorSelectedListener {
                        value->
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
        }
        binding.btnSave.setOnClickListener{
            insertDataToDb()
        }
        return binding.root
    }
    private fun insertDataToDb(){
        if(binding.etTitle.text.toString().isEmpty() ||
                binding.etNoteContent.text.toString().isEmpty()){
            Toast.makeText(requireContext(),"Please fill all the fields!",Toast.LENGTH_SHORT).show()
        }else{
            mNoteViewModel.insertData(
                Note(
                    0,
                    binding.etTitle.text.toString(),
                    binding.etNoteContent.text.toString(),
                    color
                )
            )
            Toast.makeText(requireContext(),"SuccessFully Added!",Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_addFragment_to_noteFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}