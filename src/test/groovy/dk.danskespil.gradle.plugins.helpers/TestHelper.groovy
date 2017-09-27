package dk.danskespil.gradle.plugins.terraform.dk.danskespil.gradle.plugins.dk.danskespil.gradle.plugins.helpers

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.rules.TemporaryFolder

class TestHelper {
    TemporaryFolder testProjectDir

    BuildResult buildWithTasks(String... tasks) {
        return base(testProjectDir.root, tasks).build()
    }

    BuildResult buildAndFailWithTasks(String... tasks) {
        return base(testProjectDir.root, tasks).buildAndFail()
    }

    private static base(File projectDir, String... tasks) {
        return GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(projectDir)
                .withArguments(tasks)
    }
}