package dk.danskespil.gradle.plugins.terraform.tasks

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecSpec

/**
 * Wraps cli command: terraform apply
 */
class Apply extends TerraformBaseTask {
    @Optional
    @InputFile
    File plan

    @Optional
    @Input
    boolean autoApprove = false

    @TaskAction
    action() {
        commandLine.addToEnd('terraform', 'apply')

        if (autoApprove) {
            commandLine.addToEnd('-auto-approve')
        }

        if (plan) {
            commandLine.addToEnd(plan.absolutePath)
        }

        executor.executeExecSpec(this, { ExecSpec e ->
            e.commandLine this.commandLine
            e.workingDir project.projectDir
        })
    }

    @Override
    String getDescription() {
        return """Wraps cli command: terraform apply"""
    }
}
