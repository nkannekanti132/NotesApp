package com.example.notesapp.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
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
import com.example.notesapp.MainActivity
import com.example.notesapp.R
import com.example.notesapp.databinding.FragmentAddNoteBinding
import com.example.notesapp.model.Note
import com.example.notesapp.notifications.NotificationHelper
import com.example.notesapp.utils.requestExactAlarmPermission
import com.example.notesapp.viewmodel.NoteViewModel
import java.util.Locale


class AddNoteFragment : Fragment(R.layout.fragment_add_note), MenuProvider {

    private var addNoteBinding: FragmentAddNoteBinding ?= null
    private val binding get() = addNoteBinding!!

    private lateinit var noteViewModel: NoteViewModel
    private lateinit var addNoteView: View
    private var selectedReminderDateTime: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        addNoteBinding = FragmentAddNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost : MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        noteViewModel = (activity as MainActivity).noteViewModel



        addNoteView = view
        binding.addReminderButton.setOnClickListener {
            showDateTimePicker()
        }
    }


    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_add_note, menu)

    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId)
        {
            R.id.saveMenu ->{
                saveNote(addNoteView)
                return true
            }
            else->return false
        }
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

    private fun saveNote(view: View) {
        val noteTitle = binding.addNoteTitle.text.trim().toString()
        val noteDesc = binding.addNoteDesc.text.trim().toString()

        if (noteTitle.isNotEmpty()) {
            val note = Note(
                id = 0,
                noteTitle = noteTitle,
                noteDesc = noteDesc,
                reminderDate = selectedReminderDateTime
            )
            noteViewModel.addNote(note)


            requestExactAlarmPermission(requireContext())

            selectedReminderDateTime?.let {
                val notificationHelper = NotificationHelper(requireContext())
                notificationHelper.scheduleNotification(noteTitle, it)
            }

            Toast.makeText(requireContext(), "Note Saved", Toast.LENGTH_SHORT).show()
            view.findNavController().popBackStack(R.id.homeFragment, false)
        } else {
            Toast.makeText(requireContext(), "Please Enter Note Title", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        addNoteBinding = null
    }




}