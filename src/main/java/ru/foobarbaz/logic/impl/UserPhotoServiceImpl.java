package ru.foobarbaz.logic.impl;

import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;
import ru.foobarbaz.logic.UserPhotoService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class UserPhotoServiceImpl implements UserPhotoService {
    private static final Path ROOT_DIR = Paths.get(System.getProperty("user.home"), ".foobarbaz", "img", "users");
    public static final String FORMAT_NAME = "png";

    @Override
    public void uploadPhoto(String username, byte[] data) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(data));
        for (PhotoSize photoSize: PhotoSize.values()) {
            Scalr.Mode mode = image.getWidth() < image.getHeight()
                    ? Scalr.Mode.FIT_TO_WIDTH
                    : Scalr.Mode.FIT_TO_HEIGHT;

            BufferedImage scaledImage = Scalr.resize(image, mode, photoSize.getSize());

            Path folder = ROOT_DIR.resolve(username);
            Files.createDirectories(folder);
            File file = folder.resolve(photoSize.name()).toFile();
            if (file.exists() || file.createNewFile())
                ImageIO.write(scaledImage, FORMAT_NAME, file);
        }
    }

    @Override
    public byte[] downloadPhoto(String username, PhotoSize size) throws IOException {
        File file = ROOT_DIR.resolve(username).resolve(size.name()).toFile();
        if (!file.exists()) return null;
        BufferedImage image = ImageIO.read(file);
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        ImageIO.write(image, FORMAT_NAME, bao);
        return bao.toByteArray();
    }
}
