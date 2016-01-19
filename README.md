## Goal
Capture execution steps of your Maven build and report about their status within your Pull Request

## Usage
Add the path to the [JAR](https://github.com/helaili/m2github/releases/download/0.0.3/m2github-0.0.3-release.jar), a GitHub token and the GitHub repo_owoner/repo_name in the mvn command-line of the target project. 

Therefore, your usual command : 
```
mvn test
```
turns into : 
```
mvn -Dmaven.ext.class.path=<PATH_TO_m2github_jar> -Dm2github.repo=OctoCheese/Calculator -Dm2github.token=xxxxxxxxxxxxxxxxxxxxx test
```
If you repository is hosted on a GitHub Enterprise instance, add the extra ```-Dm2github.endpoint``` parameter. 
```
mvn -Dmaven.ext.class.path=<PATH_TO_m2github_jar> -Dm2github.endpoint=https://octodemo.com/api/v3 -Dm2github.repo=OctoCheese/Calculator -Dm2github.token=xxxxxxxxxxxxxxxxxxxxx test
```

Default status name are very long. You can create mappings in order to rename them. Mapping are configured in a JSON file. m2github looks for a file named ```m2github.json``` [(see sample in this repo)](./m2github.json) in the root of your project, but you can also provide a different name/path for the file with the ```-Dm2github.configFile``` parameter.

This file allows also to specifiy status to omit (look for the *ignore* section of the sample configuration file).

## Result

![Alt text](/../screenshots/status.png?raw=true "GitHub Status from Maven")

