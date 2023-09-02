package com.vustuntas.studentcourseinformationsystem.helperClass

import java.io.Serializable

class GradesInfo(
    val courseName : String?,
    val midtermNote : String?,
    val finalNote : String?,
    val letterNote :String?
) :Serializable{
}