pipeline {

  environment {
    dockerRegistry = "https://registry.hub.docker.com"
    dockerimagename = "tamleduc/auth-extension"
    version = "0.0.2"
    dockerImage = ""
    DOCKER_IMAGE_INFO = ""
  }

  agent any
  tools {
    maven 'Maven_3_8_7'
    dockerTool 'docker_20_10_22'
  }

  stages {
    stage('Cancel Previous Builds') {
      steps {
        script {
          cancelPreviousBuilds()
        }
      }
    }

    stage('Build source') {
      steps {
        script {
          sh (script: 'mvn clean install -Dmaven.test.skip=true')
        }
      }
    }

    stage('Build image') {
      steps{
        script {
          def tag = sh (script: 'echo $(date +%Y%m%d%H%M%S)', returnStdout: true).trim()
          DOCKER_IMAGE_INFO = dockerimagename + ':' + version + '_' +  tag
          dockerImage = docker.build("$DOCKER_IMAGE_INFO")
        }
      }
    }

    stage('Pushing Image') {
      environment {
               registryCredential = 'dockerhub-credentials'
      }
      steps{
        script {
          docker.withRegistry( dockerRegistry, registryCredential ) {
            dockerImage.push()
          }
        }
      }
    }

    stage('Remove local image') {
      steps{
        script {
          def imageTag = 'registry.hub.docker.com/' + DOCKER_IMAGE_INFO
          sh (script: "docker image rm $DOCKER_IMAGE_INFO")
          sh (script: "docker image rm $imageTag")
        }
      }
    }

    stage('Deploy k8s') {
      environment {
        url = credentials('k8s_uri')
      }
      steps{
        withKubeConfig(caCertificate: '', clusterName: 'minikube', contextName: '', credentialsId: 'k8_credential', namespace: 'default', restrictKubeConfigAccess: false, serverUrl: url) {
          sh (script: "/opt/homebrew/bin/kubectl set image deployment/auth-server auth-service-image=$DOCKER_IMAGE_INFO")
          sh (script: "/opt/homebrew/bin/kubectl rollout restart deployment/auth-extension")
        }
      }
    }
  }
}

/*
(Note)
Script approvals in jenkins:
method hudson.model.Executor interrupt hudson.model.Result jenkins.model.CauseOfInterruption[]
method hudson.model.Job getBuilds
method hudson.model.Run getExecutor
method hudson.model.Run getNumber
method hudson.model.Run isBuilding
method jenkins.model.Jenkins getItemByFullName java.lang.String
new jenkins.model.CauseOfInterruption$UserInterruption java.lang.String
staticMethod jenkins.model.Jenkins getInstance
staticMethod org.codehaus.groovy.runtime.DefaultGroovyMethods toInteger java.lang.Number
*/
@NonCPS //https://medium.com/@vedranvucetic/run-only-last-triggered-job-for-branch-in-descriptive-jenkins-pipeline-a07bb84ae075
def cancelPreviousBuilds() {
    def jobName = env.JOB_NAME
    def buildNumber = env.BUILD_NUMBER.toInteger()
    /* Get job name */
    def currentJob = Jenkins.instance.getItemByFullName(jobName)

    /* Iterating over the builds for specific job */
    for (def build : currentJob.builds) {
        def exec = build.getExecutor()
        /* If there is a build that is currently running and it's not current build */
        if (build.isBuilding() && build.number.toInteger() != buildNumber && exec != null) {
            /* Then stop it */
            exec.interrupt(
                    Result.ABORTED,
                    new CauseOfInterruption.UserInterruption("Aborted by #${currentBuild.number}")
            )
            println("Aborted previously running build #${build.number}")
        }
    }
}