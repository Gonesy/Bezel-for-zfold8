package com.example.bezel

import com.example.bezel.ui.handlePickedImage
import org.junit.Assert.assertEquals
import org.junit.Test

class PickedImageHandlerTest {
    @Test
    fun retainsPermissionBeforeOpeningEditor() {
        val calls = mutableListOf<String>()

        handlePickedImage(
            uriString = "content://photo/1",
            retainReadPermission = { calls += "retain:$it" },
            onImageSelected = { calls += "open:$it" }
        )

        assertEquals(
            listOf("retain:content://photo/1", "open:content://photo/1"),
            calls
        )
    }

    @Test
    fun stillOpensEditorWhenProviderRejectsPersistentPermission() {
        val opened = mutableListOf<String>()

        handlePickedImage(
            uriString = "content://photo/2",
            retainReadPermission = { throw SecurityException("unsupported") },
            onImageSelected = { opened += it }
        )

        assertEquals(listOf("content://photo/2"), opened)
    }

    @Test
    fun ignoresCancelledPickerResult() {
        var called = false

        handlePickedImage(
            uriString = null,
            retainReadPermission = { called = true },
            onImageSelected = { called = true }
        )

        assertEquals(false, called)
    }
}
