import com.dslexample.entities.Project
import com.dslexample.util.GlobalVar
import hudson.FilePath
import hudson.model.Executor
import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.helpers.ScmContext
import javaposse.jobdsl.dsl.helpers.scm.GitContext
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

    def currentJob = job(dirProject) {

        steps {
            shell("echo 'hello world'")
        }

    }

    addMultiRepos(currentJob)
}

void addMultiRepos(Job job) {

    ScmContext context

    context.git {
        remote {
            credentials(GlobalVar.GITHUB_CREDENTIALS_ID)
            url(String.format(GlobalVar.GITHUB_REPO_LOCATION_URL, 'ciab-plugin'))
        }
        branch(projectConfig.branch)
        extensions {
            relativeTargetDirectory('ciab')
        }
    }

    println "Preprating to iterate stuff"
    GlobalVar.CIAB_PROJECTS.each { pName ->
        println "Iterating ${pName}"
        context.git {
            remote {
                credentials(GlobalVar.GITHUB_CREDENTIALS_ID)
                url(String.format(GlobalVar.GITHUB_REPO_LOCATION_URL, pName))
            }
            branch(projectConfig.branch)
            extensions {
                relativeTargetDirectory('ciab/${pName}')
            }
        }
    }

    job.multiscm(context)

}


