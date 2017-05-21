package ru.foobarbaz.logic;

import java.io.IOException;

public interface UserPhotoService {
    void uploadPhoto(String  username, byte[] data) throws IOException;
    byte[] downloadPhoto(String  username, PhotoSize size) throws IOException;

    enum PhotoSize{
        MIN(20), MID(100), MAX(300);

        private int size;

        PhotoSize(int size) {
            this.size = size;
        }

        public int getSize() {
            return size;
        }
    }
}


