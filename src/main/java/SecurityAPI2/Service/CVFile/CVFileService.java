package SecurityAPI2.Service.CVFile;

import SecurityAPI2.Exceptions.CVDoesntExistsException;
import SecurityAPI2.utils.CV.CVEncryption;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class CVFileService implements  ICVFileService {
    private final CVEncryption cvEncryption;

    private static final String OUT_PATH = "./CVs/";
    public Document loadDocument(String file) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(new File(OUT_PATH + file + ".xml"));
        } catch (FileNotFoundException e) {
            throw new CVDoesntExistsException("Engineer didn't add cv!");
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public void saveDocument(Document multipartFileDoc, String fileName) {
        try {
            File outFile = new File(OUT_PATH + fileName + ".xml");
            FileOutputStream f = new FileOutputStream(outFile);

            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();

            DOMSource source = new DOMSource(multipartFileDoc);
            StreamResult result = new StreamResult(f);

            transformer.transform(source, result);

            f.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteDocument(String fileName) {
        try {
            Path file = Paths.get(OUT_PATH + fileName + ".xml");
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}
