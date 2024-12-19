import org.devops.*

def call(serviceName) {
    def unitTest = new BackendInstall()
    unitTest.runUnitTests()
}