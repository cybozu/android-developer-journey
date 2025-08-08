# メッセージの取得に失敗した場合にエラーを表示する

## タスク内容

- `ThreadScreen`でメッセージの取得に失敗したときに、失敗したことをユーザに伝えるエラーメッセージを表示する
- 具体的なエラーUIは自由に決めて良い
- エラー文言は「メッセージを取得できませんでした」
  - 日本語文言のみでよい

## 学び

- 個人開発だと無視しがちなエラーハンドリングの基礎
- 簡単な状態変更 + レイアウト変更

## ヒント

### エラー表現によく使われるUI

- Snackbar
  - 画面下に一時的に表示されるメッセージ
  - ユーザの操作を妨げない
  - [Snackbar \- Material Design 3](https://m3.material.io/components/snackbar/overview)
  - [ComposeでのSnackbarの使い方](https://developer.android.com/develop/ui/compose/components/snackbar)
- Toast
  - Snackbarと同じように一時的に表示されるメッセージ
  - Snackbarとの違いはAndroid OSに組み込まれている点
  - [Toasts overview \| Android Developers](https://developer.android.com/guide/topics/ui/notifiers/toasts)
- ダイアログ
  - 画面を覆うように表示されるメッセージ
  - ユーザの操作を妨げる
  - [Dialogs \- Material Design 3](https://m3.material.io/components/dialogs/overview)
  - [ComposeでのDialogの使い方](https://developer.android.com/develop/ui/compose/components/dialog)

### エラーハンドリングの実装

Kotlinでエラーを表現する方法は2種類あります。

- エラーを関数の戻り値で型表現する方法（Result, Nullableなど）
- 例外（Exceptionクラス）を投げる方法

それぞれ、メリット・デメリットがあります。
どの方法がよいのか調べてみてください。

- [Kotlinの元プロジェクトリードRoman ElizarovさんによるKotlinのエラー設計の解説ブログ](https://elizarov.medium.com/kotlin-and-exceptions-8062f589d07)
