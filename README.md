# TapPass

**English** | [正體中文](README_zhTW.md) | [简体中文](README_zhCN.md)

TapPass lets the **quick action key** on the vivo X500 Pro Max open the built-in **Access Card** page in one press.

The quick action key can only be bound to apps that have a launcher icon. Access Card is a system component without one, so it never appears in the list. TapPass is a tiny trampoline app: it shows up in the list, and when triggered it opens the Access Card page and finishes immediately. You never see TapPass itself.

## Features

- **One-press access**: bind TapPass to the quick action key and the Access Card page opens directly.
- **No root, no Shizuku**: the target page is exported, so a normal app can open it.
- **Same intent as the system**: TapPass sends exactly what the system desktop shortcut sends (action, flags and extras).
- **Diagnostics screen**: shows whether the target is installed, its version, whether the page resolves and is exported, and the last launch error. It also has a test launch button.
- **Clear failure handling**: if the page cannot be opened, TapPass shows the reason in a toast and opens the diagnostics screen.
- **Languages**: English, Traditional Chinese (Taiwan) and Simplified Chinese.

## Requirements

| Item | Requirement |
| --- | --- |
| Device | vivo X500 Pro Max (OriginOS) |
| Android | 11 (API 30) or later |
| Target app | Access Card (`com.vivo.seclient`), preinstalled |

## Installation

Build and install with Android Studio, or from the command line:

```bat
:: cmd
gradlew.bat assembleDebug
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

> AGP needs a full JDK that includes `jlink`. If the build fails with `jlink executable ... does not exist`, point `JAVA_HOME` to the JBR bundled with Android Studio:
> `set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"`

## Usage

1. Open **Settings → Quick action key** and choose the action you want, for example double press.
2. Under **Open app**, pick **TapPass**.
   Do not pick the TapPass entry under **Open app services**; that one opens the diagnostics screen.
3. Press the key. The Access Card page opens.

### Diagnostics screen

The diagnostics screen can be opened in either of these ways:

- Long-press the TapPass icon and choose **Diagnostics**.
- Go to **App info → TapPass** and tap the gear icon.

The **About** section at the bottom shows the app version and the author.

## How it works

TapPass starts the card page with the same intent as the system desktop shortcut that Access Card creates:

```
action    vivo.intent.action.NFC_BUS_SWINGSPLASH
component com.vivo.seclient/com.vivo.pay.fexswipe.activity.NewSwipeActivity
flags     FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
extras    lb_from = "5"   (String)
```

The trampoline activity uses `Theme.NoDisplay`, `excludeFromRecents` and `noHistory`, so it never shows a window and never stays in the recent apps list.

## Troubleshooting

If pressing the key does nothing, or you see a "Can't open Access Card" toast, open the diagnostics screen:

| Diagnostics shows | Likely cause |
| --- | --- |
| Not installed | Access Card is missing on this device |
| Resolvable: No | A system update renamed or removed the card page |
| Exported: No | A system update made the page private; third-party apps can no longer open it |

After a system update, the target may change. If that happens, TapPass needs an update to follow the new target.

## Author

**Edward Wu** · [GitHub @bluehomewu](https://github.com/bluehomewu)

## License

Copyright 2026 Edward Wu

Licensed under the [Apache License 2.0](LICENSE).

The GitHub mark in the About section comes from [Octicons](https://github.com/primer/octicons) (MIT License).
