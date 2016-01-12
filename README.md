## Goal
Capture execution steps of your Maven build and report about their status within your Pull Request

## Usage
Add the path to the [JAR](https://github.com/helaili/m2github/releases/download/0.0.1/m2github-0.0.1-SNAPSHOT-release.jar), a GitHub token and the GitHub repo_owoner/repo_name in the mvn command-line of the target project. 

Therefore, your usual command : 
```
mvn test
```
turns into : 
```
mvn -Dmaven.ext.class.path=<PATH_TO_m2github_jar> -Dm2github.repo=OctoCheese/Calculator -Dm2github.token=xxxxxxxxxxxxxxxxxxxxx test
```
If you repository is hosted on a GitHub Enterprise instance, add the extra -Dm2github.endpoint parameter. 
```
mvn -Dmaven.ext.class.path=<PATH_TO_m2github_jar> -Dm2github.endpoint=https://octodemo.com/api/v3 -Dm2github.repo=OctoCheese/Calculator -Dm2github.token=xxxxxxxxxxxxxxxxxxxxx test
```


## Result

![Alt text](/../screenshots/status.png?raw=true "GitHub Status from Maven")
