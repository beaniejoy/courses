package com.example.imageserver.image

import net.coobird.thumbnailator.Thumbnails
import net.coobird.thumbnailator.geometry.Position
import net.coobird.thumbnailator.geometry.Positions
import net.coobird.thumbnailator.name.Rename
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import java.nio.file.Paths
import java.util.UUID
import javax.imageio.ImageIO
import kotlin.jvm.Throws

@Service
class ImageStorageService(
    @Value("\${images.upload-root}")
    private val imageRoot: String,
) {
    @Throws(IOException::class)
    fun store(file: MultipartFile): String {
        val imageId = UUID.randomUUID().toString()
        val original = ImageIO.read(file.inputStream)

        val imageFile = File("${imageRoot}/${imageId}.jpg")
        Thumbnails.of(original)
            .scale(1.0)
            .outputFormat("jpg")
            .toFile(imageFile)

        Thumbnails.of(imageFile)
            .crop(Positions.CENTER)
            .size(500, 500)
            .outputFormat("jpg")
            .toFiles(Rename.SUFFIX_HYPHEN_THUMBNAIL)

        return imageId
    }

    fun get(imageId: String, isThumbnail: Boolean): Resource? {
        val file = Paths.get(imageRoot)
            .resolve("${imageId}${if (isThumbnail) "-thumbnail" else ""}.jpg")

        return try {
            val resource = UrlResource(file.toUri())
            if (resource.exists() || resource.isReadable) {
                resource
            } else null
        } catch (e: MalformedURLException) {
            null
        }
    }


}
