## Goal
Capture execution steps of your Maven build and report about their status within your Pull Request

## Usage
Add the path to the [JAR](https://github.com/helaili/m2github/releases/download/0.0.1/m2github-0.0.1-SNAPSHOT-release.jar), a GitHub token and the GitHub repowoner/reponame in the mvn command-line of the target project, so your usual command : 
```
mvn test
```
turns into : 
```
mvn -Dmaven.ext.class.path=<PATH_TO_m2github_jar> -Dm2github.repo=OctoCheese/Calculator -Dm2github.token=xxxxxxxxxxxxxxxxxxxxx test
```

![Alt text](/../screenshots/status.png?raw=true "GitHub Status from Maven")
