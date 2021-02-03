package com.datnq.stack.overflow.users.core

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.text.TextUtils
import android.util.Base64
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.text.isDigitsOnly
import com.datnq.stack.overflow.users.datnq.library.utilities.LoggerUtil
import com.datnq.stack.overflow.users.hotel.GiHotelApplication
import com.datnq.stack.overflow.users.hotel.R
import com.datnq.stack.overflow.users.hotel.model.LoginInformation
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

class Utilities {
    companion object {
        @JvmStatic
        fun askToAct(activity: Context, title: String, message: String,
                     confirmButtonText: String, acceptAction: Runnable?,
                     denyButtonText: String, denyAction: Runnable?): AlertDialog {
            return AlertDialog.Builder(activity, R.style.CustomDialogTheme)
                    .setIcon(R.drawable.app_icon3)
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
        fun askToAct(activity: Context, title: String, message: String,
                     buttonText: Array<String>, action: Array<Runnable>?): AlertDialog {

            val confirmButtonText = buttonText[0]
            val acceptAction = action?.get(0)
            val denyButtonText = buttonText[1]
            val denyAction = action?.get(1)
            val neutralButtonText = buttonText[2]
            val neutralAction = action?.get(2)

            return AlertDialog.Builder(activity, R.style.CustomDialogTheme)
                    .setIcon(R.drawable.app_icon3)
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
        fun askToAct(activity: Context, @StringRes title: Int, message: String,
                     @StringRes confirmButtonText: Int, acceptAction: Runnable?,
                     @StringRes denyButtonText: Int, denyAction: Runnable?): AlertDialog {
            return askToAct(activity, activity.getString(title), message,
                    activity.getString(confirmButtonText), acceptAction,
                    activity.getString(denyButtonText), denyAction)
        }

        @JvmStatic
        fun askToAct(activity: Context, @StringRes title: Int, @StringRes message: Int,
                     @StringRes confirmButtonText: Int, acceptAction: Runnable?,
                     @StringRes denyButtonText: Int, denyAction: Runnable?): AlertDialog {
            return askToAct(activity, title, activity.getString(message), confirmButtonText, acceptAction, denyButtonText, denyAction)
        }

        @JvmStatic
        fun encrypt(text: String): String {
            val array: ByteArray = text.toByteArray(StandardCharsets.UTF_8)
            val finalStringByteArray = GiHotelApplication.mySecurity().maHoaConfigNode(array)
            return Base64.encodeToString(finalStringByteArray, Base64.NO_WRAP)
        }

        @JvmStatic
        fun decrypt(byteArray: ByteArray): String {
            val byteArrayMessage: ByteArray = Base64.decode(byteArray, Base64.DEFAULT)
            return GiHotelApplication.mySecurity().giaiMaConfigNode(byteArrayMessage, byteArrayMessage.size)
        }

        @JvmStatic
        fun isOnline(): Boolean {
            try {
                val connectivityManager: ConnectivityManager? = GiHotelApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

                val activeNetwork = connectivityManager?.activeNetwork

                val networkCapabilities = connectivityManager?.getNetworkCapabilities(activeNetwork)
                networkCapabilities?.let {
                    return it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                }

                return false
            } catch (e: Exception) {
                LoggerUtil.e(Utilities::class.java.simpleName, "isOnline()", e)
                return false
            }
        }

        @JvmStatic
        fun activateAccount(userName: String, clientName: String) {
            val currentList = GiHotelApplication.sharedPreferencesCenter().getAccounts()
            if (currentList.isNotEmpty()) {
                for (i: Int in currentList.indices) {
                    val account = currentList[i]
                    account.active = userName.trim().equals(account.userName.trim(), true) && clientName.trim().equals(account.clientName.trim(), true)
                    currentList[i] = account
                }
                GiHotelApplication.sharedPreferencesCenter().setAccounts(currentList)
            }
        }

        @JvmStatic
        fun addAccountToApplication(account: LoginInformation) {
            val currentList = GiHotelApplication.sharedPreferencesCenter().getAccounts()
            if (currentList.isEmpty()) {
                currentList.add(account)
                GiHotelApplication.sharedPreferencesCenter().setAccounts(currentList)
                return
            }
            for (i: Int in currentList.indices) {
                if (currentList[i].userName.equals(account.userName, true) && currentList[i].clientName.equals(account.clientName, true)) {
                    val acc = currentList[i]
                    acc.password = account.password
                    acc.displayName = account.displayName
                    currentList[i] = acc
                    GiHotelApplication.sharedPreferencesCenter().setAccounts(currentList)
                    return
                }
            }
            currentList.add(account)
            GiHotelApplication.sharedPreferencesCenter().setAccounts(currentList)
        }

        @JvmStatic
        fun getStringTime(time: String): String {
            return try {
                val dateFormat = SimpleDateFormat("HH:mm  dd/MM/yyyy", Locale.getDefault())
                val calendar = Calendar.getInstance()
                if (!TextUtils.isEmpty(time) && time.isDigitsOnly()) {
                    calendar.timeInMillis = time.toLong()
                    dateFormat.format(calendar.time)
                } else {
                    Constants.EMPTY_STRING
                }
            } catch (e: Exception) {
                LoggerUtil.e(Utilities::class.java.simpleName, "getStringTime(String time)", e)
                Constants.EMPTY_STRING
            }
        }

        @JvmStatic
        fun toGrayscale(resources: Resources, @DrawableRes originalDrawableResourceId: Int): Drawable {
            val bmpOriginal = BitmapFactory.decodeResource(resources, originalDrawableResourceId)
            val height = bmpOriginal.height
            val width = bmpOriginal.width
            val bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val c = Canvas(bmpGrayscale)
            val paint = Paint()
            val cm = ColorMatrix()
            cm.setSaturation(0f)
            val f = ColorMatrixColorFilter(cm)
            paint.colorFilter = f
            c.drawBitmap(bmpOriginal, 0f, 0f, paint)
            return BitmapDrawable(resources, bmpGrayscale)
        }

        @JvmStatic
        fun switchThreads(backgroundTask: Runnable, mainTask: Runnable) {
            val compositeDisposable = CompositeDisposable()
            compositeDisposable.add(Completable.fromRunnable { backgroundTask.run() }
                    .subscribeOn(GiHotelApplication.subscribeScheduler())
                    .doOnError { LoggerUtil.e(Utilities::class.java.simpleName, "switchThreads(backgroundTask: Runnable, mainTask: Runnable)", it) }
                    .observeOn(GiHotelApplication.observeScheduler())
                    .subscribe({
                        mainTask.run()
                    }, { LoggerUtil.e(Utilities::class.java.simpleName, "switchThreads(backgroundTask: Runnable, mainTask: Runnable)", it) }))
        }
    }
}