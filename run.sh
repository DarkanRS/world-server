while :
do
  git pull origin
  ./gradlew clean build run >> mainlog.txt
done
