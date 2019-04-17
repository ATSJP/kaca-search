echo 'start...'

# 去除修改的环境
git -c core.quotepath=false -c log.showSignature=false rm --cached -f -- src/main/resources/application.yml
git -c core.quotepath=false -c log.showSignature=false checkout HEAD -- src/main/resources/application.yml

#  拉代码
git pull --rebase

# 替换环境文件
echo "spring:
        profiles:
          active: prod" > ./src/main/resources/application.yml

mvn clean package

# 启动打包后的文件
java -jar target/kaca-search.jar

echo 'start end'