package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityNewPostBinding

class NewPostOrEditPostActivity : AppCompatActivity() {
    lateinit var binding: ActivityNewPostBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityNewPostBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val text = intent.getStringExtra(Intent.EXTRA_TEXT)
        binding.edit.setText(text)

        binding.ok.setOnClickListener {
            val intent = Intent()
            val text = binding.edit.text
            if (text.isNullOrBlank()) {
                setResult(Activity.RESULT_CANCELED, intent)
            } else {
                intent.putExtra(Intent.EXTRA_TEXT, text.toString())
                setResult(Activity.RESULT_OK, intent)
            }
            finish()
        }
    }
}