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