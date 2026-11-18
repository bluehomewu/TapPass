package tw.bluehomewu.tappass

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * 系統 Access Card 卡片頁的唯一啟動入口。
 *
 * intent 內容完全複製系統桌面捷徑（seclient 的 pinned shortcut「Card Page」），
 * 依據為 adb 對系統捷徑的調查結果，不要自行增減 flags 或 extras。
 */
object AccessCard {
    const val PACKAGE = "com.vivo.seclient"
    val COMPONENT = ComponentName(PACKAGE, "com.vivo.pay.fexswipe.activity.NewSwipeActivity")

    private const val TAG = "TapPass"
    private const val ACTION = "vivo.intent.action.NFC_BUS_SWINGSPLASH"
    private const val PREFS = "access_card"
    private const val KEY_LAST_ERROR = "last_error"
    private const val KEY_LAST_ERROR_TIME = "last_error_time"

    fun intent(): Intent = Intent(ACTION)
        .setComponent(COMPONENT)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        // lb_from 必須是 String；"5" 代表從桌面捷徑進入
        .putExtra("lb_from", "5")

    /** 啟動卡片頁；失敗時回傳錯誤描述並存成最近一次錯誤，成功回傳 null。 */
    fun launch(context: Context): String? {
        val error = try {
            context.startActivity(intent())
            return null
        } catch (e: ActivityNotFoundException) {
            e
        } catch (e: SecurityException) {
            e
        }
        Log.e(TAG, "Failed to launch $COMPONENT", error)
        val message = "${error.javaClass.simpleName}: ${error.message}"
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(KEY_LAST_ERROR, message)
            .putLong(KEY_LAST_ERROR_TIME, System.currentTimeMillis())
            .apply()
        return message
    }

    /** 最近一次錯誤與發生時間（epoch ms）；沒有錯誤時回傳 null。 */
    fun lastError(context: Context): Pair<String, Long>? {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val message = prefs.getString(KEY_LAST_ERROR, null) ?: return null
        return message to prefs.getLong(KEY_LAST_ERROR_TIME, 0L)
    }

    fun clearLastError(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .remove(KEY_LAST_ERROR)
            .remove(KEY_LAST_ERROR_TIME)
            .apply()
    }
}
