package fsextra

external interface CopyOptions {
    var dereference: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var overwrite: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var preserveTimestamps: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var errorOnExist: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var filter: dynamic /* CopyFilterSync? | CopyFilterAsync? */
        get() = definedExternally
        set(value) = definedExternally
    var recursive: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}
