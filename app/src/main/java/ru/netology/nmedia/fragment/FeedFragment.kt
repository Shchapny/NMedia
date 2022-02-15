package ru.netology.nmedia.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostActionListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.FeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.fragment.NewPostOrEditPostFragment.Companion.textArg
import ru.netology.nmedia.fragment.ShowPostFragment.Companion.showOnePost
import ru.netology.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment(R.layout.feed) {

    private val viewModel by viewModels<PostViewModel>(
        ownerProducer = ::requireParentFragment
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FeedBinding.bind(view)


        val adapter = PostAdapter(
            object : PostActionListener {
                override fun edit(post: Post) {
                    viewModel.edit(post)
                    findNavController().navigate(
                        R.id.to_newPostOrEditPostFragment,
                        Bundle().apply { textArg = post.content })
                }

                override fun remove(post: Post) {
                    viewModel.remove(post.id)
                }

                override fun like(post: Post) {
                    if (!post.likedByMe) viewModel.like(post.id)
                    else viewModel.unlikeById(post.id)
                }

                override fun share(post: Post) {
                    viewModel.share(post.id)

                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, post.content)
                        type = "text/plain"
                    }

                    val shareIntent =
                        Intent.createChooser(intent, getString(R.string.chooser_share_post))
                    startActivity(shareIntent)
                }

                override fun playVideo(post: Post) {
                    val intentVideo = Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
                    startActivity(intentVideo)
                }

                override fun showPost(post: Post) {
                    findNavController().navigate(
                        R.id.action_feedFragment_to_showPostFragment,
                        Bundle().apply { showOnePost = post.id })
                }
            }
        )

        binding.container.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.posts)
            binding.progress.isVisible = state.loading
//            binding.errorGroup.isVisible = state.error
            binding.emptyText.isVisible = state.empty
            binding.swipeRefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.loadPosts() }
                    .show()
            }
        }

//        binding.retryButton.setOnClickListener {
//            viewModel.loadPosts()
//        }

        with(binding) {
            fab.setOnClickListener {
                findNavController().navigate(R.id.to_newPostOrEditPostFragment)
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }
}