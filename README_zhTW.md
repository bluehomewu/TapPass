# TapPass

[English](README.md) | **正體中文** | [简体中文](README_zhCN.md)

TapPass 讓 vivo X500 Pro Max 的**快捷操作按鍵**一鍵打開系統內建的 **Access Card（門禁卡）**卡片頁。

快捷操作按鍵只能綁定有桌面圖示的 App。Access Card 是沒有桌面圖示的系統元件，所以不會出現在清單裡。TapPass 是一個很小的跳板 App：它會出現在清單中，被觸發時打開門禁卡卡片頁並立刻結束，你不會看到 TapPass 自己的畫面。

## 功能

- **一鍵開啟**：把 TapPass 綁到快捷操作按鍵，按下就直接出現門禁卡卡片頁。
- **不需要 root，也不需要 Shizuku**：目標頁面是 exported，一般 App 就能打開。
- **和系統送出的 intent 相同**：TapPass 送出的 action、flags 和 extras 完全照抄系統桌面捷徑。
- **診斷頁**：顯示目標是否安裝、版本、卡片頁能否解析、是否 exported，以及最近一次錯誤；也可以手動測試啟動。
- **失敗時說明原因**：打不開時會用 Toast 顯示原因，並自動開啟診斷頁。
- **多語系**：英文、正體中文、簡體中文。

## 系統需求

| 項目 | 需求 |
| --- | --- |
| 裝置 | vivo X500 Pro Max（OriginOS） |
| Android | 11（API 30）以上 |
| 目標 App | 系統內建的 Access Card（`com.vivo.seclient`） |

## 安裝

用 Android Studio 編譯安裝，或在命令列執行：

```bat
:: cmd
gradlew.bat assembleDebug
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

> AGP 需要包含 `jlink` 的完整 JDK。如果編譯時出現 `jlink executable ... does not exist`，請把 `JAVA_HOME` 指向 Android Studio 附帶的 JBR：
> `set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"`

## 使用方式

1. 打開 **設定 → 快捷操作按鍵**，選擇要使用的動作，例如「按兩下啟動」。
2. 在 **打開應用程式** 底下選擇 **TapPass**。
   不要選 **打開應用程式服務** 底下的 TapPass，那個會打開診斷頁。
3. 按下按鍵，門禁卡卡片頁就會出現。

### 診斷頁

可以用以下任一方式打開診斷頁：

- 長按 TapPass 圖示，選擇 **診斷**。
- 進入 **應用程式資訊 → TapPass**，點右上角的齒輪。

頁面底部的 **關於** 區塊會顯示 App 版本和作者資訊。

## 運作原理

TapPass 用和 Access Card 系統桌面捷徑相同的 intent 啟動卡片頁：

```
action    vivo.intent.action.NFC_BUS_SWINGSPLASH
component com.vivo.seclient/com.vivo.pay.fexswipe.activity.NewSwipeActivity
flags     FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
extras    lb_from = "5"   (String)
```

跳板 Activity 使用 `Theme.NoDisplay`、`excludeFromRecents` 和 `noHistory`，所以不會顯示畫面，也不會留在最近使用的 App 列表裡。

## 疑難排解

如果按下按鍵沒有反應，或出現「無法開啟門禁卡」的提示，請打開診斷頁查看：

| 診斷頁顯示 | 可能原因 |
| --- | --- |
| 未安裝 | 這台裝置沒有 Access Card |
| 可解析：否 | 系統更新後，卡片頁被改名或移除 |
| exported：否 | 系統更新後，卡片頁改為不對外開放，第三方 App 無法再打開它 |

系統更新可能會改變目標。遇到這種情況，TapPass 需要更新以對應新的目標。

## 作者

**Edward Wu** · [GitHub @bluehomewu](https://github.com/bluehomewu)

## 授權

Copyright 2026 Edward Wu

本專案採用 [Apache License 2.0](LICENSE) 授權。

「關於」區塊中的 GitHub 標誌取自 [Octicons](https://github.com/primer/octicons)（MIT License）。
