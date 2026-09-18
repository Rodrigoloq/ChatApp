package com.rodrigoloq.chatapp.profile.editinformation.viewmodel

import com.rodrigoloq.chatapp.entities.User

data class EditInformationUIState(val user: User = User(),
                                  val loadUserSuccess: Boolean = true,
                                  val loadUserErrorMsg: String = "",
                                  val updateNamesSuccess: Boolean? = null,
                                  val updateNamesErrorMsg: String = "",
                                  val updateImageSuccess: Boolean? = null,
                                  val updateImageErrorMsg: String = "",
                                  val inProgress: Boolean = false
    )
