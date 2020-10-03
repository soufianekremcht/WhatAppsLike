package com.luisppinheiroj.whatappslike.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.appcompat.widget.AppCompatEditText
import androidx.emoji.widget.EmojiEditTextHelper

open class EmojiableEditText : AppCompatEditText {

    private val useEmojiCompat: Boolean = true

    private var mEmojiEditTextHelper: EmojiEditTextHelper? = null
    private val emojiEditTextHelper: EmojiEditTextHelper?
        get() {
            if (mEmojiEditTextHelper == null) {
                try {
                    mEmojiEditTextHelper = EmojiEditTextHelper(this)
                } catch (e: Exception) {
                    return null
                }
            }

            return mEmojiEditTextHelper as EmojiEditTextHelper
        }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        if (useEmojiCompat && emojiEditTextHelper != null) {
            try {
                super.setKeyListener(emojiEditTextHelper!!.getKeyListener(keyListener))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun setKeyListener(keyListener: android.text.method.KeyListener) {
        if (useEmojiCompat && emojiEditTextHelper != null) {
            try {
                super.setKeyListener(emojiEditTextHelper!!.getKeyListener(keyListener))
            } catch (e: Exception) {
                super.setKeyListener(keyListener)
            }
        } else {
            super.setKeyListener(keyListener)
        }
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
        return if (useEmojiCompat && emojiEditTextHelper != null) {
            val inputConnection = super.onCreateInputConnection(outAttrs)
            try {
                emojiEditTextHelper!!.onCreateInputConnection(inputConnection, outAttrs)!!
            } catch (e: Exception) {
                e.printStackTrace()
                inputConnection
            }
        } else {
            super.onCreateInputConnection(outAttrs)
        }
    }
}