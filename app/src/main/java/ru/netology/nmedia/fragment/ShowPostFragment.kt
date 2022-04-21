package ru.netology.nmedia.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentCardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.fragment.NewPostOrEditPostFragment.Companion.textArg
import ru.netology.nmedia.fragment.ShowImageFragment.Companion.showImage
import ru.netology.nmedia.util.DisplayCount
import ru.netology.nmedia.util.PostArg
import ru.netology.nmedia.util.loadImage
import ru.netology.nmedia.viewmodel.PostViewModel


class ShowPostFragment : Fragment(R.layout.fragment_card_post) {

    private val displayCount = DisplayCount()

    companion object {
        var Bundle.showOnePost: Long by PostArg
    }

    private var _binding: FragmentCardPostBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<PostViewModel>(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)?.also {
            _binding = FragmentCardPostBinding.bind(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.showOnePost?.let { id ->
            viewModel.data.observe(viewLifecycleOwner) { post ->
                with(binding) {
                    post.posts.filter { id == it.id }.map { post: Post ->
                        author.text = post.author
                        published.text = post.published
                        content.text = post.content
                        likes.text = displayCount.display(post.likes)
                        share.text = displayCount.display(post.share)
                        likes.isChecked = post.likedByMe
                        avatar.loadImage(BuildConfig.BASE_URL, "avatars", post.authorAvatar)

                        if (post.attachment != null && post.attachment?.type == AttachmentType.IMAGE) {
                            imagePost.visibility = View.VISIBLE
                            imagePost.loadImage(BuildConfig.BASE_URL, "media", post.attachment?.url)
                        } else imagePost.visibility = View.GONE

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

                        imagePost.setOnClickListener {
                            findNavController().navigate(
                                R.id.action_showPostFragment_to_showImageFragment,
                                Bundle().apply {
                                    showImage = post.attachment?.url
                                }
                            )
                        }

                        if (post.video != null) groupVideo.visibility = View.VISIBLE
                        else groupVideo.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}

