# メッセージの取得に失敗した場合にエラーを表示する

## タスク内容

- `ThreadScreen`でメッセージの取得に失敗したときに、失敗したことをユーザに伝えるエラーメッセージを表示する
- エラーはSnackbarで表示する
- エラー文言は「メッセージを取得できませんでした」
    - 日本語文言のみでよい

## 学び

- 個人開発だと無視しがちなエラーハンドリングの基礎
- 簡単な状態変更 + レイアウト変更

## ヒント

Kotlinでエラーを表現する方法は2種類あります。

- エラーを関数の戻り値で型表現する方法（Result, Nullableなど）
- 例外（Exceptionクラス）を投げる方法

それぞれ、メリット・デメリットがあります。
どの方法がよいのか調べてみてください。

## 参考情報

- [Snackbar \- Material Design 3](https://m3.material.io/components/snackbar/overview)
- [ComposeでのSnackbarの使い方](https://developer.android.com/develop/ui/compose/components/snackbar)
- [Kotlin and Exceptions \| by Roman Elizarov \| Medium](https://elizarov.medium.com/kotlin-and-exceptions-8062f589d07)
