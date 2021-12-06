package ru.netology.nmedia.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.DisplayCount
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostFragmentBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.fragment.NewPostOrEditPostFragment.Companion.textArg
import ru.netology.nmedia.util.PostArg
import ru.netology.nmedia.viewmodel.PostViewModel

class ShowPostFragment : Fragment(R.layout.card_post_fragment) {

    val displayCount = DisplayCount()

    companion object {
        var Bundle.showOnePost: Long by PostArg
    }

    private val viewModel by viewModels<PostViewModel>(
        ownerProducer = ::requireParentFragment
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = CardPostFragmentBinding.bind(view)

        arguments?.showOnePost?.let { id ->
            viewModel.data.observe(viewLifecycleOwner) { post ->
                with(binding) {
                    post.filter { id == it.id }.map { post ->
                        author.text = post.author
                        published.text = post.published
                        content.text = post.content
                        likes.text = displayCount.display(post.likes)
                        share.text = displayCount.display(post.share)
                        likes.isChecked = post.likedByMe

                        menu.setOnClickListener {
                            PopupMenu(it.context, it).apply {
                                inflate(R.menu.posts_menu)
                                setOnMenuItemClickListener { item ->
                                    when (item.itemId) {
                                        R.id.remove -> {
                                            findNavController().navigate(
                                                R.id.action_showPostFragment_to_feedFragment,
                                                Bundle().apply { viewModel.remove(post.id) })
                                            true
                                        }
                                        R.id.edit -> {
                                            findNavController().navigate(
                                                R.id.action_showPostFragment_to_newPostFragment,
                                                Bundle().apply {
                                                    textArg = content.text.toString()
                                                    viewModel.edit(post)
                                                })
                                            true
                                        }
                                        else -> false
                                    }
                                }
                            }.show()
                        }
                        likes.setOnClickListener {
                            viewModel.like(post.id)
                        }
                        share.setOnClickListener {
                            viewModel.share(post.id)
                            val intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, post.content)
                                type = "text/plain"
                            }
                            val shareIntent =
                                Intent.createChooser(
                                    intent,
                                    getString(R.string.chooser_share_post)
                                )
                            startActivity(shareIntent)
                        }
                        play.setOnClickListener {
                            val intentVideo =
                                Intent(
                                    Intent.ACTION_VIEW, Uri.parse(post.video)
                                )
                            startActivity(intentVideo)
                        }
                        video.setOnClickListener {
                            val intentVideo =
                                Intent(
                                    Intent.ACTION_VIEW, Uri.parse(post.video)
                                )
                            startActivity(intentVideo)
                        }
                        if (post.video != null) groupVideo.visibility = View.VISIBLE
                        else groupVideo.visibility = View.GONE
                    }
                }
            }
        }
    }
}

