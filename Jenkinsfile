pipeline {
    agent any

    tools {
        maven 'Maven 3.9'
        jdk 'Java 17'
    }

    environment {
        // App settings
        APP_NAME = 'petclinic'
        WAR_FILE = 'target/petclinic.war'
        DEPLOY_DIR = '/opt/wildfly/standalone/deployments'
        
        // SonarQube settings (if available)
        // SONAR_TOKEN = credentials('sonar-token')
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out code...'
                checkout scm
            }
        }

        stage('Build & Unit Test') {
            steps {
                echo 'Building application...'
                // Run compilation and unit tests
                sh 'mvn clean package -DskipTests=false'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Code Quality Check') {
            steps {
                echo 'Checking code formatting with Spotless...'
                sh 'mvn spotless:check'
            }
        }

        stage('Integration Tests') {
            steps {
                echo 'Running integration tests...'
                // Verify phase runs integration tests (failsafe plugin)
                sh 'mvn verify -DskipTests=false'
            }
        }

        stage('Security Scan') {
            steps {
                echo 'Running OWASP Dependency Check...'
                // Assuming dependency-check maven plugin is configured or CLI is available
                // sh 'mvn org.owasp:dependency-check-maven:check'
                echo 'Skipping for now (plugin not explicitly in pom)'
            }
        }

        stage('Archive Artifacts') {
            steps {
                archiveArtifacts artifacts: 'target/*.war', fingerprint: true
            }
        }

        stage('Deploy to Wildfly') {
            steps {
                echo 'Deploying to Wildfly...'
                // Check if deployment script exists and is executable
                sh 'chmod +x deployment/jenkins-deploy.sh'
                sh './deployment/jenkins-deploy.sh'
            }
        }
    }

    post {
        success {
            echo 'Pipeline succeeded!'
            // mail to: 'team@example.com', subject: "Success: ${currentBuild.fullDisplayName}", body: "Build succeeded."
        }
        failure {
            echo 'Pipeline failed!'
            // mail to: 'team@example.com', subject: "Failed: ${currentBuild.fullDisplayName}", body: "Build failed."
        }
    }
}
