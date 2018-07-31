import com.dslexample.entities.Project
import com.dslexample.util.GlobalVar
import hudson.FilePath
import hudson.model.Executor
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor


String basePath = GlobalVar.CIAB

folder(basePath) {
    description 'Contains all the CIAB related projects'
}

def customClassLoaderConstructor = new CustomClassLoaderConstructor(this.class.classLoader)
def yaml = new Yaml(customClassLoaderConstructor)

// Build a list of all config files ending in .yml
def cwd = Executor.currentExecutor().getCurrentWorkspace().absolutize()
FilePath[] configFiles = new FilePath(cwd, GlobalVar.CONFIG_DIR).list('*.yml')

// Create/update a pull request job for each config file
configFiles.each { file ->

    Project projectConfig = yaml.loadAs(file.readToString(), Project.class)

    println "[CIAB] Name " + projectConfig.name + " file name " + file.name.replace(".yml", '')

    // Change the spaces to '-'
    def project = projectConfig.name.replaceAll(' ', '-')

    String dirProject = basePath + '/' + project

    job(dirProject) {
        scm {
            git {
                remote {
                    url(String.format(GlobalVar.GITHUB_REPO_LOCATION_URL, 'ciab-plugin'))
                }
                branch(projectConfig.branch)
                extensions {
                    relativeTargetDirectory('ciab')
                }
            }
        }

        steps {
            shell("echo 'hello world'")
        }

    }
}


