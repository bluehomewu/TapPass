package tw.bluehomewu.tappass

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast

/**
 * 跳板：快捷操作按鍵綁定的唯一 LAUNCHER。
 * 使用 Theme.NoDisplay，必須在 onResume() 前 finish()，所以所有工作都在 onCreate() 內完成。
 */
class LaunchActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val error = AccessCard.launch(this)
        if (error != null) {
            Toast.makeText(applicationContext, getString(R.string.launch_failed, error), Toast.LENGTH_LONG).show()
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }
}
