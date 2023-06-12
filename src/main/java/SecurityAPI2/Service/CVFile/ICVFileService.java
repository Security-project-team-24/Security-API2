package SecurityAPI2.Service.CVFile;

import org.w3c.dom.Document;

public interface ICVFileService {
    void saveDocument(Document doc, String fileName);
    Document loadDocument(String file);
    void deleteDocument(String fileName);
}
