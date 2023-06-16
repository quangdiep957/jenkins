def call()
{
  // Khai báo các hàm dùng chung
  STRING_DELIMITER = ','
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
        }
    }
}
