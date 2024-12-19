package org.devops

def installDependencies() {
    stage('Install Dependencies') {
        script {
            if (fileExists('package.json')) {
                if (isUnix()) {
                    sh 'npm install'
                } else {
                    bat 'npm install'
                }
            } else {
                error "No package.json file found. Unable to install dependencies."
            }
        }
    }
}

def runReactUnitTests() {
    stage('Run React Unit Tests') {
        script {
            if (isUnix()) {
                sh 'npm run test -- --coverage'
            } else {
                bat 'npm run test -- --coverage'
            }
        }
    }
}

def publishCoverageReport() {
    stage('Publish Coverage Report') {
        script {
            publishHTML(target: [
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'coverage/lcov-report',
                reportFiles: 'index.html',
                reportName: 'Unit Test Coverage Report'
            ])
        }
    }
}
