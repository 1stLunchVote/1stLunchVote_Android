package com.jwd.lunchvote.core.ui.util

import android.widget.EditText
import androidx.core.widget.addTextChangedListener

inline fun EditText.setOnTextChangedListener(
    crossinline onChanged : (String) -> Unit
) {
    this.addTextChangedListener { editable ->
        onChanged(editable?.toString() ?: "")
    }
}