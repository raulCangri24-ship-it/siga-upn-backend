pipeline {
    agent any
    stages {
        stage('Clonar repositorio') {
            steps {
                git branch: 'main', url: 'https://github.com/raulCangri24-ship-it/siga-upn-backend.git'
            }
        }
        stage('Compilar') {
            steps {
                sh './mvnw compile -Dspring.profiles.active=jenkins'
            }
        }
        stage('Ejecutar pruebas') {
            steps {
                sh './mvnw test -Dspring.profiles.active=jenkins'
            }
        }
        stage('Empaquetar') {
            steps {
                sh './mvnw package -DskipTests -Dspring.profiles.active=jenkins'
            }
        }
    }
}