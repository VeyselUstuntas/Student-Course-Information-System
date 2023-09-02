package com.vustuntas.studentcourseinformationsystem.helperClass

import java.io.Serializable

class CourseInfo(
    val courseId :String,
    val courseCode : String,
    val courseName : String,
    val courseLecturer : String,
    val courseCredit : String
) :Serializable{
}