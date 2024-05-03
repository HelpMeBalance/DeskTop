package org.example.service;

import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImgurUploader {

    private static final String IMGUR_UPLOAD_URL = "https://api.imgur.com/3/image";
    private static final String IMGUR_CLIENT_ID = "Your-Client-ID";

    public static String uploadFile(File file) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(IMGUR_UPLOAD_URL).openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Client-ID " + IMGUR_CLIENT_ID);

        try (DataOutputStream dos = new DataOutputStream(conn.getOutputStream())) {
            dos.writeBytes("--boundary12345\r\n");
            dos.writeBytes("Content-Disposition: form-data; name=\"image\"; filename=\"" + file.getName() + "\"\r\n");
            dos.writeBytes("Content-Type: application/pdf\r\n\r\n");

            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    dos.write(buffer, 0, bytesRead);
                }
            }

            dos.writeBytes("\r\n--boundary12345--\r\n");
        }

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Error uploading file, response code: " + responseCode);
        }

        try (InputStream is = conn.getInputStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            JSONObject jsonResponse = new JSONObject(response.toString());
            return jsonResponse.getJSONObject("data").getString("link");
        }
    }
}
