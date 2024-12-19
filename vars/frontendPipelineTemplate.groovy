import org.devops.*

def call(serviceName) {
    def unitTest = new FrontendUnitTest()
    unitTest.installDependencies()
    unitTest.runReactUnitTests()
    unitTest.publishCoverageReport()
}