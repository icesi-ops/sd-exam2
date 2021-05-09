pipeline {

    stages {
        def container
        stage('setup gradle') {
            step {
                sh "chmod +x gradlew"
                sh "./gradlew clean"
            }
        }
        stage('test') {
            step {
                sh "./gradlew test"
            }
        }
        stage('build app') {
            step {
                sh "./gradlew installDist"
            }
        }
        stage("build image") {
            step {
                container = docker.build("zeronetdev/sd-exam-2")
            }
        }
        stage("push image") {
            step {
                docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-credentials') {
                    container.push("latest")
                }
            }
        }
        stage('deploy'){
            step {
                sh "docker-compose down"
                sh "docker-compose up -d --build || (docker-compose down && false)"
            }
        }
    }
}