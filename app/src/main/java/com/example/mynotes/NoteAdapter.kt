package com.example.mynotes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mynotes.database.Note
import com.example.mynotes.fragments.NoteFragmentDirections
import com.google.android.material.card.MaterialCardView

class NoteAdapter : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>(){
    var noteList = emptyList<Note>()
    class NoteViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var title_txt:TextView=itemView.findViewById(R.id.title_txt)
        var content_txt:TextView=itemView.findViewById(R.id.content_txt)
        var note_background:MaterialCardView=itemView.findViewById(R.id.note_background)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_layout,parent,false)
        return NoteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.title_txt.text=noteList[position].title
        holder.content_txt.text=noteList[position].content
        holder.note_background.setCardBackgroundColor(noteList[position].color)
        val action = NoteFragmentDirections.actionNoteFragmentToUpdateFragment(noteList[position])
        holder.note_background.setOnClickListener {
            holder.itemView.findNavController().navigate(action)
        }
    }
    fun setData(_noteList:List<Note>){
        val noteDiffUtil = NoteDiffUtil(noteList,_noteList)
        val noteDiffUtilResult = DiffUtil.calculateDiff(noteDiffUtil)
        this.noteList=_noteList
        noteDiffUtilResult.dispatchUpdatesTo(this)
    }
}