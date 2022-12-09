package com.feduss.pomodoro

sealed class Section(val baseRoute: String, val parametricRoute: String = "") {
    object Setup: Section("setup")
    object Edit: Section("edit", "edit/{tag}")
    object Timer: Section("timer")

    fun withArgs(args: List<String>, optionalArgs: Map<String, String>? = null): String {
        var destinationRoute = baseRoute
        for(arg in args) {
            destinationRoute += "/$arg"
        }
        optionalArgs?.let { optionalArgsNotNull ->
            destinationRoute+= "?"
            for((optionalArgName, optionaArgValue) in optionalArgsNotNull) {
                destinationRoute += "$optionalArgName=$optionaArgValue"
            }
        }
        return destinationRoute
    }
}
