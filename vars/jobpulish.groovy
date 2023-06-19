def call() {
  // Khai báo các hàm dùng chung
  STRING_DELIMITER = ','
  WORKSPACE = 'home/quangdiep'
  URL_FRONTEND = 'https://github.com/quangdiep957/BookingRoomVue.git'
  URL_BACKEND = 'https://github.com/quangdiep957/BookingRoomAPI.git'

  pipeline {
    agent {
      label 'linux'
    }

    parameters {
      extendedChoice( 
        name: 'APP', 
        defaultValue : 'frontend,backend', 
        value: 'frontend,backend', 
        multiSelectDelimiter: STRING_DELIMITER, 
        quoteValue: false,
        saveJSONParameterToFile: false,
        type: 'PT_CHECKBOX', 
        description: 'Chọn app build?', 
      )
      string(
        defaultValue: 'main',
        name: 'FRONTEND_GIT_BRANCH',
        trim: true,
        description: 'Build Front End nhánh nào?',
      )
      string(
        defaultValue: 'main',
        name: 'BACKEND_GIT_BRANCH',
        trim: true,
        description: 'Build Back End nhánh nào?',
      )
    }

    stages {
      // Get latest source code
      stage('Get latest source code') {
        steps {
          script {
            def sourceTasks = [:]
            // Lấy ra các app cần build
            def appBuild = params.APP ? params.APP.split(STRING_DELIMITER): []
            // Xây dựng các tác vụ checkout cho từng app
            appBuild.each { app ->
              sourceTasks[app] = {
                dir(WORKSPACE) {
                  script {
                    if (app == 'frontend') {
                      checkout([$class: 'GitSCM', branches: [[name: params.FRONTEND_GIT_BRANCH ? params.FRONTEND_GIT_BRANCH : 'main']], userRemoteConfigs: [[url: URL_FRONTEND]]])
                    } else if (app == 'backend') {
                      checkout([$class: 'GitSCM', branches: [[name: params.BACKEND_GIT_BRANCH ? params.FRONTEND_GIT_BRANCH : 'main']], userRemoteConfigs: [[url: URL_BACKEND]]])
                    }
                  }
                }
              }
            }

            // Thực hiện các tác vụ checkout song song
            parallel sourceTasks
          }
        }
      }
              stage('publish app')
        {
          steps
          {
            script
            {
              def sourceTasks = [:]
                // Lấy ra các app cần build
                def appBuild = params.APP ? params.APP.split(STRING_DELIMITER): []
                // publish từng app
                appBuild.each { app ->
                  sourceTasks[app] = {
                    dir(WORKSPACE) {
                      script {
                        if (app == 'frontend') {
                          stage('npm build')
                          {
                            // kiểm tra xem có nodejs chưa
                            script {
                          //  def nodeVersion = sh(returnStdout: true, script: 'node -v').trim()
                          // echo "Node.js version: ${nodeVersion}"
                          //  if (nodeVersion == "") {
                              // Node.js chưa được cài đặt, tiến hành cài đặt
                               // Đường dẫn đến thư mục cài đặt Node.js trong thư mục của Jenkins
                                def nodeInstallDir = "${env.WORKSPACE}/node"
                                
                                // Cài đặt nvm
                                sh 'curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash'
                                
                                sh '''
                                  . ~/.bashrc

                                '''
                                
                                // Cài đặt phiên bản Node.js mong muốn
                                sh "nvm install 16.13.1"
                                
                                // Đặt Node.js làm phiên bản mặc định
                                sh "nvm alias default 16.13.1"
                                
                                // Đặt biến môi trường PATH để sử dụng Node.js và npm
                                sh "export NVM_DIR=${nodeInstallDir}/.nvm"
                                sh "export NODE_VERSION=16.13.1"
                                sh 'export PATH="${NVM_DIR}/versions/node/v${NODE_VERSION}/bin:${PATH}"'
                                
                        //    }
                          }
                                def commands = [
                                   'node -v',
                                   'npm i -g @vue/cli',
                                   'npm i',
                                   'npm run build'
                                   ]
                                   commands.each{i ->
                                   runCmd(i)
                                  }
                            
                          }
                        } else if (app == 'backend') {
                          checkout([$class: 'GitSCM', branches: [[name: params.BACKEND_GIT_BRANCH ?: 'main']], userRemoteConfigs: [[url: URL_BACKEND]]])
                        }
                      }
                    }
                  }
                }
              parallel sourceTasks
            }
          }
        }
    }
  }
}
