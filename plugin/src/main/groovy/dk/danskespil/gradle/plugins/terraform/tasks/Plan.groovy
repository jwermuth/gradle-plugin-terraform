package dk.danskespil.gradle.plugins.terraform.tasks

import dk.danskespil.gradle.task.helpers.EchoOutputStream
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecSpec

class Plan extends TerraformBaseTask implements TerraformVariables
{
    // These inputfiles are the same for Validate and Plan
    @InputFiles
    FileCollection terraformFiles = project.fileTree('.').include('*.tf').include('*.tpl')

    @Optional
    @OutputFile
    // As an extra feature the output from plan is saved to a file. This can be used for auditing, e.g. keep the
    // file around, so you know what terraform said when this particular command was applied
    File outAsText

    @Optional
    @OutputFile
    // From terraform --help plan
    // -out=path           Write a plan file to the given path. This can be used as input to the "apply" command.
    File out

    @TaskAction
    action() {
        commandLine.addToEnd('terraform', 'plan')
        addVariablesToEnd(commandLine)
        if (out) {
            commandLine.addToEnd("-out=${out.absolutePath}")
        }
        OutputStream echoOutputHereToo = new EchoOutputStream(new ByteArrayOutputStream(), System.out)
        if (outAsText) {
            outAsText.createNewFile()
            echoOutputHereToo = new EchoOutputStream(echoOutputHereToo, new PrintStream(outAsText))
        }

        echoOutputHereToo.withStream { os ->
            executor.executeExecSpec(this, { ExecSpec e ->
                e.commandLine this.commandLine
                e.workingDir project.projectDir
                e.standardOutput = os
            })
        }
    }

    @Override
    String getDescription() {
        return """wraps terraform plan. You can set -out='filename' with and get textual output into a file with out
    task(type: dk.danskespil.gradle.plugins.terraform.tasks.Plan) {
      out=file('output.bin')
      out=file('textversion.txt')
    }
    """
    }
}
