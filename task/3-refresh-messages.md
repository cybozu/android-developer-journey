# メッセージ一覧を更新できる

## タスク内容

- `ThreadScreen`のメッセージ一覧をPull to Refreshで更新できるようにする
    - リストにPull to Refreshを実装する
    - 更新すると、その時点での最新のメッセージ一覧が表示される
- 上記の動きが確認できるテストを書く
    - `ThreadViewModelTest`に実装する
- 更新が失敗したときにもエラーを表示する
    - エラーの考慮は上手く設計されていれば前回までのタスクで対応できているはず

## 学び

- 業務で実際にある最低限のタスク単位を体験
- 作りたい挙動に合ったテスト内容を考える力
- 既存の実装を再利用する設計力

## 参考情報

- [Pull to refresh \| Jetpack Compose \| Android Developers](https://developer.android.com/develop/ui/compose/components/pull-to-refresh)
