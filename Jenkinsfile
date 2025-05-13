pipeline {
  agent any
  tools {
    jdk 'jdk-21'
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

    stage('Levantar Quarkus') {
      steps {
        echo 'Iniciando Quarkus en segundo plano...'
        sh 'nohup mvn quarkus:dev > quarkus.log 2>&1 & echo $! > quarkus.pid'
        sh 'sleep 10' // Ajusta el tiempo si el servidor tarda más en levantar
      }
    }

    stage('Ejecutar pruebas') {
      steps {
        echo 'Ejecutando pruebas de integración...'
        sh 'mvn test'
        junit '**/target/cucumber-reports/cucumber.xml'
      }
    }

    stage('Análisis de calidad - SonarQube') {
                steps {
                    script {
                        // Usa el ID de la credencial que creaste en Jenkins
                        withCredentials([string(credentialsId: '2cfd8264-b49f-4307-9c29-7e3bcfbcf2d9', variable: 'SONAR_TOKEN')]) {
                            // La variable SONAR_TOKEN ahora contiene el valor del token
                            sh """
                                mvn sonar:sonar \\
                                  -Dsonar.projectKey=your_project_key \\
                                  -Dsonar.host.url=http://sonarqube:9000 \\
                                  -Dsonar.login=\$SONAR_TOKEN
                            """
                            // NOTA: Usamos \$SONAR_TOKEN para escapar el '$' dentro de la cadena Groovy
                            // Si estás usando sh con comillas simples '', solo necesitarías $SONAR_TOKEN
                        }
                    }
                }
            }

    stage('Detener Quarkus') {
      steps {
        echo 'Deteniendo Quarkus...'
        sh '''
          if [ -f quarkus.pid ]; then
            kill -9 $(cat quarkus.pid) || true
            rm quarkus.pid
          fi
        '''
      }
    }
  }

  post {
    always {
      echo 'Limpieza final'
      sh 'pkill -f quarkus:dev || true'
    }
  }
}

