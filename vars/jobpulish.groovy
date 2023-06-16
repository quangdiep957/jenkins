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
        value: 'frontend,backend', 
        multiSelectDelimiter: STRING_DELIMITER, 
        quoteValue: false,
        saveJSONParameterToFile: false,
        type: 'PT_CHECKBOX', 
        description: 'Chọn app build?', 
      )
      string(
        defaultValue: 'master',
        name: 'FRONTEND_GIT_BRANCH',
        trim: true,
        description: 'Build Front End nhánh nào?',
      )
      string(
        defaultValue: 'master',
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
            echo appBuild

            // Xây dựng các tác vụ checkout cho từng app
            appBuild.each { app ->
              sourceTasks[app] = {
                dir(WORKSPACE) {
                  script {
                    if (app == 'frontend') {
                      checkout([$class: 'GitSCM', branches: [[name: params.FRONTEND_GIT_BRANCH ?: 'main']], userRemoteConfigs: [[url: URL_FRONTEND]]])
                    } else if (app == 'backend') {
                      checkout([$class: 'GitSCM', branches: [[name: params.BACKEND_GIT_BRANCH ?: 'main']], userRemoteConfigs: [[url: URL_BACKEND]]])
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
    }
  }
}
