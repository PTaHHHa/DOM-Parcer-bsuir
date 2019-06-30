import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.xml.XMLConstants;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
public class Main {
    private static ArrayList<String> menuElems = new ArrayList<>();
    static {
        menuElems.add("IF Valid press  1");
        menuElems.add("IF Print press  2");
        menuElems.add("IF Add press    3");
        menuElems.add("IF Delete press 4");
        menuElems.add("IF Search press 5");
        menuElems.add("IF Exit press   6");}
    public static void main(String[] args) {
        boolean stoped = false;
        DomHandler dh = new DomHandler();
        while(!stoped) {
            for (String elem : menuElems) System.out.println(elem);
            Scanner scanner = new Scanner(System.in);
            int point = scanner.nextInt();
            switch (point) {
                case 1:
                    System.out.println( "Is information valid or not? " + validateXMLSchema("src/db.xsd", "src/db.xml"));
                    break;
                case 2:
                    dh.read();
                    break;
                case 3:
                    add();
                    break;
                case 4:
                    remove();
                    break;
                case 5:
                    search();
                    break;
                default:
                    stoped = true;}}}
    private static void add() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter title: ");
        String name = scanner.next();
        System.out.print("Enter manufactorer's company: ");
        String proiz = scanner.next();
        System.out.print("Enter material: ");
        String mat = scanner.next();
        System.out.print("Enter date of delivery: ");
        String data = scanner.next();
        new DomHandler().add(name,proiz,mat,data);}

    private static void remove() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter ID: ");
        String ID = scanner.next();
        new DomHandler().remove(ID);}

    public static boolean validateXMLSchema(String xsdPath, String xmlPath) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(xsdPath));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(xmlPath)));
        } catch (IOException | SAXException e) {
            System.out.println("Exception: "+e.getMessage());
            return false;
        }
        return true;
    }
    private static void search() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter atribute: ");
        String attribute = scanner.next();
        new SaxSearch().start(attribute);}
    public static class DomHandler {
        public void read() {
            try {
                DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document document = documentBuilder.parse("src/db.xml");
                Node root = document.getDocumentElement();
                System.out.println("List of goods:");
                System.out.println();
                NodeList db = root.getChildNodes();
                NodeList empls = db.item(0).getChildNodes();
                for (int i = 0; i < empls.getLength(); i++) {
                    Node empl = empls.item(i);
                    System.out.println("ID: " + empl.getAttributes().getNamedItem("ID").getNodeValue());
                    if (empl.getNodeType() != Node.TEXT_NODE) {
                        NodeList emplProps = empl.getChildNodes();
                        for(int j = 0; j < emplProps.getLength(); j++) {
                            Node emplProp = emplProps.item(j);
                            if (emplProp.getNodeType() != Node.TEXT_NODE) {
                                System.out.println(emplProp.getNodeName() + ":" + emplProp.getChildNodes().item(0).getTextContent()); }}
                        System.out.println();}}
            } catch (ParserConfigurationException ex) {
                ex.printStackTrace(System.out);
            } catch (SAXException ex) {
                ex.printStackTrace(System.out);
            } catch (IOException ex) {
                ex.printStackTrace(System.out);}}
        public void add(String nm, String pz,String mt, String dt) {
            try {
                DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document document = documentBuilder.parse("src/db.xml");
                Node root = document.getDocumentElement();
                Node empls = root.getChildNodes().item(0);
                NodeList es = empls.getChildNodes();
                Node e = es.item(es.getLength() - 1);
                String lastID = e.getAttributes().getNamedItem("ID").getNodeValue();
                Element empl = document.createElement("good");
                Integer newID = Integer.parseInt(lastID) + 1;
                empl.setAttribute("ID", "" + newID);
                Element name = document.createElement("name");
                name.setTextContent(nm);
                Element proiz = document.createElement("proiz");
                proiz.setTextContent(pz);
                Element mat = document.createElement("mat");
                mat.setTextContent(mt);
                Element data = document.createElement("data");
                data.setTextContent(dt);

                empl.appendChild(name);
                empl.appendChild(proiz);
                empl.appendChild(mat);
                empl.appendChild(data);

                empls.appendChild(empl);
                writeDocument(document);
            } catch (SAXException | IOException | ParserConfigurationException ex) {
                ex.printStackTrace();}}
        public void remove(String nomer) {
            try {
                DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document dcq = documentBuilder.parse("src/db.xml");
                Node root = dcq.getDocumentElement();
                Node empls = root.getChildNodes().item(0);
                NodeList emps = empls.getChildNodes();
                for (int i=0; i < emps.getLength(); i++) {
                    if(nomer.equals(emps.item(i).getAttributes().getNamedItem("ID").getNodeValue())) empls.removeChild(emps.item(i));
                }
                writeDocument(dcq);
            } catch (SAXException | IOException | ParserConfigurationException ex) {
                ex.printStackTrace();}}
        private static void writeDocument(Document dqc) throws TransformerFactoryConfigurationError {
            try {
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                DOMSource source = new DOMSource(dqc);
                FileOutputStream fos = new FileOutputStream("src/db.xml");
                StreamResult result = new StreamResult(fos);
                tr.transform(source, result);
            } catch (TransformerException | IOException e) {
                e.printStackTrace(System.out); }}}
    public static class SaxSearch {
        public void start(String tag) {
            final String fileName = "src/db.xml";
            try {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser saxParser = factory.newSAXParser();
                DefaultHandler handler = new DefaultHandler() {
                    boolean name = false;
                    @Override
                    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                        if (qName.equalsIgnoreCase(tag)) {
                            name = true; }}
                    @Override
                    public void characters(char ch[], int start, int length) throws SAXException {
                        if (name) {
                            System.out.println(tag + ": " + new String(ch, start, length));
                            name = false;}}};
                saxParser.parse(fileName, handler);
            } catch (Exception e) {
                e.printStackTrace();}}}}