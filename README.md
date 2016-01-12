


## Usage
Add the path to the JAR in the mvn command-line of the target project, so your usual command : 
```
mvn test
```
turns into : 
```
mvn -Dmaven.ext.class.path=<PATH>/m2github/target/classes test
```

Add the following dependencies to your pom.xml
```
	<dependencies>
	...
		<dependency>
			<groupId>org.apache.httpcomponents</groupId>
			<artifactId>httpcore</artifactId>
			<version>4.4.4	</version>
		</dependency>
		<dependency>
			<groupId>org.apache.httpcomponents</groupId>
			<artifactId>httpclient</artifactId>
			<version>4.5.1</version>
		</dependency>
	...
	</dependencies>
``