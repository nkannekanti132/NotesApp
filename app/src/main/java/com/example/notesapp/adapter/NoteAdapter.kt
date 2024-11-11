package com.example.notesapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.databinding.NoteLayoutBinding
import com.example.notesapp.fragments.HomeFragmentDirections
import com.example.notesapp.model.Note
import com.example.notesapp.viewmodel.NoteViewModel
import org.jetbrains.annotations.Async

class NoteAdapter(val noteViewModel: NoteViewModel, val context: Context): RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    class NoteViewHolder(val itemBinding:NoteLayoutBinding):RecyclerView.ViewHolder(itemBinding.root)

    private val differCallBack = object :DiffUtil.ItemCallback<Note>(){
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id &&
                    oldItem.noteTitle == newItem.noteTitle &&
                    oldItem.noteDesc == newItem.noteDesc
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return  oldItem == newItem
        }


    }
    val differ = AsyncListDiffer(this, differCallBack)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            NoteLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = differ.currentList[position]

        holder.itemBinding.noteTitle.text = currentNote.noteTitle
        holder.itemBinding.noteDesc.text = currentNote.noteDesc
        holder.itemBinding.pinnedIcon.isVisible = currentNote.isPinned  // Show pinned symbol if note is pinned

        holder.itemView.setOnLongClickListener {
            currentNote.isPinned = !holder.itemBinding.pinnedIcon.isVisible
            if(!holder.itemBinding.pinnedIcon.isVisible)
            {
                Toast.makeText(context,"Pinned Successfully",Toast.LENGTH_SHORT).show()
            }
            else
            {
                Toast.makeText(context,"Unpinned Successfully",Toast.LENGTH_SHORT).show()
            }
            holder.itemBinding.pinnedIcon.isVisible = !holder.itemBinding.pinnedIcon.isVisible
            noteViewModel.updateNote(currentNote)

            true
        }
        holder.itemView.setOnClickListener {
            val direction = HomeFragmentDirections.actionHomeFragmentToEditNoteFragment(currentNote)
            it.findNavController().navigate(direction)
        }

    }

}