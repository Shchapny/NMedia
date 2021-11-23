package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityNewPostBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.PostViewModel

class NewPostOrEditPostActivity : AppCompatActivity() {

    lateinit var binding: ActivityNewPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityNewPostBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.edit.requestFocus()

        val textEdit = intent.getStringExtra(Intent.EXTRA_TEXT)
        if (textEdit != null) {
            binding.boxEdit.visibility = View.VISIBLE
            binding.textInfoEdit.text = textEdit
        }
        binding.edit.setText(textEdit)

        val viewModel by viewModels<PostViewModel>()

        binding.ok.setOnClickListener {
            val intent = Intent()
            val text = binding.edit.text
            if (text.isNullOrBlank()) {
                setResult(Activity.RESULT_CANCELED, intent)
            } else {
                intent.putExtra(Intent.EXTRA_TEXT, text.toString())
                setResult(Activity.RESULT_OK, intent)

                viewModel.editContent(text.toString())
                viewModel.add()

                binding.edit.setText("")
                binding.edit.clearFocus()
                AndroidUtils.hideKeyboard(binding.edit)

            }
            finish()
        }

        binding.close.setOnClickListener {
            viewModel.add()
            binding.edit.setText("")
            binding.edit.clearFocus()
            AndroidUtils.hideKeyboard(binding.edit)
            finish()
        }

    }
}