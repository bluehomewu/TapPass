package tw.bluehomewu.tappass

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import tw.bluehomewu.tappass.ui.theme.TapPassTheme
import java.text.DateFormat
import java.util.Date

/** 診斷頁：檢查目標套件與 Activity 的狀態，並可手動測試啟動。 */
class MainActivity : ComponentActivity() {

    private data class Status(
        val versionName: String?,
        val versionCode: Long,
        val resolvable: Boolean,
        /** null 表示查不到 ActivityInfo。 */
        val exported: Boolean?,
        val lastError: Pair<String, Long>?,
    )

    private companion object {
        const val GITHUB_URL = "https://github.com/bluehomewu"
    }

    private var status by mutableStateOf<Status?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TapPassTheme {
                status?.let { DiagnosticsScreen(it) }
            }
        }
    }

    // 從卡片頁或設定返回時重新檢查，狀態可能已改變
    override fun onResume() {
        super.onResume()
        status = readStatus()
    }

    private fun readStatus(): Status {
        val pm = packageManager
        val info = try {
            pm.getPackageInfo(AccessCard.PACKAGE, 0)
        } catch (_: PackageManager.NameNotFoundException) {
            null
        }
        val exported = try {
            pm.getActivityInfo(AccessCard.COMPONENT, 0).exported
        } catch (_: PackageManager.NameNotFoundException) {
            null
        }
        return Status(
            versionName = info?.versionName,
            versionCode = info?.longVersionCode ?: 0L,
            resolvable = pm.resolveActivity(AccessCard.intent(), 0) != null,
            exported = exported,
            lastError = AccessCard.lastError(this),
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun DiagnosticsScreen(s: Status) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { TopAppBar(title = { Text(stringResource(R.string.diag_title)) }) },
        ) { innerPadding ->
            Column(
                Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    stringResource(R.string.diag_package, AccessCard.PACKAGE),
                    if (s.versionName != null) {
                        stringResource(R.string.diag_installed, s.versionName, s.versionCode)
                    } else {
                        stringResource(R.string.diag_not_installed)
                    },
                )
                Row(stringResource(R.string.diag_resolvable), yesNo(s.resolvable))
                Row(stringResource(R.string.diag_exported), yesNo(s.exported))
                Row(
                    stringResource(R.string.diag_last_error),
                    s.lastError?.let { (message, time) ->
                        DateFormat.getDateTimeInstance().format(Date(time)) + "\n" + message
                    } ?: stringResource(R.string.diag_none),
                    trailing = if (s.lastError == null) null else {
                        {
                            TextButton(onClick = {
                                AccessCard.clearLastError(this@MainActivity)
                                status = readStatus()
                            }) { Text(stringResource(R.string.diag_clear)) }
                        }
                    },
                )
                Button(
                    onClick = { if (AccessCard.launch(this@MainActivity) != null) status = readStatus() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Text(stringResource(R.string.diag_test_launch))
                }
                AboutSection()
            }
        }
    }

    @Composable
    private fun AboutSection() {
        // 自己的套件一定查得到
        val self = packageManager.getPackageInfo(packageName, 0)
        val uriHandler = LocalUriHandler.current
        HorizontalDivider()
        Text(
            stringResource(R.string.about_title),
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
        )
        Row(
            stringResource(R.string.about_version),
            stringResource(R.string.about_version_value, self.versionName.orEmpty(), self.longVersionCode),
        )
        Row(
            stringResource(R.string.about_author),
            stringResource(R.string.author_name),
            trailing = {
                IconButton(onClick = { uriHandler.openUri(GITHUB_URL) }) {
                    Icon(
                        painterResource(R.drawable.ic_github),
                        contentDescription = stringResource(R.string.about_github),
                    )
                }
            },
        )
    }

    @Composable
    private fun Row(label: String, value: String, trailing: (@Composable () -> Unit)? = null) {
        ListItem(
            headlineContent = { Text(label) },
            supportingContent = { Text(value) },
            trailingContent = trailing,
        )
    }

    @Composable
    private fun yesNo(value: Boolean?): String = stringResource(
        when (value) {
            true -> R.string.diag_yes
            false -> R.string.diag_no
            null -> R.string.diag_unknown
        }
    )
}
