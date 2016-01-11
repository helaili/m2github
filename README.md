


## Usage
Add the path to the JAR in the mvn command-line of the target project, so your usual command : 
```
mvn test
```
turns into 
```
mvn -Dmaven.ext.class.path=<PATH>/m2github/target/classes test
```
