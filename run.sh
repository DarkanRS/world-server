while :
do
  cd ..
  cd darkan-core
  git pull origin dev
  cd ..
  cd darkan-world-server
  git pull origin dev
  ./gradlew clean build run >> mainlog.txt
done