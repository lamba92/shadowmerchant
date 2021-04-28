@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
@file:JsModule("tmp")

package tmp

import kotlin.js.*

external interface TmpNameOptions {
    var dir: String?
        get() = definedExternally
        set(value) = definedExternally
    var name: String?
        get() = definedExternally
        set(value) = definedExternally
    var postfix: String?
        get() = definedExternally
        set(value) = definedExternally
    var prefix: String?
        get() = definedExternally
        set(value) = definedExternally
    var template: String?
        get() = definedExternally
        set(value) = definedExternally
    var tmpdir: String?
        get() = definedExternally
        set(value) = definedExternally
    var tries: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface FileOptions : TmpNameOptions {
    var detachDescriptor: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var discardDescriptor: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var keep: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var mode: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface DirOptions : TmpNameOptions {
    var keep: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var mode: Number?
        get() = definedExternally
        set(value) = definedExternally
    var unsafeCleanup: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface FileResult {
    var name: String
    var fd: Number
    var removeCallback: () -> Unit
}

external interface DirResult {
    var name: String
    var removeCallback: () -> Unit
}

external var tmpdir: String

external fun file(options: FileOptions, cb: FileCallback)

external fun file(cb: FileCallback)

external fun fileSync(options: FileOptions = definedExternally): FileResult

external fun dir(options: DirOptions, cb: DirCallback)

external fun dir(cb: DirCallback)

external fun dirSync(options: DirOptions = definedExternally): DirResult

external fun tmpName(options: TmpNameOptions, cb: TmpNameCallback)

external fun tmpName(cb: TmpNameCallback)

external fun tmpNameSync(options: TmpNameOptions = definedExternally): String

external fun setGracefulCleanup()
