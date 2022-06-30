#!/usr/bin/env bash
#if type -p java; then
#    echo found java executable in PATH
   
#else
#    sudo amazon-linux-extras install java-openjdk11

#fi

#sudo kill -9 $(sudo lsof -t -i:80)
#echo "Killed process running on port 80"

#sudo java -jar api-0.0.1-SNAPSHOT.jar --server.port=80
#echo "Started server using java -jar command"
cd /home/ec2-user

unzip build-output.zip

cd /home/ec2-user/build-output

/usr/local/bin/docker-compose up --build --force-recreate -d

cd /home/ec2-user

rm -f build-output.zip || true

rm -rf build-output || true

rm -rf src || true

exit
