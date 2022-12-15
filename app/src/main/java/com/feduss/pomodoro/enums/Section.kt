package com.feduss.pomodoro.enums

sealed class Section(val baseRoute: String, val parametricRoute: String = "") {
    object Setup: Section("setup")
    object Edit: Section("edit", "edit/{tag}")
    object Timer: Section("timer", "timer?chipIndex={chipIndex}&cycleIndex={cycleIndex}&timerSeconds={timerSeconds}")

    fun withArgs(args: List<String>? = null, optionalArgs: Map<String, String>? = null): String {
        var destinationRoute = baseRoute
        args?.let { argsNotNull ->
            for(arg in argsNotNull) {
                destinationRoute += "/$arg"
            }
        }
        optionalArgs?.let { optionalArgsNotNull ->
            destinationRoute+= "?"
            optionalArgsNotNull.onEachIndexed { index, (optionalArgName, optionaArgValue) ->
                destinationRoute += "$optionalArgName=$optionaArgValue"

                if (optionalArgsNotNull.count() > 1 && index < optionalArgsNotNull.count() - 1) {
                    destinationRoute += "&"
                }
            }
        }
        return destinationRoute
    }
}
