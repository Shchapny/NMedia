package ru.netology.nmedia.util

import android.content.Intent
import androidx.fragment.app.Fragment
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider

object LoadImage {

    fun loadFromGallery(fragment: Fragment, maxSize: Int,  intent: (Intent) -> Unit) {
        ImagePicker.with(fragment)
            .crop()
            .compress(maxSize)
            .provider(ImageProvider.GALLERY)
            .galleryMimeTypes(arrayOf("image/png", "image/jpeg"))
            .createIntent(intent)
    }

    fun loadFromCamera(fragment: Fragment, maxSize: Int, intent: (Intent) -> Unit) {
        ImagePicker.with(fragment)
            .crop()
            .compress(maxSize)
            .provider(ImageProvider.CAMERA)
            .createIntent(intent)
    }
}