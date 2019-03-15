pipeline {
    agent any
    environment {
        IMG_NAME="assignforce-filehandler"
        REPO="ajduet"
    }

    stages {
        stage('Build Context'){
            steps {
                script {
                    debug = sh(script: "git log -1 | grep -c '\\[debug\\]'", returnStatus: true)
                    if(debug == 0) {
                        env.DEBUG_BLD = 1;
                    }

                    sh '/opt/login.sh'
                }
            }
        }

        stage('Quality Check') {
            parallel {
                 stage('Unit Tests') {
                  steps {
                    script {
                        try {
                            sh 'echo "run mvn test"'
                           // sh "mvn test"
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
                        sh "docker build -t ${IMG_NAME} ."
						sh "docker tag ${env.IMG_NAME} ${env.REPO}/${env.IMG_NAME}:${env.DK_TAG}"                    
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
                        env.DK_U=readFile("/opt/dk_auth").split(':')[0]
                        env.DK_P=readFile("/opt/dk_auth").split(':')[1]

                        sh "docker login -u ${env.DK_U} -p ${env.DK_P}"

                        sh "echo push"
                        sh "docker push ${REPO}/${IMG_NAME}:${env.DK_TAG}"
                        sh "echo remove local image; docker image rm ${env.REPO}/${env.IMG_NAME}:${env.DK_TAG}"
                    } catch(Exception e) {
                        env.FAIL_STG='Docker Archive'
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
        always {
            script {
                sh 'cf logout'
            }
        }
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