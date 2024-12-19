import org.devops.*

def call(serviceName) {

    def AWS_REGION = 'ap-southeast-1'
    def ECR_REGISTRY = '381492305009.dkr.ecr.ap-southeast-1.amazonaws.com'
    def ECR_REPOSITORY = 'msa-backend'
    def IMAGE_TAG = "v1.${BUILD_NUMBER}-${new Date().format('yyyyMMddHHmmss')}"

    def DEPLOYMENT_FILE_PATH = 'prod/backend/deployment.yaml'

    stage("Run Install") {
        script {
            if (fileExists('package.json')) {
                sh 'npm install'
            } else {
                error "No package.json file found. Unable to run tests."
            }
        }
    }

    withAWS(credentials: 'awscreds', region: AWS_REGION) {
        
        stage('Login to AWS ECR') {
            script {
                sh """
                aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY}
                """
            }
        }

        stage('Build Docker Image') {
            script {
                sh """
                docker build -t ${ECR_REPOSITORY}:${IMAGE_TAG} .
                """
            }
        }

        stage('Tag Docker Image') {
            script {
                sh """
                docker tag ${ECR_REPOSITORY}:${IMAGE_TAG} ${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}
                """
            }
        }

        stage('Push to ECR') {
            script {
                sh """
                docker push ${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}
                """
            }
        }

    }

    stage('Update Deployment YAML') {
        script {
            // Clone GitOps repository
            withCredentials([usernamePassword(credentialsId: 'nashtech-tuannguyenhuu1', usernameVariable: 'GITHUB_USERNAME', passwordVariable: 'GITHUB_TOKEN')]) {
                sh """
                git clone https://${GITHUB_USERNAME}:${GITHUB_TOKEN}@github.com/nashtech-tuannguyenhuu1/SD4994_msa_gitops.git gitops
                """
            }
            // Update image tag in deployment.yaml
            def deploymentYamlPath = "gitops/${DEPLOYMENT_FILE_PATH}"
            def content = readFile(deploymentYamlPath)
            def updatedContent = content.replaceAll(
                /image: \S+/,
                "image: ${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}"
            )
            writeFile file: deploymentYamlPath, text: updatedContent

            // Commit and push changes to GitOps repo
            sh """
            cd gitops
            git config user.email "jenkins@yourdomain.com"
            git config user.name "Jenkins CI"
            git add ${DEPLOYMENT_FILE_PATH}
            git commit -m "Update image to ${IMAGE_TAG}"
            git push origin main
            """
        }
    }
}
