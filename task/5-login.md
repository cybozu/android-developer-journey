# ログインを実装する
## タスク内容
- ログイン画面を作成する
  - ユーザー名、パスワードを入力し、ログインボタンを押すことによってログイン処理が始まるというシンプルな構成で良い
  - アプリ起動時にログイン画面を経由して、ホーム画面に移動できる
- 入力されたユーザー名、パスワードを暗号化して端末に保存する
- (Optional) ログアウトができる

## 学び
- 画面の構築と遷移
- データの永続化(保存)

## ヒント
### 画面遷移
画面の遷移には[Navigation 3](https://developer.android.com/guide/navigation/navigation-3)というライブラリを使用しています 

### データの永続化(保存)
端末内に保持しておきたいデータには種類があります
- アプリの設定項目
  - ユーザー名、パスワードなどはこちらに該当します
- ユーザーに提示したいコンテンツ
  - モバイル端末はデータ容量に限度があることから、基本的にはサーバーで保持するため、あくまでサーバーに繋がらない時の保険として一部のデータを保持(キャッシュ)することが多いです

今回は前者に適したライブラリとして[DataStore](https://developer.android.com/topic/libraries/architecture/datastore)をお勧めします
ユーザー名、パスワードは`local.properties`で設定した値を[`SpaceRemoteDataSource.kt`](../data/space/src/main/java/com/cybozu/sample/kintone/spaces/data/space/SpaceRemoteDataSource.kt)で直接参照しています
