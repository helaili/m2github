


## Usage
Add the path to the JAR, a GitHub token and the GitHub repowoner/reponame in the mvn command-line of the target project, so your usual command : 
```
mvn test
```
turns into : 
```
mvn -Dmaven.ext.class.path=<PATH_TO_m2github_jar> -Dm2github.repo=OctoCheese/Calculator -Dm2github.token=xxxxxxxxxxxxxxxxxxxxx test
```


