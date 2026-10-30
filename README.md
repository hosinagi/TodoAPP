# TodoAPP · 待办助手

一个基于 Kotlin 和 Android 原生 View 体系开发的本地待办应用，同时包含布局、组件、网络、数据库、Service、音频播放等 Android 学习示例。

[![Android](https://img.shields.io/badge/Android-API%2026%2B-3DDC84?logo=android&logoColor=white)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.x-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Version](https://img.shields.io/badge/version-v1.0.0-blue)](https://github.com/hosinagi/TodoAPP/releases)

## 功能

- 本地账号注册、登录与登录状态保持
- 待办新增、编辑、删除、完成状态切换
- 全部、未完成、已完成三种列表筛选
- 基于 `RecyclerView` + `DiffUtil` 的列表差异更新
- 本地资讯列表加载、下拉刷新、错误提示与重试
- 扫描并播放手机中的本地音频
- 从相册或相机更换用户头像
- 丰富的组件、布局、网络、数据库和 Service 示例页面

## 页面模块

| 模块 | 说明 |
| --- | --- |
| 待办 | 当前账号的待办 CRUD、完成状态切换和筛选 |
| 资讯 | 通过 Retrofit 请求资讯服务，支持下拉刷新 |
| 音频 | 使用 MediaStore 扫描本地音频，使用 Media3 播放 |
| 我的 | 查看当前账号、更换头像、退出登录 |
| Demo | 各类 Android 基础知识与组件示例 |

## 技术栈

| 分类 | 方案 |
| --- | --- |
| 语言 | Kotlin |
| UI | XML、ViewBinding、Material 3 |
| 列表 | RecyclerView、DiffUtil |
| 本地数据库 | SQLiteOpenHelper、SQLite |
| 登录状态 | SharedPreferences |
| 网络 | Retrofit、OkHttp、Gson |
| 图片 | Glide、Photo Picker、FileProvider |
| 媒体 | Android Media3 / ExoPlayer |
| 动画 | Lottie、Android View Animation |
| 权限 | PermissionX |
| 刷新组件 | SwipeRefreshLayout、SmartRefreshLayout |

## 环境要求

- Android Studio
- JDK 11 或更高版本
- Gradle Wrapper 9.5
- Android SDK 36
- 最低运行版本 Android 8.0，对应 `minSdk 26`

## 本地运行

克隆项目：

```bash
git clone https://github.com/hosinagi/TodoAPP.git
cd TodoAPP
```

使用 Android Studio 打开项目，等待 Gradle 同步完成后运行 `app` 模块。

也可以在命令行构建 Debug APK：

```bash
./gradlew assembleDebug
```

Windows：

```powershell
.\gradlew.bat assembleDebug
```

构建产物位于：

```text
app/build/outputs/apk/debug/app-debug.apk
```

## 资讯服务

资讯页面的接口地址配置在：

```text
app/src/main/java/com/hosinagi/todoapp/demo/netDemo/RetrofitClient.kt
```

当前默认地址：

```text
http://10.0.2.2:8081/
```

`10.0.2.2` 是 Android 模拟器访问宿主机 `localhost` 的地址。需要在宿主机 `8081` 端口运行提供 `/news/list` 接口的服务。

在真机运行时，需要把该地址改为电脑在局域网中的 IP，并确保手机和电脑处于同一网络。

## 本地存储

- 用户和待办数据保存在 `Todo.db` SQLite 数据库中
- 登录状态和当前用户名保存在名为 `todo` 的 SharedPreferences 中
- 用户头像保存在应用私有目录 `files/avatars`

项目当前使用原生 `SQLiteOpenHelper`，没有接入 Room。

## 项目结构

```text
app/src/main/java/com/hosinagi/todoapp/
├── app/db/          # SQLite 数据模型与数据库操作
├── ui/activity/     # 启动、登录、注册和主页面
├── ui/fragment/     # 待办、资讯、音频和我的页面
├── ui/adapter/      # 业务列表适配器
├── utils/           # 会话、头像和提示工具
├── customViews/     # 自定义视图
└── demo/            # Android 学习示例
```

## 测试

运行本地单元测试：

```bash
./gradlew testDebugUnitTest
```

运行设备或模拟器上的 instrumentation 测试：

```bash
./gradlew connectedDebugAndroidTest
```

现有 instrumentation 测试覆盖待办 CRUD 的用户数据隔离和头像文件保存逻辑。

## 版本

### v1.0.0

- 首个公开版本
- 完成本地账号、待办 CRUD、筛选、资讯、音频和头像功能
- 补充项目说明与构建文档

## 说明

本项目主要用于 Android 学习和功能演示。当前账号密码以明文形式保存在本地 SQLite 数据库中，不建议直接用于生产环境；正式产品应使用安全的后端认证方案，并对敏感数据加密存储。
