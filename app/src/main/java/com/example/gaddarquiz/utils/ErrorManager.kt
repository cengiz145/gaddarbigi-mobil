package com.example.gaddarquiz.utils

import androidx.compose.runtime.mutableStateListOf

object ErrorManager {
    private val _errors = mutableStateListOf<AppError>()
    val errors: List<AppError> get() = _errors

    data class AppError(
        val message: String,
        val type: ErrorType,
        val timestamp: Long = System.currentTimeMillis()
    )

    enum class ErrorType {
        DATA_LOAD,
        SOUND_ENGINE,
        UI_TRANSITION,
        UNKNOWN
    }

    fun reportError(message: String, type: ErrorType = ErrorType.UNKNOWN) {
        val error = AppError(message, type)
        _errors.add(error)
        android.util.Log.e("GaddarError", "[$type] $message")
    }

    fun clearErrors() {
        _errors.clear()
    }
}
