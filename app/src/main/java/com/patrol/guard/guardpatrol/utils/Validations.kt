package com.patrol.guard.guardpatrol.utils

object Validations {
    fun isFieldEmpty(text: String): Boolean {
        return if (!text.equals("null")&& text.trim().length > 0) {
            false
        } else {
            true
        }
    }
}