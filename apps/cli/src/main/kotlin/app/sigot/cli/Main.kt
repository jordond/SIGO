package app.sigot.cli

import app.sigot.cli.di.initKoin
import app.sigot.cli.util.BaseCommand
import com.github.ajalt.clikt.command.main

class Cli : BaseCommand() {
    override suspend fun execute() {
        info("Hello, World!")
    }
}

suspend fun main(args: Array<String>) {
    initKoin()

    Cli().main(args)
}
