String basePath = 'example8'
String repo = 'sheehan/gradle-example'

folder(basePath) {
    description 'This example shows how to use DSL extensions provided by other plugins.'
}

job("$basePath/gradle-example-build") {
    scm {
        git {
            remote {
                github repo
                refspec '+refs/pull/*:refs/remotes/origin/pr/*'
            }
            branch '${sha1}'
        }
    }

    steps {
        gradle 'assemble'
    }
}
