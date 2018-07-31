import com.dslexample.entities.Project
import hudson.FilePath
import hudson.model.Executor
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor


String basePath = 'CIAB'
//String repo = 'compareeuropegroup/ciab-plugin'

folder(basePath) {
    description 'Contains all the CIAB related projects'
}

createCIABJobs()

void createCIABJobs(){

    def constr = new CustomClassLoaderConstructor(this.class.classLoader)
    def yaml = new Yaml(constr)

    // Build a list of all config files ending in .yml
    def cwd = Executor.currentExecutor().getCurrentWorkspace().absolutize()
    def configFiles = new FilePath(cwd, 'src/configs').list('*.yml')

    println "[TIAGO] Im doing stuff??"

    // Create/update a pull request job for each config file
    configFiles.each { file ->
        Project projectConfig = yaml.loadAs(file.readToString(), Project.class)
//        def project = projectConfig.project.replaceAll(' ', '-')

        job(projectConfig.name) {
            scm {
                github 'sheehan/gradle-example'
            }
        }
    }


}