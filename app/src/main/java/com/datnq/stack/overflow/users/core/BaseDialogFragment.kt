package com.datnq.stack.overflow.users.core

import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.datnq.stack.overflow.users.R
import dagger.android.support.DaggerAppCompatDialogFragment

abstract class BaseDialogFragment: DaggerAppCompatDialogFragment() {

    fun activity(): BaseActivity {
        return activity as BaseActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.isCancelable = true
        if (showsDialog) {
            this.isCancelable = true
        }
        setStyle(DialogFragment.STYLE_NO_TITLE, 0)
    }

    override fun onStart() {
        super.onStart()

        activity?.let {
            dialog?.window?.setBackgroundDrawable(ContextCompat.getDrawable(it, R.drawable.rounded_dialog))
        }
        dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

}