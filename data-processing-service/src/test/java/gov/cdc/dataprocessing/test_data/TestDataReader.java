package gov.cdc.dataprocessing.test_data;

import com.google.gson.Gson;
import gov.cdc.dataprocessing.model.phdc.Container;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class TestDataReader {

    public <T> T readDataFromJsonPath(String path, Class<T> type) {
        String resourcePath = "/test_data" + (path.startsWith("/") ? path : "/" + path);
        T data;

        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Resource not found at path: " + resourcePath);
            }
            try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                Gson gson = new Gson();
                data = gson.fromJson(reader, type);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Failed to load resource: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Error processing JSON data: " + e.getMessage());
            throw new RuntimeException("Error processing JSON data", e);
        }

        return data;
    }
    public String readDataFromXmlPath(String path) {
        String resourcePath = "/test_data" + (path.startsWith("/") ? path : "/" + path);
        StringBuilder data = new StringBuilder();

        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Resource not found at path: " + resourcePath);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    data.append(line).append("\n");
                }
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Failed to load resource: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Error processing XML data: " + e.getMessage());
            throw new RuntimeException("Error processing XML data", e);
        }

        return data.toString();
    }

    public Container convertXmlStrToContainer(String payload) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Container.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        StringReader reader = new StringReader(payload);
        return (Container) unmarshaller.unmarshal(reader);
    }








}
