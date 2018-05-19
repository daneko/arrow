package arrow.optics

import me.eugeniomarletti.kotlin.metadata.plusIfNotBlank

fun generateLenses(ele: AnnotatedElement, target: LensTarget) = Snippet(
  content = processElement(ele, target.foci)
)

private fun String.toUpperCamelCase(): String = split(" ").joinToString("", transform = String::capitalize)

private fun processElement(ele: AnnotatedElement, foci: List<Focus>): String = foci.joinToString(separator = "\n") { focus ->
  """
  |inline val ${ele.sourceClassName}.Companion.${focus.lensParamName()}: $Lens<${ele.sourceClassName}, ${focus.className}> inline get()= $Lens(
  |  get = { ${ele.sourceName}: ${ele.sourceClassName} -> ${ele.sourceName}.${focus.paramName.plusIfNotBlank(prefix = "`", postfix = "`")} },
  |  set = { value: ${focus.className} ->
  |    { ${ele.sourceName}: ${ele.sourceClassName} ->
  |      ${ele.sourceName}.copy(${focus.paramName.plusIfNotBlank(prefix = "`", postfix = "`")} = value)
  |    }
  |  }
  |)
  |""".trimMargin()
}

fun Focus.lensParamName(): String = when (this) {
  is NullableFocus -> "nullable${paramName.toUpperCamelCase()}"
  is OptionFocus -> "option${paramName.toUpperCamelCase()}"
  is NonNullFocus -> paramName
}
