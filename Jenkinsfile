pipeline {
  agent any
  tools {
    maven 'Maven3'
  }

  environment {
    SONARQUBE_ENV = 'MySonarQube'
  }

  stages {
    stage('Clonar código') {
      steps {
        git branch: 'main', url: 'https://github.com/garojual/APISPracticas.git'
      }
    }

    stage('Build y pruebas') {
      steps {
        sh './mvnw clean verify'
        junit '**/target/surefire-reports/*.xml'
      }
    }

    stage('Análisis de calidad') {
      steps {
        withSonarQubeEnv("${SONARQUBE_ENV}") {
          sh './mvnw sonar:sonar'
        }
      }
    }
  }
}
