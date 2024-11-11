package com.example.notesapp.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.notesapp.MainActivity
import com.example.notesapp.R
import com.example.notesapp.databinding.FragmentEditNoteBinding
import com.example.notesapp.model.Note
import com.example.notesapp.notifications.NotificationHelper
import com.example.notesapp.utils.requestExactAlarmPermission
import com.example.notesapp.viewmodel.NoteViewModel
import java.util.Date
import java.util.Locale


class EditNoteFragment : Fragment(R.layout.fragment_edit_note), MenuProvider {

    private var editNoteBinding: FragmentEditNoteBinding?= null
    private val binding get() = editNoteBinding!!

    private lateinit var noteViewModel: NoteViewModel
    private lateinit var currentNote: Note
    private var selectedReminderDateTime: Long? = null


    private val args: EditNoteFragmentArgs by navArgs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        editNoteBinding = FragmentEditNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost : MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        noteViewModel = (activity as MainActivity).noteViewModel

        currentNote = args.note!!


        binding.editNoteTitle.setText(currentNote.noteTitle)
        binding.editNoteDesc.setText(currentNote.noteDesc)
        currentNote.reminderDate?.let {
            selectedReminderDateTime = it
            val formattedDateTime = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(
                Date(it)
            )
            binding.selectedReminderDate.text = "Reminder set for: $formattedDateTime"
        }

        binding.addReminderButton.setOnClickListener {
            showDateTimePicker()
        }

        binding.editNoteFab.setOnClickListener {
            saveUpdatedNote()
        }


    }
    private fun deleteNote()
    {
        AlertDialog.Builder(activity).apply{
            setTitle("Delete Note")
            setMessage("Do you want to delete note?")
            setPositiveButton("Delete"){_,_ ->
                noteViewModel.deleteNote(currentNote)
                Toast.makeText(context,"Note Deleted", Toast.LENGTH_SHORT).show()
                view?.findNavController()?.popBackStack(R.id.homeFragment,false)

            }
            setNegativeButton("Cancel", null)
        }.create().show()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_edit_note,menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId)
        {
            R.id.deleteMenu ->{
                deleteNote()
                return true
            }
            else->return false
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        editNoteBinding = null
    }

    private fun showDateTimePicker() {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val timePicker = TimePickerDialog(
                    requireContext(),
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        calendar.set(Calendar.SECOND, 0)
                        selectedReminderDateTime = calendar.timeInMillis

                        val formattedDateTime = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(calendar.time)
                        binding.selectedReminderDate.text = "Reminder set for: $formattedDateTime"
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                )
                timePicker.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun saveUpdatedNote() {
        val noteTitle = binding.editNoteTitle.text.trim().toString()
        val noteDesc = binding.editNoteDesc.text.trim().toString()

        if (noteTitle.isNotEmpty()) {
            val updatedNote = currentNote.copy(
                noteTitle = noteTitle,
                noteDesc = noteDesc,
                reminderDate = selectedReminderDateTime
            )
            noteViewModel.updateNote(updatedNote)
            selectedReminderDateTime?.let {
                scheduleNotificationIfNeeded(noteTitle, selectedReminderDateTime!!)
            }

            Toast.makeText(requireContext(), "Note Updated", Toast.LENGTH_SHORT).show()
            view?.findNavController()?.popBackStack(R.id.homeFragment, false)
        } else {
            Toast.makeText(requireContext(), "Please Enter Note Title", Toast.LENGTH_SHORT).show()
        }
    }

    private fun scheduleNotificationIfNeeded(noteTitle: String, reminderTime: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestExactAlarmPermission(requireContext())
        }

        val notificationHelper = NotificationHelper(requireContext())
        notificationHelper.scheduleNotification(noteTitle, reminderTime)
    }




}