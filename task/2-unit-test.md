# メッセージ取得エラーをテストする

## タスク内容

- `ThreadViewModelTest`に、データ取得に失敗したときのテストを実装する
- テストで確かめたい動き
    - Repositoryのデータ取得が失敗したとき、ViewModelが出力するStateがエラー状態になる

## 学び

- ユニットテストの基礎
- テストダブルの利用を通してDIの価値を理解する

## ヒント

Android Studio上でテストを実行する方法は[Test in Android Studio \| Android Developers](https://developer.android.com/studio/test/test-in-android-studio)に詳しく書かれています。

コマンドラインで実行することもできます。

```bash
./gradlew testDebugUnitTest
```

## 参考情報

- [Build local unit tests \| Test your app on Android \| Android Developers](https://developer.android.com/training/testing/local-tests)
- [Use test doubles in Android \| Test your app on Android \| Android Developers](https://developer.android.com/training/testing/fundamentals/test-doubles)
