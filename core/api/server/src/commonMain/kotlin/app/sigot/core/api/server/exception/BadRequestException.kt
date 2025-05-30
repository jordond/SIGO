package app.sigot.core.api.server.exception

public class BadRequestException(
    message: String = "Bad Request",
    public val validation: List<String> = emptyList(),
) : Exception(message)
