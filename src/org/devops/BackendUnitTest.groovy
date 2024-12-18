package org.devops

def runUnitTests() {

    stage("Run Unit Tests") {
        script {
            if (fileExists('package.json')) {

                sh 'npm install'
                sh 'npm test'

            } else {
                error "No package.json file found. Unable to run tests."
            }
        }
    }
}
