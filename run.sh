while :
do
  git pull origin
  ./gradlew run >> mainlog.txt
done
