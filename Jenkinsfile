pipeline {

    agent any

    environment {
        container = ''
    }

    stages {
        stage('setup gradle') {
            steps {
                sh "chmod +x gradlew"
                sh "chmod +x start.sh"
                sh "./gradlew clean"
            }
        }
        stage('test') {
            steps {
                sh "./gradlew test"
            }
        }
        stage('build app') {
            steps {
                sh "./gradlew installDist"
            }
        }
        stage('remove unused image') {
            steps {
                sh "docker rmi zeronetdev/sd-exam-2 || true"
            }
        }
        stage("build image") {
            steps {
                script {
                    container = docker.build("zeronetdev/sd-exam-2")
                }
            }
        }
        stage("push image") {
            steps {
                script {
                    docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-credentials') {
                        container.push("latest")
                    }
                }
            }
        }
        stage('deploy'){
            steps {
                sh "docker-compose down"
                sh "docker-compose up -d --build || (docker-compose down && false)"
            }
        }
    }
}