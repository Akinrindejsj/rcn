package com.example.rcn.exception;

/**
 * Checked exception thrown when an image upload to Cloudinary fails. The message
 * is plain-English so it can be shown directly in the CMS UI.
 */
public class CloudinaryUploadException extends Exception {

    public CloudinaryUploadException(String message) {
        super(message);
    }

    public CloudinaryUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
