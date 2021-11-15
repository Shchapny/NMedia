package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import ru.netology.nmedia.adapter.PostActionListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel by viewModels<PostViewModel>()
        val adapter = PostAdapter(
            object : PostActionListener {
                override fun edit(post: Post) {
                    viewModel.edit(post)
                    binding.boxEdit.visibility = View.VISIBLE
                    binding.textInfoEdit.text = post.content
                }

                override fun remove(post: Post) {
                    viewModel.remove(post.id)
                }

                override fun like(post: Post) {
                    viewModel.like(post.id)
                }

                override fun share(post: Post) {
                    viewModel.share(post.id)
                }

            }
        )

        binding.container.adapter = adapter
        viewModel.data.observe(this, adapter::submitList)

        with(binding) {

            textLayout.setEndIconOnClickListener {
                val text = editContent.text?.toString()
                if (text.isNullOrBlank()) {
                    Toast.makeText(this@MainActivity, getString(R.string.blank_content_error), Toast.LENGTH_SHORT).show()
                    return@setEndIconOnClickListener
                }
                viewModel.editContent(text)
                viewModel.add()

                editContent.setText("")
                editContent.clearFocus()
                AndroidUtils.hideKeyboard(editContent)
                boxEdit.visibility = View.GONE
            }

            close.setOnClickListener {
                viewModel.add()
                editContent.setText("")
                editContent.clearFocus()
                AndroidUtils.hideKeyboard(editContent)
                boxEdit.visibility = View.GONE
            }

            viewModel.edited.observe(this@MainActivity) {
                if (it.id == 0L) {
                    return@observe
                }

                editContent.requestFocus()
                editContent.setText(it.content)
            }
        }
    }
}