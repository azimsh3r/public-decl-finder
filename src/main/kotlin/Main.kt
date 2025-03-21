import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.config.addKotlinSourceRoots
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.psi.PsiFile
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierTypeOrDefault
import java.io.File

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("Usage: <program> <path-to-kotlin-source>")
        return
    }

    val sourceRoot = args[0]
    val files = File(sourceRoot).walk()
        .filter { it.extension == "kt" }
        .map { it.absolutePath }
        .toList()

    if (files.isEmpty()) {
        println("No Kotlin source files found in the given directory.")
        return
    }

    processFiles(files)
}

fun processFiles(files: List<String>) {
    val disposable = Disposer.newDisposable()
    try {
        val configuration = CompilerConfiguration().apply {
            addKotlinSourceRoots(files)
            put(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        }

        val environment = KotlinCoreEnvironment.createForProduction(
            disposable, configuration, EnvironmentConfigFiles.JVM_CONFIG_FILES
        )

        environment.getSourceFiles().forEach { extractPublicDeclarations(it) }
    } finally {
        Disposer.dispose(disposable)
    }
}

fun extractPublicDeclarations(file: PsiFile) {
    val ktFile = file as? KtFile ?: return

    for (declaration in ktFile.declarations) {  // Top-level declarations only
        if (isPublicDeclaration(declaration)) {
            println(formatDeclaration(declaration))
        }
    }
}

fun isPublicDeclaration(declaration: KtDeclaration): Boolean {
    val modifierListOwner = declaration as? KtModifierListOwner ?: return false
    val visibilityModifier = modifierListOwner.visibilityModifierTypeOrDefault()

    return visibilityModifier.value == "public" ||
            modifierListOwner.hasModifier(KtTokens.OPEN_KEYWORD) ||
            modifierListOwner.hasModifier(KtTokens.OVERRIDE_KEYWORD)
}

fun formatDeclaration(declaration: KtDeclaration): String {
    return when (declaration) {
        is KtNamedFunction -> "fun ${declaration.name}()"
        is KtClass -> "class ${declaration.name}"
        is KtObjectDeclaration -> "object ${declaration.name}"
        is KtProperty -> "val ${declaration.name}"
        else -> "unknown declaration"
    }
}
