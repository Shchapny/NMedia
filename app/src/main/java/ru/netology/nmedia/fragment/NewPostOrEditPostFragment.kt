package ru.netology.nmedia.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.NewPostOrEditPostBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class NewPostOrEditPostFragment : Fragment(R.layout.new_post_or_edit_post) {

    private val viewModel by viewModels<PostViewModel>(
        ownerProducer = ::requireParentFragment
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = NewPostOrEditPostBinding.bind(view)

        arguments?.let { arguments ->
            val text = arguments.textArg ?: return@let
            binding.edit.setText(text)
        }

        binding.ok.setOnClickListener {
            viewModel.editContent(binding.edit.text.toString())
            viewModel.save()
            AndroidUtils.hideKeyboard(requireView())
        }
        viewModel.postCreated.observe(viewLifecycleOwner) {
            viewModel.loadPosts()
            findNavController().navigateUp()
        }
    }

    companion object {
        var Bundle.textArg: String? by StringArg
    }
}