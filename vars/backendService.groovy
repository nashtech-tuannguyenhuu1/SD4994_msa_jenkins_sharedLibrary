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
                    branch 'main'
                }
                steps {
                    echo "Branch is ${env.BRANCH_NAME}"  // Kiểm tra nhánh hiện tại
                    echo "Triggered by: ${currentBuild.triggeredBy}"  // Kiểm tra trigger
                    script {
                        // Kiểm tra điều kiện thực thi và tiếp tục xử lý
                        if (env.BRANCH_NAME == 'main') {
                            echo 'Running on main branch'
                        } else {
                            echo 'Not on main branch'
                        }
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
