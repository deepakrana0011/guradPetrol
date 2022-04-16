package com.patrol.guard.guardpatrol.utils

object Validations {
    fun isFieldEmpty(text: String): Boolean {
        return if (!text.equals("null")&& text.trim().length > 0) {
            false
        } else {
            true
        }
    }

    fun isPinConfirm(newPin: String, confirmPin:String): Boolean {
        var returnConfirmPin = false
        if (newPin.length > 0 && confirmPin.length > 0) {
            returnConfirmPin = newPin.equals(confirmPin)
        } else{
            returnConfirmPin = false
        }
        return returnConfirmPin
    }

}