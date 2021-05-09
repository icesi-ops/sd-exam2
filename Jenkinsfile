node {

    def container
    stage('setup gradle') {
        sh "chmod +x gradlew"
        sh "./gradlew clean"
    }
    stage('test') {
        sh "./gradlew test"
    }
    stage('build app') {
        sh "./gradlew installDist"
    }
    stage("build image") {
        container = docker.build("zeronetdev/sd-exam-2")
    }
    stage("push image") {
        docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-credentials') {
            container.push("latest")
        }
    }
    stage('deploy'){
        sh "docker-compose down"
        sh "docker-compose up -d --build"
    }
}