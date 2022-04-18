package ru.netology.nmedia.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentShowImageBinding
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.util.loadImage

class ShowImageFragment : Fragment(R.layout.fragment_show_image) {

    private var _binding: FragmentShowImageBinding? = null
    private val binding get() = _binding!!

    companion object {
        var Bundle.showImage: String? by StringArg
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)?.also {
            _binding = FragmentShowImageBinding.bind(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.showImage?.let {
            binding.image.loadImage(BuildConfig.BASE_URL, "media", it)
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}