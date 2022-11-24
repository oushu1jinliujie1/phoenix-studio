
## build
```shell
mvn clean package
```

## run
```shell
nohup java -jar phoenix-studio-backend-1.0-SNAPSHOT.jar > log.log 2>1 &

# 指定application.yml文件的路径
nohup java -jar -Dspring.config.location=/xxx/application.yml phoenix-studio-backend-1.0-SNAPSHOT.jar > log.log 2>1 &
```