@file:JsModule("fs-extra")

package fsextra

import kotlin.js.Promise

external fun copy(src: String, dest: String, options: CopyOptions = definedExternally): Promise<Unit>

external fun copy(src: String, dest: String): Promise<Unit>

external fun copy(src: String, dest: String, callback: (err: Error) -> Unit)

external fun copy(src: String, dest: String, options: CopyOptions, callback: (err: Error) -> Unit)

external fun copySync(src: String, dest: String, options: CopyOptionsSync = definedExternally)

external fun copyFile(src: String, dest: String, flags: Number = definedExternally): Promise<Unit>

external fun copyFile(src: String, dest: String): Promise<Unit>

external fun copyFile(src: String, dest: String, callback: (err: Error) -> Unit)

external fun copyFile(src: String, dest: String, flags: Number, callback: (err: Error) -> Unit)

external fun move(src: String, dest: String, options: MoveOptions = definedExternally): Promise<Unit>

external fun move(src: String, dest: String): Promise<Unit>

external fun move(src: String, dest: String, callback: (err: Error) -> Unit)

external fun move(src: String, dest: String, options: MoveOptions, callback: (err: Error) -> Unit)

external fun moveSync(src: String, dest: String, options: MoveOptions = definedExternally)

external fun createFile(file: String): Promise<Unit>

external fun createFile(file: String, callback: (err: Error) -> Unit)

external fun createFileSync(file: String)
