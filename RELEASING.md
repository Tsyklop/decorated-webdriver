Memo to myself: How to release this project
===========================================

* Change decoratedWebdriver.version property in pom.xml to the release version number
* Start Git Bash
* eval "$(ssh-agent -s)"
* ssh-add ~/.ssh/github_rsa
* mvn release:prepare
* mvn release:perform
