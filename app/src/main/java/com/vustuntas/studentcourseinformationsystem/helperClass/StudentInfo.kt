package com.vustuntas.studentcourseinformationsystem.helperClass

import java.io.Serializable

class StudentInfo(
    val studentId :String,
    val studentName : String,
    val studentSurname : String,
    val studentMailAddress : String,
    val studentImage : ByteArray? = null
) :Serializable{
}