#!/bin/bash
# ============================================================
#  PVSalesCalculator - GitHub上传 & APK打包 操作指南
#  请在本地电脑的 TRAE 终端中逐步执行以下命令
# ============================================================

# 【重要】将 YOUR_GITHUB_USERNAME 替换为你的 GitHub 用户名！
GITHUB_USERNAME="YOUR_GITHUB_USERNAME"
REPO_NAME="PVSalesCalculator"

echo "=========================================="
echo "  第一步：上传到 GitHub"
echo "=========================================="

# 1. 先在 GitHub 网站上创建仓库（二选一）：
#    方式A：打开 https://github.com/new 手动创建（推荐）
#           仓库名填: PVSalesCalculator
#           不要勾选 README / .gitignore / License
#
#    方式B：如果安装了 gh CLI，执行：
#    gh repo create $REPO_NAME --public --source=. --remote=origin

echo ""
echo ">>> 2. 进入项目目录并关联远程仓库（替换 YOUR_GITHUB_USERNAME）"
echo "cd /path/to/PVSalesCalculator"
echo "git branch -M main"
echo "git remote add origin https://github.com/${GITHUB_USERNAME}/${REPO_NAME}.git"
echo "git push -u origin main"

echo ""
echo "=========================================="
echo "  第二步：打包 APK"
echo "=========================================="

echo ""
echo ">>> 方式一：TRAE / Android Studio 中操作（推荐）"
echo "  1. 用 TRAE 打开 PVSalesCalculator 项目"
echo "  2. 等待 Gradle Sync 完成"
echo "  3. 菜单栏 → Build → Build Bundle(s) / APK(s) → Build APK(s)"
echo "  4. 等待编译完成，右下角弹出通知 → 点击 locate"
echo "  5. APK 文件在: app/build/outputs/apk/debug/app-debug.apk"
echo ""

echo ">>> 方式二：命令行打包（需要已安装 Android SDK）"
echo "  cd /path/to/PVSalesCalculator"
echo "  ./gradlew assembleDebug"
echo "  # APK 输出路径: app/build/outputs/apk/debug/app-debug.apk"
echo ""

echo ">>> 方式三：命令行打包正式签名版"
echo "  # 1. 生成签名密钥"
echo "  keytool -genkey -v -keystore pv-sales.jks -keyalg RSA -keysize 2048 -validity 10000 -alias pv"
echo ""
echo "  # 2. 在 app/build.gradle.kts 的 android {} 中添加 signingConfigs"
echo "  # 3. 执行打包"
echo "  ./gradlew assembleRelease"
echo "  # APK 输出路径: app/build/outputs/apk/release/app-release.apk"
echo ""

echo "=========================================="
echo "  完成！"
echo "=========================================="
