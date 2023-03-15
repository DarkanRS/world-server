while :
do
  git pull origin
  ./download-latest.sh
  java --enable-preview -jar world-server.jar com.rs.Launcher >> mainlog.txt
done
