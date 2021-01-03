package me.hannah

import me.hannah.parser.HannahLanguageParser.*
import me.hannah.parser.HannahLanguageParser

data class FunctionTypes(val rtrn: String, val args: List<String> = listOf())

class FunctionDefinitionFinder {

  fun find(parser: HannahLanguageParser): HashMap<String, FunctionTypes> {
    val map = HashMap<String, FunctionTypes>()

    parser.root().rootDef()
      .mapNotNull { it.typeDef() }
      .mapNotNull {
        it.noArgsTypeDef() ?: it.argsTypeDef()
      }
      .forEach { def ->
        when(def) {
          is NoArgsTypeDefContext -> {
            map[def.fnName().ID().text] = FunctionTypes(def.type().ID().text)
          }
          is ArgsTypeDefContext -> {
            val args = def.defArgs().type().map { it.ID().text }
            map[def.fnName().ID().text] = FunctionTypes(def.type().ID().text, args)
          }
        }
      }

    return map
  }
}