package com.hosinagi.todoapp.demo.musicDemo

import android.os.Parcel
import android.os.Parcelable

data class MusicInfo(
    val id: Long, // id唯一标识
    val title: String, // 标题
    val artist: String, // 歌手
    val duration: Long, // 时长
    val size: Long, // 大小
    val path: String // 音频实际路径
) : Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readLong(),
        title = parcel.readString().orEmpty(),
        artist = parcel.readString().orEmpty(),
        duration = parcel.readLong(),
        size = parcel.readLong(),
        path = parcel.readString().orEmpty()
    )

    override fun describeContents(): Int = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(title)
        parcel.writeString(artist)
        parcel.writeLong(duration)
        parcel.writeLong(size)
        parcel.writeString(path)
    }

    companion object CREATOR : Parcelable.Creator<MusicInfo> {
        override fun createFromParcel(parcel: Parcel): MusicInfo = MusicInfo(parcel)

        override fun newArray(size: Int): Array<MusicInfo?> = arrayOfNulls(size)
    }
}
