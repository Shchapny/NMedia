package ru.netology.nmedia.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentAuthenticationBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.AuthenticationViewModel


class AuthenticationFragment : Fragment(R.layout.fragment_authentication) {

    private val viewModel by viewModels<AuthenticationViewModel>(
        ownerProducer = ::requireParentFragment
    )

    private var _binding: FragmentAuthenticationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)?.also {
            _binding = FragmentAuthenticationBinding.bind(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btSignin.setOnClickListener {
                viewModel.reset()
                resetErrorInfo()
                val login = login.text?.trim().toString()
                val password = password.text?.trim().toString()
                when {
                    login.isBlank() -> loginLayout.error = getString(R.string.error_login)
                    password.isBlank() -> passwordLayout.error = getString(R.string.error_password)
                    else -> viewModel.authentication(login, password)
                }
                binding.warning.visibility = View.INVISIBLE
            }
        }

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            when {
                state.error -> binding.warning.visibility = View.VISIBLE
                state.loading -> onShowSnackbar(R.string.error_network)
                state.server -> onShowSnackbar(R.string.error_server)
                state.authState -> {
                    AndroidUtils.hideKeyboard(requireView())
                    findNavController().navigateUp()
                    binding.warning.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun resetErrorInfo() {
        binding.loginLayout.error = null
        binding.passwordLayout.error = null
    }

    private fun onShowSnackbar(res: Int) {
        Snackbar.make(binding.root, res, Snackbar.LENGTH_LONG).show()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.removeGroup(R.id.unauthenticated)
        super.onPrepareOptionsMenu(menu)
    }

    override fun onDestroyView() {
        viewModel.reset()
        _binding = null
        super.onDestroyView()
    }
}