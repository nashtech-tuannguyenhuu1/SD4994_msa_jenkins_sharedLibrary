#!/usr/bin/env groovy
void call(Map pipelineParams) {

    pipeline {

        agent any

        triggers {
            githubPush()
        }

        options {
            disableConcurrentBuilds()
            disableResume()
            timeout(time: 1, unit: 'HOURS')
        }

        stages {
            stage ('Load Pipeline') {
                when {
                    echo "Branch is ${env.BRANCH_NAME}"
                    echo "Triggered by: ${currentBuild.triggeredBy}"
                    anyOf {
                        branch 'main'
                        triggeredBy 'UserIdCause'
                    }
                }
                steps {
                    script {
                        dir('src/backend') {
                            backendPipelineTemplate("backend")
                        }
                    }
                }
            }
        }

        post {
            cleanup {
                cleanWs()
            }
        }
    }
}