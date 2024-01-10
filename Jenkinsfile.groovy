pipeline {
    agent any

    environment {
        // Credentials ID
        credentialsId = 'KimMunjin'
    }

    stages {
        stage('Prepare') {
            agent any
            steps {
                script {
                    git branch: 'dev',
                    credentialsId: credentialsId,
                    url: 'https://github.com/AnonymousZB14/USports_BE.git'
                }
            }
            post {
                failure{
                    error "Fail Cloned Repository"
                }
            }
        }

        stage('Build') {\
            agent any
            steps {
                sh 'chmod +x gradlew'
                sh './gradlew clean build -x test'
            }
            post{
                failure{
                    error 'Fail Build'
                }
            }
        }

        stage('Deploy') {
            agent any
            steps {
                script {
                    sshPublisher(
                        continueOnError: false,
                        failOnError: true,
                        publishers: [
                            sshPublisherDesc(
                                configName: 'usports',
                                transfers: [
                                    sshTransfer(
                                        sourceFiles: 'build/libs/*.jar',
                                        removePrefix: 'build/libs',
                                        remoteDirectory: '/app/usports'
                                    )
                                ],
                                execCommand: 'sh /home/ubuntu/app/usports/start_server.sh'
                            )
                        ]
                    )
                }
            }
            post{
                failure{
                    error 'Fail Deploy'
                }
            }
        }
    }
}
