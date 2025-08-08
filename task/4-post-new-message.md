# メッセージを投稿できる

## タスク内容

- あるスレッドに新しいメッセージを投稿できる
- 投稿後、投稿したメッセージを表示する
- 上記の動きが確認できるViewModelのテストを書く
- （Nice to have）エラーの考慮

## 学び

- エラーまで考慮した実践的な機能開発の体験
- API通信の新設

## ヒント

### 投稿UI

シンプルなテキスト入力と送信ボタンがあれば十分です。
チャットアプリのように、`ThreadScreen`内で入力できるシンプルな構成でよいです。

### メッセージを新規作成するAPI

kintoneの公開APIを使用します。
APIの具体的な仕様は以下のページを参照してください。

[スペースのスレッドにコメントを投稿する \- cybozu developer network](https://cybozu.dev/ja/kintone/docs/rest-api/spaces/add-thread-comment/)

API呼び出しの実装は `:data:space` にあるRepositoryの実装を参考にしてみてください。

## 参考情報

本アプリで使用している通信ライブラリ

- [square/okhttp](https://github.com/square/okhttp)
- [square/retrofit](https://github.com/square/retrofit)
