# jmeterpresstesthejin
如何使用jmeter进行压测
备注：先写个简单的版本，后续再添枝加叶，进行完善
1 clone demo https://gitlab.gotokeep.com/lixiangkun/jmeterdemo
2 代码用idea或者eclipse打开
3 在pom.xml中，引入被压测服务的相关jar包(找开发要)
如下图：
![image](https://github.com/hejin521/jmeterpresstesthejin/assets/44940918/9dadb83c-890d-4a5a-9d22-5e7cfe9f1f71)


4 在applicationContext-dubbo.xml中，添加被压测的bean配置(如果是压测dubbo接口，需要这步)
如下图：
![image](https://github.com/hejin521/jmeterpresstesthejin/assets/44940918/2964be3f-beef-433e-a585-d936bbe28343)


5 拷贝一个com.gotokeep.jmeter包下面的，以PressTest结尾的类，如果是以RPC开头的，说明这个类是压测dubbo接口的，拷贝后，注意改类名
6 在新拷贝的类中，重新修改下setupTest和runTest两个方法的实现，其中
setupTest类似于junit中的beforetest，在测试前调用，用于初始化或者环境等准备；
一般情况下，如果压测dubbo接口，那么启动spring容器的工作放在这个方法中，如下：
![image](https://github.com/hejin521/jmeterpresstesthejin/assets/44940918/a8a0144d-c73a-49da-9e67-1bf1a9d9298b)

1处是从spring容器中，拿到一个bean，需要和applicationContext-dubbo.xml中的id配置一致
2处是这次要压测的dubbo方法的类，这里做的是类型转换
3 和 4的意思是，4处创建了一个对象，在3处初始化了一下
如果压测http接口，则不需要启动spring容器，可以参考前缀不是RPC的压测代码
7 压测的请求参数封装及接口请求都放在runTest中
![image](https://github.com/hejin521/jmeterpresstesthejin/assets/44940918/a663a23c-9442-4780-96f2-239660be38f2)

8 代码写完后，修改一                                                                                                     下入口类，在pom.xml中，如下
![image](https://github.com/hejin521/jmeterpresstesthejin/assets/44940918/56babc61-90b7-455f-9dc5-4d5d5a005052)

上面箭头处，是给jar包起个名字，看个人喜好
下面箭头处，是指定运行主类，要包含完整的包名+类名
9 运行mvn clean package打包 
![image](https://github.com/hejin521/jmeterpresstesthejin/assets/44940918/52819a9c-fdde-43e6-8456-a3924fc920dd)
![image](https://github.com/hejin521/jmeterpresstesthejin/assets/44940918/e5d4f9f4-6eb9-4e52-9e6f-a93620097373)
![image](https://github.com/hejin521/jmeterpresstesthejin/assets/44940918/c5b8b629-3768-4a17-9b08-2c85697ba553)


10 将打出来的包上传的kfs中，kfs的安装，可以找sre咨询(安装教程)，类似于~/Desktop/tool/kfs/kfs put ./getSuitTabV2Preview-jar-with-dependencies.jar
如果 kfs 不能执行，可以切到 kfs 所在文件夹下，执行 chmod a+x ${path}/kfs 后，使用命令 ./kfs put xxx
bjtx-pts-01  11 访问机器bjtx-pre-qa-01，进入到/data/apps/apache-jmeter-5.0/lib/ext目录，将第10步上传的jar包下载下来，命令类似于kfs get getSuitTabV2Preview-jar-with-dependencies.jar
12 进入到c/，拷贝一个jmx文件，修改其中的主类，及压测时间，并发数等，如下图cd
![image](https://github.com/hejin521/jmeterpresstesthejin/assets/44940918/58264c4e-8e49-4836-a62c-099372327a2a)



箭头1，并发数
箭头2，压测持续时间，单位秒
箭头3，主类，与步骤8中指定的一致
13 保存后，就可以进行压测了，压测命令 /data/apps/apache-jmeter-5.0/bin/jmeter  -n -t /data/apps/apache-jmeter-5.0/jmx/qa-jmeter/xxx.jmx -j ~/jmeter.log，其中最后的xxx.jmx就是你在步骤12中拷贝的jmx文件

