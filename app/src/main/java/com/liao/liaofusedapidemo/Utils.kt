package com.liao.liaofusedapidemo

import android.content.Context
import android.widget.Toast


fun Context.myToast(context: Context, str: String) {
    Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
}