#!/usr/bin/env groovy

// 빌드/버전 정보 변수
def APP_NAME = "user-service"
def APP_VERSION
def DOCKER_IMAGE_NAME

pipeline {
    agent {
        node {
            label 'master'
        }
    }

    parameters {
        // 브랜치/태그 선택을 위한 Git Parameter
        gitParameter(
            name: 'TAG',
            type: 'PT_BRANCH_TAG',
            defaultValue: 'develop',
            description: '빌드할 Git 브랜치 또는 태그',
            branchFilter: '.*',
            tagFilter: '*',
            sortMode: 'DESCENDING_SMART'
        )

        // DockerHub에 푸시 여부
        booleanParam(
            name: 'RELEASE',
            defaultValue: false,
            description: 'DockerHub에 이미지 푸시 여부'
        )
    }

    environment {
        GIT_URL = "https://github.com/PersonalizedNews-MSA/UserService"
        GITHUB_CREDENTIAL = "github-token"          // GitHub 인증 정보 ID
        DOCKER_REGISTRY = "suin4328"                // DockerHub ID
        DOCKERHUB_CREDENTIAL = "dockerhub-token"    // DockerHub 인증 정보 ID
    }

    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: "30", artifactNumToKeepStr: "30"))
        timeout(time: 60, unit: 'MINUTES')
    }

    tools {
        gradle 'Gradle 8.14.2'
        jdk 'OpenJDK 17'
        dockerTool 'Docker'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: "${params.TAG}"]],
                    userRemoteConfigs: [[
                        url: "${GIT_URL}",
                        credentialsId: "${GITHUB_CREDENTIAL}"
                    ]]
                ])
            }
        }

        stage('Set Version') {
            steps {
                script {
                    APP_VERSION = sh(script: "./gradlew -q getAppVersion", returnStdout: true).trim()
                    APP_VERSION += params.RELEASE ? "-RELEASE" : "-TAG"
                    DOCKER_IMAGE_NAME = "${DOCKER_REGISTRY}/${APP_NAME}:${APP_VERSION}"

                    echo "▶ APP_VERSION: ${APP_VERSION}"
                    echo "▶ DOCKER_IMAGE_NAME: ${DOCKER_IMAGE_NAME}"
                }
            }
        }

        stage('Build & Test') {
            steps {
                sh './gradlew clean build --no-daemon'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${DOCKER_IMAGE_NAME}")
                }
            }
        }

        stage('Push Docker Image') {
            when {
                expression { return params.RELEASE }
            }
            steps {
                script {
                    docker.withRegistry('', DOCKERHUB_CREDENTIAL) {
                        docker.image("${DOCKER_IMAGE_NAME}").push()
                    }
                    sh "docker rmi ${DOCKER_IMAGE_NAME}"
                }
            }
        }
    }
}