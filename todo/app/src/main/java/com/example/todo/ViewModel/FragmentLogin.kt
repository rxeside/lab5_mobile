package com.example.todo.ViewModel

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.todo.Infrastructure.SettingStorage
import com.example.todo.R
import com.example.todo.databinding.FragmentLoginBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FragmentLogin : Fragment(R.layout.fragment_login) {

	private lateinit var binding: FragmentLoginBinding

	private val viewModel: LoginViewModel by activityViewModels {
		LoginViewModelFactory(SettingStorage(requireContext()))
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding = FragmentLoginBinding.bind(view)

		lifecycleScope.launch {
			viewModel.loadActualPin()
		}

		viewLifecycleOwner.lifecycleScope.launch {
			viewModel.state.collectLatest { loginState ->
				handleState(loginState)
			}
		}

		setButtonListeners()
	}

	private fun handleState(loginState: LoginState) {
		updatePinView(loginState)

		if (loginState.currentState in listOf(State.ENTER_PIN, State.REPEAT_PIN) &&
			loginState.pin.length == 4 &&
			!loginState.errored
		) {
			navigateToDiary()
		}

		if (loginState.errored) {
			showErrorToast()
		}
	}

	private fun navigateToDiary() {
		val navOptions = NavOptions.Builder()
			.setPopUpTo(R.id.fragmentLogin, true)
			.build()
		findNavController().navigate(R.id.action_fragmentLogin_to_fragmentDiary, null, navOptions)
	}

	private fun showErrorToast() {
		Toast.makeText(requireContext(), "Неверный PIN-код", Toast.LENGTH_SHORT).show()
	}

	private fun setButtonListeners() {
		val buttons = listOf(
			binding.button0, binding.button1, binding.button2, binding.button3,
			binding.button4, binding.button5, binding.button6, binding.button7,
			binding.button8, binding.button9
		)

		buttons.forEach { button ->
			button.setOnClickListener {
				viewModel.appendToPin(button.text.toString())
			}
		}
	}

	private fun updatePinView(loginState: LoginState) {
		binding.title.text = when (loginState.currentState) {
			State.ENTER_PIN -> "Введи PIN-код"
			State.CREATE_PIN -> "Придумай PIN-код"
			State.REPEAT_PIN -> "Повтори PIN-код"
		}

		val pinCircles = listOf(binding.circle1, binding.circle2, binding.circle3, binding.circle4)
		pinCircles.forEachIndexed { index, circle ->
			val isFilled = index < loginState.pin.length
			circle.setBackgroundResource(if (isFilled) R.drawable.filled_round_pin_code else R.drawable.round_pin_code)
		}
	}
}
