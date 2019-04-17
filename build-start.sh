git revert

git pull -rebase

# 替换环境文件
echo "spring:
        profiles:
          active: prod" > ./src/main/resources/application.yml

mvn clean package

# 启动打包后的文件

java -jar ./target/kaca-search.jar