pipeline {
    agent any

    environment {
        app = ''
        phase = "dev"
        registry_name = "142123216080.dkr.ecr.ap-northeast-2.amazonaws.com"
        module_name = "dev/grip-media-management-system"
        image_path = "${registry_name}/${module_name}"
    }

    stages {
        stage('Clone repository') {
            steps {
                checkout scm
            }
        }

        stage('Initialization') {
            steps {
                script {
                    env.app_version = "${env.BRANCH_NAME.replaceAll('/', '_')}" // 특수 문자 치환, 브랜치 설정
                    env.current_timestamp = (System.currentTimeMillis() / 1000L) as Long
                    env.deploy_version = "${env.app_version}-${env.current_timestamp}"
                }
                echo "[app_version]: ${app_version}"
                echo "[deploy_version]: ${deploy_version}"
            }
        }

        stage('Build app') {
            steps {
                container('jdk21') {
                    sh "./gradlew clean build --gradle-user-home=/home/jenkins/.gradle/caches/${module_name} -Pprofile=${phase}"
                }
            }
        }

        stage('Build image') {
            /* This builds the actual image; synonymous to
             * docker build on the command line */
             steps {
                 container('kaniko') {
                     script {
                         sh '''
                         /kaniko/executor --dockerfile `pwd`/Dockerfile --context `pwd` --build-arg phase=${phase} --destination=${image_path}:${deploy_version} --destination=${image_path}:latest
                         '''
                     }
                 }
             }
        }
    }

    post {
        success {
            slackSend(channel: 'C06643PN2ER', color: '#00FF00', message: """
                :white_check_mark: [${phase}] ${module_name} 도커 이미지 빌드 완료 (${image_path}:${deploy_version})
확인: (${env.BUILD_URL})""")
        }
        failure {
            slackSend(channel: 'C06643PN2ER', color: '#FF0000', message: """
                :octagonal_sign: [${phase}] ${module_name} 도커 이미지 빌드 실패 (${image_path}:${deploy_version})
확인: (${env.BUILD_URL})""")
        }
    }
}
