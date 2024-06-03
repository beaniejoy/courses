package com.example.imageserver.image

import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

@RestController
@RequestMapping("/api/images")
class ImageController(
    private val imageService: ImageStorageService
) {
    @PostMapping("/upload")
    fun uploadImage(@RequestParam("image") file: MultipartFile): ResponseEntity<String> {
        return try {
            val imageId = imageService.store(file)
            ResponseEntity.ok(imageId)
        } catch (e: IOException) {
            ResponseEntity.status(5000).body("Failed to upload image ${e.message}")
        }
    }

    @GetMapping("/view/{imageId}")
    fun getImage(
        @PathVariable("imageId") imageId: String,
        @RequestParam("thumbnail", defaultValue = "false") isThumbnail: Boolean,
    ): ResponseEntity<Resource> {
        val image = imageService.get(imageId, isThumbnail)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("image/jpeg"))
            .body(image)
    }
}