package com.vustuntas.studentcourseinformationsystem.fragment

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.withStateAtLeast
import com.vustuntas.studentcourseinformationsystem.R
import com.vustuntas.studentcourseinformationsystem.databinding.FragmentAddOrEditStudentGradeBinding

class AddOrEditStudentGrade : Fragment() {
    private lateinit var _binding : FragmentAddOrEditStudentGradeBinding
    private val binding get() = _binding.root
    private lateinit var database: SQLiteDatabase

    private var midtermNote : String? = null
    private var finalNote : String? = null
    private var letterNote : String? = null

    private var studentId :String? = null
    private var lecturerId : String? = null
    private var courseId : String? = null

    private var gradeAvailable : Boolean = false
    private var selectedCourseID :String?  =null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddOrEditStudentGradeBinding.inflate(inflater,container,false)
        _binding.addOrUpdateGrade.setOnClickListener {  addOrUpdateGrade(it)}
        activity?.let {
            database = it.openOrCreateDatabase("SCIS",Context.MODE_PRIVATE,null)
        }

        arguments?.let {
            studentId = AddOrEditStudentGradeArgs.fromBundle(it).studentId
            lecturerId = AddOrEditStudentGradeArgs.fromBundle(it).lecturerId
            courseId = AddOrEditStudentGradeArgs.fromBundle(it).courseId
        }

        try {
            val cursor = database.rawQuery("select ID from Tbl_SelectedCourses WHERE student_id = ? and course_id = ?", arrayOf(studentId,courseId))
            val selectedCourseIDIndx = cursor.getColumnIndex("ID")
            while (cursor.moveToNext()){
                selectedCourseID = cursor.getString(selectedCourseIDIndx)
            }
            cursor.close()
        }
        catch (e:Exception){
            println(e.localizedMessage)
        }
        return binding.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getGradesInfo()
    }

    private fun addOrUpdateGrade(view :View){
        try {
            if(gradeAvailable){
                val updatedMidterm = _binding.midtermNoteTextView.text.toString().toIntOrNull()
                val updatedFinal = _binding.finalNoteTextView.text.toString().toIntOrNull()

                if(updatedMidterm != null && updatedFinal != null){
                    if(updatedMidterm.toInt() >=0 && updatedMidterm.toInt() <=100 && updatedFinal.toInt() >= 0 && updatedFinal.toInt() <= 100){
                        val updatedLetter = calculateLetterGrade(updatedMidterm,updatedFinal)
                        val updateGradesQuery = "UPDATE Tbl_StudentsNote SET midterm_grade = ?, final_grade = ?, letter_grade = ? WHERE studentId = ? and lecturerId = ? and selectedCourseId = ?"
                        val statment = database.compileStatement(updateGradesQuery)
                        statment.bindString(1,updatedMidterm.toString())
                        statment.bindString(2,updatedFinal.toString())
                        statment.bindString(3,updatedLetter)
                        statment.bindString(4,studentId)
                        statment.bindString(5,lecturerId)
                        statment.bindString(6,selectedCourseID)
                        statment.execute()
                        Toast.makeText(context,"Student Grade Updated Successfully",Toast.LENGTH_SHORT).show()
                    }
                    else
                        Toast.makeText(context,"Please Enter a Valid Grade",Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(context,"Midterm and Final grades must be entered at the same time",Toast.LENGTH_LONG).show()
                }
            }
            else{
                midtermNote = _binding.midtermNoteTextView.text.toString()
                finalNote = _binding.finalNoteTextView.text.toString()
                if(!midtermNote.equals("") && !finalNote.equals("")){
                    letterNote = calculateLetterGrade(midtermNote!!.toInt(),finalNote!!.toInt())
                    if(midtermNote!!.toInt() >=0 && midtermNote!!.toInt() <=100 && finalNote!!.toInt() >= 0 && finalNote!!.toInt() <= 100){
                        val insertGradesQuery = "INSERT INTO Tbl_StudentsNote (studentId, lecturerId, selectedCourseId, midterm_grade, final_grade, letter_grade) VALUES(?,?,?,?,?,?)"
                        val statment = database.compileStatement(insertGradesQuery)
                        statment.bindString(1,studentId)
                        statment.bindString(2,lecturerId)
                        statment.bindString(3,selectedCourseID)
                        statment.bindString(4,midtermNote.toString())
                        statment.bindString(5,finalNote.toString())
                        statment.bindString(6,letterNote)
                        statment.execute()
                        Toast.makeText(context,"Student Grade Added Successfully",Toast.LENGTH_SHORT).show()
                    }
                    else
                        Toast.makeText(context,"Please Enter a Valid Grade",Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(context,"Midterm and Final grades must be entered at the same time",Toast.LENGTH_LONG).show()
                }
            }
            getGradesInfo()
        }
        catch (E:Exception){
            println(E.localizedMessage)
            println("deneme")
        }
    }

    private fun getGradesInfo(){
        try {
            val cursor = database.rawQuery("SELECT * FROM Tbl_StudentsNote WHERE studentId = ? and lecturerId = ? and selectedCourseId = ?",
                arrayOf(studentId,lecturerId,selectedCourseID))
            val midtermNoteIndx = cursor.getColumnIndex("midterm_grade")
            val finalNoteIndx = cursor.getColumnIndex("final_grade")
            val letterNoteIndx = cursor.getColumnIndex("letter_grade")
            while (cursor.moveToNext()){
                midtermNote = cursor.getString(midtermNoteIndx)
                finalNote = cursor.getString(finalNoteIndx)
                letterNote = cursor.getString(letterNoteIndx)

                if(midtermNote.equals("") && finalNote.equals("") && letterNote.equals("")){
                    Toast.makeText(context,"No notes have been entered before. Enter Grade",Toast.LENGTH_LONG).show()
                }
                else{
                    _binding.midtermNoteTextView.setText(midtermNote)
                    _binding.finalNoteTextView.setText(finalNote)
                    _binding.letterNoteTextView.setText(letterNote)
                    gradeAvailable = true
                }
            }

        }
        catch (e:Exception){
            println(e.localizedMessage)
        }
    }

    private fun calculateLetterGrade(midtermNote:Int, finalNote:Int) : String{
        val avgGrade = (midtermNote + finalNote) / 2
        var letterGrade : String? = null
        if(avgGrade >= 90)
            letterGrade = "AA"
        else if(avgGrade >= 80)
            letterGrade = "BA"
        else if(avgGrade >= 70)
            letterGrade = "BB"
        else if(avgGrade >= 60)
            letterGrade = "CB"
        else if(avgGrade >= 50)
            letterGrade = "CC"
        else if(avgGrade >= 45)
            letterGrade = "DC"
        else
            letterGrade = "FF"

        return letterGrade
    }

}