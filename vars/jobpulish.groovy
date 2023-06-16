def call()
{
  // Khai báo các hàm dùng chung
  STRING_DELIMITER = ','
  WORKSPACCE = 'hoem/quangdiep'
  URL_FRONTEND = 'https://github.com/quangdiep957/BookingRoomVue.git'
  URL_BACKEND = 'https://github.com/quangdiep957/BookingRoomAPI.git'
    pipline{
        agent{
          label 'linux'
        }
         parameters
        {
          extendedChoise(
            defaultValue:'frontend, backend',
            multiSelectDelimiter: STRING_DELIMITER,
            name : 'APP',
            quoteValue : false,
            saveJsonParameterToFile: false,
            type : 'PT_CHECKBOX',
            value: 'frontend, backend',
            visibleItemCount: 50 ,
            description: 'Chọn app cần build '
          )
        }
        stages
        {
            // get source code mới nhất về     
            stage('get latest source code')
            {
              step
              {
                script
                {
                  sourceTask = [:]
                  // Lấy ra app nào cần build
                  appBuild = params.APP.split(STRING_DELIMITER)
                  // get source của app đó 
                  appbuild.each
                  {
                    app->
                    sourceTask[app] = 
                    {
                      dir(WORKSPACCE)
                      {
                        if (appBuild.contains('frontend'))
                        {
                            script
                            {
                                 checkout([$class: 'GitSCM',
                                  branches: [
                                      [name: 'main']
                                  ],
                                  userRemoteConfigs: [
                                      [url: URL_FRONTEND]
                                  ]
                                ])
                            }
                        }
                        else{
                            script
                            {
                                 checkout([$class: 'GitSCM',
                                  branches: [
                                      [name: 'main']
                                  ],
                                  userRemoteConfigs: [
                                      [url: URL_BACKEND]
                                  ]
                                ])
                            }
                        }
                        
                      }
                    }
                  }
                  parallel sourceTask
                }
              }
            }
        }
    }
}
