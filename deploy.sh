#!/usr/bin/env bash

mvn package
echo "Generate package"

#Copy execute_commands_on_ec2.sh file which has commands to be executed on server... Here we are copying this file
# every time to automate this process through 'deploy.sh' so that whenever that file changes, it's taken care of
scp -i "~/t2micro.ec2.pem" execute_commands_on_ec2.sh ec2-user@ec2-18-139-221-206.ap-southeast-1.compute.amazonaws.com:/home/ec2-user
echo "Copied latest 'execute_commands_on_ec2.sh' file from local machine to ec2 instance"

#scp -i "~/t2micro.ec2.pem" target/api-0.0.1-SNAPSHOT.jar ec2-user@ec2-18-139-221-206.ap-southeast-1.compute.amazonaws.com:/home/ec2-user
#echo "Copied jar file from local machine to ec2 instance"

ssh -i "~/t2micro.ec2.pem" ec2-user@ec2-18-139-221-206.ap-southeast-1.compute.amazonaws.com 'rm -r /home/ec2-user/src/*'
echo "delete the src"

scp -i "~/t2micro.ec2.pem" -r src ec2-user@ec2-18-139-221-206.ap-southeast-1.compute.amazonaws.com:/home/ec2-user/
echo "copied source"

scp -i "~/t2micro.ec2.pem" pom.xml ec2-user@ec2-18-139-221-206.ap-southeast-1.compute.amazonaws.com:/home/ec2-user/
echo "copied pom"


scp -i "~/t2micro.ec2.pem" Dockerfile ec2-user@ec2-18-139-221-206.ap-southeast-1.compute.amazonaws.com:/home/ec2-user/
echo "copied dockerfile"


scp -i "~/t2micro.ec2.pem" docker-compose.yml ec2-user@ec2-18-139-221-206.ap-southeast-1.compute.amazonaws.com:/home/ec2-user/
echo "copied docker-compose.yml"


ssh -i "~/t2micro.ec2.pem" ec2-user@ec2-18-139-221-206.ap-southeast-1.compute.amazonaws.com sudo chmod 775 'execute_commands_on_ec2.sh'
echo "Grant permision to excute_comands_on_ec2.sh"

echo "Connecting to ec2 instance and starting server using java -jar command"
ssh -i "~/t2micro.ec2.pem" ec2-user@ec2-18-139-221-206.ap-southeast-1.compute.amazonaws.com ./execute_commands_on_ec2.sh
