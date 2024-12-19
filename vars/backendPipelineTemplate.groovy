import org.devops.*

def call(serviceName) {
    def install = new BackendInstall()
    install.runInstall()
}