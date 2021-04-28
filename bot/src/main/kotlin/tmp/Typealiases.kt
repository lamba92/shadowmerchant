package tmp

typealias FileCallback = (err: Error?, name: String, fd: Number, removeCallback: () -> Unit) -> Unit

typealias DirCallback = (err: Error?, name: String, removeCallback: () -> Unit) -> Unit

typealias TmpNameCallback = (err: Error?, name: String) -> Unit
