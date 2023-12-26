/*
db フォルダ内に mysql_setup.sql ファイルを新規作成し、下記の内容で保存します。
ここでは、daily_report_system というデータベースを作成し、
どこからでもアクセス可能ですべての権限を持つユーザ repuser を作成します
（rootなどデフォルトで存在するユーザは、ローカルPCからしかログインできません）。
*/

CREATE DATABASE IF NOT EXISTS daily_report_system CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
CREATE USER IF NOT EXISTS 'repuser'@'%' IDENTIFIED BY 'reppass';
GRANT ALL PRIVILEGES ON *.* TO 'repuser'@'%';
CREATE DATABASE IF NOT EXISTS wordpress CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
