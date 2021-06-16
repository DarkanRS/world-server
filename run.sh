while :
do
  git pull origin master
  git lfs pull origin master
  mvn compile
  mvn exec:java >> mainlog.txt
done