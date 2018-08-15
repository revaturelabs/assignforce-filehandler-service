pipeline {
    agent any
    environment {
        APP_NAME="curriculum-service"
        IMG_NAME="af-curriculum"
    }

    stages {
        stage('Quality Check') {
            parallel {
                stage('Unit Tests') {
                  steps {
                    script {
                        try {
                            result = sh(script: "git log -1 | grep -c '\\[debug\\]'", returnStatus: true)
                            if(result == 0 ) {
                                sh 'echo running debug build'
                                env.DEBUG_BLD=1
                            } else {
                                sh 'echo not running debug build'
                            }

                            sh 'echo "run mvn test"'
                            sh "mvn test"
                        } catch(Exception e) {
                            env.FAIL_STG="unit tests"
                            currentBuild.result='FAILURE'
                            throw e
                        }
                    }
                  }
                }
                stage('Code Scan') {
                  steps {
                    script {
                        try {
                            sh "git log -1 > commit"
                            commitMsg = readFile("./commit").trim()
                            slackSend "Started ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>) - Commit: ${commitMsg}"
                            sh 'echo "run quality gate"'
                        } catch(Exception e) {
                            env.FAIL_STG='Code Scan'
                            currentBuild.result='FAILURE'
                            throw e
                        }
                    }
                  }
                }
            }
        }

        stage('Artifact Build') {
            when {
                anyOf {
                    branch 'master'
                    branch 'development'
                    environment name: 'DEBUG_BLD', value: '1'
                }
            }
            steps {
                script {
                    try {
                        sh "echo run mvn package -DskipTests"
                        sh "mvn install -DskipTests"
                    } catch(Exception e) {
                        env.FAIL_STG='Maven Build'
                        currentBuild.result='FAILURE'
                        throw e
                    }
                }
            }
        }

        stage('Container Build') {
            when {
                anyOf{
                    branch 'master'
                    branch 'development'
                    environment name: 'DEBUG_BLD', value: '1'
                }
            }
            steps {
                script {
                    try {
                        env.DK_U=readFile("/opt/dk_auth").trim().split(':')[0]
                        env.DK_TAG_GOAL='tag-latest'
                        env.DK_TAG='latest'

                        if(env.BRANCH_NAME == 'development' || env.DEBUG_BLD == '1') {
                            env.DK_TAG_GOAL='tag-dev'
                            env.DK_TAG='dev-latest'
                        }
                        sh "echo run docker build"
                        //this may have to replace dockerfile:tag
                        sh "mvn dockerfile:build@${env.DK_TAG_GOAL}"
                    } catch(Exception e) {
                        env.FAIL_STG='Docker Build'
                        currentBuild.result='FAILURE'
                        throw e
                    }
                }
            }
        }

        stage('Docker Push') {
            when {
                anyOf {
                    branch 'master'
                    branch 'development'
                    environment name: 'DEBUG_BLD', value: '1'
                }
            }
            steps {
                script {
                    try {
                        sh "echo push; mvn dockerfile:push"
                        sh "echo remove local image; docker image rm ${env.DK_U}/${env.IMG_NAME}:${env.DK_TAG}"
                    } catch(Exception e) {
                        env.FAIL_STG='Docker Archive'
                        currentBuild.result='FAILURE'
                        throw e
                    }
                }
            }
        }

        stage('CF Push') {
            when {
                anyOf {
                    branch 'master'
                    branch 'development'
                    environment name: 'DEBUG_BLD', value: '1'
                }
            }
            steps {
                script {
                    try {
                        if(env.BRANCH_NAME == 'master') {
                            env.SPACE = "master"
                            env.IMG="${env.DK_U}/${env.IMG_NAME}:latest"
                            env.PROFILE="master"
                        } else if(env.BRANCH_NAME == 'development' || env.DEBUG_BLD == '1') {
                            env.SPACE = "development"
                            env.IMG="${env.DK_U}/${env.IMG_NAME}:dev-latest"
                            env.PROFILE="development"
                        }
                        env.CF_DOCKER_PASSWORD=readFile("/run/secrets/CF_DOCKER_PASSWORD").trim()
                        sh "cf target -s ${env.SPACE}"
                        sh "cf push -o ${env.IMG} --docker-username ${env.DK_U} --no-start"
                        sh "cf set-env ${env.APP_NAME} SPRING_PROFILES_ACTIVE ${env.PROFILE}"
                        sh "cf start ${env.APP_NAME}"
                    } catch(Exception e) {
                        env.FAIL_STG="PCF Deploy"
                        currentBuild.result='FAILURE'
                        throw e
                    }
                }
            }
        }

        stage('Clean') {
            steps {
                cleanWs(cleanWhenAborted: true, cleanWhenFailure: true, cleanWhenNotBuilt: true, cleanWhenSuccess: true, cleanWhenUnstable: true, deleteDirs: true)
            }
        }
    }
    post {
        success {
            script {
                slackSend color: "good", message: "Build Succeeded: ${env.JOB_NAME} ${env.BUILD_NUMBER}"
            }
        }
        failure {
            script {
                slackSend color: "danger", message: "Build Failed: ${env.JOB_NAME} ${env.BUILD_NUMBER} - Stage ${env.FAIL_STG}"
            }
        }
    }
}