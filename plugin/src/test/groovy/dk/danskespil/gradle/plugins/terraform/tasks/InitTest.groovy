package dk.danskespil.gradle.plugins.terraform.tasks

import dk.danskespil.gradle.plugins.helpers.BaseSpecification
import org.gradle.testkit.runner.TaskOutcome

class InitTest extends BaseSpecification {
    def "init can be called"() {
        given:
        buildFile << """
          plugins {
              id 'dk.danskespil.gradle.plugins.terraform'
          }

          task cut(type: dk.danskespil.gradle.plugins.terraform.tasks.Init)
        """

        when:
        def build = buildWithTasks(':cut')

        then:
        build
        build.task(':cut').outcome == TaskOutcome.SUCCESS
    }

    def "init with backend config"() {
        given:
        buildFile << """
          plugins {
              id 'dk.danskespil.gradle.plugins.terraform'
          }

          ext {
              prop1 = 'value1'
          }

          task cut(type: dk.danskespil.gradle.plugins.terraform.tasks.Init) {
            backendConfig 'key1', prop1
          }
        """

        when:
        def build = buildWithTasks(':cut')

        then:
        build
        build.task(':cut').outcome == TaskOutcome.SUCCESS
        build.output.find(/-backend=true/)
        build.output.find(/-backend-config=key1=value1/)
    }


    def "To improve performance, init task should only be called once on a new project"() {
        given:
        buildFile << """
          plugins {
              id 'dk.danskespil.gradle.plugins.terraform'
          }

          task cut(type: dk.danskespil.gradle.plugins.terraform.tasks.Init) {
            doLast {
              mkdir('.terraform')
              file('.terraform/terraform.tfstate').createNewFile()
            }
          }
        """

        when:
        def build1 = buildWithTasks(':cut')
        def build2 = buildWithTasks(':cut')

        then:
        build1
        build1.output.contains('terraform init')
        build1.task(':cut').outcome == TaskOutcome.SUCCESS

        !build2.output.contains('terraform')
        build2.task(':cut').outcome == TaskOutcome.UP_TO_DATE
    }
}
