# ubuntu:22.04をベースにする
FROM ubuntu:22.04

# lyta-dailyreport-systemディレクトリをルートディレクトリに作成してその中にfirstvi.txtを作成
RUN mkdir /lyta-dailyreport-system
RUN touch /lyta-dailyreport-system/firstvi.txt

# viのインストール
RUN apt update
RUN apt -y install vim

# ubuntuの日本語対応
RUN apt -y install language-pack-ja
RUN update-locale LANG=ja_JP.utf8
ENV LANG ja_JP.utf8

# 日本時間(JST)の設定
RUN DEBIAN_FRONTEND=noninteractive apt -y install tzdata
RUN echo 'Asia/Tokyo' > /etc/timezone
ENV TZ Asia/Tokyo

# Apacheのインストール
RUN apt -y install apache2
# httpへアクセスしても自動でhttpsへ飛ばす
RUN a2enmod rewrite
RUN a2enmod ssl
RUN a2ensite default-ssl
# COPY => コンテナ外（ローカルPC）にあるファイルをコンテナ内へ複製して格納するための命令
#COPY index.html /var/www/html
RUN sed -i -e "s/^<\/VirtualHost>/RewriteEngine on/" /etc/apache2/sites-available/000-default.conf
RUN echo 'RewriteCond %{HTTPS} off' >> /etc/apache2/sites-available/000-default.conf
RUN echo 'RewriteRule ^(.*)$ https://%{HTTP_HOST}%{REQUEST_URI} [L]' >> /etc/apache2/sites-available/000-default.conf
RUN echo '</VirtualHost>' >> /etc/apache2/sites-available/000-default.conf
EXPOSE 80
EXPOSE 443

# JDKとSpring Bootアプリケーションのインストール
RUN apt -y install wget apt-transport-https gnupg
RUN wget -O - https://packages.adoptium.net/artifactory/api/gpg/key/public | apt-key add -
RUN echo "deb https://packages.adoptium.net/artifactory/deb $(awk -F= '/^VERSION_CODENAME/{print$2}' /etc/os-release) main" | tee /etc/apt/sources.list.d/adoptium.list
RUN apt update
RUN apt -y install temurin-17-jdk=17.0.5.0.0+8
RUN mkdir /var/www/java
COPY DailyReportSystemApplication-0.0.1-SNAPSHOT.jar /var/www/java/
RUN a2enmod proxy
RUN a2enmod proxy_ajp
RUN echo "ProxyPass / ajp://localhost:8009/" >> /etc/apache2/apache2.conf

# 一般ユーザmyuserの作成及びmyuserでログイン
RUN apt -y install sudo
RUN useradd -s /bin/bash -G sudo -m myuser
RUN echo "myuser:mypass" | chpasswd
RUN echo "root:rootpass" | chpasswd

# 起動時コマンドの指定
COPY startup.sh /startup.sh
RUN chmod 744 /startup.sh
CMD /startup.sh
