pipeline {
    agent any

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '20', artifactNumToKeepStr: '10'))
        disableConcurrentBuilds()
        timeout(time: 30, unit: 'MINUTES')
    }

    parameters {
        choice(
            name: 'ENVIRONMENT',
            choices: ['production', 'staging'],
            description: 'Chọn môi trường mục tiêu để triển khai'
        )
        booleanParam(
            name: 'RUN_TESTS',
            defaultValue: false,
            description: 'Chạy Unit Test với Maven trước khi đóng gói'
        )
        booleanParam(
            name: 'PUSH_REGISTRY',
            defaultValue: false,
            description: 'Đẩy Docker Image lên Docker Hub / Private Registry'
        )
        string(
            name: 'DOCKER_REGISTRY',
            defaultValue: '',
            description: 'Tên tài khoản hoặc địa chỉ Registry (Ví dụ: yourusername hoặc registry.yourdomain.com)'
        )
        string(
            name: 'APP_PORT',
            defaultValue: '8081',
            description: 'Cổng ứng dụng Backend trên Host (Mặc định 8081 để tránh đụng độ 8080 của IntelliJ)'
        )
    }

    environment {
        IMAGE_NAME = 'jurisprudence-backend'
        BUILD_TAG  = "${env.BUILD_NUMBER}"
        COMPOSE_PROJECT_NAME = 'jurisprudence'
    }

    stages {
        stage('Initialize & Pre-flight') {
            steps {
                script {
                    echo "==============================================================="
                    echo "KHOI TAO PIPELINE TRIEN KHAI: JURISPRUDENCE HUB BACKEND"
                    echo "Job: ${env.JOB_NAME} | Build: #${env.BUILD_TAG}"
                    echo "Moi truong: ${params.ENVIRONMENT}"
                    echo "Node Agent: ${env.NODE_NAME}"
                    echo "==============================================================="
                    
                    // Kiem tra Docker tren Agent
                    sh 'docker --version'
                    sh 'docker compose version'
                }
            }
        }

        stage('Code Analysis & Tests') {
            when {
                expression { return params.RUN_TESTS == true }
            }
            steps {
                echo "===> Dang chay Maven Unit Tests..."
                sh 'chmod +x ./mvnw'
                sh './mvnw test -B'
            }
            post {
                always {
                    junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    echo "===> Dang dong goi Docker Image cho ung dung: ${IMAGE_NAME}:${BUILD_TAG}..."
                    sh """
                        docker build \
                            -t ${IMAGE_NAME}:${BUILD_TAG} \
                            -t ${IMAGE_NAME}:latest \
                            -f Dockerfile .
                    """
                    echo "Build thanh cong cac tags:"
                    echo " - ${IMAGE_NAME}:${BUILD_TAG}"
                    echo " - ${IMAGE_NAME}:latest"
                }
            }
        }

        stage('Push to Registry') {
            when {
                allOf {
                    expression { return params.PUSH_REGISTRY == true }
                    expression { return params.DOCKER_REGISTRY != '' }
                }
            }
            steps {
                script {
                    echo "===> Dang dang nhap va day image len Docker Registry: ${params.DOCKER_REGISTRY}..."
                    // Su dung Jenkins Credentials ID: 'docker-registry-credentials'
                    withCredentials([usernamePassword(credentialsId: 'docker-registry-credentials', usernameVariable: 'REG_USER', passwordVariable: 'REG_PASS')]) {
                        sh """
                            echo "\$REG_PASS" | docker login -u "\$REG_USER" --password-stdin ${params.DOCKER_REGISTRY}
                            docker tag ${IMAGE_NAME}:${BUILD_TAG} ${params.DOCKER_REGISTRY}/${IMAGE_NAME}:${BUILD_TAG}
                            docker tag ${IMAGE_NAME}:${BUILD_TAG} ${params.DOCKER_REGISTRY}/${IMAGE_NAME}:latest
                            docker push ${params.DOCKER_REGISTRY}/${IMAGE_NAME}:${BUILD_TAG}
                            docker push ${params.DOCKER_REGISTRY}/${IMAGE_NAME}:latest
                        """
                    }
                    echo "Day image len Registry thanh cong!"
                }
            }
        }

        stage('Deploy (Docker Compose)') {
            steps {
                script {
                    echo "===> Dang tien hanh trien khai bang Docker Compose..."
                    sh 'chmod +x scripts/*.sh'
                    sh """
                        export HOST_PORT="${params.APP_PORT ?: '8081'}"
                        export PORT="8080"
                        export DOCKER_IMAGE="${IMAGE_NAME}"
                        export BUILD_TAG="${BUILD_TAG}"
                        
                        # Chay script deploy tu dong
                        bash scripts/deploy.sh
                    """
                }
            }
        }

        stage('Verify Healthcheck') {
            steps {
                script {
                    echo "===> Dang xac minh trang thai UP qua Spring Boot Actuator..."
                    sh 'chmod +x scripts/*.sh'
                    sh """
                        export HOST_PORT="${params.APP_PORT ?: '8081'}"
                        bash scripts/healthcheck.sh
                    """
                }
            }
        }
    }

    post {
        always {
            script {
                echo "===> Don dep cac Docker dangling images sau build..."
                sh 'docker image prune -f --filter "until=24h" || true'
            }
        }
        success {
            echo "==============================================================="
            echo "[SUCCESS] Pipeline da hoan tat xuat sac! Backend da san sang tai port ${params.APP_PORT}."
            echo "==============================================================="
        }
        failure {
            echo "==============================================================="
            echo "[FAILURE] Pipeline gap su co trong qua trinh build hoac deploy!"
            echo "Kiem tra logs container de tim nguyen nhan..."
            echo "==============================================================="
            sh 'docker logs --tail 40 jurisprudence-backend || true'
        }
    }
}
