package com.luisppinheiroj.whatappslike.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.core.view.inputmethod.EditorInfoCompat
import androidx.core.view.inputmethod.InputConnectionCompat
import com.luisppinheiroj.whatappslike.helper.AndroidVersionUtil

class ImageKeyboardEditText : androidx.appcompat.widget.AppCompatEditText {

    private var commitContentListener: InputConnectionCompat.OnCommitContentListener? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setCommitContentListener(listener: InputConnectionCompat.OnCommitContentListener) {
        this.commitContentListener = listener
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
        val con = super.onCreateInputConnection(outAttrs)
        EditorInfoCompat.setContentMimeTypes(outAttrs, arrayOf("image/gif", "image/png"))

        return InputConnectionCompat.createWrapper(con, outAttrs) { inputContentInfo, flags, opts ->
            if (commitContentListener != null) {
                if (AndroidVersionUtil.isAndroidN_MR1 && flags and InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION != 0) {
                    try {
                        inputContentInfo.requestPermission()
                    } catch (e: Exception) {
                        return@createWrapper false
                    }
                }

                commitContentListener?.onCommitContent(
                    inputContentInfo, flags, opts
                )

                return@createWrapper true
            } else {
                return@createWrapper false
            }
        }



    }
}