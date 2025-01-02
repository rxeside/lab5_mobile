package com.example.todo.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todo.Infrastructure.SettingStorage

class LoginViewModelFactory(private val storage: SettingStorage) : ViewModelProvider.Factory {
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		return if (modelClass == LoginViewModel::class.java) {
			LoginViewModel(storage) as T
		} else {
			throw IllegalArgumentException("ViewModel class not recognized")
		}
	}
}