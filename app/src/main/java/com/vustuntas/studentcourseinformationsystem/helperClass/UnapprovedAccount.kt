package com.vustuntas.studentcourseinformationsystem.helperClass

import java.io.Serializable

class UnapprovedAccount(
    val approval : String,
    val accountId :String,
    val accountUserName : String,
    val accountUserSurname : String,
    val accountTitle : String
) :Serializable{
}