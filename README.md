# BT Finder — поиск потерянного телефона по Bluetooth (RSSI → метры)

## Как пользоваться
1. На потерянном телефоне Bluetooth должен быть включён.
2. На втором телефоне установи APK, введи часть BT-имени потерянного, жми ВКЛЮЧИТЬ ПОИСК.
3. Ходи и смотри дистанцию: RSSI → метры по формуле `10^((TxPower-RSSI)/10n)`.

## Как забилдить через GitHub Actions
1. Создай репозиторий на GitHub, запушь эти файлы в ветку `main`:
   `git init; git add .; git commit -m init; git branch -M main; git remote add origin https://github.com/USER/REPO.git; git push -u origin main`
2. Открой вкладку Actions → Build APK → скачай артефакт `app-debug`.
3. Установи APK на телефон.
