How to monitor DB queries in a Spring web application using this project.
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
	
2)            
Add below dataSource proxy class with already created listener.
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
