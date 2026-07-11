-- flywayの履歴テーブルからアプリケーションユーザーの権限を剥奪(spring.flyway.placeholdersからプレースホルダーを使用)
REVOKE ALL PRIVILEGES ON TABLE ${history-table-name} FROM ${app-user};
