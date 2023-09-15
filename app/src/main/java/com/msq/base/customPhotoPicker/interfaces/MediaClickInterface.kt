package com.msq.base.customPhotoPicker.interfaces

import com.msq.base.customPhotoPicker.gallery.MediaModel

interface MediaClickInterface {
    fun onMediaClick(media: MediaModel)
    fun onMediaLongClick(media: MediaModel, intentFrom: String)
}