package com.example.product_service.Service;
import com.example.product_service.Entity.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class ImageService {
    private final Path storageLocation;
    @Autowired
    public ImageService(@Value("${file.upload-dir}") String uploadDir) throws IOException {
        storageLocation = Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize();
        Files.createDirectories(storageLocation);
    }
    public String save(MultipartFile file) throws IOException {
        String cleaned = StringUtils.cleanPath(file.getOriginalFilename());
        String unique = UUID.randomUUID() + "-" + cleaned;
        Path target = storageLocation.resolve(unique);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return unique;
    }
    public Resource load(String filePath) throws MalformedURLException {
        Path file = storageLocation.resolve(filePath).normalize();
        Resource resource = new UrlResource(file.toUri());
        if (resource.exists()) return resource;
        throw new RuntimeException("File not found " + filePath);
    }
    public void deleteFiles(List<Image> image)
    {
        image.stream().forEach(img->{
            String fileName=img.getUrl();
            Path path=storageLocation.resolve(fileName).normalize();
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


}
