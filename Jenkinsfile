#!/usr/bin/env groovy
def APP_NAME
def APP_VERSION
def DOCKER_IMAGE_NAME
def DOCKER_IMAGE_VERSION
def PROD_BUILD = false
pipeline {
    agent {
        node {
            label 'master'
        }
    }

    parameters {
        gitParameter branch: '',
                    branchFilter: '.*',
                    defaultValue: 'origin/main',
                    description: '', listSize: '0',
                    name: 'TAG',
                    quickFilterEnabled: false,
                    selectedValue: 'DEFAULT',
                    sortMode: 'DESCENDING_SMART',
                    tagFilter: '*',
                    type: 'PT_BRANCH_TAG'

        booleanParam defaultValue: false, description: '', name: 'RELEASE'
    }

    environment {
        GIT_URL = "https://github.com/PersonalizedNews-MSA/UserService"
        GITHUB_CREDENTIAL = "github-token"
        ARTIFACTS = "build/libs/**"
        DOCKER_REGISTRY = "suin4328"
        DOCKERHUB_CREDENTIAL = 'dockerhub-token'

        KAFKA_BROKER = "${params.KAFKA_BROKER}" // Jenkins UI Parameter 등록
    }

    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: "30", artifactNumToKeepStr: "30"))
        timeout(time: 120, unit: 'MINUTES')
    }

    tools {
        gradle 'Gradle 8.14.2'
        jdk 'OpenJDK 17'
        dockerTool 'Docker'
    }

    stages {
        stage('Set Version') {
            steps {
                script {
                    APP_NAME = sh (
                            script: "gradle -q getAppName",
                            returnStdout: true
                    ).trim()
                    APP_VERSION = sh (
                            script: "gradle -q getAppVersion",
                            returnStdout: true
                    ).trim()

                    sh "echo IMAGE_NAME is ${APP_NAME}"
                    sh "echo IMAGE_VERSION is ${APP_VERSION}"
                    sh "echo TAG is ${params.TAG}"

                    // 기존 APP_VERSION을 수정해서 suffix 붙이기
                    if (!params.TAG.startsWith('origin') && !params.TAG.endsWith('/main')) {
                        if (params.RELEASE == true) {
                            APP_VERSION += '-RELEASE'
                            PROD_BUILD = true
                        } else {
                            APP_VERSION += '-TAG'
                        }
                    }
                    // 이 시점에서 APP_VERSION은 예: "0.0.1-RELEASE" 또는 "0.0.1-TAG"
                    DOCKER_IMAGE_NAME = "${DOCKER_REGISTRY}/${APP_NAME}:${APP_VERSION}"

                    sh "echo IMAGE_VERSION_AFTER is ${APP_VERSION}"
                    sh "echo DOCKER_IMAGE_NAME is ${DOCKER_IMAGE_NAME}"
                }
            }
        }

        stage('Build & Test Application') {
            steps {
                script {
                    sh 'chmod +x ./gradlew'
                    def gradleCmd = './gradlew clean build'
                    if (!params.TAG.startsWith('origin') && !params.TAG.endsWith('/main')) {
                        gradleCmd = './gradlew --no-daemon clean build'
                    }
                    sh "export GRADLE_OPTS='-Xmx2g -Xms512m -Dfile.encoding=UTF-8' && ${gradleCmd}"
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    docker.build "${DOCKER_IMAGE_NAME}"
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    docker.withRegistry("", DOCKERHUB_CREDENTIAL) {
                        docker.image("${DOCKER_IMAGE_NAME}").push()
                    }

                    sh "docker rmi ${DOCKER_IMAGE_NAME}"
                }
            }
        }
    }
}