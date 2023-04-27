package com.practicum.room_kotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.practicum.room_kotlin.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var appDb : AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appDb = AppDatabase.getDatabase(this)

        binding.readButton.setOnClickListener {
            val rollNo = binding.rollSearchEdit.text.toString()
            readData(rollNo)
        }

        binding.writeButton.setOnClickListener {
            val firstName = binding.firstNameEdit.text.toString()
            val secondName = binding.lastNameEdit.text.toString()
            val rollNo = binding.rollNoEdit.text.toString()

            writeData(firstName,secondName,rollNo)
        }

        binding.updateButton.setOnClickListener {
            val firstName = binding.firstNameEdit.text.toString()
            val secondName = binding.lastNameEdit.text.toString()
            val rollNo = binding.rollNoEdit.text.toString()

            updateData(firstName,secondName,rollNo)
        }

        binding.deleteAllButton.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO){
                appDb.studentDao().deleteALL()
            }
        }

        binding.deleteButton.setOnClickListener {
            val rollNo = binding.rollSearchEdit.text.toString()
            deleteData(rollNo)
        }
    }

    private fun writeData(firstName: String, lastName: String, rollNo: String){

        if(firstName.isNotEmpty() && lastName.isNotEmpty() && rollNo.isNotEmpty()){
            val student = Student(null,firstName,lastName,rollNo.toInt())

            GlobalScope.launch{
                appDb.studentDao().insert(student)
            }

            clearField()

            Toast.makeText(this,"Successfully written",Toast.LENGTH_SHORT).show()
        }else Toast.makeText(this,"Please Enter Data",Toast.LENGTH_SHORT).show()
    }

    private fun updateData(firstName: String, lastName: String, rollNo: String){

        if(firstName.isNotEmpty() && lastName.isNotEmpty() && rollNo.isNotEmpty()){

            GlobalScope.launch(Dispatchers.IO){
                appDb.studentDao().update(firstName, lastName, rollNo.toInt())
            }

            clearField()

            Toast.makeText(this,"Successfully update",Toast.LENGTH_SHORT).show()
        }else Toast.makeText(this,"Please Enter Data",Toast.LENGTH_SHORT).show()
    }

    private fun readData(rollNo: String){

        if (rollNo.isNotEmpty()){

            GlobalScope.launch {
                val student = appDb.studentDao().findByRoll(rollNo.toInt())
                displayData(student)
            }

        }else Toast.makeText(this,"Please Enter Data",Toast.LENGTH_SHORT).show()
    }

    private fun deleteData(rollNo: String){
        if (rollNo.isNotEmpty()){

            GlobalScope.launch {
                val student = appDb.studentDao().findByRoll(rollNo.toInt())
                appDb.studentDao().delete(student)
            }

            Toast.makeText(this,"Student deleted",Toast.LENGTH_SHORT).show()
        }else Toast.makeText(this,"Please Enter Data",Toast.LENGTH_SHORT).show()
    }

    private suspend fun displayData(student: Student) {
        if(student != null){
            withContext(Dispatchers.Main){
                binding.firstNameText.text = student.firstName
                binding.lastNameText.text = student.lastName
                binding.rollNoText.text = student.rollNo.toString()
            }
        }else Toast.makeText(this,"User not exist",Toast.LENGTH_SHORT).show()
    }

    private fun clearField() {
        binding.firstNameEdit.text.clear()
        binding.lastNameEdit.text.clear()
        binding.rollNoEdit.text.clear()
    }
}