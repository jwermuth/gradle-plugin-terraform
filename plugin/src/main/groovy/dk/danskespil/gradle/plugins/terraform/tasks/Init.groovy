package dk.danskespil.gradle.plugins.terraform.tasks

import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFiles

import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecSpec

class Init extends TerraformBaseTask {
    @OutputFiles
    FileCollection outputFilesSoGradleOnlyBuildsWhenItChanges = project.fileTree('.terraform/terraform.tfstate')

    @Optional
    @Input
    Map<String, String> backendConfig = [:]

    @Internal
    List extraArgs = []

    @TaskAction
    action() {
        commandLine.addToEnd('terraform', 'init')

        if (!backendConfig.isEmpty()) {
          extraArgs.add('-backend=true')
        }

        backendConfig.each{ k, v -> extraArgs.add('-backend-config=' + k + '=' + v) }

        executor.executeExecSpec(this, { ExecSpec e ->
            e.commandLine this.commandLine
            e.args extraArgs
            e.workingDir project.projectDir
        })
    }

    def backendConfig(String key, String value) {
        backendConfig.put(key, value)
    }

    @Override
    String getDescription() {
        return """Wraps terraform init. Will only execute if no .terraform/terraform.state file is present. Designed to initialize automatically in a fresh clone"""
    }
}
