@echo off
echo Stopping WigellGym
docker stop wigellgym-container
echo Deleting container WigellGym
docker rm wigellgym-container
echo Deleting image WigellGym
docker rmi wigellgym
echo Running mvn package
call mvn package -DskipTests
echo Creating image wigellgym
docker build -t wigellgym:latest .
echo Creating and running container WigellGym
docker run -d -p 6565:6565 --name wigellgym-container --network services-network wigellgym:latest
echo Done!