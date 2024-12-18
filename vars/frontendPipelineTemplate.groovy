import org.devops.*

def call(serviceName) {
    def unitTest = new BackendUnitTest()
    unitTest.runUnitTests()
}