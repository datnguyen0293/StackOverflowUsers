package com.datnq.stack.overflow.users.core

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.datnq.stack.overflow.users.GlobalApplication
import com.datnq.stack.overflow.users.R
import com.gico.datnq.library.utilities.LoggerUtil
import com.squareup.picasso.Picasso
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class Utilities {
    companion object {
        @JvmStatic
        fun askToAct(
            activity: Context, title: String, message: String,
            confirmButtonText: String, acceptAction: Runnable?,
            denyButtonText: String, denyAction: Runnable?
        ): AlertDialog {
            return AlertDialog.Builder(activity, R.style.CustomDialogTheme)
                    .setIcon(R.drawable.logo)
                    .setCancelable(true)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(confirmButtonText) { dialog, _ ->
                        acceptAction?.run()
                        dialog?.dismiss()
                    }
                    .setNegativeButton(denyButtonText) { dialog, _ ->
                        denyAction?.run()
                        dialog?.dismiss()
                    }
                    .create()
        }

        @JvmStatic
        fun askToAct(
            activity: Context, title: String, message: String,
            buttonText: Array<String>, action: Array<Runnable>?
        ): AlertDialog {

            val confirmButtonText = buttonText[0]
            val acceptAction = action?.get(0)
            val denyButtonText = buttonText[1]
            val denyAction = action?.get(1)
            val neutralButtonText = buttonText[2]
            val neutralAction = action?.get(2)

            return AlertDialog.Builder(activity, R.style.CustomDialogTheme)
                    .setIcon(R.drawable.logo)
                    .setCancelable(true)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(confirmButtonText) { dialog, _ ->
                        acceptAction?.run()
                        dialog?.dismiss()
                    }
                    .setNegativeButton(denyButtonText) { dialog, _ ->
                        denyAction?.run()
                        dialog?.dismiss()
                    }
                    .setNeutralButton(neutralButtonText) { dialog, _ ->
                        neutralAction?.run()
                        dialog?.dismiss()
                    }
                    .create()
        }

        @JvmStatic
        fun askToAct(
            activity: Context, @StringRes title: Int, message: String,
            @StringRes confirmButtonText: Int, acceptAction: Runnable?,
            @StringRes denyButtonText: Int, denyAction: Runnable?
        ): AlertDialog {
            return askToAct(
                activity, activity.getString(title), message,
                activity.getString(confirmButtonText), acceptAction,
                activity.getString(denyButtonText), denyAction
            )
        }

        @JvmStatic
        fun askToAct(
            activity: Context, @StringRes title: Int, @StringRes message: Int,
            @StringRes confirmButtonText: Int, acceptAction: Runnable?,
            @StringRes denyButtonText: Int, denyAction: Runnable?
        ): AlertDialog {
            return askToAct(
                activity,
                title,
                activity.getString(message),
                confirmButtonText,
                acceptAction,
                denyButtonText,
                denyAction
            )
        }

        @JvmStatic
        fun isOnline(): Boolean {
            try {
                val connectivityManager: ConnectivityManager? = GlobalApplication.getInstance().getSystemService(
                    Context.CONNECTIVITY_SERVICE
                ) as? ConnectivityManager

                val activeNetwork = connectivityManager?.activeNetwork

                val networkCapabilities = connectivityManager?.getNetworkCapabilities(activeNetwork)
                networkCapabilities?.let {
                    return it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || it.hasTransport(
                        NetworkCapabilities.TRANSPORT_WIFI
                    )
                }

                return false
            } catch (e: Exception) {
                LoggerUtil.e(Utilities::class.java.simpleName, "isOnline()", e)
                return false
            }
        }

        @JvmStatic
        fun switchThreads(backgroundTask: Runnable, mainTask: Runnable) {
            val compositeDisposable = CompositeDisposable()
            compositeDisposable.add(Completable.fromRunnable { backgroundTask.run() }
                .subscribeOn(Schedulers.io())
                .doOnError {
                    LoggerUtil.e(
                        Utilities::class.java.simpleName,
                        "switchThreads(backgroundTask: Runnable, mainTask: Runnable)",
                        it
                    )
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mainTask.run()
                }, {
                    LoggerUtil.e(
                        Utilities::class.java.simpleName,
                        "switchThreads(backgroundTask: Runnable, mainTask: Runnable)",
                        it
                    )
                }))
        }

        /**
         * Format date from the unixTimestamp
         * @param unixTimestamp  The unixTimestamp
         * @return               The string of date
         */
        @JvmStatic
        fun formatDate(unixTimestamp: Long): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val time = Date(TimeUnit.MILLISECONDS.convert(unixTimestamp, TimeUnit.SECONDS))
            return dateFormat.format(time)
        }

        @JvmStatic
        fun toBitmap(image: ByteArray): Bitmap? {
            return BitmapFactory.decodeByteArray(image, 0, image.size)
        }

        @JvmStatic
        @Throws(IOException::class)
        fun toBytes(imageUrl: String): ByteArray {
            val bitmap = Picasso.get().load(imageUrl).get()
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray: ByteArray = stream.toByteArray()
            bitmap.recycle()
            return byteArray
        }
    }
}