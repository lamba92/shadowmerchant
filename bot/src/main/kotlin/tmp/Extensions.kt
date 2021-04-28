package tmp

import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun dirSuspending(options: DirOptions) = suspendCoroutine<String> { continuation ->
    dir(options.apply { keep = false }) { err, name: String?, _ ->
        when {
            err != null -> continuation.resumeWithException(err)
            name != null -> continuation.resume(name)
            else -> continuation.resumeWithException(Throwable("Both error and name are null. WTF"))
        }
    }
}

suspend fun dirSuspending() = suspendCoroutine<String> { continuation ->
    dir { err, name: String?, _ ->
        when {
            err != null -> continuation.resumeWithException(err)
            name != null -> continuation.resume(name)
            else -> continuation.resumeWithException(Throwable("Both error and name are null. WTF"))
        }
    }
}
