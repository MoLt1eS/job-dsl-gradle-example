import com.dslexample.entities.Project
import com.dslexample.util.GlobalVar
import hudson.FilePath
import hudson.model.Executor
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor

/**
 * This script is to create CIAB build only jobs
 */

String basePath = GlobalVar.CIAB

folder(basePath) {
    description 'Contains all the CIAB related projects'
}

def customClassLoaderConstructor = new CustomClassLoaderConstructor(this.class.classLoader)
def yaml = new Yaml(customClassLoaderConstructor)

// Build a list of all config files ending in .yml
def cwd = Executor.currentExecutor().getCurrentWorkspace().absolutize()
FilePath[] configFiles = new FilePath(cwd, GlobalVar.CONFIG_CIAB_DIR).list('*.yml')


configFiles.each { file ->

    Project projectConfig = yaml.loadAs(file.readToString(), Project.class)

    println "[CIAB] Name " + projectConfig.name + " file name " + file.name.replace(".yml", '')

    // Change the spaces to '-'
    def project = projectConfig.name.replaceAll(' ', '-')

    String dirProject = basePath + '/' + project

    job(dirProject) {
        multiscm {

            git {
                remote {
                    credentials(GlobalVar.GITHUB_CREDENTIALS_ID)
                    url(String.format(GlobalVar.GITHUB_REPO_LOCATION_URL, 'ciab-base'))
                }
                branch(projectConfig.branch)
                extensions {
                    relativeTargetDirectory('.')
                }
            }


            println "Preprating to iterate stuff"
            GlobalVar.CIAB_PROJECTS.each { pName ->
                println "Iterating ${pName}"
                git {
                    remote {
                        credentials(GlobalVar.GITHUB_CREDENTIALS_ID)
                        url(String.format(GlobalVar.GITHUB_REPO_LOCATION_URL, pName))
                    }
                    branch(projectConfig.branch)
                    extensions {
                        relativeTargetDirectory(pName)
                    }
                }
            }

        }


        steps {
            shell(projectConfig.buildCommand)
        }

    }

}



