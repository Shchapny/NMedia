package ru.netology.nmedia.dto

import android.os.Parcel
import android.os.Parcelable
import java.text.SimpleDateFormat
import java.util.*

data class Post(
    val id: Long = 0,
    val author: String? = "Нетология. Университет интернет-профессий будущего",
    val content: String? = "",
    val published: String? = formattedDate,
    val likedByMe: Boolean = false,
    val views: Long = 0,
    val likes: Long = 0,
    val share: Long = 0,
    val video: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(author)
        parcel.writeString(content)
        parcel.writeString(published)
        parcel.writeByte(if (likedByMe) 1 else 0)
        parcel.writeLong(views)
        parcel.writeLong(likes)
        parcel.writeLong(share)
        parcel.writeString(video)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Post> {
        override fun createFromParcel(parcel: Parcel): Post {
            return Post(parcel)
        }

        override fun newArray(size: Int): Array<Post?> {
            return arrayOfNulls(size)
        }
    }
}

private val formattedDate = SimpleDateFormat.getDateTimeInstance()!!
    .format(Calendar.getInstance().time!!)!!