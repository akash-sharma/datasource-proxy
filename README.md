**How to monitor DB queries in a Spring web application using this project.**

Steps :

1) Add below filter to your web.xml
(filter class is in provided in this project jar)
	```
	<filter>
	   <filter-name>queryCountFilter</filter-name>
	   <filter-class>com.dsproxy.QueryCountFilter</filter-class>
	</filter>
	<filter-mapping>
		<filter-name>queryCountFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>
	```
	
2) Add below dataSource proxy class with already created listener.
(listener class is in provided in this project jar)

     ```           
	<bean id="proxyDataSource" primary="true" class="net.ttddyy.dsproxy.support.ProxyDataSource">
		<property name="dataSource" ref="yourDataSourceBean"/>
			<property name="listener">
				<bean class="com.dsproxy.DataSourceQueryCountListener">
				</bean>
		</property>
	</bean>
	```

3) Add below dependency to parent pom and utils pom :
	```
	<dependency>
		<groupId>net.ttddyy</groupId>
		<artifactId>datasource-proxy</artifactId>
		<version>${latest.ttddyy.version}</version>
	</dependency>
	```

4) Add jar to your target repository (datasource-proxy-1.0.jar)
(via maven or manually)

5) Add below content to logback.xml file to have a separate logs file for performance monitoring :

	```
	<appender name="PERF_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
		<file>logs/PERF_FILE.log</file>
		<rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
			<FileNamePattern>logs/PERF_FILE.%d{yyyy-MM-dd}.%i.log</FileNamePattern>
			<timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
							<!-- or whenever the file size reaches 10MB -->
							<maxFileSize>10MB</maxFileSize>
			</timeBasedFileNamingAndTriggeringPolicy>
		</rollingPolicy>
		<encoder>
			<pattern>%X{clientRemoteAddress} %X{clientRemotehost} %date %level [%thread] %logger{10} [%file:%line] %msg%n</pattern>
		</encoder>
	</appender>

	<logger name="com.dsproxy.QueryCountFilter" additivity="false" level="INFO">
		<appender-ref ref="PERF_FILE" />
	</logger>
	```

	
**Demo output file :**

```
Request URL : http://localhost:8080/portal/person/id/39666500
 Total SQL query count : 2
 Total server execution time :4 ms 
<------------------------------------------->
Query Type : SELECT
Query : [select pers.name as name, pers.id as id from person as pers where id=?]
arguments : [[{1=39666500}]] 
time taken : 1 ms 
Stack Trace : 
		[[java.lang.Thread.getStackTrace(Unknown Source), com.hcentive.utils.listener.DataSourceQueryCountListener.afterQuery(DataSourceQueryCountListener.java:26), net.ttddyy.dsproxy.listener.ChainListener.afterQuery(ChainListener.java:27), net.ttddyy.dsproxy.proxy.PreparedStatementProxyLogic.performQueryExecutionListener(PreparedStatementProxyLogic.java:292), net.ttddyy.dsproxy.proxy.PreparedStatementProxyLogic.access$500(PreparedStatementProxyLogic.java:34), net.ttddyy.dsproxy.proxy.PreparedStatementProxyLogic$1.execute(PreparedStatementProxyLogic.java:107), net.ttddyy.dsproxy.listener.MethodExecutionListenerUtils.invoke(MethodExecutionListenerUtils.java:37), net.ttddyy.dsproxy.proxy.PreparedStatementProxyLogic.invoke(PreparedStatementProxyLogic.java:104), net.ttddyy.dsproxy.proxy.jdk.PreparedStatementInvocationHandler.invoke(PreparedStatementInvocationHandler.java:35), com.sun.proxy.$Proxy170.executeQuery(Unknown Source), org.hibernate.engine.jdbc.internal.ResultSetReturnImpl.extract(ResultSetReturnImpl.java:70), org.hibernate.loader.plan.exec.internal.AbstractLoadPlanBasedLoader.getResultSet(AbstractLoadPlanBasedLoader.java:434), org.hibernate.loader.plan.exec.internal.AbstractLoadPlanBasedLoader.executeQueryStatement(AbstractLoadPlanBasedLoader.java:186), org.hibernate.loader.plan.exec.internal.AbstractLoadPlanBasedLoader.executeLoad(AbstractLoadPlanBasedLoader.java:121), org.hibernate.loader.plan.exec.internal.AbstractLoadPlanBasedLoader.executeLoad(AbstractLoadPlanBasedLoader.java:86), org.hibernate.loader.entity.plan.AbstractLoadPlanBasedEntityLoader.load(AbstractLoadPlanBasedEntityLoader.java:167), org.hibernate.persister.entity.AbstractEntityPersister.load(AbstractEntityPersister.java:3991), org.hibernate.event.internal.DefaultLoadEventListener.loadFromDatasource(DefaultLoadEventListener.java:508), org.hibernate.event.internal.DefaultLoadEventListener.doLoad(DefaultLoadEventListener.java:478), org.hibernate.event.internal.DefaultLoadEventListener.load(DefaultLoadEventListener.java:219), org.hibernate.event.internal.DefaultLoadEventListener.proxyOrLoad(DefaultLoadEventListener.java:278), org.hibernate.event.internal.DefaultLoadEventListener.doOnLoad(DefaultLoadEventListener.java:121), org.hibernate.event.internal.DefaultLoadEventListener.onLoad(DefaultLoadEventListener.java:89), org.hibernate.internal.SessionImpl.fireLoad(SessionImpl.java:1142), org.hibernate.internal.SessionImpl.access$2600(SessionImpl.java:167), org.hibernate.internal.SessionImpl$IdentifierLoadAccessImpl.doLoad(SessionImpl.java:2762), org.hibernate.internal.SessionImpl$IdentifierLoadAccessImpl.load(SessionImpl.java:2741), org.hibernate.internal.SessionImpl.get(SessionImpl.java:978), org.hibernate.jpa.spi.AbstractEntityManagerImpl.find(AbstractEntityManagerImpl.java:1075), org.hibernate.jpa.spi.AbstractEntityManagerImpl.find(AbstractEntityManagerImpl.java:1033), sun.reflect.GeneratedMethodAccessor236.invoke(Unknown Source), sun.reflect.DelegatingMethodAccessorImpl.invoke(Unknown Source), java.lang.reflect.Method.invoke(Unknown Source), org.springframework.orm.jpa.ExtendedEntityManagerCreator$ExtendedEntityManagerInvocationHandler.invoke(ExtendedEntityManagerCreator.java:344), com.sun.proxy.$Proxy168.find(Unknown Source), sun.reflect.GeneratedMethodAccessor236.invoke(Unknown Source), sun.reflect.DelegatingMethodAccessorImpl.invoke(Unknown Source), java.lang.reflect.Method.invoke(Unknown Source), org.springframework.orm.jpa.SharedEntityManagerCreator$SharedEntityManagerInvocationHandler.invoke(SharedEntityManagerCreator.java:293), com.sun.proxy.$Proxy168.find(Unknown Source), 
		com.myProject.PersonDao.get(PersonDao.java:86),
		........]
<------------------------------------------->
Query Type : SELECT
Query : [select pers.name as name, pers.id as id from person as pers where id=?]
arguments : [[{1=39666500}]] 
time taken : 1 ms 
Stack Trace : 
		[[java.lang.Thread.getStackTrace(Unknown Source), com.hcentive.utils.listener.DataSourceQueryCountListener.afterQuery(DataSourceQueryCountListener.java:26), net.ttddyy.dsproxy.listener.ChainListener.afterQuery(ChainListener.java:27), net.ttddyy.dsproxy.proxy.PreparedStatementProxyLogic.performQueryExecutionListener(PreparedStatementProxyLogic.java:292), net.ttddyy.dsproxy.proxy.PreparedStatementProxyLogic.access$500(PreparedStatementProxyLogic.java:34), net.ttddyy.dsproxy.proxy.PreparedStatementProxyLogic$1.execute(PreparedStatementProxyLogic.java:107), net.ttddyy.dsproxy.listener.MethodExecutionListenerUtils.invoke(MethodExecutionListenerUtils.java:37), net.ttddyy.dsproxy.proxy.PreparedStatementProxyLogic.invoke(PreparedStatementProxyLogic.java:104), net.ttddyy.dsproxy.proxy.jdk.PreparedStatementInvocationHandler.invoke(PreparedStatementInvocationHandler.java:35), com.sun.proxy.$Proxy170.executeQuery(Unknown Source), org.hibernate.engine.jdbc.internal.ResultSetReturnImpl.extract(ResultSetReturnImpl.java:70), org.hibernate.loader.plan.exec.internal.AbstractLoadPlanBasedLoader.getResultSet(AbstractLoadPlanBasedLoader.java:434), org.hibernate.loader.plan.exec.internal.AbstractLoadPlanBasedLoader.executeQueryStatement(AbstractLoadPlanBasedLoader.java:186), org.hibernate.loader.plan.exec.internal.AbstractLoadPlanBasedLoader.executeLoad(AbstractLoadPlanBasedLoader.java:121), org.hibernate.loader.plan.exec.internal.AbstractLoadPlanBasedLoader.executeLoad(AbstractLoadPlanBasedLoader.java:86), org.hibernate.loader.entity.plan.AbstractLoadPlanBasedEntityLoader.load(AbstractLoadPlanBasedEntityLoader.java:167), org.hibernate.persister.entity.AbstractEntityPersister.load(AbstractEntityPersister.java:3991), org.hibernate.event.internal.DefaultLoadEventListener.loadFromDatasource(DefaultLoadEventListener.java:508), org.hibernate.event.internal.DefaultLoadEventListener.doLoad(DefaultLoadEventListener.java:478), org.hibernate.event.internal.DefaultLoadEventListener.load(DefaultLoadEventListener.java:219), org.hibernate.event.internal.DefaultLoadEventListener.proxyOrLoad(DefaultLoadEventListener.java:278), org.hibernate.event.internal.DefaultLoadEventListener.doOnLoad(DefaultLoadEventListener.java:121), org.hibernate.event.internal.DefaultLoadEventListener.onLoad(DefaultLoadEventListener.java:89), org.hibernate.internal.SessionImpl.fireLoad(SessionImpl.java:1142), org.hibernate.internal.SessionImpl.access$2600(SessionImpl.java:167), org.hibernate.internal.SessionImpl$IdentifierLoadAccessImpl.doLoad(SessionImpl.java:2762), org.hibernate.internal.SessionImpl$IdentifierLoadAccessImpl.load(SessionImpl.java:2741), org.hibernate.internal.SessionImpl.get(SessionImpl.java:978), org.hibernate.jpa.spi.AbstractEntityManagerImpl.find(AbstractEntityManagerImpl.java:1075), org.hibernate.jpa.spi.AbstractEntityManagerImpl.find(AbstractEntityManagerImpl.java:1033), sun.reflect.GeneratedMethodAccessor236.invoke(Unknown Source), sun.reflect.DelegatingMethodAccessorImpl.invoke(Unknown Source), java.lang.reflect.Method.invoke(Unknown Source), org.springframework.orm.jpa.ExtendedEntityManagerCreator$ExtendedEntityManagerInvocationHandler.invoke(ExtendedEntityManagerCreator.java:344), com.sun.proxy.$Proxy168.find(Unknown Source), sun.reflect.GeneratedMethodAccessor236.invoke(Unknown Source), sun.reflect.DelegatingMethodAccessorImpl.invoke(Unknown Source), java.lang.reflect.Method.invoke(Unknown Source), org.springframework.orm.jpa.SharedEntityManagerCreator$SharedEntityManagerInvocationHandler.invoke(SharedEntityManagerCreator.java:293), com.sun.proxy.$Proxy168.find(Unknown Source), 
		com.myProject.PersonDao.get(PersonDao.java:86),
		........]
<------------------------------------------->
###############################################
```