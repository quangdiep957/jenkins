def call()
{
pipeline {
    agent
    {
        label 'linux'
        }
    

    stages {
        stage('Build') {
            steps {
                // Bước build ứng dụng
                
                echo "abc"
            }
        }

        stage('Test') {
            steps {
                // Bước kiểm thử ứng dụng
               echo "abcdđ"
            }
        }

        stage('Deploy') {
            steps {
                // Bước triển khai ứng dụng
                echo "step 3"
            }
        }
    }
}
}
