while :
do
  cd ..
  cd darkan-core
  git pull origin master
  cd ..
  cd darkan-world-server
  gradle clean build run >> mainlog.txt
done