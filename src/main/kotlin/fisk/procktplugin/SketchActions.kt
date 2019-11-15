package fisk.procktplugin

import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManager
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import java.io.File

const val CLASS = "class"
const val KAPL = ":KApplet(){"
const val DOT_FILE = ".processingkt_sketch_launch"
const val PROC_REF = "https://processing.org/reference/"
const val PROC_ENV = "https://processing.org/reference/environment/"

class SketchActions {

    class SketchRunAction : AnAction("Sketch>Run") {
        override fun actionPerformed(event: AnActionEvent) {
            val project = event.project ?: return

            val projectPath = project.basePath ?: ""

            //Alternative method: https://www.jetbrains.org/intellij/sdk/docs/basics/psi_cookbook.html#how-do-i-find-a-superclass-of-a-java-class
            val code = event.getData(PlatformDataKeys.EDITOR)?.document?.text ?: ""

            code.lines().forEach { line ->
                if (line.startsWith(CLASS)) {
                    val squashed = line.replace(" ", "")
                    if (squashed.contains(KAPL)) {
                        //Valid sketch, so find it and run it
                        val clazzName = squashed.subSequence(5, squashed.indexOf(":"))
                        println("Launch clazzName: $clazzName")

                        val dotFilePath = "$projectPath/$DOT_FILE"
                        println("Write to file: $dotFilePath")
                        val dotFile = File(dotFilePath)
                        if (!dotFile.exists()) dotFile.createNewFile()

                        dotFile.printWriter().use { out ->
                            out.println("$clazzName")
                        }

                        val config = RunManager.getInstance(project).selectedConfiguration ?: return

                        //todo - find the non-deprecated way of launching current run config
                        @Suppress("DEPRECATION")
                        ProgramRunnerUtil.executeConfiguration(project, config, DefaultRunExecutor.getRunExecutorInstance())
                    } else {
                        //Show message in gutter
                    }
                }
            }
        }
    }

    class ProcessingReferenceAction: AnAction("Sketch>Reference") {
        override fun actionPerformed(event: AnActionEvent) {
            BrowserUtil.browse(PROC_REF)
        }
    }

    class ProcessingEnvironmentAction: AnAction("Sketch>Environment") {
        override fun actionPerformed(event: AnActionEvent) {
            BrowserUtil.browse(PROC_ENV)
        }
    }
}