package now.shouldigooutside.core.platform.di

import dev.jordond.connectivity.Connectivity

internal actual fun getConnectivity(): Connectivity = Connectivity()
