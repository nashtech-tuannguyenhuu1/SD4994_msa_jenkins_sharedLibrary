package org.devops

def runUnitTests() {

    stage("Run Unit Tests") {
        script {
            if (fileExists('package.json')) {

                if (isUnix()) {
                    // Dành cho Linux/Mac
                    sh 'npm install'
                  
                } else {
                    // Dành cho Windows
                    bat 'npm install'
          
                }

            } else {
                error "No package.json file found. Unable to run tests."
            }
        }
    }
}
