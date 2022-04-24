package ru.netology.nmedia.fragment

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentRegistrationBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.LoadImage
import ru.netology.nmedia.viewmodel.RegistrationViewModel


class RegistrationFragment : Fragment(R.layout.fragment_registration) {

    private val viewModel by viewModels<RegistrationViewModel>(
        ownerProducer = ::requireParentFragment
    )

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)?.also {
            _binding = FragmentRegistrationBinding.bind(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            when {
                state.loading -> onShowSnackbar(R.string.error_network)
                state.server -> onShowSnackbar(R.string.error_server)
                state.authState -> {
                    viewModel.reset()
                    AndroidUtils.hideKeyboard(requireView())
                    findNavController().navigateUp()
                }
            }
        }

        val pickPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                when (result.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            ImagePicker.getError(result.data),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    Activity.RESULT_OK -> {
                        val uri: Uri? = result.data?.data
                        viewModel.changePhoto(uri, uri?.toFile())
                    }
                }
            }

        val items = arrayOf(getString(R.string.gallery), getString(R.string.camera))

        binding.avatar.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setItems(items) { _, which ->
                    when (which) {
                        0 -> LoadImage.loadFromGallery(this, 200, pickPhotoLauncher::launch)
                        1 -> LoadImage.loadFromCamera(this, 200, pickPhotoLauncher::launch)
                    }
                }
                .show()
        }

        binding.apply {
            btSignup.setOnClickListener {
                val login = login.text?.trim().toString()
                val password = password.text?.trim().toString()
                val name = userName.text?.trim().toString()
                val avatar = viewModel.photo.value?.file
                when {
                    login.isBlank() -> loginLayout.error = getString(R.string.error_login)
                    password.isBlank() -> passwordLayout.error = getString(R.string.error_password)
                    name.isBlank() -> userNameLayout.error = getString(R.string.error_name)
                    passwordConf.text?.trim().toString().isBlank() -> passwordConfLayout.error =
                        getString(R.string.error_password)
                    passwordConf.text?.trim().toString() != password -> passwordConfLayout.error =
                        getString(R.string.error_conf_password)
                    else -> {
                        if (avatar == null) {
                            viewModel.registrationWithoutAvatars(login, password, name)
                        } else {
                            viewModel.registrationWithAvatars(login, password, name, avatar)
                        }
                    }
                }
            }
        }

        viewModel.photo.observe(viewLifecycleOwner) { photo ->
            if (photo.uri == null) {
                return@observe
            }
            binding.avatar.setImageURI(photo.uri)
        }
    }

    private fun onShowSnackbar(res: Int) {
        Snackbar.make(binding.root, res, Snackbar.LENGTH_LONG).show()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.removeGroup(R.id.unauthenticated)
        super.onPrepareOptionsMenu(menu)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
