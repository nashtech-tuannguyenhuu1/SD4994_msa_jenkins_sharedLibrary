import org.devops.*

def call(serviceName) {

    def AWS_REGION = 'ap-southeast-1'
    def ECR_REGISTRY = '381492305009.dkr.ecr.ap-southeast-1.amazonaws.com'
    def ECR_REPOSITORY = 'msa-backend'
    def IMAGE_TAG = 'v1.0.0'

    // def install = new BackendInstall()
    // install.runInstall()

    stage('Checkout Code') {
        steps {
            checkout scm
        }
    }

    stage("Run Install") {
        script {
            if (fileExists('package.json')) {
                bat 'npm install'
            } else {
                error "No package.json file found. Unable to run tests."
            }
        }
    }

    stage('Login to AWS ECR') {
        steps {
            script {
                bat '''
                aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $ECR_REGISTRY
                '''
            }
        }
    }

    stage('Build Docker Image') {
        steps {
            script {
                bat '''
                docker build -t $ECR_REPOSITORY:$IMAGE_TAG .
                '''
            }
        }
    }

    stage('Tag Docker Image') {
        steps {
            script {
                bat '''
                docker tag $ECR_REPOSITORY:$IMAGE_TAG $ECR_REGISTRY/$ECR_REPOSITORY:$IMAGE_TAG
                '''
            }
        }
    }

    stage('Push to ECR') {
        steps {
            script {
                bat '''
                docker push $ECR_REGISTRY/$ECR_REPOSITORY:$IMAGE_TAG
                '''
            }
        }
    }
    
}