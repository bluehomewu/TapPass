# TapPass

[English](README.md) | [正體中文](README_zhTW.md) | **简体中文**

TapPass 让 vivo X500 Pro Max 的**快捷操作按键**一键打开系统自带的 **Access Card（门禁卡）**卡片页。

快捷操作按键只能绑定有桌面图标的应用。Access Card 是没有桌面图标的系统组件，所以不会出现在列表里。TapPass 是一个很小的跳板应用：它会出现在列表中，被触发时打开门禁卡卡片页并立即退出，你不会看到 TapPass 自己的界面。

## 功能

- **一键打开**：把 TapPass 绑定到快捷操作按键，按下就直接显示门禁卡卡片页。
- **不需要 root，也不需要 Shizuku**：目标页面是 exported，普通应用就能打开。
- **与系统发送的 intent 相同**：TapPass 发送的 action、flags 和 extras 与系统桌面快捷方式完全一致。
- **诊断页**：显示目标是否已安装、版本、卡片页能否解析、是否 exported，以及最近一次错误；也可以手动测试启动。
- **失败时说明原因**：打不开时会用 Toast 显示原因，并自动打开诊断页。
- **多语言**：英文、繁体中文、简体中文。

## 系统要求

| 项目 | 要求 |
| --- | --- |
| 设备 | vivo X500 Pro Max（OriginOS） |
| Android | 11（API 30）及以上 |
| 目标应用 | 系统自带的 Access Card（`com.vivo.seclient`） |

## 安装

使用 Android Studio 编译安装，或在命令行执行：

```bat
:: cmd
gradlew.bat assembleDebug
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

> AGP 需要包含 `jlink` 的完整 JDK。如果编译时出现 `jlink executable ... does not exist`，请把 `JAVA_HOME` 指向 Android Studio 自带的 JBR：
> `set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"`

## 使用方法

1. 打开 **设置 → 快捷操作按键**，选择要使用的动作，例如“按两下启动”。
2. 在 **打开应用** 下选择 **TapPass**。
   不要选 **打开应用服务** 下的 TapPass，那个会打开诊断页。
3. 按下按键，门禁卡卡片页就会出现。

### 诊断页

可以通过以下任一方式打开诊断页：

- 长按 TapPass 图标，选择 **诊断**。
- 进入 **应用信息 → TapPass**，点击右上角的齿轮。

页面底部的 **关于** 区域会显示应用版本和作者信息。

## 工作原理

TapPass 使用与 Access Card 系统桌面快捷方式相同的 intent 启动卡片页：

```
action    vivo.intent.action.NFC_BUS_SWINGSPLASH
component com.vivo.seclient/com.vivo.pay.fexswipe.activity.NewSwipeActivity
flags     FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
extras    lb_from = "5"   (String)
```

跳板 Activity 使用 `Theme.NoDisplay`、`excludeFromRecents` 和 `noHistory`，因此不会显示界面，也不会留在最近任务列表中。

## 故障排查

如果按下按键没有反应，或出现“无法打开门禁卡”的提示，请打开诊断页查看：

| 诊断页显示 | 可能原因 |
| --- | --- |
| 未安装 | 这台设备没有 Access Card |
| 可解析：否 | 系统更新后，卡片页被改名或移除 |
| exported：否 | 系统更新后，卡片页不再对外开放，第三方应用无法再打开它 |

系统更新可能会改变目标。遇到这种情况，TapPass 需要更新以适配新的目标。

## 作者

**Edward Wu** · [GitHub @bluehomewu](https://github.com/bluehomewu)

## 许可证

Copyright 2026 Edward Wu

本项目采用 [Apache License 2.0](LICENSE) 许可证。

“关于”区域中的 GitHub 标志来自 [Octicons](https://github.com/primer/octicons)（MIT License）。
