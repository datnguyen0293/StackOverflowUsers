package com.datnq.stack.overflow.users.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.datnq.stack.overflow.users.hotel.R

abstract class BaseDialogFragment: AppCompatDialogFragment() {

    @LayoutRes abstract fun getLayoutResourceId(): Int

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

    override fun onCreateView(@NonNull inflater: LayoutInflater, @Nullable container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(getLayoutResourceId(), container, false)
        super.onCreateView(inflater, container, savedInstanceState)
        return rootView
    }

}