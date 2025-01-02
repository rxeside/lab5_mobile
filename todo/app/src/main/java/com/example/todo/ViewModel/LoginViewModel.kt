package com.example.todo.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.Infrastructure.SettingStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class State {
	CREATE_PIN,
	REPEAT_PIN,
	ENTER_PIN,
}

data class LoginState(
	val currentState: State,
	val pin: String = "",
	val errored: Boolean = false
)

class LoginViewModel(private val settingStorage: SettingStorage) : ViewModel() {

	private val _state = MutableStateFlow(LoginState(State.CREATE_PIN))
	val state = _state.asStateFlow()

	private var actualPin: String? = null

	fun appendToPin(value: String) {
		val newPin = _state.value.pin + value
		updatePinState(newPin)
	}

	private fun updatePinState(newPin: String) {
		when (_state.value.currentState) {
			State.ENTER_PIN -> processEnterPin(newPin)
			State.CREATE_PIN -> processCreatePin(newPin)
			State.REPEAT_PIN -> processRepeatPin(newPin)
		}
	}

	private fun processEnterPin(pin: String) {
		if (pin.length == 4) {
			if (pin == actualPin) {
				_state.value = _state.value.copy(pin = pin, errored = false)
			} else {
				_state.value = _state.value.copy(pin = "", errored = true)
			}
		} else {
			_state.value = _state.value.copy(pin = pin, errored = false)
		}
	}

	private fun processCreatePin(pin: String) {
		if (pin.length == 4) {
			actualPin = pin
			_state.value = _state.value.copy(pin = "", currentState = State.REPEAT_PIN, errored = false)
		} else {
			_state.value = _state.value.copy(pin = pin, errored = false)
		}
	}

	private fun processRepeatPin(pin: String) {
		if (pin.length == 4) {
			if (pin == actualPin) {
				viewModelScope.launch {
					settingStorage.savePin(pin)
				}
				_state.value = _state.value.copy(pin = pin, errored = false)
			} else {
				_state.value = _state.value.copy(pin = "", currentState = State.CREATE_PIN, errored = true)
			}
		} else {
			_state.value = _state.value.copy(pin = pin, errored = false)
		}
	}

	suspend fun loadActualPin() {
		actualPin = settingStorage.getPin()
		Log.i("Your Tag for identy", "Loaded PIN: $actualPin")
		_state.value = if (actualPin.isNullOrEmpty()) {
			LoginState(State.CREATE_PIN)
		} else {
			LoginState(State.ENTER_PIN)
		}
	}
}
