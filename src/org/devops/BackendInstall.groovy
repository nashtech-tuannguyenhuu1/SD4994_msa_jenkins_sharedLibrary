package org.devops

def runInstall() {

    stage("Run Install") {
        script {
            if (fileExists('package.json')) {

                if (isUnix()) {
                    sh 'npm install'
                } else {
                    bat 'npm install'
                }

            } else {
                error "No package.json file found. Unable to run tests."
            }
        }
    }
}
